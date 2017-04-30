package com.taylor.simulation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import com.taylor.localization.GenerateFiles;
import com.taylor.tools.*;
import com.taylor.tools.Tools.FILETYPE;

public class ConvertDatFile {
    
    private File srcFile;
    private File convFile;
    private File csvFile;
    private static final File cellIdsAndCellNames = new File (GenerateFiles.GIT_DIRECTORY + GenerateFiles.MEASUREMENT_DATA + "\\aircom_cellkeys.csv");
    private static int maxNoOfServers = 0;
    private static FILETYPE fileType = FILETYPE.UNDEFINED;
    
    public ConvertDatFile(File srcFile, File convFile, File csvFile) {
        this.setsrcFile(srcFile);
        this.setConvFile(convFile);
        this.setCsvFile(csvFile);
    }
    
    public File getSrcFile() {
        return srcFile;
    }

    private void setsrcFile(File srcFile) {
        this.srcFile = srcFile;
    }
    
    public File getConvFile() {
        return convFile;
    }

    private void setConvFile(File convFile) {
        this.convFile = convFile;
        Tools.createFile(this.convFile);
    }
    
    public File getCsvFile() {
        return csvFile;
    }
    
    private void setCsvFile(File csvFile) {
        this.csvFile = csvFile;
        Tools.createFile(this.csvFile);
    }
    
    
    
    private HashMap<String, String> setCellIdsAndCellName (File cellDatabaseFile) {
        ArrayList<String> cellDatabase = null;
        HashMap<String, String> cellDatabaseMap = null;
        String[] splittedDatabaseRow = null;
        String cellIdInDatabase = null;
        String cellNameInDatabase = null;
        
        cellDatabase = Tools.readFileToMemory(cellDatabaseFile);
        cellDatabaseMap = new HashMap<String, String>();
        
        
        for (int rowCounter = 1; rowCounter < cellDatabase.size(); rowCounter++) {
            splittedDatabaseRow = cellDatabase.get(rowCounter).trim().split(";");
            cellIdInDatabase = splittedDatabaseRow[0];
            cellNameInDatabase = splittedDatabaseRow[5];
            cellDatabaseMap.put(cellIdInDatabase, cellNameInDatabase);
        }
        
        return cellDatabaseMap;
    }
    
    private void createRawData() {
        FileInputStream readFile = null;
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        StringBuilder numOfServers = null;
        StringBuilder headerRow = null;
        ArrayList<String> dataRow = null;
        boolean isNumOfServersSet = false;
        int carriageReturnSensor = 0;
        int emptyLineSize = 2;
        int readedCharLength;
        int numOfServersInt = 0;
        int numOfServersCharLength = 2;
        int serverCounter = 0;
        int read;
        char character = 0;
        String hexNewLine = "0a";
        String hexCarriageReturn = "0d";
        String zeroChar = "0";
        String readedChar = null;
        
        dataRow = new ArrayList<String>();
        
        try {
            readFile = new FileInputStream(getSrcFile());
            fileWriter = new FileWriter(getConvFile());
            bufferedWriter = new BufferedWriter(fileWriter);
  
            while ((read = readFile.read()) != -1) {
                readedChar = Integer.toHexString(read);
                readedCharLength = readedChar.length();
                
                if (readedCharLength == 1) {
                    readedChar = zeroChar.concat(readedChar);
                }
                
                dataRow.add(readedChar);
                
                if (fileType == FILETYPE.BESTSERVER  && carriageReturnSensor == 3 && dataRow.size() == 10) {
                    for (String data : dataRow) {
                        bufferedWriter.write(data + " ");
                    }
                    
                    bufferedWriter.newLine();
                    dataRow.clear();
                }
                
                if (fileType == FILETYPE.NTHSERVER && carriageReturnSensor == 3) { 
                    if (dataRow.size() == numOfServersCharLength && isNumOfServersSet == false) {
                        numOfServers = new StringBuilder();
                        
                        for (String data : dataRow) {
                            character = Tools.convertHex2Char(data);
                            
                            if (character != zeroChar.charAt(0)) {
                                numOfServers.append(character);
                            } else {
                                if (numOfServers.length() > 0) {
                                    numOfServers.append(character);
                                }
                            }   
                        }
                        
                        if (numOfServers.length() > 0) {
                            numOfServersInt = Integer.parseInt(numOfServers.toString());
                            isNumOfServersSet = true;
                        } else {
                            isNumOfServersSet = false;
                            
                            for (int zeroRowCharCounter = 0; zeroRowCharCounter < 10; zeroRowCharCounter++) {
                                bufferedWriter.write("00 ");
                            }
                            
                            bufferedWriter.newLine();
                        }
                        
                        dataRow.clear();   
                    } else {
                        if (dataRow.size() == 10 && serverCounter < numOfServersInt) {
                            for (String data : dataRow) {
                                bufferedWriter.write(data + " ");
                            }
                            
                            dataRow.clear();
                            serverCounter++;
                            
                            if (serverCounter == numOfServersInt) {
                                bufferedWriter.newLine();
                            }
                        }
                        
                        if (serverCounter == numOfServersInt) {
                            serverCounter = 0;
                            isNumOfServersSet = false;
                        }
                    }  
                }
                
                if (readedChar.equals(hexNewLine)) {
                    if (carriageReturnSensor < 3) {
                        if (dataRow.size() > emptyLineSize) {
                            headerRow = new StringBuilder();
                            dataRow.remove(hexNewLine);
                            dataRow.remove(hexCarriageReturn);
                            
                            for (String data : dataRow) {
                                character = Tools.convertHex2Char(data);
                                headerRow.append(character);
                                bufferedWriter.write(character);
                            }
                            
                            bufferedWriter.newLine();
                            String[] headerRowArray = headerRow.toString().trim().split(" ");
                            ArrayList<String> headerRowArrayList = new ArrayList<String>(Arrays.asList(headerRowArray));
                            
                            if (headerRowArrayList.contains("BESTSERVER")) {
                                fileType = FILETYPE.BESTSERVER;
                            } else if (headerRowArrayList.contains("NTHSERVER")) {
                                fileType = FILETYPE.NTHSERVER;
                                maxNoOfServers = Integer.parseInt(headerRowArrayList.get(headerRowArrayList.size() - 1));
                            }
                        } else {
                            dataRow.remove(hexNewLine);
                            dataRow.remove(hexCarriageReturn);
                            bufferedWriter.write("NO COMMENT");
                            bufferedWriter.newLine();
                        }
                        
                        carriageReturnSensor++;
                        dataRow.clear();
                    }
                }
            }
        } catch(Exception e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            try {
                bufferedWriter.close();
                fileWriter.close();
                readFile.close();
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }  
        }
    }
    
    private void convertRawData() {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        HashMap<String, Object> mapData = null;
        HashMap<String, String> cellIdCellNameMap = null;
        ArrayList<String> rawData = null;
        ArrayList<String> arrayToCsvFile = null;
        ArrayList<String> dataPair = null;
        int dataRowNumFromRawData = 3;
        int hd72EovYcoordinate = 0;
        int hd72EovXcoordinate = 0;
        String headerRow = null;
        String rowToCsvFile = null;
        String[] mapDataRow = null;
        String[] processedRow = null;
        String[] hexCharBuffer = null;
        
        mapData = new HashMap<String, Object>();
        arrayToCsvFile = new ArrayList<String>();
        hexCharBuffer = new String[10];
        cellIdCellNameMap = setCellIdsAndCellName(cellIdsAndCellNames);
        
        if (fileType == FILETYPE.BESTSERVER) {
            headerRow = "latitude,longitude,cellID,signalStrength";
        }
        
        if (fileType == FILETYPE.NTHSERVER) {
            headerRow = "latitude,longitude,cellID,signalStrength";
            
            for (int serverCounter = 1; serverCounter < maxNoOfServers; serverCounter++) {
                String headerRowForNthServer = ",n" + serverCounter + "cellID,n" + serverCounter + "signalStrength";
                headerRow = headerRow.concat(headerRowForNthServer);
            }
        }
        
        try {
            fileWriter = new FileWriter(getCsvFile());
            bufferedWriter = new BufferedWriter(fileWriter);
            rawData = Tools.readFileToMemory(getConvFile());
            mapDataRow = rawData.get(1).split(" ",6);
            mapData.put("eastMin", Integer.parseInt(mapDataRow[0]));
            mapData.put("eastMax", Integer.parseInt(mapDataRow[1]));
            mapData.put("northMin", Integer.parseInt(mapDataRow[2]));
            mapData.put("northMax", Integer.parseInt(mapDataRow[3]));
            mapData.put("resolution", Integer.parseInt(mapDataRow[4]));
            mapData.put("fileInfo", mapDataRow[5]);
            bufferedWriter.write(headerRow);
            bufferedWriter.newLine();
            
            for (hd72EovYcoordinate = (int) mapData.get("northMax"); hd72EovYcoordinate > (int) mapData.get("northMin"); hd72EovYcoordinate = hd72EovYcoordinate - (int) mapData.get("resolution")) {
                for (hd72EovXcoordinate = (int) mapData.get("eastMin"); hd72EovXcoordinate < (int) mapData.get("eastMax"); hd72EovXcoordinate = hd72EovXcoordinate + (int) mapData.get("resolution")) {
                    arrayToCsvFile.clear();
                    rowToCsvFile = null;
                    arrayToCsvFile.add(Integer.toString(hd72EovXcoordinate));
                    arrayToCsvFile.add(Integer.toString(hd72EovYcoordinate));
                    
                    if (fileType == FILETYPE.BESTSERVER) {
                        processedRow = rawData.get(dataRowNumFromRawData).split(" ");
                        dataPair = getDataFromRow(processedRow, cellIdCellNameMap);
                        arrayToCsvFile.addAll(dataPair);
                    }
                    
                    if (fileType == FILETYPE.NTHSERVER) {
                        processedRow = rawData.get(dataRowNumFromRawData).split(" ");
                        int hexCharCounter = 0;
                        
                        for (String hexChar : processedRow) {
                            if (hexCharCounter != 10) {
                                hexCharBuffer[hexCharCounter] = hexChar;
                                hexCharCounter++;
                                
                                if (hexChar.equals("ff") && hexCharCounter == 10) {
                                    dataPair = getDataFromRow(hexCharBuffer, cellIdCellNameMap);
                                    arrayToCsvFile.addAll(dataPair);
                                    hexCharCounter = 0;
                                }
                            }
                        }
                    }
                    
                    dataRowNumFromRawData++;
                    
                    if (arrayToCsvFile.size() > 2) {
                        rowToCsvFile = String.join(",", arrayToCsvFile);
                        bufferedWriter.write(rowToCsvFile);
                        bufferedWriter.newLine();
                    } 
                }
            }
        } catch(Exception e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            try {
                bufferedWriter.close();
                fileWriter.close();
                rawData = null;
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }  
        }        
    }
    
    private ArrayList<String> getDataFromRow(String[] row, HashMap<String, String> cellIdCellNameDatabase) {
        HashMap <String, Integer> cellData =  null;
        ArrayList<String> dataPair = null;
        int cellId = 0;
        int signalStrength = 0;
        String cellName = null;
        
        dataPair = new ArrayList<String>();
        cellData = new HashMap <String, Integer>();
        
        cellData = getCellData(row);
        cellId = cellData.get("cellID");
        cellName = cellIdCellNameDatabase.get(Integer.toString(cellId));
        
        dataPair.add(cellName);
        signalStrength = cellData.get("signalStrength");
        dataPair.add(Integer.toString(signalStrength));
        
        return dataPair;
        
    }
    
    private HashMap<String,Integer> getCellData(String[] processedCell){
        HashMap<String, Integer> cellData = null;
        int cellID = 0;
        int signalStrength = 0;
        String[] hexCellID = null;
        String[] hexSignalStrength = null;
        
        cellData = new HashMap<String, Integer>();
        
        hexCellID = Arrays.copyOfRange(processedCell, 0, 4);
        hexSignalStrength = Arrays.copyOfRange(processedCell, 8, 10);
        cellID = Tools.convertHex2Dec(Tools.reversArray(hexCellID));
        signalStrength = Tools.convertHex2Dec(Tools.reversArray(hexSignalStrength));
        cellData.put("cellID",cellID);
        cellData.put("signalStrength",signalStrength);
        
        return cellData;
    }
      
    public File convertDat2Csv() {
        System.out.println("Creating raw data " + getConvFile());
        createRawData();
        System.out.println("Creating CSV file "  + getCsvFile());
        convertRawData();
        getConvFile().delete();
        
        return getCsvFile();
    }
}
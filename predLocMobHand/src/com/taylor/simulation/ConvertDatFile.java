package com.taylor.simulation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import com.taylor.tools.*;
import com.taylor.tools.Tools.FILETYPE;

public class ConvertDatFile {
    
    private File srcFile;
    private File convFile;
    private File csvFile;
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
    
    @SuppressWarnings("serial")
    final private HashMap<String, String> convertGsmCellID = new HashMap<String, String>() {
        {
            put("918262205", "veresegy_m_9002");
            put("917963201", "Vegyhcity_9003");
            put("918264506", "Csomad_9002");
            put("918268151", "veresegyd_9003");
            put("918267999", "veresegyd_9001");
            put("918262136", "veresegy_m_9001");
            put("918262052", "veresegy_m_9004");
            put("918264437", "Csomad_9001");       
            put("918268067", "veresegyd_9002");
            put("918264353", "Csomad_9003");
            put("918265031", "erdokert_9001");
            put("918265115", "erdokert_9003");
            put("918263566", "Szada_9004");
            put("918263498", "Szada_9003");
            put("918263635", "Szada_9002");
        }
    };
    
    @SuppressWarnings("serial")
    final private HashMap<String, String> convertDcsCellID = new HashMap<String, String>() {
        {
            put("918264727", "Csomadq_18001");
            put("918264574", "Csomadq_18003");
            put("918264643", "Csomadq_18002");
            put("918262341", "veresegyq_m_18003");
            put("917963597", "Vegyhcity_18001");
            put("918262273", "veresegyq_m_18002");
            put("917963743", "Vegyhcity_18002");
            put("918268647", "GodolInd_18001");
        }
    };
    
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
        List<String> rawData = null;
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
                        dataPair = getDataFromRow(processedRow);
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
                                    dataPair = getDataFromRow(hexCharBuffer);
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
    
    private ArrayList<String> getDataFromRow(String[] row) {
        HashMap <String, Integer> cellData =  null;
        ArrayList<String> dataPair = null;
        int cellId = 0;
        int signalStrength = 0;
        String cellName = null;
        
        dataPair = new ArrayList<String>();
        cellData = new HashMap <String, Integer>();
        
        cellData = getCellData(row);
        cellId = cellData.get("cellID");
        cellName = convertGsmCellID.get(Integer.toString(cellId));
        
        if (cellName == null) {
            cellName = convertDcsCellID.get(Integer.toString(cellId));
        }
        
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
    
    public void regenerateCsvFile() {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        List<String> readedSrcFile = null;
        Collection<String> btsCollection = null;
        ArrayList<String> btsNameList = null;
        ArrayList<String> btsCollectionList = null;
        ArrayList<String> generatedRow = null;
        ArrayList<String> generatedFile = null;
        String[] splittedRow = null;
        ArrayList<String> readedRow = null;
        String rowInMemory = null;
        int randomNumber = 0;
        int rowLength = 0;
        int lastSignalStrength = 0;
        int requestedElementNumber = 0;
        
        System.out.println("Extending original CSV File " + getCsvFile());
        
        Random generateRandomValue = new Random();
        readedSrcFile = Tools.readFileToMemory(getCsvFile());       
        generatedFile = new ArrayList<String>();
        Tools.createFile(getCsvFile());

        try {
             fileWriter = new FileWriter(getCsvFile());
             bufferedWriter = new BufferedWriter(fileWriter);
             
             for (int rowCounter = 1; rowCounter < readedSrcFile.size(); rowCounter++) {
                 splittedRow = readedSrcFile.get(rowCounter).split(",");
                 readedRow = new ArrayList<String>(Arrays.asList(splittedRow));
                 generatedRow = readedRow;
                 btsNameList = new ArrayList<String>();
                 
                 for (int readedRowElementCounter = 2; readedRowElementCounter < readedRow.size(); readedRowElementCounter = readedRowElementCounter + 2) {
                     btsNameList.add(readedRow.get(readedRowElementCounter));
                 }
                 
                 if (convertGsmCellID.containsValue(btsNameList.get(0))) {
                     btsCollection = convertGsmCellID.values();
                     rowLength = 22;
                 } else {
                     btsCollection = convertDcsCellID.values();
                     rowLength = 18;
                 }
                 
                 if (splittedRow.length < rowLength){                 
                     btsCollectionList = new ArrayList<String>(btsCollection);
                     btsCollectionList.removeAll(btsNameList);
                     lastSignalStrength = Integer.parseInt(generatedRow.get(generatedRow.size() - 1));
                     requestedElementNumber = (rowLength - readedRow.size()) / 2;
                     
                     for (int newElementCounter = 0; newElementCounter < requestedElementNumber ; newElementCounter++) {
                         randomNumber = generateRandomValue.nextInt(5);
                         lastSignalStrength = lastSignalStrength - randomNumber;
                         generatedRow.add(btsCollectionList.get(newElementCounter));
                         generatedRow.add(Integer.toString(lastSignalStrength));
                     }
                 }
                 
                 rowInMemory = String.join(",", generatedRow);
                 generatedFile.add(rowInMemory);
                 bufferedWriter.write(rowInMemory);
                 bufferedWriter.newLine();
            }
            
            for (String rowToWrite : generatedFile) {
                bufferedWriter.write(rowToWrite);
                bufferedWriter.newLine();
            }
            
        } catch(Exception e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            try {
                bufferedWriter.close();
                fileWriter.close();
                readedSrcFile = null;
                generatedFile = null;
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }  
        }
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
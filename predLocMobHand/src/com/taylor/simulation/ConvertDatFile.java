package com.taylor.simulation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import com.taylor.tools.*;

public class ConvertDatFile {
    
    private File srcFile;
    private File convFile;
    private File csvFile;
    private static String fileType = "UNDEFINED";
    private static int maxNoOfServers = 0;

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
    
    private void createRawData() {
        FileInputStream readFile = null;
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        StringBuilder numOfServers = null;
        StringBuilder headerRow = null;
        ArrayList<String> dataRow = new ArrayList<String>();
        boolean isNumOfServersSet = false;
        int carriageReturnSensor = 0;
        int emptyLineSize = 2;
        int readedCharLength;
        int numOfServersInt = 0;
        int numOfServersCharLength = 2;
        int serverCounter = 0;
        String hexNewLine = "0a";
        String hexCarriageReturn = "0d";
        String zeroChar = "0";
        String readedChar = null;
        
        try {
            readFile = new FileInputStream(getSrcFile());
            fileWriter = new FileWriter(getConvFile());
            bufferedWriter = new BufferedWriter(fileWriter);
            int read;
            while((read = readFile.read()) != -1) {
                readedChar = Integer.toHexString(read);
                readedCharLength = readedChar.length();
                if (readedCharLength == 1) {
                    readedChar = zeroChar.concat(readedChar);
                }
                dataRow.add(readedChar);
                if (fileType.equals("BESTSERVER") && carriageReturnSensor == 3 && dataRow.size() == 10) {
                    for (String data : dataRow) {
                        bufferedWriter.write(data + " ");
                    }
                    bufferedWriter.newLine();
                    dataRow.clear();
                }
                if (fileType.equals("NTHSERVER") && carriageReturnSensor == 3) { 
                    if (dataRow.size() == numOfServersCharLength && isNumOfServersSet == false) {
                        numOfServers = new StringBuilder();
                        for (String data : dataRow) {
                            char character = Tools.convertHex2Char(data);
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
                                char character = Tools.convertHex2Char(data);
                                headerRow.append(character);
                                bufferedWriter.write(character);
                            }
                            bufferedWriter.newLine();
                            String[] headerRowArray = headerRow.toString().trim().split(" ");
                            ArrayList<String> headerRowArrayList = new ArrayList<String>(Arrays.asList(headerRowArray));
                            if (headerRowArrayList.contains("BESTSERVER")) {
                                fileType = "BESTSERVER";
                            } else if (headerRowArrayList.contains("NTHSERVER")) {
                                fileType = "NTHSERVER";
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
        ArrayList<String> rawData = new ArrayList<String>();
        ArrayList<String> arrToCsvFile = null;
        List<Integer> arrToWrite = Arrays.asList();
        int dataRowNumFromRawData = 3;
        int hd72EovYcoordinate = 0;
        int hd72EovXcoordinate = 0;
        String headerRow = null;
        String rowToCsvFile = null;
        String[] mapDataRow = null;
        String[] processedLine = null;
        String[] hexCharBuffer = new String[10];
        Hashtable<Object, Object> mapData = new Hashtable<>();
        Hashtable<String,Integer> cellData = null;
        
        if (fileType.equals("BESTSERVER")) {
            headerRow = "latitude,longitude,cellID,signalStrength";
        }
        if (fileType.equals("NTHSERVER")) {
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
                    arrToCsvFile = new ArrayList<String>();
                    arrToWrite = new ArrayList<Integer>();
                    rowToCsvFile = null;
                    arrToWrite.add(hd72EovXcoordinate);
                    arrToWrite.add(hd72EovYcoordinate);
                    if (fileType.equals("BESTSERVER")) {
                        processedLine = rawData.get(dataRowNumFromRawData).split(" ");
                        cellData = getCellData(processedLine);
                        //arrToWrite.add(cellData.get("cellLayerID"));
                        arrToWrite.add(cellData.get("cellID"));
                        arrToWrite.add(cellData.get("signalStrength"));
                    }
                    if (fileType.equals("NTHSERVER")) {
                        processedLine = rawData.get(dataRowNumFromRawData).split(" ");
                        int hexCharCounter = 0;
                        for (String hexChar : processedLine) {
                            if (hexCharCounter != 10) {
                                hexCharBuffer[hexCharCounter] = hexChar;
                                hexCharCounter++;
                                if (hexChar.equals("ff") && hexCharCounter == 10) {
                                    cellData = getCellData(hexCharBuffer);
                                    //arrToWrite.add(cellData.get("cellLayerID"));
                                    arrToWrite.add(cellData.get("cellID"));
                                    arrToWrite.add(cellData.get("signalStrength"));
                                    hexCharCounter = 0;
                                }
                            }
                        }
                    }
                    
                    dataRowNumFromRawData++;
                    if (arrToWrite.size() > 2) {
                        for (Integer value : arrToWrite) {
                            arrToCsvFile.add(Integer.toString(value));
                        }
                    } else {
                        arrToCsvFile.add(Integer.toString(hd72EovXcoordinate));
                        arrToCsvFile.add(Integer.toString(hd72EovYcoordinate));
                    }
                    rowToCsvFile = String.join(",", arrToCsvFile);
                    bufferedWriter.write(rowToCsvFile);
                    bufferedWriter.newLine();
                }
            }
        } catch(Exception e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            try {
                bufferedWriter.close();
                fileWriter.close();
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }  
        }        
    }
    
    private Hashtable<String,Integer> getCellData(String[] processedCell){
        String[] hexCellID = Arrays.copyOfRange(processedCell, 0, 4);
        //String[] hexCellLayerID = Arrays.copyOfRange(processedCell, 4, 8);
        String[] hexSignalStrength = Arrays.copyOfRange(processedCell, 8, 10);
        int cellID = Tools.convertHex2Dec(Tools.reversArray(hexCellID));
        //int cellLayerID = Tools.convertHex2Dec(Tools.reversArray(hexCellLayerID));
        int signalStrength = Tools.convertHex2Dec(Tools.reversArray(hexSignalStrength));
        @SuppressWarnings("serial")
        Hashtable<String, Integer> cellData = new Hashtable<String, Integer>(){{
                put("cellID",cellID);
                //put("cellLayerID",cellLayerID);
                put("signalStrength",signalStrength);
        }};
            
        return cellData;
    }
    
    public File convertDat2Csv() {
        createRawData();
        convertRawData();
        getConvFile().delete();
        
        return getCsvFile();
    }
}
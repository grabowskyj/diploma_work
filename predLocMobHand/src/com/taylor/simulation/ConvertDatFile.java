package com.taylor.simulation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

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
                            if (serverCounter != numOfServersInt) {
                                bufferedWriter.write("| "); 
                            } else {
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
        FileReader fileReader = null;
        FileWriter fileWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        ArrayList<String> rawData = new ArrayList<String>();
        int cellID = 0;
        int cellLayerID = 0;
        int signalStrength = 0;
        int dataRowNumFromRawData = 3;
        int northVal = 0;
        int eastVal = 0;
        int[] arrToWrite = new int[5];
        String headerRow = null;
        String[] mapDataRow;
        Hashtable<Object, Object> mapData = new Hashtable<>();
        
        if (fileType.equals("BESTSERVER")) {
            headerRow = "latitude,longitude,cellLayerID,cellID,signalStrength";
        }
        if (fileType.equals("NTHSERVER")) {
            headerRow = "latitude,longitude,";
            for (int serverCounter = 1; serverCounter <= maxNoOfServers; serverCounter++) {
                String headerRowForNthServer = "n" + serverCounter + "cellLayerID,n" + serverCounter + "cellID,n" + serverCounter + "signalStrength";
                headerRow.concat(headerRowForNthServer);
            }
        }
                
        try {
            fileReader = new FileReader(getConvFile());
            bufferedReader = new BufferedReader(fileReader);
            fileWriter = new FileWriter(getCsvFile());
            bufferedWriter = new BufferedWriter(fileWriter);
            String readedLine;
            while ((readedLine = bufferedReader.readLine()) != null) {
                rawData.add(readedLine.trim());
            }
            mapDataRow = rawData.get(1).split(" ",6);
            mapData.put("eastMin", Integer.parseInt(mapDataRow[0]));
            mapData.put("eastMax", Integer.parseInt(mapDataRow[1]));
            mapData.put("northMin", Integer.parseInt(mapDataRow[2]));
            mapData.put("northMax", Integer.parseInt(mapDataRow[3]));
            mapData.put("resolution", Integer.parseInt(mapDataRow[4]));
            mapData.put("fileInfo", mapDataRow[5]);
            bufferedWriter.write(headerRow);
            bufferedWriter.newLine();
            for (northVal = (int) mapData.get("northMax"); northVal > (int) mapData.get("northMin"); northVal = northVal - (int) mapData.get("resolution")) {
                for (eastVal = (int) mapData.get("eastMin"); eastVal < (int) mapData.get("eastMax"); eastVal = eastVal + (int) mapData.get("resolution")) {
                    ArrayList<String> arrToCsvFile = new ArrayList<String>();
                    String rowToCsvFile = null;
                    String[] processedLine = rawData.get(dataRowNumFromRawData).split(" ");
                    String[] hexCellID = Arrays.copyOfRange(processedLine, 0, 4);
                    String[] hexCellLayerID = Arrays.copyOfRange(processedLine, 4, 8);
                    String[] hexSignalStrength = Arrays.copyOfRange(processedLine, 8, 10);
                    cellID = Tools.convertHex2Dec(Tools.reversArray(hexCellID));
                    cellLayerID = Tools.convertHex2Dec(Tools.reversArray(hexCellLayerID));
                    signalStrength = Tools.convertHex2Dec(Tools.reversArray(hexSignalStrength));
                    arrToWrite = new int[]{northVal, eastVal, cellLayerID, cellID, signalStrength};
                    dataRowNumFromRawData++;
                    for (int value : arrToWrite) {
                        arrToCsvFile.add(Integer.toString(value));
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
                bufferedReader.close();
                fileReader.close();
                bufferedWriter.close();
                fileWriter.close();
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }  
        }        
    }
    
    public File convertDat2Csv() {
        createRawData();
        //convertRawData();
        //getConvFile().delete();
        
        return getCsvFile();
    }
}
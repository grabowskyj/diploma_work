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
        ArrayList<String> dataRow = new ArrayList<String>();
        int carriageReturnSensor = 0;
        int emptyLineSize = 2;
        int readedCharLength;
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
                if ((carriageReturnSensor == 3) && (dataRow.size() == 10)) {
                    for (String data : dataRow) {
                        bufferedWriter.write(data + " ");
                    }
                    bufferedWriter.newLine();
                    dataRow.clear();
                }
                if (readedChar.equals(hexNewLine)) {
                    if (carriageReturnSensor < 3) {
                        if (dataRow.size() != emptyLineSize) {
                            dataRow.remove(hexNewLine);
                            dataRow.remove(hexCarriageReturn);
                            for (String data : dataRow) {
                                int data2decimal = Integer.parseInt(data, 16);
                                char character = (char) data2decimal; 
                                bufferedWriter.write(character);
                            }
                            bufferedWriter.newLine();
                        } else {
                            dataRow.remove(hexNewLine);
                            dataRow.remove(hexCarriageReturn);
                            bufferedWriter.write("NO ENTRY");
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
        String headerRow = "latitude,longitude,cellLayerID,cellID,signalStrength";
        String[] mapDataRow;
        Hashtable<Object, Object> mapData = new Hashtable<>();
                
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
        convertRawData();
        getConvFile().delete();
        
        return getCsvFile();
    }
}
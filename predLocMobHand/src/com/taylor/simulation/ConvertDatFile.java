package com.taylor.simulation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

public class ConvertDatFile {
    
    private File datFile;
    private File convFile;
    private File csvFile;

    ConvertDatFile(File datFile, File convFile, File csvFile) {
        this.setDatFile(datFile);
        this.setConvFile(convFile);
        this.setCsvFile(csvFile);
    }

    public File getDatFile() {
        return datFile;
    }

    public void setDatFile(File datFile) {
        this.datFile = datFile;
    }
    
    public File getconvFile() {
        return convFile;
    }

    public void setConvFile(File convFile) {
        this.convFile = convFile;
        createFile(this.convFile);
    }
    
    public File getCsvFile() {
        return csvFile;
    }

    public void setCsvFile(File csvFile) {
        this.csvFile = csvFile;
        createFile(this.csvFile);
    }
    
    public void createFile (File file) {
        if (file.exists()) {
            file.delete();
        }
        
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public void createRawData() {
        FileInputStream readFile = null;
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        ArrayList<String> dataRow = new ArrayList<String>();
        int carriageReturn = 0;
        int read;
        String hexNewLine = "a";
        String hexCarriageReturn = "d";
        String readedChar = null;
        
        try {
            readFile = new FileInputStream(getDatFile());
            fileWriter = new FileWriter(getconvFile());
            bufferedWriter = new BufferedWriter(fileWriter);
            while((read = readFile.read()) != -1) {
                readedChar = Integer.toHexString(read);
                if (!readedChar.equals(hexCarriageReturn) && !readedChar.equals(hexNewLine)) {
                    dataRow.add(readedChar);
                    if ((carriageReturn == 3) && (dataRow.size() == 10)) {
                        for (String data : dataRow) {
                            bufferedWriter.write(data + " ");
                        }
                        bufferedWriter.newLine();
                        dataRow.clear();
                    }
                }
                if (readedChar.equals(hexCarriageReturn)) {
                    if (dataRow.size() > 0) {
                        for (String data : dataRow) {
                            int data2decimal = Integer.parseInt(data, 16);
                            char character = (char) data2decimal; 
                            bufferedWriter.write(character);
                        }
                        bufferedWriter.newLine();
                    } else {
                        bufferedWriter.write("NO ENTRY");
                        bufferedWriter.newLine();
                    }
                    carriageReturn++;
                    dataRow.clear();
                }
            }
        } catch(Exception e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            try {
                readFile.close();
                bufferedWriter.close();
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }  
        }
    }
    
    public void convertRawData() {
        FileReader fileReader = null;
        FileWriter fileWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        ArrayList<String> rawData = new ArrayList<String>(); 
        int cellID = 0;
        int cellLayerID = 0;
        int signalStrength = 0;
        int dataRowNumFromRawData = 3;
        int[] rowToWrite = new int[5];
        String[] mapDataRow;
        Hashtable<Object, Object> mapData = new Hashtable<>();
                
        try {
            fileReader = new FileReader(getconvFile());
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
            for (int northVal = (int) mapData.get("northMax"); northVal >= (int) mapData.get("northMin"); northVal = northVal - (int) mapData.get("resolution")) {
                for (int eastVal = (int) mapData.get("eastMin"); eastVal <= (int) mapData.get("eastMax"); eastVal = eastVal + (int) mapData.get("resolution")) {
                    String[] processedLine = rawData.get(dataRowNumFromRawData).split(" ");
                    String[] hexCellID = Arrays.copyOfRange(processedLine, 0, 4);
                    String[] hexCellLayerID = Arrays.copyOfRange(processedLine, 4, 8);
                    String[] hexSignalStrength = Arrays.copyOfRange(processedLine, 8, 10);
                    cellID = convertHex2Dec(reversArray(hexCellID));
                    cellLayerID = convertHex2Dec(reversArray(hexCellLayerID));
                    signalStrength = convertHex2Dec(reversArray(hexSignalStrength));
                    
                    rowToWrite = new int[]{eastVal, northVal, cellID, cellLayerID, signalStrength};
                    //valami valahol rossz itt, el vannak csuszva a sorok a programmal konvertalt es az ezzel szamolt kozott
                    dataRowNumFromRawData++;
                    String toCsvFile = Arrays.toString(rowToWrite);
                    System.out.println(toCsvFile);
                    System.out.println("CellID: " + cellID);
                    
                    System.out.println("LayerID: " + cellLayerID);
                   
                    System.out.println("SignalStrength: " + signalStrength);
                }
            }
           
            
        } catch(Exception e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
                bufferedReader.close();
                fileWriter.close();
                bufferedWriter.close();
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }  
        }
    }
    
    public String[] reversArray(String[] array) {
        int reverseArrayLength = array.length;
        String[] reversArray = new String[reverseArrayLength];
        for (int elementCounter = 0; elementCounter < array.length; elementCounter++ ) {
            reversArray[--reverseArrayLength] = array[elementCounter];
        }
        
        return reversArray;
    }
    
    public int convertHex2Dec(String[] array) {
        int value = 0;
        String hexString = String.join("", array);
        switch (array.length) {
            case 2:
                value = (short) Integer.parseInt(hexString, 16);
                break;
            case 4:
                value = (int) Long.parseLong(hexString, 16);
                break;
        }     
        
        return value;
    }
    
    public static void main(String[] args) {
        
        File simFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\minta1_25m_bestserver.dat");
        File convFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\minta1_25m_bestserver.conv");
        File csvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\minta1_25m_bestserver.csv");
              
        ConvertDatFile baseData = new ConvertDatFile(simFile, convFile, csvFile);
        
        baseData.createRawData();
        baseData.convertRawData();
        
    }
    
}
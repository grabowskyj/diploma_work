package com.taylor.simulation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import sun.text.normalizer.RangeValueIterator;

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
    }
    
    public File getCsvFile() {
        return csvFile;
    }

    public void setCsvFile(File csvFile) {
        this.csvFile = csvFile;
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
        int eastMin = 0;
        int eastMax = 0;
        int northMin = 0;
        int northMax = 0;
        int resolution = 0;
        int cellID;
        int layerID;
        int signalStrength;        
                
        try {
            fileReader = new FileReader(getconvFile());
            bufferedReader = new BufferedReader(fileReader);
            for (String readedLine; (readedLine = bufferedReader.readLine()) != null;) {
                rawData.add(readedLine.trim());
            }
            String[] mapInfo = rawData.get(1).split(" ");
            eastMin = Integer.parseInt(mapInfo[0]);
            eastMax = Integer.parseInt(mapInfo[1]);
            northMin = Integer.parseInt(mapInfo[2]);
            northMax = Integer.parseInt(mapInfo[3]);
            resolution = Integer.parseInt(mapInfo[4]);
            
            for (int rowNum = 3; rowNum <= rawData.size(); rowNum++) {
            	String[] processedLine = rawData.get(rowNum).split(" ");
            	String[] hexCellID = new String[]{processedLine[3],processedLine[2],processedLine[1],processedLine[0]};
            	String[] hexLayerID = new String[]{processedLine[7],processedLine[6],processedLine[5],processedLine[4]};
            	String[] hexSignalStrength = new String[]{processedLine[9],processedLine[8]};
            	//csinalni egy fuggvenyt, ami csak a jo sorrendben levo hex arrayt forditja at decimalisra
            	//csinalni egy fuggvenyt, ami osszerakja a csv-be beirando sort a jo koordinatakkal, amit persze elotte ki kell szamolni valahogy
            	
            }
            
        } catch(Exception e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
                bufferedReader.close();
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }  
        }
    }
    
    public static void main(String[] args) {
        
        File simFile = new File("D:\\Downloads\\OneDrive\\Documents\\Suli\\MSc\\Diplomamunka\\Mérések\\Aircom array\\Minta\\minta1_25m_bestserver.dat");
        File convFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\minta1_25m_bestserver.conv");
        File csvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\minta1_25m_bestserver.csv");
        
        if (convFile.exists()) {
            convFile.delete();
        }
        
        if (csvFile.exists()) {
            csvFile.delete();
        }
        
        try {
            convFile.createNewFile();
            csvFile.createNewFile();
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        
        ConvertDatFile baseData = new ConvertDatFile(simFile, convFile, csvFile);
        
        baseData.createRawData();
        baseData.convertRawData();
        
    }
    
}
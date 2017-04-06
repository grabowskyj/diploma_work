package com.taylor.measurement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import com.taylor.tools.*;

public class ConvertMeasurementFile {
    
    private File srcFile;
    private File csvFile;
    
    public ConvertMeasurementFile(File srcFile, File csvFile) {
        this.setSrcFile(srcFile);
        this.setCsvFile(csvFile);
    }
    
    private void setSrcFile(File srcFile) {
        this.srcFile = srcFile;
    }
    
    public File getSrcFile() {
        return srcFile;
    }
    
    private void setCsvFile(File csvFile) {
        this.csvFile = csvFile;
        Tools.createFile(getCsvFile());
    }
    
    public File getCsvFile() {
        return csvFile;
    }
    
    public File convertMeasurement2Csv() {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        ArrayList<String> data = Tools.readFileToMemory(getSrcFile());
        String separator = ";";
        String headerRowToCsv = "latitude,longitude,cellID,signalStrength,n1cellID,n1signalStrength,n2cellID,n2signalStrength,n3cellID,n3signalStrength,n4cellID,n4signalStrength,n5cellID,n5signalStrength,n6cellID,n6signalStrength";
        String dataRowToCsv = null;
        String[] dataRow = null;
        
        try {
            fileWriter = new FileWriter(getCsvFile());
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(headerRowToCsv);
            bufferedWriter.newLine();
            for (int rowCounter = 1; rowCounter < data.size(); rowCounter++ ) {
                dataRow = data.get(rowCounter).trim().split(separator);
                dataRowToCsv = prepareArrayForWrite(dataRow);
                bufferedWriter.write(dataRowToCsv);
                bufferedWriter.newLine();
            }  
        } catch (Exception e) {
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
        
        return getCsvFile();
    }
    
    private String prepareArrayForWrite(String[] array) {
        List<String> setOfData = new ArrayList<String>();
        int[] neededCellOfArray = new int[]{1,6,19,21,22,24,25,27,28,30,31,33,34,36};
        Hashtable<Object, Object> coordinates = Tools.wgs84ToHd72Eov(Double.parseDouble(array[12]), Double.parseDouble(array[13]));
        String latitude = Double.toString((double) coordinates.get("latitude"));
        String longitude = Double.toString((double) coordinates.get("longitude"));
        setOfData.add(latitude);
        setOfData.add(longitude);
        for (int cellNum : neededCellOfArray) {
            if (cellNum <= (array.length - 1)) {
                setOfData.add(array[cellNum]);
            } else {
                break;
            }
        }
        String[] filledArray = new String[setOfData.size()];
        filledArray = setOfData.toArray(filledArray);        
        String rowToWrite = String.join(",", filledArray);
        
        return rowToWrite;
    }
}

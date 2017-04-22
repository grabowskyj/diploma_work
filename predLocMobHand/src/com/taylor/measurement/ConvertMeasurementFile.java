package com.taylor.measurement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import com.taylor.tools.*;
import com.taylor.tools.Tools.COORDINATES;

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
    
    @SuppressWarnings("serial")
    final private Hashtable<String, String> convertCellID = new Hashtable<String, String>() {
        {
            put("21431", "veresegy_m_9001");
            put("21432", "veresegy_m_9002");
            put("21434", "veresegy_m_9004");
            put("21831", "veresegyd_9001");
            put("21833", "veresegyd_9003");
            put("21437", "veresegyq_m_18002");
            put("21438", "veresegyq_m_18003");
            put("41631", "Csomad_9001");
            put("41632", "Csomad_9002");
            put("41633", "Csomad_9003");
            put("41636", "Csomadq_18001");
            put("41637", "Csomadq_18002");
            put("41638", "Csomadq_18003");
            put("21921", "GodolloI_m_9001");
            put("21923", "GodolloI_m_9003");
            put("60553", "Vegyhcity_9003");
            put("41013", "erdokert_9003");
            put("51612", "vacratot_9002");
            put("41052", "szodliget_9002");
            put("60557", "Vegyhcity_18002");
            put("60556", "Vegyhcity_18001");
            put("41451", "r30godollo_9001");
            put("49191", "hungring_9001");
        }
    };
    
    public File convertMeasurement2Csv() {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        ArrayList<String> data = null;
        String headerRowToCsv = "latitude,longitude,cellID,signalStrength,"
                + "n1cellID,n1signalStrength,n2cellID,n2signalStrength,"
                + "n3cellID,n3signalStrength,n4cellID,n4signalStrength,"
                + "n5cellID,n5signalStrength,n6cellID,n6signalStrength";
        String dataRowToCsv = null;
        String[] dataRow = null;
        data = Tools.readFileToMemory(getSrcFile());
        
        try {
            fileWriter = new FileWriter(getCsvFile());
            bufferedWriter = new BufferedWriter(fileWriter);
            
            bufferedWriter.write(headerRowToCsv);
            bufferedWriter.newLine();
            
            for (int rowCounter = 1; rowCounter < data.size(); rowCounter++ ) {
                dataRow = data.get(rowCounter).trim().split(";");
                dataRowToCsv = prepareArrayForWrite(dataRow);
                if (!dataRowToCsv.contains("NaN")) {
                    bufferedWriter.write(dataRowToCsv);
                    bufferedWriter.newLine();
                }
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
        List<String> setOfData = null;
        int[] neededCellOfArray = null;
        int neededCellOfArrayCounter = 1;
        Hashtable<String, Double> coordinates = null;
        String latitude = null;
        String longitude = null;
        String[] filledArray = null;
        String rowToWrite = null;
        String cellName = null;
        
        setOfData = new ArrayList<String>();
        neededCellOfArray = new int[]{1,6,19,21,22,24,25,27,28,30,31,33,34,36};
        coordinates = Tools.wgs84ToHd72Eov(Double.parseDouble(array[12]), Double.parseDouble(array[13]));
        latitude = Double.toString((double) coordinates.get(COORDINATES.LATITUDE.toString()));
        longitude = Double.toString((double) coordinates.get(COORDINATES.LONGITUDE.toString()));
        setOfData.add(latitude);
        setOfData.add(longitude);
        
        for (int cellNum : neededCellOfArray) { 
            
            if (cellNum <= (array.length - 1)) {
                
                if (neededCellOfArrayCounter % 2 == 1) {
                    cellName = convertCellID.get(array[cellNum]);
                    
                    if (cellName != null) {
                        setOfData.add(cellName);
                    } else {
                        setOfData.add(array[cellNum]);
                        /*
                         * for discovering hidden CellIDs 
                         *
                        if (Integer.parseInt(array[cellNum]) > -1) {
                            System.out.println(array[cellNum]);
                        }*/ 
                    }  
                } else {
                    setOfData.add(array[cellNum]);
                }
                
                neededCellOfArrayCounter++;
            } else {
                break;
            }
        }
        
        filledArray = new String[setOfData.size()];
        filledArray = setOfData.toArray(filledArray);        
        rowToWrite = String.join(",", filledArray);
        
        return rowToWrite;
    }
}

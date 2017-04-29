package com.taylor.measurement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.taylor.tools.*;
import com.taylor.tools.Tools.COORDINATES;
import com.taylor.tools.Tools.DATATYPE;

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
    final private HashMap<String, String> convertGsmCellID = new HashMap<String, String>() {
        {
            put("21431", "veresegy_m_9001");
            put("21432", "veresegy_m_9002");
            put("21434", "veresegy_m_9004");
            put("21831", "veresegyd_9001");
            put("21833", "veresegyd_9003");
            put("41631", "Csomad_9001");
            put("41632", "Csomad_9002");
            put("41633", "Csomad_9003");
            put("21921", "GodolloI_m_9001");
            put("21923", "GodolloI_m_9003");
            put("60553", "Vegyhcity_9003");
            put("41013", "erdokert_9003");
            put("51612", "vacratot_9002");
            put("41052", "szodliget_9002");
            put("41451", "r30godollo_9001");
            put("49191", "hungring_9001");
        }
    };
    
    @SuppressWarnings("serial")
    final private HashMap<String, String> convertDcsCellID = new HashMap<String, String>() {
        {
            put("60557", "Vegyhcity_18002");
            put("60556", "Vegyhcity_18001");
            put("41636", "Csomadq_18001");
            put("41637", "Csomadq_18002");
            put("41638", "Csomadq_18003");
            put("21437", "veresegyq_m_18002");
            put("21438", "veresegyq_m_18003");
        }
    };
    
    
    public File convertMeasurement2Csv() {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        ArrayList<String> data = null;
        String headerRowToCsv = null;
        String dataRowToCsv = null;
        String[] dataRow = null;
        
        headerRowToCsv = "latitude,longitude,cellID,signalStrength,"
                + "n1cellID,n1signalStrength,n2cellID,n2signalStrength,"
                + "n3cellID,n3signalStrength,n4cellID,n4signalStrength,"
                + "n5cellID,n5signalStrength,n6cellID,n6signalStrength";
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
        HashMap<String, Double> coordinates = null;
        HashMap<String, Integer> cellData = null;
        HashMap<String, Integer> sortedHashMap = null;
        Set<Entry<String, Integer>> entries = null;
        ArrayList<String> arrLstToCsvFile = null;
        ArrayList<String> listOfCellIds = null;
        ArrayList<Integer> listOfSignalStrengths = null;
        boolean isCellNameUnrecognizable = false;
        boolean isCellNameAlreadyAdded = false;
        int[] neededCellOfArray = null;
        int neededCellOfArrayCounter = 1;
        String latitude = null;
        String longitude = null;
        String[] filledArray = null;
        String rowToWrite = null;
        String cellName = null;
        
        cellData = new HashMap<String, Integer>();
        arrLstToCsvFile = new ArrayList<String>();
        listOfCellIds = new ArrayList<String>();
        listOfSignalStrengths = new ArrayList<Integer>();
        neededCellOfArray = new int[]{1,6,19,21,22,24,25,27,28,30,31,33,34,36};
        coordinates = Tools.wgs84ToHd72Eov(Double.parseDouble(array[12]), Double.parseDouble(array[13]));
        latitude = Double.toString((double) coordinates.get(COORDINATES.LATITUDE.toString()));
        longitude = Double.toString((double) coordinates.get(COORDINATES.LONGITUDE.toString()));
        arrLstToCsvFile.add(latitude);
        arrLstToCsvFile.add(longitude);
        
        for (int cellNum : neededCellOfArray) {  
            if (cellNum <= (array.length - 1)) {
                if (neededCellOfArrayCounter % 2 == 1) {
                    cellName = convertGsmCellID.get(array[cellNum]);
                    
                    if (cellName == null) {
                        cellName = convertDcsCellID.get(array[cellNum]);
                    }
                    
                    if (cellName != null && !listOfCellIds.contains(cellName)) {
                        listOfCellIds.add(cellName);
                    } else if (array[cellNum].equals("-1")) {
                        isCellNameUnrecognizable = true;
                    } else if (listOfCellIds.contains(cellName)) {
                        isCellNameAlreadyAdded = true;
                    } else {
                        listOfCellIds.add(array[cellNum]);
                    }
                } else {
                    if (isCellNameUnrecognizable == false && isCellNameAlreadyAdded == false) {
                        listOfSignalStrengths.add(Integer.parseInt(array[cellNum]));
                    } else {
                        isCellNameUnrecognizable = false;
                        isCellNameAlreadyAdded = false;
                    }
                }
                
                neededCellOfArrayCounter++;
            } else {
                break;
            }
        }
        
        for (int cellIdCounter = 0; cellIdCounter < listOfCellIds.size(); cellIdCounter++) {
            cellData.put(listOfCellIds.get(cellIdCounter), listOfSignalStrengths.get(cellIdCounter));
        }
        
        sortedHashMap = Tools.sortHashMap(cellData, DATATYPE.SIGNALSTRENGTH);
        entries = sortedHashMap.entrySet();
        
        for (Entry<String, Integer> entry : entries) {
            arrLstToCsvFile.add(entry.getKey());
            arrLstToCsvFile.add(Integer.toString(entry.getValue()));
        }

        filledArray = new String[arrLstToCsvFile.size()];
        filledArray = arrLstToCsvFile.toArray(filledArray);        
        rowToWrite = String.join(",", filledArray);
        
        return rowToWrite;
    }
}

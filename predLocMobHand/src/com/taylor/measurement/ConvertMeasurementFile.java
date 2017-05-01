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
    final private HashMap<String, String> convertGsmDcsCellID = new HashMap<String, String>() {
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
            put("59312", "baranyai_9002");
            put("59313", "baranyai_9003");
            put("59311", "baranyai_9001");
            put("4311", "Skala_9001");
            put("4313", "Skala_G09_3");
            put("3321", "Hamzsabegi_9001");
            put("1921", "Fehervari_9001");
            put("9886", "Szeremiq_18001");
            put("59452", "allee_9002");
            put("602", "Bartok_18002");
            put("7763", "Egryut_9003");
            put("7762", "Egryut_9002");
            put("7761", "Egryut_9001");
            put("972", "Villanyi_9002");
            put("3103", "Karinthy_9003");
            put("973", "Villanyi_9003");
            put("6751", "Kosztol_9001");
            put("3322", "Hamzsabegi_9002");
            put("3102", "Karinthy_9002");
            put("4321", "Szuret_9001");
            put("3971", "Hengermal_9001");
            put("1923", "Fehervari_9003");
            put("7673", "UVATERV_G09_3");
            put("1031", "Karolina_9001");
            put("2043", "Eromu_9003");
            put("59451", "allee_9001");
            put("60557", "Vegyhcity_18002");
            put("60556", "Vegyhcity_18001");
            put("41636", "Csomadq_18001");
            put("41637", "Csomadq_18002");
            put("41638", "Csomadq_18003");
            put("21437", "veresegyq_m_18002");
            put("21438", "veresegyq_m_18003");
            put("3328", "Hamzsabegi_18003");
            put("8686", "dorhonv_18001");
            put("3101", "Karinthy_18001");
            put("1037", "Karolinaq7_18002");
            put("3977", "Hengermalq_18002");
        }
    };
    
    @SuppressWarnings("serial")
    final private HashMap<String, String> convertUmtsCellID = new HashMap<String, String>() {
        {
            put("59312", "baranyai__UMTS2");
            put("59313", "baranyai__UMTS3");
            put("59318", "baranyai__UMTS8");
            put("59317", "baranyai__UMTS7");
            put("3107", "Karinthy2__UMTS6");
            put("6756", "Kosztol__UMTS6");
            put("44566", "Kosztol__UMTS_11");
            put("19278", "baranyai__UMTS_8");
            put("48758", "Hamzsabegi__UMTS_8");
            put("48756", "Hamzsabegi__UMTS_6");
            put("3326", "Hamzsabegi__UMTS6");
            put("6756", "Kosztol__UMTS6");
            put("4311", "Skala__UMTS1");
            put("36917", "KARINTHY_UMTS_7");
            put("4316", "Skala__UMTS6");
            put("40328", "Skala__UMTS_8");
            put("4318", "Skala__UMTS8");
            put("40326", "Skala__UMTS_6");
            put("6751", "Kosztol__UMTS1");
            put("4317", "Skala__UMTS7");
            put("7768", "Egryut__UMTS8");
            put("3321", "Hamzsabegi__UMTS1");
            put("3102", "Karinthy2__UMTS1");
            put("3106", "Karinthy1__UMTS6");
            put("59316", "baranyai__UMTS6");
            put("2435", "VERESEGYHAZ__UMTS_12");
            put("41638", "Csomad__UMTS8");
            put("21437", "VERESEGYHAZ__UMTS7");
            put("41633", "Csomad__UMTS3");
            put("60558", "Vegyhcity_UMTS8");
            put("52117", "ORBOTTYAN__UMTS7");
            put("21432", "VERESEGYHAZ__UMTS2");
            put("21838", "veresegyd__UMTS8");
            put("52112", "ORBOTTYAN__UMTS2");
            put("41017", "erdokert__UMTS7");            
        }
    };
    
    
    public File convertMeasurement2Csv() {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        ArrayList<String> data = null;
        String headerRowToCsv = null;
        String dataRowToCsv = null;
        String[] dataRow = null;
        
        System.out.println("Converting G-Mon file " + getCsvFile());
        
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
                    
                    if (getCsvFile().getName().contains("gsm")) {
                        cellName = convertGsmDcsCellID.get(array[cellNum]);
                    }
                    
                    if (getCsvFile().getName().contains("umts")) {
                        cellName = convertUmtsCellID.get(array[cellNum]);
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

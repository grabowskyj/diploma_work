package com.taylor.localization;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.rosuda.JRI.Rengine;

import com.taylor.tools.Tools;
import com.taylor.tools.Tools.COORDINATES;
import com.taylor.tools.Tools.DATATYPE;

public class HorizontalSearchMethod {
    
    private File databaseFile;
    private File measurementFile;

    final private String fingerprintDifferenceValue = "fingerprintDifferenceValue";
        
    public HorizontalSearchMethod(File databaseFile, File measurementFile) {
        this.setdatabaseFile(databaseFile);
        this.setMeasurementFile(measurementFile);

    }

    public File getdatabaseFile() {
        return databaseFile;
    }

    private void setdatabaseFile(File databaseFile) {
        this.databaseFile = databaseFile;
    }

    public File getMeasurementFile() {
        return measurementFile;
    }

    private void setMeasurementFile(File measurementFile) {
        this.measurementFile = measurementFile;
    }

    private ArrayList<String> getValuesFromList(DATATYPE data, List<String> lstDatabaseEntry) {
        ArrayList<String> result = null;
        int startPoint = 0;
        
        result = new ArrayList<String>();
        
        if (data == DATATYPE.CELLNAME) {
            startPoint = 0;
        } else if (data == DATATYPE.SIGNALSTRENGTH) {
            startPoint = 1;
        }
        
        for (int entryElementCounter = startPoint; entryElementCounter < lstDatabaseEntry.size(); entryElementCounter = entryElementCounter + 2) {
            result.add(lstDatabaseEntry.get(entryElementCounter));
        }
        
        return result;
    }
    
    private ArrayList<ArrayList<String>> getCoordinatesFromList(List<String> lstDatabaseEntry) {
        ArrayList<ArrayList<String>> result = null;
        ArrayList<String> coordinate = null;
        ArrayList<String> databaseEntry = null;
        String latitude = null;
        String longitude = null;
        
        result = new ArrayList<ArrayList<String>>();
        coordinate = new ArrayList<String>();
        databaseEntry = new ArrayList<String>();
        
        databaseEntry.addAll(lstDatabaseEntry);
        longitude = databaseEntry.remove(1);
        latitude = databaseEntry.remove(0);
        
        coordinate.add(latitude);
        coordinate.add(longitude);
        
        result.add(coordinate);
        result.add(databaseEntry);
        
        return result;
    }
    
    private ArrayList<ArrayList<String>> extendValueListsWithPenalties(ArrayList<String> srcCellNames, ArrayList<String> srcSignalStrengths, ArrayList<String> destCellNames, ArrayList<String> destSignalStrengths) {
        ArrayList<ArrayList<String>> result = null;
        ArrayList<String> resultCellNames = null;
        ArrayList<String> resultSignalStrengths = null;
        int extremlyLowSignalStrength = -140;
        
        result = new ArrayList<ArrayList<String>>();
        resultCellNames = destCellNames;
        resultSignalStrengths = destSignalStrengths;
                
        for (String cellName: srcCellNames) {
            if (!resultCellNames.contains(cellName)) {
                resultCellNames.add(cellName);
                resultSignalStrengths.add(Integer.toString(extremlyLowSignalStrength));
            }
        }
        
        result.add(resultCellNames);
        result.add(resultSignalStrengths);
        
        return result;
    }
    
    private HashMap<String, Integer> createDatabaseForHorizontalSearch(ArrayList<ArrayList<String>> srcData) {
        HashMap<String, Integer> result = null;
        ArrayList<String> cellNames = null;
        ArrayList<Integer> signalStrengths = null;
        
        result = new HashMap<String, Integer>();
        cellNames = srcData.get(0);
        signalStrengths = new ArrayList<Integer>();
                
        for (String signalStrength : srcData.get(1)) {
            signalStrengths.add(Integer.parseInt(signalStrength));
        }
        
        for (int elementCounter = 0; elementCounter < cellNames.size(); elementCounter++) {
            result.put(cellNames.get(elementCounter), signalStrengths.get(elementCounter));
        }
        
        return result;
    }
    
    private HashMap<String, Integer> shiftDataValuesWithOffset(HashMap<String, Integer> srcData, int offset) {
        HashMap<String, Integer> result = null;
        Set<Entry<String, Integer>> hashmapEntrySet = null;
        List<Entry<String, Integer>> entries = null;
        
        result = new LinkedHashMap<String, Integer>();
        hashmapEntrySet = srcData.entrySet();
        entries = new LinkedList<>(hashmapEntrySet);
        
        for (Entry<String, Integer> entry : entries) {
            result.put(entry.getKey(), entry.getValue() + offset);
        }
        
        return result;
    }
    
    private double calculateFingerprintDifference(HashMap<String, Integer> minuedMap, HashMap<String, Integer> subtrahendMap) {
        Set<Entry<String, Integer>> minuedEntrySet = null;
        List<Entry<String, Integer>> minuedEntries = null;
        int minued = 0;
        int subtrahend = 0;
        int difference = 0;
        double powerOfDifference = 0;
        double result = 0;
        
        minuedEntrySet = minuedMap.entrySet();
        minuedEntries = new LinkedList<>(minuedEntrySet);
        
        for (Entry<String, Integer> entry : minuedEntries) {
            minued = entry.getValue();
            subtrahend = subtrahendMap.get(entry.getKey());
            difference = minued - subtrahend;
            powerOfDifference = Math.pow(difference, 2);
                       
            result = result + powerOfDifference;
        }
        
        return result;
    }
    
    private HashMap<String, Double> checkMeasurementPointInDatabase(String[] databaseEntry, String[] measurementPoint) {
        HashMap<String, Integer> databaseMap = null;
        HashMap<String, Integer> measurementMap = null;
        HashMap<String, Double> result = null;
        HashMap<String, Integer> offsetShiftedMeasurementPoint = null;
        ArrayList<ArrayList<String>> penaltyExtendedDatabaseEntry = null;
        ArrayList<ArrayList<String>> penaltyExtendedMeasurementPoint = null;
        ArrayList<String> databaseCellNameList = null;
        ArrayList<String> databaseSignalStrengthList = null;
        ArrayList<String> measurementCellNameList = null;
        ArrayList<String> meausrementSignalStrengthList = null;
        ArrayList<String> decoordinatedDatabaseEntry = null;
        ArrayList<String> coordinates = null;
        List<String> lstDatabaseEntry = null;
        List<String> lstMeasurementPoint = null;
        int offset = 0;
        double latitude = 0;
        double longitude = 0;
        double fingerprintDifference = 0;
        ArrayList<Entry<String, Integer>> measurementEntries = null;
        Entry<String, Integer> firstEntry = null;
        
        databaseMap = new HashMap<String, Integer>();
        measurementMap = new HashMap<String, Integer>();
        result = new HashMap<String, Double>();
        coordinates = new ArrayList<String>();
        decoordinatedDatabaseEntry = new ArrayList<String>();
        lstDatabaseEntry = Arrays.asList(databaseEntry);
        lstMeasurementPoint = Arrays.asList(measurementPoint);
        coordinates = getCoordinatesFromList(lstDatabaseEntry).get(0);
        decoordinatedDatabaseEntry = getCoordinatesFromList(lstDatabaseEntry).get(1);
        
        latitude = Double.parseDouble(coordinates.get(0));
        longitude = Double.parseDouble(coordinates.get(1));
        
        databaseCellNameList = getValuesFromList(DATATYPE.CELLNAME, decoordinatedDatabaseEntry);
        databaseSignalStrengthList = getValuesFromList(DATATYPE.SIGNALSTRENGTH, decoordinatedDatabaseEntry);
        measurementCellNameList = getValuesFromList(DATATYPE.CELLNAME, lstMeasurementPoint);
        meausrementSignalStrengthList = getValuesFromList(DATATYPE.SIGNALSTRENGTH, lstMeasurementPoint);
        
        penaltyExtendedMeasurementPoint = extendValueListsWithPenalties(databaseCellNameList, databaseSignalStrengthList, measurementCellNameList, meausrementSignalStrengthList);
        penaltyExtendedDatabaseEntry = extendValueListsWithPenalties(measurementCellNameList, meausrementSignalStrengthList, databaseCellNameList, databaseSignalStrengthList);
        
        measurementMap = createDatabaseForHorizontalSearch(penaltyExtendedMeasurementPoint);
        databaseMap = createDatabaseForHorizontalSearch(penaltyExtendedDatabaseEntry);
        measurementMap = Tools.sortHashMap(measurementMap, DATATYPE.SIGNALSTRENGTH);
        databaseMap = Tools.sortHashMapByReference(databaseMap, measurementMap);      
        
        measurementEntries = new ArrayList<Entry<String, Integer>>(measurementMap.entrySet());
        firstEntry = measurementEntries.get(0);
        offset = databaseMap.get(firstEntry.getKey()) - measurementMap.get(firstEntry.getKey());
        offsetShiftedMeasurementPoint = shiftDataValuesWithOffset(measurementMap, offset);
        fingerprintDifference = calculateFingerprintDifference(offsetShiftedMeasurementPoint, databaseMap);
        
        result.put(fingerprintDifferenceValue, fingerprintDifference);
        result.put(COORDINATES.LONGITUDE.toString(), longitude);
        result.put(COORDINATES.LATITUDE.toString(), latitude);
        
        return result;
    }
    
    public void getLocation(File resultFile) {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        HashMap<String, Double> coordinates = null;
        HashMap<String, Double> measurementResult = null;
        ArrayList<String> lstLatitude = null;
        ArrayList<String> lstLongitude = null;
        ArrayList<String> measurement = null;
        ArrayList<String> database = null;
        String[] measurementPoint = null;
        String[] databaseEntry = null;
        String pointLatitude = null;
        String pointLongitude = null;
        String resultFileHeader = null;
        String rowToWrite = null;
        double fingerprintDifference = 0;
        double minimumFingerprintDifference = 0;
        Rengine rEngine = null;
        
        coordinates = new HashMap<String, Double>();
        lstLatitude = new ArrayList<String>();
        lstLongitude = new ArrayList<String>();
        measurementResult = new HashMap<String, Double>();
        measurement = Tools.readFileToMemory(getMeasurementFile());
        database = Tools.readFileToMemory(getdatabaseFile());

        try {
            fileWriter = new FileWriter(resultFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            
            resultFileHeader = "point," + COORDINATES.LATITUDE.toString() + "," + COORDINATES.LONGITUDE.toString();
            bufferedWriter.write(resultFileHeader);
            bufferedWriter.newLine();
            
            rEngine = new Rengine(new String[] { "--no-save" }, false, null);
            
            for (int measurementDataRowCounter = 1; measurementDataRowCounter < measurement.size(); measurementDataRowCounter++) {
                measurementPoint = measurement.get(measurementDataRowCounter).split(",");
                minimumFingerprintDifference = Integer.MAX_VALUE;
                
                for (int databaseDataRowCounter = 0; databaseDataRowCounter < database.size(); databaseDataRowCounter++) {
                    databaseEntry = database.get(databaseDataRowCounter).split(",");
                    measurementResult = checkMeasurementPointInDatabase(databaseEntry, measurementPoint);
                    
                    pointLatitude = Double.toString(measurementResult.get(COORDINATES.LATITUDE.toString()));
                    pointLongitude = Double.toString(measurementResult.get(COORDINATES.LONGITUDE.toString()));
                    fingerprintDifference = measurementResult.get(fingerprintDifferenceValue);
                    
                    if (fingerprintDifference < minimumFingerprintDifference) {
                         minimumFingerprintDifference = fingerprintDifference;
                         lstLatitude.clear();
                         lstLongitude.clear();
                         lstLatitude.add(pointLatitude);
                         lstLongitude.add(pointLongitude);
                    }
                }
                
                coordinates = Tools.getMeanValueOfCoordinates(rEngine, lstLatitude, lstLongitude);
                pointLatitude = Double.toString(coordinates.get(COORDINATES.LATITUDE.toString()));
                pointLongitude = Double.toString(coordinates.get(COORDINATES.LONGITUDE.toString()));
                rowToWrite = "Point" + measurementDataRowCounter + "," + pointLatitude + "," + pointLongitude;
                System.out.println(Arrays.asList(measurementPoint));
                System.out.println(rowToWrite);
                bufferedWriter.write(rowToWrite);
                bufferedWriter.newLine();
                coordinates.clear();
            }
            
            rEngine.end();
            
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
    }
    
}
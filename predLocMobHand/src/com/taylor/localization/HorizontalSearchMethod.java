package com.taylor.localization;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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
    
    private final ArrayList<String> database;
    private final ArrayList<String> measurement;
    private final String penaltySignalStrength = "-140";
    private final int databaseOffset = 20;
        
    public HorizontalSearchMethod(ArrayList<String> database, ArrayList<String> measurement) {
        this.database = database;
        this.measurement = measurement;
    }
    
    private HashMap<String, String> createHashMappedDatabase(ArrayList<String> database) {
        HashMap<String, String> databaseHashMap = null;
        String[] databaseRow = null;
        String latitude = null;
        String longitude = null;
        String coordinate = null;
        String cellsAndSignals = null;
        String headerRow = null;
        String measurementPoint = null;
        boolean isDatabase = true;
        
        databaseHashMap = new LinkedHashMap<String, String>();
        headerRow = database.get(0);
        
        if (headerRow.startsWith("cellID")) {
            isDatabase = false;
        }
        
        for (int databaseElementCounter = 1; databaseElementCounter < database.size(); databaseElementCounter++) {
            if (isDatabase) {
                databaseRow = database.get(databaseElementCounter).split(",",3);
                latitude = databaseRow[0];
                longitude = databaseRow[1];
                coordinate = latitude + "," + longitude;
                cellsAndSignals = databaseRow[2];
                databaseHashMap.put(coordinate, cellsAndSignals);
            } else {
                measurementPoint = Integer.toString(databaseElementCounter);
                cellsAndSignals = database.get(databaseElementCounter);
                databaseHashMap.put(measurementPoint, cellsAndSignals);
            }
        }
      
        return databaseHashMap;
    }
    
    private String putPenalties(String srcCellsAndSignals, String destCellsAndSignals) {
        String penaltyCell = null;
        String dataWithPenalties = null; 
        String[] data = null;
        ArrayList<String> srcData = null;
        ArrayList<String> dcsData = null;
        
        data = srcCellsAndSignals.split(",");
        srcData = new ArrayList<String>(Arrays.asList(data));
        data = destCellsAndSignals.split(",");
        dcsData = new ArrayList<String>(Arrays.asList(data));
        
        for (int elemCounter = 0; elemCounter < srcData.size(); elemCounter = elemCounter + 2) {
            if (!dcsData.contains(srcData.get(elemCounter))) {
                penaltyCell = srcData.get(elemCounter); 
                dcsData.add(penaltyCell);
                dcsData.add(penaltySignalStrength);
            }
        }
        
        dataWithPenalties = String.join(",", dcsData);
        
        return dataWithPenalties;
    }
    
    private HashMap<String, Integer> hashMapper(String toHashMap) {
        HashMap<String, Integer> resultHashMap = null;
        ArrayList<String> cellsAndSignals = null;
        Iterator<String> iterableCellsAndSignals = null;
        String[] data = null;
        String cellName = null;
        int signalStrength = 0; 
        
        resultHashMap = new HashMap<String, Integer>();
        data = toHashMap.split(",");
        cellsAndSignals = new ArrayList<String>(Arrays.asList(data));
        iterableCellsAndSignals = cellsAndSignals.iterator();
        
        while(iterableCellsAndSignals.hasNext()) {
            cellName = iterableCellsAndSignals.next();
            signalStrength = Integer.parseInt(iterableCellsAndSignals.next());
            resultHashMap.put(cellName, signalStrength);
        }
        
        return resultHashMap;
    }
    
    private int countOriginalElements(Set<Entry<String, Integer>> entrySet) {
        int number = 0;
        int signalStrength = 0;
        
        for (Entry<String, Integer> entry : entrySet) {
            signalStrength = entry.getValue();
            if ( signalStrength > Integer.parseInt(penaltySignalStrength)) {
                number++;
            }
        }

        return number;     
    }
    
    private double calculateFingerprintDifferenceMethod1(HashMap<String, Integer> minuedMap, HashMap<String, Integer> subtrahendMap, int avrageOffset) {
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
            difference = minued - (subtrahend + avrageOffset);
            powerOfDifference = Math.pow(difference, 2);
                       
            result = result + powerOfDifference;
        }
        
        return result;
    }
    
    private double calculateFingerprintDifferenceMethod2(HashMap<String, Integer> minuedMap, HashMap<String, Integer> subtrahendMap, int averageOffset) {
        Set<Entry<String, Integer>> minuedEntrySet = null;
        List<Entry<String, Integer>> minuedEntries = null;
        Set<Entry<String, Integer>> subtrahendEntrySet = null;
        List<Entry<String, Integer>> subtrahendEntries = null;
        Entry<String, Integer> lastMinuedEntry = null;
        Entry<String, Integer> lastSubtrahendEntry = null;
        int minued = 0;
        int subtrahend = 0;
        int difference = 0;
        int minuedPartDifference = 0;
        int subtrahendPartDifference = 0;
        int weakestMinued = 0;
        int weakestSubtrahend = 0;
        double weightValue = 0.0;
        double powerOfDifference = 0;
        double minuedPartpowerOfDifference = 0;
        double subtrahendPartpowerOfDifference = 0;
        double result = 0;
        double minuedPartresult = 0;
        double subtrahendPartresult = 0;
        
        minuedEntrySet = minuedMap.entrySet();
        minuedEntries = new LinkedList<>(minuedEntrySet);
        subtrahendEntrySet = subtrahendMap.entrySet();
        subtrahendEntries = new LinkedList<>(subtrahendEntrySet);
        lastMinuedEntry = minuedEntries.get(minuedEntries.size() - 1);
        lastSubtrahendEntry = subtrahendEntries.get(subtrahendEntries.size() - 1);
        weakestMinued = lastMinuedEntry.getValue();
        weakestSubtrahend = lastSubtrahendEntry.getValue();
        weightValue = 0.0;

        for (Entry<String, Integer> entry : minuedEntries) {
            minued = entry.getValue();
            subtrahend = subtrahendMap.get(entry.getKey());
            
            if (minued == Integer.parseInt(penaltySignalStrength)) {
                subtrahendPartDifference = weakestSubtrahend - subtrahend + averageOffset;
                subtrahendPartpowerOfDifference = Math.pow(subtrahendPartDifference, 2);
                subtrahendPartresult = subtrahendPartresult + subtrahendPartpowerOfDifference;
            } else if (subtrahend == Integer.parseInt(penaltySignalStrength)) {
                minuedPartDifference = minued - weakestMinued + averageOffset;
                minuedPartpowerOfDifference = Math.pow(minuedPartDifference, 2);
                minuedPartresult = minuedPartresult + minuedPartpowerOfDifference;
            } else {
                difference = minued - subtrahend + averageOffset;
                powerOfDifference = Math.pow(difference, 2);
                result = result + powerOfDifference;
            }
        }
        
        result = result + (weightValue * minuedPartresult) + (weightValue * subtrahendPartresult);
        
        return result;
    }
    
    public void getLocation(File resultFile) {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        HashMap<String, String> measurementHashMap = null;
        HashMap<String, String> databaseHashMap = null;
        HashMap<String, Integer> hashMappedMeasurementCellsAndSignals = null;
        HashMap<String, Integer> hashMappedDatabaseCellsAndSignals = null;
        Set<Entry<String, String>> measurementEntries = null;
        Set<Entry<String, String>> databaseEntries = null;
        String pointLatitude = null;
        String pointLongitude = null;
        String resultFileHeader = null;
        String rowToWrite = null;
        String measurementCellsAndSignals = null;
        String databaseCellsAndSignals = null;
        String coordinate[] = null;
        int pointCounter = 0;
        double fingerprintDifference = 0;
        double minimumFingerprintDifference = 0;
        measurementHashMap = new HashMap<String, String>();
        databaseHashMap = new HashMap<String, String>();

        try {
            fileWriter = new FileWriter(resultFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            
            resultFileHeader = "point," + COORDINATES.LATITUDE.toString() + "," + COORDINATES.LONGITUDE.toString();
            bufferedWriter.write(resultFileHeader);
            bufferedWriter.newLine();
            
            measurementHashMap = createHashMappedDatabase(measurement);
            databaseHashMap = createHashMappedDatabase(database);
            measurementEntries = measurementHashMap.entrySet();
            databaseEntries = databaseHashMap.entrySet();
            
            pointCounter = 1;
            
            for (Entry<String, String> measurementEntry : measurementEntries) {
                measurementCellsAndSignals = measurementEntry.getValue(); 
                minimumFingerprintDifference = Integer.MAX_VALUE;
                
                for (Entry<String, String> databaseEntry : databaseEntries) {
                    coordinate = databaseEntry.getKey().split(",");
                    databaseCellsAndSignals = databaseEntry.getValue();

                    measurementCellsAndSignals = putPenalties(databaseCellsAndSignals, measurementCellsAndSignals);
                    hashMappedMeasurementCellsAndSignals = hashMapper(measurementCellsAndSignals);
                    hashMappedMeasurementCellsAndSignals = Tools.sortHashMap(hashMappedMeasurementCellsAndSignals, DATATYPE.SIGNALSTRENGTH);

                    databaseCellsAndSignals = putPenalties(measurementCellsAndSignals, databaseCellsAndSignals);
                    hashMappedDatabaseCellsAndSignals = hashMapper(databaseCellsAndSignals);
                    hashMappedDatabaseCellsAndSignals = Tools.sortHashMapByReference(hashMappedDatabaseCellsAndSignals, hashMappedMeasurementCellsAndSignals);
                    fingerprintDifference = calculateFingerprintDifferenceMethod2(hashMappedMeasurementCellsAndSignals, hashMappedDatabaseCellsAndSignals, databaseOffset);
                    
                    if (fingerprintDifference <= minimumFingerprintDifference) {
                        minimumFingerprintDifference = fingerprintDifference;
                        pointLatitude = coordinate[0];
                        pointLongitude = coordinate[1];
                    }
                }

                rowToWrite = "Point" + pointCounter + "," + pointLatitude + "," + pointLongitude;
                bufferedWriter.write(rowToWrite);
                bufferedWriter.newLine();
                pointCounter++;
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
    }
    
}
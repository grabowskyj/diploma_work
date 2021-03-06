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

import com.taylor.tools.Tools;
import com.taylor.tools.Tools.COORDINATES;
import com.taylor.tools.Tools.DATATYPE;

public class HorizontalSearchMethod {
    
    private final ArrayList<String> database;
    private final ArrayList<String> measurement;
    private final String penaltySignalStrength = "-140";
    
    /**
     * Constructor for HorizontalSearchMethod
     * @param database ArrayList type datasource
     * @param measurement ArrayList type measurement data
     */
    public HorizontalSearchMethod(ArrayList<String> database, ArrayList<String> measurement) {
        this.database = database;
        this.measurement = measurement;
    }
    
    /**
     * Creates HashMap type datasource from the ArrayList type input datasource
     * @param database ArrayList type input datasource
     * @return HashMap type input data
     */
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
    
    /**
     * Puts penalty cells and data to the target signal strength fingerprint 
     * @param srcCellsAndSignals source signal strength fingerprint to be taken as base fingerprint
     * @param targetCellsAndSignals target signal strength fingerprint where penalties will be put
     * @return penalty modified target fingerprint
     */
    private String putPenalties(String srcCellsAndSignals, String targetCellsAndSignals) {
        String penaltyCell = null;
        String dataWithPenalties = null; 
        String[] data = null;
        ArrayList<String> srcData = null;
        ArrayList<String> dcsData = null;
        
        data = srcCellsAndSignals.split(",");
        srcData = new ArrayList<String>(Arrays.asList(data));
        data = targetCellsAndSignals.split(",");
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
    
    /**
     * Creates HashMap type fingerprint from String type fingerpring
     * @param srcFingerprint String type source fingerprint to be modified
     * @return HashMap type fingerprint
     */
    private HashMap<String, Integer> hashMapper(String srcFingerprint) {
        HashMap<String, Integer> resultHashMap = null;
        ArrayList<String> cellsAndSignals = null;
        Iterator<String> iterableCellsAndSignals = null;
        String[] data = null;
        String cellName = null;
        int signalStrength = 0; 
        
        resultHashMap = new HashMap<String, Integer>();
        data = srcFingerprint.split(",");
        cellsAndSignals = new ArrayList<String>(Arrays.asList(data));
        iterableCellsAndSignals = cellsAndSignals.iterator();
        
        while(iterableCellsAndSignals.hasNext()) {
            cellName = iterableCellsAndSignals.next();
            signalStrength = Integer.parseInt(iterableCellsAndSignals.next());
            resultHashMap.put(cellName, signalStrength);
        }
        
        return resultHashMap;
    }
    
    /**
     * Calculates the distance between two fingerprint
     * @param minuedMap fingerprint at measurement point
     * @param subtrahendMap fingerprint from the datasource
     * @return distance between the measurement point fingerprint and the datasource fingerprint
     */
    private double calculateFingerprintDifferenceMethod(HashMap<String, Integer> minuedMap, HashMap<String, Integer> subtrahendMap) {
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
    
    /**
     * Determinates the coordinates of the measurement points
     * @param resultFile Result file, where the results of the localizations will be stored 
     */
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
                System.out.println(Thread.currentThread().getName() + " Measurement: " + measurementEntry + " (" + pointCounter + "/" + measurementEntries.size() + ")");
                
                for (Entry<String, String> databaseEntry : databaseEntries) {
                    coordinate = databaseEntry.getKey().split(",");
                    databaseCellsAndSignals = databaseEntry.getValue();
                    //System.out.println(Thread.currentThread().getName() + " Checking: " +databaseEntry);

                    measurementCellsAndSignals = putPenalties(databaseCellsAndSignals, measurementCellsAndSignals);
                    hashMappedMeasurementCellsAndSignals = hashMapper(measurementCellsAndSignals);
                    hashMappedMeasurementCellsAndSignals = Tools.sortHashMap(hashMappedMeasurementCellsAndSignals, DATATYPE.SIGNALSTRENGTH);

                    databaseCellsAndSignals = putPenalties(measurementCellsAndSignals, databaseCellsAndSignals);
                    hashMappedDatabaseCellsAndSignals = hashMapper(databaseCellsAndSignals);
                    hashMappedDatabaseCellsAndSignals = Tools.sortHashMapByReference(hashMappedDatabaseCellsAndSignals, hashMappedMeasurementCellsAndSignals);
                    fingerprintDifference = calculateFingerprintDifferenceMethod(hashMappedMeasurementCellsAndSignals, hashMappedDatabaseCellsAndSignals);
                    
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
package com.taylor.localization;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.rosuda.JRI.Rengine;

import com.taylor.simulation.ConvertDatFile;
import com.taylor.tools.Tools;
import com.taylor.tools.Tools.COORDINATES;
import com.taylor.tools.Tools.DATATYPE;

public class HorizontalSearchMethod {
    
    private File databaseFile;
    private File measurementFile;
    final private String penaltySignalStrength = "-140";
        
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
    
    private HashMap<String, String> createHashMappedDatabase(ArrayList<String> database) {
        HashMap<String, String> databaseHashMap = null;
        String[] databaseRow = null;
        String latitude = null;
        String longitude = null;
        String coordinate = null;
        String cellsAndSignals = null;
        String headerRow = null;
        String measurementPoint = null;
        boolean databaseFile = true;
        
        databaseHashMap = new LinkedHashMap<String, String>();
        headerRow = database.get(0);
        
        if (headerRow.startsWith("cellID")) {
            databaseFile = false;
        }
        
        for (int databaseElementCounter = 1; databaseElementCounter < database.size(); databaseElementCounter++) {
            if (databaseFile) {
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
    
    private String putPenalties(String toCellsAndSignals) {
        String dataWithPenalties = null;
        String cellWithPenalty = null;
        String[] data = null;
        ArrayList<String> modifiableData = null;
        Collection<String> penaltyCells = null;
        
        data = toCellsAndSignals.split(",");
        modifiableData = new ArrayList<String>(Arrays.asList(data));
        penaltyCells = ConvertDatFile.convertCellID.values();
        
        for (String penaltyCell : penaltyCells) {
            if (!modifiableData.contains(penaltyCell)) {
                cellWithPenalty = penaltyCell + "," + penaltySignalStrength; 
                modifiableData.add(cellWithPenalty);
            }
        }
        
        dataWithPenalties = String.join(",", modifiableData);
        
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
    
    public void getLocation(File resultFile) {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        HashMap<String, Double> coordinates = null;
        HashMap<String, String> measurementHashMap = null;
        HashMap<String, String> databaseHashMap = null;
        HashMap<String, Integer> hashMappedMeasurementCellsAndSignals = null;
        HashMap<String, Integer> hashMappedDatabaseCellsAndSignals = null;
        Set<Entry<String, String>> measurementEntries = null;
        Set<Entry<String, String>> databaseEntries = null;
        ArrayList<String> lstLatitude = null;
        ArrayList<String> lstLongitude = null;
        ArrayList<String> measurement = null;
        ArrayList<String> database = null;
        String pointLatitude = null;
        String pointLongitude = null;
        String resultFileHeader = null;
        String rowToWrite = null;
        String pointCounter = null;
        String measurementCellsAndSignals = null;
        String databaseCellsAndSignals = null;
        String coordinate = null;
        double fingerprintDifference = 0;
        Rengine rEngine = null;
        
        coordinates = new HashMap<String, Double>();
        measurementHashMap = new HashMap<String, String>();
        databaseHashMap = new HashMap<String, String>();
        lstLatitude = new ArrayList<String>();
        lstLongitude = new ArrayList<String>();
        measurement = Tools.readFileToMemory(getMeasurementFile());
        database = Tools.readFileToMemory(getdatabaseFile());

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
            
            rEngine = new Rengine(new String[] { "--no-save" }, false, null);
            
            for (Entry<String, String> measurementEntry : measurementEntries) {
                pointCounter = measurementEntry.getKey();
                System.out.println("point: " + pointCounter);
                measurementCellsAndSignals = measurementEntry.getValue();
                measurementCellsAndSignals = putPenalties(measurementCellsAndSignals);
                hashMappedMeasurementCellsAndSignals = hashMapper(measurementCellsAndSignals);
                hashMappedMeasurementCellsAndSignals = Tools.sortHashMap(hashMappedMeasurementCellsAndSignals, DATATYPE.SIGNALSTRENGTH);
                
                for (Entry<String, String> databaseEntry : databaseEntries) {
                    coordinate = databaseEntry.getKey();
                    databaseCellsAndSignals = databaseEntry.getValue();
                    databaseCellsAndSignals = putPenalties(databaseCellsAndSignals);
                    hashMappedDatabaseCellsAndSignals = hashMapper(databaseCellsAndSignals);
                    hashMappedDatabaseCellsAndSignals = Tools.sortHashMapByReference(hashMappedDatabaseCellsAndSignals, hashMappedDatabaseCellsAndSignals);
                    fingerprintDifference = calculateFingerprintDifference(hashMappedMeasurementCellsAndSignals, hashMappedDatabaseCellsAndSignals);
                    
                }
                
                
                coordinates = Tools.getMeanValueOfCoordinates(rEngine, lstLatitude, lstLongitude);
                pointLatitude = Double.toString(coordinates.get(COORDINATES.LATITUDE.toString()));
                pointLongitude = Double.toString(coordinates.get(COORDINATES.LONGITUDE.toString()));
                rowToWrite = "Point" + pointCounter + "," + pointLatitude + "," + pointLongitude;
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
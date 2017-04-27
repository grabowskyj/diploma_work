package com.taylor.localization;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.rosuda.JRI.Rengine;

import com.taylor.tools.Tools;
import com.taylor.tools.Tools.COORDINATES;

public class VerticalSearchMethod {
    
    private File databaseFile;
    private File measurementFile;
        
    public VerticalSearchMethod(File databaseFile, File measurementFile) {
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
    
    public HashMap<String, ArrayList<String>> createDatabaseForVerticalSearch() {
        HashMap<String, ArrayList<String>> database = null;
        ArrayList<String> data = null;
        int simulationDataHeaderElementCounter = 0;
        String[] simulationDataHeader = null;
        
        data = Tools.readFileToMemory(getdatabaseFile());

        if (!data.isEmpty()) {
            simulationDataHeader = data.get(0).trim().split(",");
            database = new HashMap<String, ArrayList<String>>();
            
            for (int headerElement = 0; headerElement < simulationDataHeader.length; headerElement++) {
                database.put(simulationDataHeader[headerElement], new ArrayList<String>());
            }
            
            for (int simulationDataRowCounter = 1; simulationDataRowCounter < data.size(); simulationDataRowCounter++) {
                String[] splittedSimulationDataRow = data.get(simulationDataRowCounter).split(",");
                simulationDataHeaderElementCounter = 0;
                
                for (String simulationDataHeaderElement : simulationDataHeader) {
                    if (simulationDataHeaderElementCounter < splittedSimulationDataRow.length) {
                        database.get(simulationDataHeaderElement).add(splittedSimulationDataRow[simulationDataHeaderElementCounter]);    
                    } else {
                        database.get(simulationDataHeaderElement).add("0");
                    }
                    
                    simulationDataHeaderElementCounter++;
                }
            }
        }
        
        return database;
    }
    
    private HashMap<String, ArrayList<String>> createDatabaseFromSelection(String indexListName, ArrayList<Integer> indexList, HashMap<String, ArrayList<String>> srcDatabase) {
        HashMap<String, ArrayList<String>> databaseSubset = new HashMap<String, ArrayList<String>>();
        String elementName = null;
        String srcElement = null;
        Set<String> hashmapKeySet= null;
        Iterator<String> hashmapKeys = null;
        
        hashmapKeySet = srcDatabase.keySet();
        hashmapKeys = hashmapKeySet.iterator();
        
        while (hashmapKeys.hasNext()) {
            elementName = hashmapKeys.next();
            databaseSubset.put(elementName, new ArrayList<String>());
            
            for (int index : indexList) {
                srcElement = srcDatabase.get(elementName).get(index);
                databaseSubset.get(elementName).add(srcElement);
            }
        }
        
        return databaseSubset;
    }
    
    private ArrayList<Integer> getIndexListForNewDatabase(String[] elementNameAndElement, HashMap<String, ArrayList<String>> database){
        ArrayList<String> dataset = null;
        ArrayList<Integer> indexListOfElement = null;
        String elementName = null;
        String element = null;
        
        indexListOfElement = new ArrayList<Integer>();
        elementName = elementNameAndElement[0];
        element = elementNameAndElement[1];
        dataset = database.get(elementName);
                
        for (int datasetElementCounter = 0; datasetElementCounter < dataset.size(); datasetElementCounter++) {
            if (element.equals(dataset.get(datasetElementCounter))) {
                indexListOfElement.add(datasetElementCounter);
            }
        }
        
        return indexListOfElement; 
    }
    
    private ArrayList<Integer> assembleSideValues(int inRange, ArrayList<String> srcDataset) {
        ArrayList<Integer> sideValueList = null;
        int startIndex = 0;
        int endIndex = 0;
        
        sideValueList = new ArrayList<Integer>();
        startIndex = srcDataset.indexOf(Integer.toString(inRange));
        endIndex = srcDataset.lastIndexOf(Integer.toString(inRange));
        
        for (int index = startIndex; index <= endIndex; index++ ) {
            if (inRange == Integer.parseInt(srcDataset.get(index))) {
                sideValueList.add(index);
            }
        }
        
        return sideValueList;
    }
    
    private ArrayList<Integer> checkSideValues(String[] elementNameAndElement, HashMap<String, ArrayList<String>> database) {
        ArrayList<Integer> indexListOfElements = null;
        ArrayList<Integer> positiveSideIndexList = null;
        ArrayList<Integer> negativeSideIndexList = null;
        ArrayList<String> dataset = null;
        boolean isValueFound = false;
        int checkRange = 7;
        int range = 1;
        int positiveRangeNumber = 0;
        int negativeRangeNumber = 0;
        int element = 0;
        String elementName = null;
        
        element = Integer.parseInt(elementNameAndElement[1]);
        elementName = elementNameAndElement[0];
        indexListOfElements = new ArrayList<Integer>();
        positiveSideIndexList = new ArrayList<Integer>();
        negativeSideIndexList = new ArrayList<Integer>();
        dataset = database.get(elementName);
        
        while (isValueFound == false && range <= checkRange) {
            positiveRangeNumber = element + range;
            negativeRangeNumber = element - range;

            if (dataset.contains(Integer.toString(positiveRangeNumber))) {
                positiveSideIndexList = assembleSideValues(positiveRangeNumber, dataset);
            }
            
            if (dataset.contains(Integer.toString(negativeRangeNumber))) {
                negativeSideIndexList = assembleSideValues(negativeRangeNumber, dataset);
            }
            
            if (!positiveSideIndexList.isEmpty() || !negativeSideIndexList.isEmpty()) {
                isValueFound = true;
            }
            
            range++;
        }
        
        if (!positiveSideIndexList.isEmpty()) {
            indexListOfElements.addAll(positiveSideIndexList);
        }
        
        if (!negativeSideIndexList.isEmpty()) {
            indexListOfElements.addAll(negativeSideIndexList);
        }
        
        Collections.sort(indexListOfElements);
        
        return indexListOfElements;
    }
    
    public void getLocation(HashMap<String, ArrayList<String>> database, File resultFile) {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        HashMap<String, ArrayList<String>> transitionalDatabase = null; 
        HashMap<String, ArrayList<String>> savedDatabase = null;
        HashMap<String, Double> coordinates = null;
        ArrayList<Integer> indexListOfElements = null;
        ArrayList<String> lstLatitude = null;
        ArrayList<String> lstLongitude = null;
        ArrayList<String> measurement = null;
        String[] elementNameAndElement = null;
        String[] splittedMeasurementRow = null;
        String[] measurementDataHeader = null;
        String elementName = null;
        String rowToWrite = null;
        String pointLatitude = null;
        String pointLongitude = null;
        String resultFileHeader = null;
        int measurementDataHeaderElementCounter = 0;
        Rengine rEngine = null;
        
        coordinates = new HashMap<String, Double>();
        indexListOfElements = new ArrayList<Integer>();
        lstLatitude = new ArrayList<String>();
        lstLongitude = new ArrayList<String>();
        measurement = Tools.readFileToMemory(getMeasurementFile());
        elementNameAndElement = new String[2];
        measurementDataHeader = measurement.get(0).split(",");
        
        Tools.createFile(resultFile);

        try {
            fileWriter = new FileWriter(resultFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            
            resultFileHeader = "point," + COORDINATES.LATITUDE.toString() + "," + COORDINATES.LONGITUDE.toString();
            bufferedWriter.write(resultFileHeader);
            bufferedWriter.newLine();
            
            rEngine = new Rengine(new String[] { "--no-save" }, false, null);
            
            for (int measurementDataRowCounter = 1; measurementDataRowCounter < measurement.size(); measurementDataRowCounter++) {
                transitionalDatabase = database;
                splittedMeasurementRow = measurement.get(measurementDataRowCounter).split(",");
                measurementDataHeaderElementCounter = 0;
                
                for (String splittedMeasurementRowElement : splittedMeasurementRow) {
                    elementName = measurementDataHeader[measurementDataHeaderElementCounter];
                    elementNameAndElement[0] = elementName;
                    elementNameAndElement[1] = splittedMeasurementRowElement;
                    savedDatabase = transitionalDatabase;
                    indexListOfElements = getIndexListForNewDatabase(elementNameAndElement, transitionalDatabase);
                    
                    if (indexListOfElements.isEmpty() && elementName.endsWith("signalStrength")) {
                        indexListOfElements = checkSideValues(elementNameAndElement, transitionalDatabase); 
                    }
                    
                    transitionalDatabase = createDatabaseFromSelection(elementName, indexListOfElements, transitionalDatabase);
                    lstLatitude = transitionalDatabase.get(COORDINATES.LATITUDE.toString());
                    lstLongitude = transitionalDatabase.get(COORDINATES.LONGITUDE.toString());
                    
                    if (indexListOfElements.isEmpty()) {
                        lstLatitude = savedDatabase.get(COORDINATES.LATITUDE.toString());
                        lstLongitude = savedDatabase.get(COORDINATES.LONGITUDE.toString());
                        break;
                    }
                    
                    measurementDataHeaderElementCounter++;
                }
                
                coordinates = Tools.getMeanValueOfCoordinates(rEngine, lstLatitude, lstLongitude);
                pointLatitude = Double.toString(coordinates.get(COORDINATES.LATITUDE.toString()));
                pointLongitude = Double.toString(coordinates.get(COORDINATES.LONGITUDE.toString()));
                rowToWrite = "Point" + measurementDataRowCounter + "," + pointLatitude + "," + pointLongitude;
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
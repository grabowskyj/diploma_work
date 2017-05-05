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
    
    private final ArrayList<String> database;
    private final ArrayList<String> measurement;
        
    public VerticalSearchMethod(ArrayList<String> database, ArrayList<String> measurement) {
        this.database = database;
        this.measurement = measurement;
    }
    
    private HashMap<String, ArrayList<String>> createDatabaseForVerticalSearch() {
        HashMap<String, ArrayList<String>> databaseData = null;
        int simulationDataHeaderElementCounter = 0;
        String[] simulationDataHeader = null;

        if (!database.isEmpty()) {
            simulationDataHeader = database.get(0).trim().split(",");
            databaseData = new HashMap<String, ArrayList<String>>();
            
            for (int headerElement = 0; headerElement < simulationDataHeader.length; headerElement++) {
                databaseData.put(simulationDataHeader[headerElement], new ArrayList<String>());
            }
            
            for (int simulationDataRowCounter = 1; simulationDataRowCounter < database.size(); simulationDataRowCounter++) {
                String[] splittedSimulationDataRow = database.get(simulationDataRowCounter).split(",");
                simulationDataHeaderElementCounter = 0;
                
                for (String simulationDataHeaderElement : simulationDataHeader) {
                    if (simulationDataHeaderElementCounter < splittedSimulationDataRow.length) {
                        databaseData.get(simulationDataHeaderElement).add(splittedSimulationDataRow[simulationDataHeaderElementCounter]);    
                    } else {
                        databaseData.get(simulationDataHeaderElement).add("0");
                    }
                    
                    simulationDataHeaderElementCounter++;
                }
            }
        }
        
        return databaseData;
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
        
        if (dataset != null) {
            for (int datasetElementCounter = 0; datasetElementCounter < dataset.size(); datasetElementCounter++) {
                if (element.equals(dataset.get(datasetElementCounter))) {
                    indexListOfElement.add(datasetElementCounter);
                }
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
    
    public void getLocation(File resultFile) {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        HashMap<String, ArrayList<String>> transitionalDatabase = null; 
        HashMap<String, ArrayList<String>> savedDatabase = null;
        HashMap<String, Double> coordinates = null;
        ArrayList<Integer> indexListOfElements = null;
        ArrayList<String> lstLatitude = null;
        ArrayList<String> lstLongitude = null;
        String[] elementNameAndElement = null;
        String[] splittedMeasurementRow = null;
        String[] measurementDataHeader = null;
        String elementName = null;
        String rowToWrite = null;
        String pointLatitude = null;
        String pointLongitude = null;
        String resultFileHeader = null;
        int measurementDataHeaderElementCounter = 0;
        boolean isElementInList = true;
        Rengine rEngine = null;
        
        coordinates = new HashMap<String, Double>();
        indexListOfElements = new ArrayList<Integer>();
        lstLatitude = new ArrayList<String>();
        lstLongitude = new ArrayList<String>();
        elementNameAndElement = new String[2];
        measurementDataHeader = measurement.get(0).split(",");
        
        Tools.createFile(resultFile);
        HashMap<String, ArrayList<String>> createdDatabase = createDatabaseForVerticalSearch();
        
        try {
            fileWriter = new FileWriter(resultFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            
            resultFileHeader = "point," + COORDINATES.LATITUDE.toString() + "," + COORDINATES.LONGITUDE.toString();
            bufferedWriter.write(resultFileHeader);
            bufferedWriter.newLine();
            
            rEngine = new Rengine(new String[] { "--no-save" }, false, null);
            
            for (int measurementDataRowCounter = 1; measurementDataRowCounter < measurement.size(); measurementDataRowCounter++) {
                transitionalDatabase = createdDatabase;
                splittedMeasurementRow = measurement.get(measurementDataRowCounter).split(",");
                measurementDataHeaderElementCounter = 0;
                System.out.println("Measurement: " + measurementDataRowCounter + "/" + measurement.size());
                
                for (String splittedMeasurementRowElement : splittedMeasurementRow) {
                    if (isElementInList != true) {
                        isElementInList = true;
                        continue;
                    }
                    
                    elementName = measurementDataHeader[measurementDataHeaderElementCounter];
                    elementNameAndElement[0] = elementName;
                    elementNameAndElement[1] = splittedMeasurementRowElement;
                    savedDatabase = transitionalDatabase;
                    indexListOfElements = getIndexListForNewDatabase(elementNameAndElement, transitionalDatabase);
                    
                    if (indexListOfElements.isEmpty() && elementName.endsWith("signalStrength")) {
                        indexListOfElements = checkSideValues(elementNameAndElement, transitionalDatabase); 
                    }
                    
                    if (indexListOfElements.isEmpty() && elementName.endsWith("cellID")) {
                        isElementInList = false;
                        continue;
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
                bufferedWriter.flush();
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
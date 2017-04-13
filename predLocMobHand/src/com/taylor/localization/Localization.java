package com.taylor.localization;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.taylor.measurement.ConvertMeasurementFile;
import com.taylor.simulation.ConvertDatFile;
import com.taylor.tools.Tools;
import com.taylor.tools.Tools.COORDINATES;

public class Localization {
    
    private File databaseFile;
    private File measurementFile;

    
    public Localization(File databaseFile, File measurementFile) {
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

    private Hashtable<String, ArrayList<String>> createDatabase() {
        Hashtable<String, ArrayList<String>> database = null;
        ArrayList<String> data = null;
        int simulationDataHeaderElementCounter = 0;
        String[] simulationDataHeader = null;
        
        data = Tools.readFileToMemory(databaseFile);

        if (!data.isEmpty()) {
            simulationDataHeader = data.get(0).trim().split(",");
            database = new Hashtable<String, ArrayList<String>>();
            
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
    
    private Hashtable<String, ArrayList<String>> createDatabaseFromSelection(String indexListName, List<Integer> indexList, Hashtable<String, ArrayList<String>> srcDatabase){
        Hashtable<String, ArrayList<String>> databaseSubset = new Hashtable<String, ArrayList<String>>();
        String elementName = null;
        String srcElement = null;
        Enumeration<String> keys;
        
        keys = srcDatabase.keys();
        
        while (keys.hasMoreElements()) {
            elementName = keys.nextElement();
            databaseSubset.put(elementName, new ArrayList<String>());
            
            for (int index : indexList) {
                srcElement = srcDatabase.get(elementName).get(index);
                databaseSubset.get(elementName).add(srcElement);
            }
        }
        
        return databaseSubset;
    }
    
    private List<Integer> getIndexListForNewDatabase(String[] elementNameAndElement, Hashtable<String, ArrayList<String>> database){
        List<Integer> indexListOfElement = new ArrayList<Integer>();
        String elementName = null;
        String element = null;
        ArrayList<String> dataset = null;
        
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
    
    private List<Integer> assembleSideValues(int inRange, ArrayList<String> srcDataset) {
        List<Integer> sideValueList = new ArrayList<Integer>();
        int startIndex = 0;
        int endIndex = 0;
        
        startIndex = srcDataset.indexOf(Integer.toString(inRange));
        endIndex = srcDataset.lastIndexOf(Integer.toString(inRange));
        
        for (int index = startIndex; index <= endIndex; index++ ) {
            
            if (inRange == Integer.parseInt(srcDataset.get(index))) {
                sideValueList.add(index);
            }
        }
        
        return sideValueList;
    }
    
    private List<Integer> checkSideValues(String[] elementNameAndElement, Hashtable<String, ArrayList<String>> database) {
        List<Integer> indexListOfElements = new ArrayList<Integer>();
        List<Integer> positiveSideIndexList = new ArrayList<Integer>();
        List<Integer> negativeSideIndexList = new ArrayList<Integer>();
        String elementName = elementNameAndElement[0];
        int element = Integer.parseInt(elementNameAndElement[1]);
        ArrayList<String> dataset = database.get(elementName);
        boolean isValueFound = false;
        int checkRange = 10;
        int range = 1;
        int positiveRangeNumber = 0;
        int negativeRangeNumber = 0;
        
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
    
    private Hashtable<COORDINATES, Double> getLocationFromDatabase(Hashtable<String, ArrayList<String>> database) {
        @SuppressWarnings("serial")
        Hashtable<COORDINATES, Double> coordinates = new Hashtable<COORDINATES, Double>() {{
            put(COORDINATES.LATITUDE, 0.0);
            put(COORDINATES.LONGITUDE, 0.0);
        }};
        Hashtable<String, ArrayList<String>> transitionalDatabase = null; 
        List<Integer> indexListOfElements = new ArrayList<Integer>();
        List<String> latitude = new ArrayList<String>();
        List<String> longitude = new ArrayList<String>();
        ArrayList<String> measurement = Tools.readFileToMemory(measurementFile);
        String[] elementNameAndElement = new String[2];
        String[] splittedMeasurementRow = null;
        String[] measurementDataHeader = measurement.get(0).split(",");
        String elementName = null;

        for (int measurementDataRowCounter = 1; measurementDataRowCounter < measurement.size(); measurementDataRowCounter++) {
            transitionalDatabase = database;
            splittedMeasurementRow = measurement.get(measurementDataRowCounter).split(",");
            int measurementDataHeaderElementCounter = 0;
            
            for (String splittedMeasurementRowElement : splittedMeasurementRow) {
                elementName = measurementDataHeader[measurementDataHeaderElementCounter];
                elementNameAndElement[0] = elementName;
                elementNameAndElement[1] = splittedMeasurementRowElement;
                indexListOfElements = getIndexListForNewDatabase(elementNameAndElement, transitionalDatabase);
                
                if (!indexListOfElements.isEmpty()) {
                    System.out.println("IF");
                    System.out.println(transitionalDatabase);
                    transitionalDatabase = createDatabaseFromSelection(elementName, indexListOfElements, transitionalDatabase);
                    
                } else {
                    System.out.println("ELSE");
                    System.out.println(transitionalDatabase);
                    indexListOfElements = checkSideValues(elementNameAndElement, transitionalDatabase);
                    transitionalDatabase = createDatabaseFromSelection(elementName, indexListOfElements, transitionalDatabase);
                }
                //hiba itt, mert nincs olyan programresz, ami lekezelne azt az esetet, hogy elraktarozza az utolso nem nulla eredmenyt
                //futtatni ezt a kodot, megnezni a 7. eredmenyt es latni fogom
                
                latitude = transitionalDatabase.get(COORDINATES.LATITUDE.toString());
                longitude = transitionalDatabase.get(COORDINATES.LONGITUDE.toString());
                
                measurementDataHeaderElementCounter++;
                //itt kepbe johet az R, ugyanis szamitsa ki a koordinatak atlagat, ha az utolso elem illesztese utan egy tobbelemu tomb keletkezik
            }
            System.out.println(measurementDataRowCounter);
            System.out.println(transitionalDatabase);
            System.out.println("latitude: " + latitude);
            System.out.println("longitude: " + longitude);
            
        }
        System.out.println(coordinates);
        return coordinates;
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        
        String GIT_DIRECTORY = System.getenv("GIT_DIRECTORY") + "\\";
        String MEASUREMENT_DATA = "diploma_work\\test_dir\\measurement_data\\";
        String CONVERTED_DATA = "diploma_work\\test_dir\\converted_data\\";
        
        File veresegyhaz_bestserverDAT = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "veresegyhaz_bestserver.dat");
        File veresegyhaz_bestserverCONV = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyhaz_bestserver.conv");
        File veresegyhaz_bestserverCSV = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyhaz_bestserver.csv");
        File veresegyhaz_nthserverDAT = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "veresegyhaz_nthserver.dat");
        File veresegyhaz_nthserverCONV = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyhaz_nthserver.conv");
        File veresegyhaz_nthserverCSV = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyhaz_nthserver.csv");
        
        /*ConvertDatFile veresegyhaz_bestserver = new ConvertDatFile(veresegyhaz_bestserverDAT, veresegyhaz_bestserverCONV, veresegyhaz_bestserverCSV);
        ConvertDatFile veresegyhaz_nthserver = new ConvertDatFile(veresegyhaz_nthserverDAT, veresegyhaz_nthserverCONV, veresegyhaz_nthserverCSV);
        
        veresegyhaz_bestserver.convertDat2Csv();
        veresegyhaz_nthserver.convertDat2Csv();*/
        
        File gmon_gsm_veresegyhaz_1TXT = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "gmon_gsm_veresegyhaz_1.txt");
        File gmon_gsm_veresegyhaz_1CSV = new File(GIT_DIRECTORY + CONVERTED_DATA + "gmon_gsm_veresegyhaz_1.csv");
        File gmon_gsm_veresegyhaz_2TXT = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "gmon_gsm_veresegyhaz_2.txt");
        File gmon_gsm_veresegyhaz_2CSV = new File(GIT_DIRECTORY + CONVERTED_DATA + "gmon_gsm_veresegyhaz_2.csv");
        File gmon_gsm_budapestTXT = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "gmon_gsm_budapest.txt");
        File gmon_gsm_budapestCSV = new File(GIT_DIRECTORY + CONVERTED_DATA + "gmon_gsm_budapest.csv");
        File gmon_umts_budapestTXT = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "gmon_umts_budapest.txt");
        File gmon_umts_budapestCSV = new File(GIT_DIRECTORY + CONVERTED_DATA + "gmon_umts_budapest.csv");
        
        /*ConvertMeasurementFile gmon_gsm_veresegyhaz_1 = new ConvertMeasurementFile(gmon_gsm_veresegyhaz_1TXT, gmon_gsm_veresegyhaz_1CSV);
        ConvertMeasurementFile gmon_gsm_veresegyhaz_2 = new ConvertMeasurementFile(gmon_gsm_veresegyhaz_2TXT, gmon_gsm_veresegyhaz_2CSV);
        ConvertMeasurementFile gmon_gsm_budapest = new ConvertMeasurementFile(gmon_gsm_budapestTXT, gmon_gsm_budapestCSV);
        ConvertMeasurementFile gmon_umts_budapest = new ConvertMeasurementFile(gmon_umts_budapestTXT, gmon_umts_budapestCSV);
        
        gmon_gsm_veresegyhaz_1.convertMeasurement2Csv();
        gmon_gsm_veresegyhaz_2.convertMeasurement2Csv();
        gmon_gsm_budapest.convertMeasurement2Csv();
        gmon_umts_budapest.convertMeasurement2Csv();*/
        
        File veresegyhaz_bestserver_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyhaz_bestserver_created.csv");
        File veresegyhaz_nthserver_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyhaz_nthserver_created.csv");
        File veresegyhaz_1_gmon_gsm_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyhaz_1_gmon_gsm_created.csv");
        File veresegyhaz_2_gmon_gsm_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyhaz_2_gmon_gsm_created.csv");
        File budapest_gmon_gsm_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "budapest_gmon_gsm_created.csv");
        File budapest_gmon_umts_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "budapest_gmon_umts_created.csv");

        /*Tools.createTestMeasurementFile(3, veresegyhaz_bestserverCSV, veresegyhaz_bestserver_created, 3);
        Tools.createTestMeasurementFile(3, veresegyhaz_nthserverCSV, veresegyhaz_nthserver_created, 3);
        Tools.createTestMeasurementFile(3, gmon_gsm_veresegyhaz_1CSV, veresegyhaz_1_gmon_gsm_created, 3);
        Tools.createTestMeasurementFile(3, gmon_gsm_veresegyhaz_2CSV, veresegyhaz_2_gmon_gsm_created, 3);
        Tools.createTestMeasurementFile(3, gmon_gsm_budapestCSV, budapest_gmon_gsm_created, 3);
        Tools.createTestMeasurementFile(3, gmon_umts_budapestCSV, budapest_gmon_umts_created, 3);*/
        
        Localization newLocaction = new Localization(gmon_umts_budapestCSV, budapest_gmon_umts_created);
        
        Hashtable<String, ArrayList<String>> database = newLocaction.createDatabase();
        newLocaction.getLocationFromDatabase(database);
        
        
        
        
    }
}
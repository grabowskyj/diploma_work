package com.taylor.localization;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import com.taylor.measurement.ConvertMeasurementFile;
import com.taylor.simulation.ConvertDatFile;
import com.taylor.tools.Tools;

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
        String[] simulationDataHeader = null;
        ArrayList<String> data = Tools.readFileToMemory(databaseFile);
        if (!data.isEmpty()) {
            simulationDataHeader = data.get(0).trim().split(",");
            database = new Hashtable<String, ArrayList<String>>();
            for (int headerElement = 0; headerElement < simulationDataHeader.length; headerElement++) {
                database.put(simulationDataHeader[headerElement], new ArrayList<String>());
            }
            for (int simulationDataRowCounter = 1; simulationDataRowCounter < data.size(); simulationDataRowCounter++) {
                String[] splittedSimulationDataRow = data.get(simulationDataRowCounter).split(",");
                int simulationDataHeaderElementCounter = 0;
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
        srcDatabase.remove(indexListName);
        Enumeration<String> keys = srcDatabase.keys();
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
        String elementName = elementNameAndElement[0];
        String element = elementNameAndElement[1];
        ArrayList<String> dataset = database.get(elementName);
        for (int datasetElementCounter = 0; datasetElementCounter < dataset.size(); datasetElementCounter++) {
            if (element.equals(dataset.get(datasetElementCounter))) {
                indexListOfElement.add(datasetElementCounter);
            }
        }
        
        return indexListOfElement; 
    }
    
    private List<Integer> checkSideValues(String[] elementNameAndElement, Hashtable<String, ArrayList<String>> database) {
        List<Integer> indexListOfElement = new ArrayList<Integer>();
        List<Integer> positiveSideIndexList = new ArrayList<Integer>();
        List<Integer> negativeSideIndexList = new ArrayList<Integer>();
        String elementName = elementNameAndElement[0];
        int element = Integer.parseInt(elementNameAndElement[1]);
        ArrayList<String> dataset = database.get(elementName);
        boolean isValueFound = false;
        int checkRange = 10;
        int positiveRangeNumber = 0;
        int negativeRangeNumber = 0;
        while (isValueFound == false) {
            for (int range = 1; range <= checkRange; range++ ) {
                positiveRangeNumber = element + range;
                negativeRangeNumber = element - range;
                if (dataset.contains(Integer.toString(positiveRangeNumber))) {
                    int startIndex = dataset.indexOf(Integer.toString(positiveRangeNumber));
                    int endIndex = dataset.lastIndexOf(Integer.toString(positiveRangeNumber));
                    for (int index = startIndex; index <= endIndex; index++ ) {
                        if (positiveRangeNumber == Integer.parseInt(dataset.get(index))) {
                            positiveSideIndexList.add(index);
                        }
                    }
                }//pozitiv tartomany vegigjarasa jonak tunik, de azert meg ellenorizni, valamint negativ tartomany vegigjarasat megcsinalni
            }
        }
        
        
        return indexListOfElement;
    }
    
    private Hashtable<String, Double> getLocationFromDatabase(Hashtable<String, ArrayList<String>> database) {
        @SuppressWarnings("serial")
        Hashtable<String, Double> coordinates = new Hashtable<String, Double>() {{
            put("latitude", 0.0);
            put("longitude", 0.0);
        }};
        Hashtable<String, ArrayList<String>> transitionalDatabase = new Hashtable<String, ArrayList<String>>();
        List<Integer> indexListOfElements = new ArrayList<Integer>();
        List<String> latitude = new ArrayList<String>();
        List<String> longitude = new ArrayList<String>();
        ArrayList<String> measurement = Tools.readFileToMemory(measurementFile);
        String[] elementNameAndElement = new String[2];
        String[] splittedMeasurementRow = null;
        String[] measurementDataHeader = measurement.get(0).split(",");
        String elementName = null;
        for (int measurementDataRowCounter = 1; measurementDataRowCounter < measurement.size(); measurementDataRowCounter++) {
            splittedMeasurementRow = measurement.get(measurementDataRowCounter).split(",");
            int measurementDataHeaderElementCounter = 0;
            for (String splittedMeasurementRowElement : splittedMeasurementRow) {
                elementName = measurementDataHeader[measurementDataHeaderElementCounter];
                elementNameAndElement[0] = elementName;
                elementNameAndElement[1] = splittedMeasurementRowElement;
                indexListOfElements = getIndexListForNewDatabase(elementNameAndElement, database);
                if (!indexListOfElements.isEmpty()) {
                    transitionalDatabase = createDatabaseFromSelection(elementName, indexListOfElements, database);
                }
                if (indexListOfElements.isEmpty() && measurementDataHeaderElementCounter % 2 != 0) {
                    //indexListOfElements = checkSideValues(elementNameAndElement, database);
                }
                if (!transitionalDatabase.isEmpty()) {
                    database = transitionalDatabase;
                }
                latitude = database.get("latitude");
                longitude = database.get("longitude");
                System.out.println(latitude);
                System.out.println(longitude);
                measurementDataHeaderElementCounter++;
                System.exit(1);
            }
            
            
            
        }
        
        
        
        
        
        
        return coordinates;
    }

    public static void main(String[] args) {
        
        ////E PC files
        /*File veresegyhazBestDatFileOnE = new File("C:\\Users\\eptrszb\\git\\diploma_work\\test_dir\\measurement_data\\Veresegyhaz_bestserver.dat");
        File veresegyhazBestConvFileOnE = new File("C:\\Users\\eptrszb\\git\\diploma_work\\test_dir\\Veresegyhaz_bestserver.conv");
        File veresegyhazBestSimCsvFileOnE = new File("C:\\Users\\eptrszb\\git\\diploma_work\\test_dir\\converted_data\\Veresegyhaz_bestserver.csv");
        File veresegyhazNthDatFileOnE = new File("C:\\Users\\eptrszb\\git\\diploma_work\\test_dir\\measurement_data\\Veresegyhaz_nthserver.dat");
        File veresegyhazNthConvFileOnE = new File("C:\\Users\\eptrszb\\git\\diploma_work\\test_dir\\Veresegyhaz_nthserver.conv");
        File veresegyhazNthSimCsvFileOnE = new File("C:\\Users\\eptrszb\\git\\diploma_work\\test_dir\\converted_data\\Veresegyhaz_nthserver.csv");
        
        ConvertDatFile simVeresBest = new ConvertDatFile(veresegyhazBestDatFileOnE, veresegyhazBestConvFileOnE, veresegyhazBestSimCsvFileOnE);
        ConvertDatFile simVeresNth = new ConvertDatFile(veresegyhazNthDatFileOnE, veresegyhazNthConvFileOnE, veresegyhazNthSimCsvFileOnE);*/
        
        
        ////home PC files
        //File veresegyhazBestDatFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\Veresegyhaz_bestserver.dat");
        //File veresegyhazBestConvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\Veresegyhaz_bestserver.conv");
        //File veresegyhazBestSimCsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\Veresegyhaz_bestserver.csv");
        //File veresegyhazNthDatFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\Veresegyhaz_nthserver.dat");
        //File veresegyhazNthConvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\Veresegyhaz_nthserver.conv");
        //File veresegyhazNthSimCsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\Veresegyhaz_nthserver.csv");
        
        //ConvertDatFile simVeresBest = new ConvertDatFile(veresegyhazBestDatFile, veresegyhazBestConvFile, veresegyhazBestSimCsvFile);
        //ConvertDatFile simVeresNth = new ConvertDatFile(veresegyhazNthDatFile, veresegyhazNthConvFile, veresegyhazNthSimCsvFile);
        
        //simVeresBest.convertDat2Csv();
        //simVeresNth.convertDat2Csv();
        
        ////E PC files
        //File veresegyhazNo1MeasurementFileOnE = new File("C:\\Users\\eptrszb\\git\\diploma_work\\test_dir\\measurement_data\\gmon_gsm_veresegyhaz_1.txt");
        //File veresegyhazNo1MeasurementCsvFileOnE = new File("C:\\Users\\eptrszb\\git\\diploma_work\\test_dir\\converted_data\\gmon_gsm_veresegyhaz_1.csv");
        //File budapestMeasurementFileOnE = new File("C:\\Users\\eptrszb\\git\\diploma_work\\test_dir\\measurement_data\\gmon_gsm_budapest.txt");
        //File budapestMeasurementCsvFileOnE = new File("C:\\Users\\eptrszb\\git\\diploma_work\\test_dir\\converted_data\\gmon_gsm_budapest.csv");
        //File veresegyhazNo2MeasurementFileOnE = new File("C:\\Users\\eptrszb\\git\\diploma_work\\test_dir\\measurement_data\\gmon_gsm_veresegyhaz_2.txt");
        File veresegyhazNo2MeasurementCsvFileOnE = new File("C:\\Users\\eptrszb\\git\\diploma_work\\test_dir\\converted_data\\gmon_gsm_veresegyhaz_2.csv");
        
        //ConvertMeasurementFile measurementVeresegyhazNo1 = new ConvertMeasurementFile(veresegyhazNo1MeasurementFileOnE, veresegyhazNo1MeasurementCsvFileOnE);
        //ConvertMeasurementFile measurementBudapest = new ConvertMeasurementFile(budapestMeasurementFileOnE, budapestMeasurementCsvFileOnE);
        //ConvertMeasurementFile measurementVeresegyhazNo2 = new ConvertMeasurementFile(veresegyhazNo2MeasurementFileOnE, veresegyhazNo2MeasurementCsvFileOnE);
        
        ////home PC files
        //File veresegyhazNo1MeasurementFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\gmon_gsm_veresegyhaz_1.txt");
        //File veresegyhazNo1MeasurementCsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\gmon_gsm_veresegyhaz_1.csv");
        //File budapestMeasurementFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\gmon_gsm_budapest.txt");
        //File budapestMeasurementCsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\gmon_gsm_budapest.csv");
        //File veresegyhazNo2MeasurementFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\gmon_gsm_veresegyhaz_2.txt");
        //File veresegyhazNo2MeasurementCsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\gmon_gsm_veresegyhaz_2.csv");
        
        //ConvertMeasurementFile measurementVeresegyhazNo1 = new ConvertMeasurementFile(veresegyhazNo1MeasurementFile, veresegyhazNo1MeasurementCsvFile);
        //ConvertMeasurementFile measurementBudapest = new ConvertMeasurementFile(budapestMeasurementFile, budapestMeasurementCsvFile);
        //ConvertMeasurementFile measurementVeresegyhazNo2 = new ConvertMeasurementFile(veresegyhazNo2MeasurementFile, veresegyhazNo2MeasurementCsvFile);
        
        //measurementVeresegyhazNo1.convertMeasurement2Csv();
        //measurementBudapest.convertMeasurement2Csv();
        //measurementVeresegyhazNo2.convertMeasurement2Csv();
        
        ////E PC files
        //File createdMeasurementVeresegyhazBestOnE = new File("C:\\Users\\eptrszb\\git\\diploma_work\\test_dir\\converted_data\\veresegyhaza_bestserver_created_measurement.csv");
        //File createdMeasurementVeresegyhazNthOnE = new File("C:\\Users\\eptrszb\\git\\diploma_work\\test_dir\\converted_data\\veresegyhaza_nthserver_created_measurement.csv");
        //File createdMeasurementVeresegyhaz1GmonOnE = new File("C:\\Users\\eptrszb\\git\\diploma_work\\test_dir\\converted_data\\veresegyhaza_1_gmon_created_measurement.csv");
        File createdMeasurementVeresegyhaz2GmonOnE = new File("C:\\Users\\eptrszb\\git\\diploma_work\\test_dir\\converted_data\\veresegyhaza_2_gmon_created_measurement.csv");
        //File createdMeasurementBudapestGmonOnE = new File("C:\\Users\\eptrszb\\git\\diploma_work\\test_dir\\converted_data\\budapest_gmon_created_measurement.csv");

        //Tools.createTestMeasurementFile(3, veresegyhazBestSimCsvFileOnE, createdMeasurementVeresegyhazBestOnE);
        //Tools.createTestMeasurementFile(3, veresegyhazNthSimCsvFileOnE, createdMeasurementVeresegyhazNthOnE);
        //Tools.createTestMeasurementFile(3, veresegyhazNo1MeasurementCsvFileOnE, createdMeasurementVeresegyhaz1GmonOnE);
        //Tools.createTestMeasurementFile(3, budapestMeasurementCsvFileOnE, createdMeasurementBudapestGmonOnE);
        //Tools.createTestMeasurementFile(3, veresegyhazNo2MeasurementCsvFileOnE, createdMeasurementVeresegyhaz2GmonOnE);
        
        ////home PC files
        //File createdMeasurementVeresegyhazBest = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\veresegyhaza_bestserver_created_measurement.csv");
        //File createdMeasurementVeresegyhazNth = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\veresegyhaza_nthserver_created_measurement.csv");
        //File createdMeasurementVeresegyhaz1Gmon = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\veresegyhaza_1_gmon_created_measurement.csv");
        //File createdMeasurementVeresegyhaz2Gmon = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\veresegyhaza_2_gmon_created_measurement.csv");
        //File createdMeasurementBudapestGmon = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\budapest_gmon_created_measurement.csv");

        //Tools.createTestMeasurementFile(3, veresegyhazBestSimCsvFile, createdMeasurementVeresegyhazBest);
        //Tools.createTestMeasurementFile(3, veresegyhazNthSimCsvFile, createdMeasurementVeresegyhazNth);
        //Tools.createTestMeasurementFile(3, veresegyhazNo1MeasurementCsvFile, createdMeasurementVeresegyhaz1Gmon);
        //Tools.createTestMeasurementFile(3, budapestMeasurementCsvFile, createdMeasurementBudapestGmon);
        //Tools.createTestMeasurementFile(3, veresegyhazNo2MeasurementCsvFile, createdMeasurementVeresegyhaz2Gmon);
        
        Localization newLocaction = new Localization(veresegyhazNo2MeasurementCsvFileOnE, createdMeasurementVeresegyhaz2GmonOnE);
        //Localization newLocaction = new Localization(veresegyhazNo2MeasurementCsvFile, createdMeasurementVeresegyhaz2Gmon);
        
        Hashtable<String, ArrayList<String>> database = newLocaction.createDatabase();
        //System.out.println(database.get("latitude"));
        newLocaction.getLocationFromDatabase(database);
        
        
        
        
    }
}
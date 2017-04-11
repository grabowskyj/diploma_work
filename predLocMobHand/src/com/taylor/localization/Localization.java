package com.taylor.localization;

import java.io.File;
import java.util.ArrayList;
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
                        ((ArrayList<String>) database.get(simulationDataHeaderElement)).add(splittedSimulationDataRow[simulationDataHeaderElementCounter]);
                    } else {
                        ((ArrayList<String>) database.get(simulationDataHeaderElement)).add("0");
                    }
                    simulationDataHeaderElementCounter++;
                }
            }
        }
        
        return database;
    }
    
    private List<Integer> getIndexListForNewDatabase(LinkedList<String> elementNameAndElement, Hashtable<String, ArrayList<String>> database){
        List<Integer> indexListOfElement = new ArrayList<Integer>();
        String elementName = elementNameAndElement.getFirst();
        String element = elementNameAndElement.getLast();
        ArrayList<String> dataset = database.get(elementName);
        for (int datasetElementCounter = 0; datasetElementCounter < dataset.size(); datasetElementCounter++) {
            if (element.equals(dataset.get(datasetElementCounter))) {
                indexListOfElement.add(datasetElementCounter);
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
        List<Integer> indexListOfElements = new ArrayList<Integer>();
        ArrayList<String> measurement = Tools.readFileToMemory(measurementFile);
        LinkedList<String> elementNameAndElement = new LinkedList<String>();
        String[] splittedMeasurementRow = null;
        String[] measurementDataHeader = measurement.get(0).split(",");
        for (int measurementDataRowCounter = 1; measurementDataRowCounter < measurement.size(); measurementDataRowCounter++) {
            splittedMeasurementRow = measurement.get(measurementDataRowCounter).split(",");
            int measurementDataHeaderElementCounter = 0;
            for (String splittedMeasurementRowElement : splittedMeasurementRow) {
                elementNameAndElement.addFirst(measurementDataHeader[measurementDataHeaderElementCounter]);
                elementNameAndElement.add(splittedMeasurementRowElement);
                System.out.println(elementNameAndElement);
                indexListOfElements = getIndexListForNewDatabase(elementNameAndElement, database); //listaepitest ellenorizni, de elvileg ugy nez ki, hogy mukodik; a lista elemeibol kell egy uj adatbazist felepiteni
                System.out.println(indexListOfElements);
                elementNameAndElement.clear();
                measurementDataHeaderElementCounter++;
            }
            System.exit(1);
            
            
        }
        
        
        
        
        
        
        return coordinates;
    }

    public static void main(String[] args) {
        
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
        
        //File veresegyhazNo1MeasurementFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\gmon_gsm_veresegyhaz_1.txt");
        //File veresegyhazNo1MeasurementCsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\gmon_gsm_veresegyhaz_1.csv");
        //File budapestMeasurementFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\gmon_gsm_budapest.txt");
        //File budapestMeasurementCsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\gmon_gsm_budapest.csv");
        //File veresegyhazNo2MeasurementFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\gmon_gsm_veresegyhaz_2.txt");
        File veresegyhazNo2MeasurementCsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\gmon_gsm_veresegyhaz_2.csv");
        
        //ConvertMeasurementFile measurementVeresegyhazNo1 = new ConvertMeasurementFile(veresegyhazNo1MeasurementFile, veresegyhazNo1MeasurementCsvFile);
        //ConvertMeasurementFile measurementBudapest = new ConvertMeasurementFile(budapestMeasurementFile, budapestMeasurementCsvFile);
        //ConvertMeasurementFile measurementVeresegyhazNo2 = new ConvertMeasurementFile(veresegyhazNo2MeasurementFile, veresegyhazNo2MeasurementCsvFile);
        
        //measurementVeresegyhazNo1.convertMeasurement2Csv();
        //measurementBudapest.convertMeasurement2Csv();
        //measurementVeresegyhazNo2.convertMeasurement2Csv();
        
        //File createdMeasurementVeresegyhazBest = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\veresegyhaza_bestserver_created_measurement.csv");
        //File createdMeasurementVeresegyhazNth = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\veresegyhaza_nthserver_created_measurement.csv");
        //File createdMeasurementVeresegyhaz1Gmon = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\veresegyhaza_1_gmon_created_measurement.csv");
        File createdMeasurementVeresegyhaz2Gmon = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\veresegyhaza_2_gmon_created_measurement.csv");
        //File createdMeasurementBudapestGmon = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\budapest_gmon_created_measurement.csv");

        //Tools.createTestMeasurementFile(3, veresegyhazBestSimCsvFile, createdMeasurementVeresegyhazBest);
        //Tools.createTestMeasurementFile(3, veresegyhazNthSimCsvFile, createdMeasurementVeresegyhazNth);
        //Tools.createTestMeasurementFile(3, veresegyhazNo1MeasurementCsvFile, createdMeasurementVeresegyhaz1Gmon);
        //Tools.createTestMeasurementFile(3, budapestMeasurementCsvFile, createdMeasurementBudapestGmon);
        //Tools.createTestMeasurementFile(3, veresegyhazNo2MeasurementCsvFile, createdMeasurementVeresegyhaz2Gmon);
        
        Localization newLocaction = new Localization(veresegyhazNo2MeasurementCsvFile, createdMeasurementVeresegyhaz2Gmon);
        
        
        Hashtable<String, ArrayList<String>> database = newLocaction.createDatabase();
        //System.out.println(database.get("latitude"));
        newLocaction.getLocationFromDatabase(database);
        
        
        
        
    }
}
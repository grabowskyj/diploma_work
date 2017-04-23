package com.taylor.localization;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.rosuda.JRI.Rengine;

import com.taylor.measurement.ConvertMeasurementFile;
import com.taylor.measurement.LocalizationAnalysis;
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
    
    private Hashtable<String, ArrayList<String>> createDatabaseFromSelection(String indexListName, List<Integer> indexList, Hashtable<String, ArrayList<String>> srcDatabase) {
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
    
    private void getLocationFromDatabase(Hashtable<String, ArrayList<String>> database, File resultFile) {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        Hashtable<String, ArrayList<String>> transitionalDatabase = null; 
        Hashtable<String, ArrayList<String>> savedDatabase = null;
        Hashtable<String, Double> coordinates = new Hashtable<String, Double>();
        List<Integer> indexListOfElements = new ArrayList<Integer>();
        List<String> lstLatitude = new ArrayList<String>();
        List<String> lstLongitude = new ArrayList<String>();
        ArrayList<String> measurement = Tools.readFileToMemory(measurementFile);
        String[] elementNameAndElement = new String[2];
        String[] splittedMeasurementRow = null;
        String[] measurementDataHeader = measurement.get(0).split(",");
        String elementName = null;
        String rowToWrite = null;
        String pointLatitude = null;
        String pointLongitude = null;
        String resultFileHeader = null;
        int measurementDataHeaderElementCounter;
        
        Tools.createFile(resultFile);

        try {
            fileWriter = new FileWriter(resultFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            
            resultFileHeader = "point," + COORDINATES.LATITUDE.toString() + "," + COORDINATES.LONGITUDE.toString();
            bufferedWriter.write(resultFileHeader);
            bufferedWriter.newLine();
            
            Rengine rEngine = new Rengine(new String[] { "--no-save" }, false, null);
            
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

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        
        //hashmap-hashtable atirasokat megcsinlani
        
        String GIT_DIRECTORY = System.getenv("GIT_DIRECTORY") + "\\";
        String MEASUREMENT_DATA = "diploma_work\\test_dir\\measurement_data\\";
        String CONVERTED_DATA = "diploma_work\\test_dir\\converted_data\\";
        String RESULTS = "diploma_work\\test_dir\\measurement_data\\results\\";
        
        File veresegyhaz_bestserver_dat = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "veresegyhaz_bestserver.dat");
        File veresegyhaz_bestserver_conv = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyhaz_bestserver.conv");
        File veresegyhaz_bestserver_csv = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyhaz_bestserver.csv");
        File veresegyhaz_nthserver_dat = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "veresegyhaz_nthserver.dat");
        File veresegyhaz_nthserver_conv = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyhaz_nthserver.conv");
        File veresegyhaz_nthserver_csv = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyhaz_nthserver.csv");
        File veresegyh_5m_G900_nthserver_dat = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "veresegyh_5m_G900_nthserver.dat");
        File veresegyh_5m_G900_nthserver_conv = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyh_5m_G900_nthserver.conv");
        File veresegyh_5m_G900_nthserver_csv = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyh_5m_G900_nthserver.csv");
        File veresegyh_5m_G900_bestserver_dat = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "Veresegyh_5m_G900_v1_bestserver.dat");
        File veresegyh_5m_G900_bestserver_conv = new File(GIT_DIRECTORY + CONVERTED_DATA + "Veresegyh_5m_G900_v1_bestserver.conv");
        File veresegyh_5m_G900_bestserver_csv = new File(GIT_DIRECTORY + CONVERTED_DATA + "Veresegyh_5m_G900_v1_bestserver.csv");
                
        ConvertDatFile veresegyhaz_bestserver = new ConvertDatFile(veresegyhaz_bestserver_dat, veresegyhaz_bestserver_conv, veresegyhaz_bestserver_csv);
        ConvertDatFile veresegyhaz_nthserver = new ConvertDatFile(veresegyhaz_nthserver_dat, veresegyhaz_nthserver_conv, veresegyhaz_nthserver_csv);
        ConvertDatFile veresegyh_5m_G900_nthserver = new ConvertDatFile(veresegyh_5m_G900_nthserver_dat, veresegyh_5m_G900_nthserver_conv, veresegyh_5m_G900_nthserver_csv);
        ConvertDatFile veresegyh_5m_G900_bestserver = new ConvertDatFile(veresegyh_5m_G900_bestserver_dat, veresegyh_5m_G900_bestserver_conv, veresegyh_5m_G900_bestserver_csv);
        
        veresegyhaz_bestserver.convertDat2Csv();
        veresegyhaz_nthserver.convertDat2Csv();
        veresegyh_5m_G900_nthserver.convertDat2Csv();
        veresegyh_5m_G900_bestserver.convertDat2Csv();
        
        File gmon_gsm_veresegyhaz_1_txt = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "gmon_gsm_veresegyhaz_1.txt");
        File gmon_gsm_veresegyhaz_1_csv = new File(GIT_DIRECTORY + CONVERTED_DATA + "gmon_gsm_veresegyhaz_1.csv");
        File gmon_gsm_veresegyhaz_2_txt = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "gmon_gsm_veresegyhaz_2.txt");
        File gmon_gsm_veresegyhaz_2_csv = new File(GIT_DIRECTORY + CONVERTED_DATA + "gmon_gsm_veresegyhaz_2.csv");
        File gmon_gsm_budapestTXT = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "gmon_gsm_budapest.txt");
        File gmon_gsm_budapestCSV = new File(GIT_DIRECTORY + CONVERTED_DATA + "gmon_gsm_budapest.csv");
        File gmon_umts_budapestTXT = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "gmon_umts_budapest.txt");
        File gmon_umts_budapestCSV = new File(GIT_DIRECTORY + CONVERTED_DATA + "gmon_umts_budapest.csv");
        
        ConvertMeasurementFile gmon_gsm_veresegyhaz_1 = new ConvertMeasurementFile(gmon_gsm_veresegyhaz_1_txt, gmon_gsm_veresegyhaz_1_csv);
        ConvertMeasurementFile gmon_gsm_veresegyhaz_2 = new ConvertMeasurementFile(gmon_gsm_veresegyhaz_2_txt, gmon_gsm_veresegyhaz_2_csv);
        ConvertMeasurementFile gmon_gsm_budapest = new ConvertMeasurementFile(gmon_gsm_budapestTXT, gmon_gsm_budapestCSV);
        ConvertMeasurementFile gmon_umts_budapest = new ConvertMeasurementFile(gmon_umts_budapestTXT, gmon_umts_budapestCSV);
        
        gmon_gsm_veresegyhaz_1.convertMeasurement2Csv();
        gmon_gsm_veresegyhaz_2.convertMeasurement2Csv();
        gmon_gsm_budapest.convertMeasurement2Csv();
        gmon_umts_budapest.convertMeasurement2Csv();
        
        File veresegyhaz_1_gmon_gsm_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyhaz_1_gmon_gsm_created.csv");
        File veresegyhaz_2_gmon_gsm_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyhaz_2_gmon_gsm_created.csv");
        File budapest_gmon_gsm_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "budapest_gmon_gsm_created.csv");
        File budapest_gmon_umts_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "budapest_gmon_umts_created.csv");
        
        File checkFile_veresegyhaz_1_gmon_gsm_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "checkFile_veresegyhaz_1_gmon_gsm_created.csv");
        File checkFile_veresegyhaz_2_gmon_gsm_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "checkFile_veresegyhaz_2_gmon_gsm_created.csv");
        File checkFile_budapest_gmon_gsm_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "checkFile_budapest_gmon_gsm_created.csv");
        File checkFile_budapest_gmon_umts_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "checkFile_budapest_gmon_umts_created.csv");

        //Tools.createTestMeasurementFile(3, gmon_gsm_veresegyhaz_1_csv, veresegyhaz_1_gmon_gsm_created, checkFile_veresegyhaz_1_gmon_gsm_created, 3);
        //Tools.createTestMeasurementFile(3, gmon_gsm_veresegyhaz_2_csv, veresegyhaz_2_gmon_gsm_created, checkFile_veresegyhaz_2_gmon_gsm_created, 3);
        //Tools.createTestMeasurementFile(3, gmon_gsm_budapestCSV, budapest_gmon_gsm_created, checkFile_budapest_gmon_gsm_created, 3);
        //Tools.createTestMeasurementFile(3, gmon_umts_budapestCSV, budapest_gmon_umts_created, checkFile_budapest_gmon_umts_created, 3);
        
        File localization_results = new File (GIT_DIRECTORY + RESULTS + "localization_results.csv");
        File localization_error_results = new File(GIT_DIRECTORY + RESULTS + "localization_error_results.csv");
        
        //Localization newLocaction = new Localization(veresegyh_5m_G900_nthserver_csv, gmon_gsm_veresegyhaz_1_csv);
        
        //Hashtable<String, ArrayList<String>> database = newLocaction.createDatabase();
        //newLocaction.getLocationFromDatabase(database, localization_results);
        
        //LocalizationAnalysis.calculateErrorDistance(localization_results, checkFile_veresegyhaz_2_gmon_gsm_created, localization_error_results);
        
        //System.out.println("CERP 95%: " + LocalizationAnalysis.calculateCERP(95, localization_error_results));
        //System.out.println("CERP 67%: " + LocalizationAnalysis.calculateCERP(67, localization_error_results));
        
        
        
        
    }
}
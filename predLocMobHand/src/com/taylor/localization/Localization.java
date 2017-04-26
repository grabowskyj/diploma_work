package com.taylor.localization;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.rosuda.JRI.Rengine;

import com.taylor.measurement.ConvertMeasurementFile;
import com.taylor.measurement.LocalizationAnalysis;
import com.taylor.simulation.ConvertDatFile;
import com.taylor.tools.Tools;
import com.taylor.tools.Tools.COORDINATES;
import com.taylor.tools.Tools.DATATYPE;

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

    private HashMap<String, ArrayList<String>> createDatabaseForVerticalSearch() {
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
    
    private void getLocationFromDatabaseV1(HashMap<String, ArrayList<String>> database, File resultFile) {
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
    
    private HashMap<String, Integer> calculateSignalStrengthDifference(HashMap<String, Integer> minuedMap, HashMap<String, Integer> subtrahendMap) {
        HashMap<String, Integer> result = null;
        Set<Entry<String, Integer>> minuedEntrySet = null;
        List<Entry<String, Integer>> minuedEntries = null;
        int minued = 0;
        int subtrahend = 0;
        int difference = 0;
        
        result = new LinkedHashMap<String, Integer>();
        minuedEntrySet = minuedMap.entrySet();
        minuedEntries = new LinkedList<>(minuedEntrySet);
        
        for (Entry<String, Integer> entry : minuedEntries) {
            minued = entry.getValue();
            subtrahend = subtrahendMap.get(entry.getKey());
            difference = Math.abs(minued - subtrahend);
            
            result.put(entry.getKey(), difference);
        }
        
        return result;
    }
    
    private HashMap<String, Double> checkMeasurementPointInDatabase(String[] databaseEntry, String[] measurementPoint) {
        //ez a metodus adja vissza a ket pont kozotti tavolsagot(ezt egy hashmapben, kulcs ertekkel) es a hozza tartozo koordinatat(ez is lehet kulcs ertek a koordinatakkal)
        HashMap<String, Integer> databaseMap = null;
        HashMap<String, Integer> measurementMap = null;
        HashMap<String, Double> result = null;
        HashMap<String, Integer> offsetShiftedMeasurementPoint = null;
        HashMap<String, Integer> signalStrengthDistances = null;
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
        ArrayList<Entry<String, Integer>> measurementEntries = null;
        Entry<String, Integer> firstEntry = null;
        
        databaseMap = new HashMap<String, Integer>();
        measurementMap = new HashMap<String, Integer>();
        signalStrengthDistances = new HashMap<String, Integer>();
        coordinates = new ArrayList<String>();
        decoordinatedDatabaseEntry = new ArrayList<String>();
        lstDatabaseEntry = Arrays.asList(databaseEntry);
        lstMeasurementPoint = Arrays.asList(measurementPoint);
        coordinates = getCoordinatesFromList(lstDatabaseEntry).get(0);
        decoordinatedDatabaseEntry = getCoordinatesFromList(lstDatabaseEntry).get(1);

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
        
        System.out.println(measurementMap);
        System.out.println(databaseMap);
        
        measurementEntries = new ArrayList<Entry<String, Integer>>(measurementMap.entrySet());
        firstEntry = measurementEntries.get(0);
        offset = databaseMap.get(firstEntry.getKey()) - measurementMap.get(firstEntry.getKey());
        offsetShiftedMeasurementPoint = shiftDataValuesWithOffset(measurementMap, offset);
        signalStrengthDistances = calculateSignalStrengthDifference(offsetShiftedMeasurementPoint, databaseMap);
        
        System.out.println(offsetShiftedMeasurementPoint);
        System.out.println(signalStrengthDistances);
        System.out.println();
        
        //kimenetet leformazni koordinatakbol es a signalStrength tavolsagbol
        
        
        
        
        
        
        return result;
    }
    
    private void getLocationFromDatabaseV2(File resultFile) {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        HashMap<String, Double> coordinates = null;
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
        Rengine rEngine = null;
        
        coordinates = new HashMap<String, Double>();
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
            
            rEngine = new Rengine(new String[] { "--no-save" }, false, null);
            
            for (int measurementDataRowCounter = 1; measurementDataRowCounter < measurement.size(); measurementDataRowCounter++) {
                measurementPoint = measurement.get(measurementDataRowCounter).split(",");
                
                
                for (int databaseDataRowCounter = 1; databaseDataRowCounter < database.size(); databaseDataRowCounter++) {
                    databaseEntry = database.get(databaseDataRowCounter).split(",");
                    
                    checkMeasurementPointInDatabase(databaseEntry, measurementPoint);
                    
                    if (databaseDataRowCounter == 5) {
                        System.exit(1);
                    }
                    
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
        File alle_5m_G900_nthserver_dat = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "Alle_5m_G900_nthserver.dat");
        File alle_5m_G900_nthserver_conv = new File(GIT_DIRECTORY + CONVERTED_DATA + "Alle_5m_G900_nthserver.conv");
        File alle_5m_G900_nthserver_csv = new File(GIT_DIRECTORY + CONVERTED_DATA + "Alle_5m_G900_nthserver.csv");
        File veresegyh_5m_DCS_10th_nthserver_dat = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "Veresegyh_5m_DCS_10th_nthserver.dat");
        File veresegyh_5m_DCS_10th_nthserver_conv = new File(GIT_DIRECTORY + CONVERTED_DATA + "Veresegyh_5m_DCS_10th_nthserver.conv");
        File veresegyh_5m_DCS_10th_nthserver_csv = new File(GIT_DIRECTORY + CONVERTED_DATA + "Veresegyh_5m_DCS_10th_nthserver.csv");
                
        ConvertDatFile veresegyhaz_bestserver = new ConvertDatFile(veresegyhaz_bestserver_dat, veresegyhaz_bestserver_conv, veresegyhaz_bestserver_csv);
        ConvertDatFile veresegyhaz_nthserver = new ConvertDatFile(veresegyhaz_nthserver_dat, veresegyhaz_nthserver_conv, veresegyhaz_nthserver_csv);
        ConvertDatFile veresegyh_5m_G900_nthserver = new ConvertDatFile(veresegyh_5m_G900_nthserver_dat, veresegyh_5m_G900_nthserver_conv, veresegyh_5m_G900_nthserver_csv);
        ConvertDatFile veresegyh_5m_G900_bestserver = new ConvertDatFile(veresegyh_5m_G900_bestserver_dat, veresegyh_5m_G900_bestserver_conv, veresegyh_5m_G900_bestserver_csv);
        ConvertDatFile alle_5m_G900_nthserver = new ConvertDatFile(alle_5m_G900_nthserver_dat, alle_5m_G900_nthserver_conv, alle_5m_G900_nthserver_csv);
        ConvertDatFile veresegyh_5m_DCS_10th_nthserver = new ConvertDatFile(veresegyh_5m_DCS_10th_nthserver_dat, veresegyh_5m_DCS_10th_nthserver_conv, veresegyh_5m_DCS_10th_nthserver_csv);
        
        veresegyhaz_bestserver.convertDat2Csv();
        veresegyhaz_nthserver.convertDat2Csv();
        veresegyh_5m_G900_nthserver.convertDat2Csv();
        veresegyh_5m_G900_bestserver.convertDat2Csv();
        alle_5m_G900_nthserver.convertDat2Csv();
        veresegyh_5m_DCS_10th_nthserver.convertDat2Csv();
        
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
        
        Tools.decoordinateMeasurementFile(gmon_gsm_veresegyhaz_2_csv, veresegyhaz_2_gmon_gsm_created, checkFile_veresegyhaz_2_gmon_gsm_created);
        
        File localization_results = new File (GIT_DIRECTORY + RESULTS + "localization_results.csv");
        File localization_error_results = new File(GIT_DIRECTORY + RESULTS + "localization_error_results.csv");
        
        Localization newLocaction = new Localization(veresegyh_5m_G900_nthserver_csv, veresegyhaz_2_gmon_gsm_created);
        
        //HashMap<String, ArrayList<String>> database = newLocaction.createDatabaseForVerticalSearch();
        //newLocaction.getLocationFromDatabaseV1(database, localization_results);
        
        newLocaction.getLocationFromDatabaseV2(localization_results);
        
        //LocalizationAnalysis.calculateDistanceError(localization_results, checkFile_veresegyhaz_2_gmon_gsm_created, localization_error_results);
        
        //System.out.println("CERP 95%: " + LocalizationAnalysis.calculateCERP(95, localization_error_results));
        //System.out.println("CERP 67%: " + LocalizationAnalysis.calculateCERP(67, localization_error_results));
        
        
        
        
    }
}
package com.taylor.localization;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import com.taylor.measurement.ConvertMeasurementFile;
import com.taylor.simulation.ConvertDatFile;
import com.taylor.tools.Tools;

public class Localization {
    
    private File dataBaseFile;
    private File measurementFile;
    private ArrayList<String> simulationData;
    private ArrayList<String> measurementData;
    
    public Localization(File dataBaseFile, File measurementFile) {
        this.setDataBaseFile(dataBaseFile);
        this.setMeasurementFile(measurementFile);
    }

    public File getDataBaseFile() {
        return dataBaseFile;
    }

    private void setDataBaseFile(File dataBaseFile) {
        this.dataBaseFile = dataBaseFile;
    }

    public File getMeasurementFile() {
        return measurementFile;
    }

    private void setMeasurementFile(File measurementFile) {
        this.measurementFile = measurementFile;
    }
    
    private boolean readFilesToMemory(File simulationFile, File measurementFile) {
        boolean areFilesReaded = false;
        simulationData = Tools.readFileToMemory(simulationFile);
        measurementData = Tools.readFileToMemory(measurementFile);
        if (!simulationData.isEmpty() && !measurementData.isEmpty()) {
            areFilesReaded = true;
        }
        
        return areFilesReaded;  
    }
    
    private Hashtable<String, ArrayList<String>> createDataBase() {
        Hashtable<String, ArrayList<String>> dataBase = null;
        String[] simulationDataHeader = null;
        boolean isReadToMemorySuccess = readFilesToMemory(dataBaseFile, measurementFile);
        if (isReadToMemorySuccess == true) {
            simulationDataHeader = simulationData.get(0).trim().split(",");
            dataBase = new Hashtable<String, ArrayList<String>>();
            for (int headerElement = 0; headerElement < simulationDataHeader.length; headerElement++) {
                dataBase.put(simulationDataHeader[headerElement], new ArrayList<String>());
            }
            for (int simulationDataRowCounter = 1; simulationDataRowCounter < simulationData.size(); simulationDataRowCounter++) {
                String[] splittedSimulationDataRow = simulationData.get(simulationDataRowCounter).split(",");
                int simulationDataHeaderElementCounter = 0;
                for (String simulationDataHeaderElement : simulationDataHeader) {
                    if (simulationDataHeaderElementCounter < splittedSimulationDataRow.length) {
                        ((ArrayList<String>) dataBase.get(simulationDataHeaderElement)).add(splittedSimulationDataRow[simulationDataHeaderElementCounter]);
                    } else {
                        ((ArrayList<String>) dataBase.get(simulationDataHeaderElement)).add("0");
                    }
                    simulationDataHeaderElementCounter++;
                }
            }
        }
        
        return dataBase;
    }

    public static void main(String[] args) {
        
        File veresegyhazBestDatFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\Veresegyhaz_bestserver.dat");
        File veresegyhazBestConvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\Veresegyhaz_bestserver.conv");
        File veresegyhazBestSimCsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\Veresegyhaz_bestserver.csv");
        File veresegyhazNthDatFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\Veresegyhaz_nthserver.dat");
        File veresegyhazNthConvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\Veresegyhaz_nthserver.conv");
        File veresegyhazNthSimCsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\Veresegyhaz_nthserver.csv");
        
        ConvertDatFile simVeresBest = new ConvertDatFile(veresegyhazBestDatFile, veresegyhazBestConvFile, veresegyhazBestSimCsvFile);
        ConvertDatFile simVeresNth = new ConvertDatFile(veresegyhazNthDatFile, veresegyhazNthConvFile, veresegyhazNthSimCsvFile);
        
        simVeresBest.convertDat2Csv();
        simVeresNth.convertDat2Csv();
        
        File veresegyhazNo1MeasurementFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\gmon_gsm_veresegyhaz_1.txt");
        File veresegyhazNo1MeasurementCsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\gmon_gsm_veresegyhaz_1.csv");
        File budapestMeasurementFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\gmon_gsm_budapest.txt");
        File budapestMeasurementCsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\gmon_gsm_budapest.csv");
        File veresegyhazNo2MeasurementFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\gmon_gsm_veresegyhaz_2.txt");
        File veresegyhazNo2MeasurementCsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\gmon_gsm_veresegyhaz_2.csv");
        
        ConvertMeasurementFile measurementVeresegyhazNo1 = new ConvertMeasurementFile(veresegyhazNo1MeasurementFile, veresegyhazNo1MeasurementCsvFile);
        ConvertMeasurementFile measurementBudapest = new ConvertMeasurementFile(budapestMeasurementFile, budapestMeasurementCsvFile);
        ConvertMeasurementFile measurementVeresegyhazNo2 = new ConvertMeasurementFile(veresegyhazNo2MeasurementFile, veresegyhazNo2MeasurementCsvFile);
        
        measurementVeresegyhazNo1.convertMeasurement2Csv();
        measurementBudapest.convertMeasurement2Csv();
        measurementVeresegyhazNo2.convertMeasurement2Csv();
        
        File createdMeasurementVeresegyhazBest = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\veresegyhaza_bestserver_created_measurement.csv");
        File createdMeasurementVeresegyhazNth = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\veresegyhaza_nthserver_created_measurement.csv");
        File createdMeasurementVeresegyhaz1Gmon = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\veresegyhaza_1_gmon_created_measurement.csv");
        File createdMeasurementVeresegyhaz2Gmon = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\veresegyhaza_2_gmon_created_measurement.csv");
        File createdMeasurementBudapestGmon = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\converted_data\\budapest_gmon_created_measurement.csv");

        Tools.createTestMeasurementFile(3, veresegyhazBestSimCsvFile, createdMeasurementVeresegyhazBest);
        Tools.createTestMeasurementFile(3, veresegyhazNthSimCsvFile, createdMeasurementVeresegyhazNth);
        Tools.createTestMeasurementFile(3, veresegyhazNo1MeasurementCsvFile, createdMeasurementVeresegyhaz1Gmon);
        Tools.createTestMeasurementFile(3, budapestMeasurementCsvFile, createdMeasurementBudapestGmon);
        Tools.createTestMeasurementFile(3, veresegyhazNo2MeasurementCsvFile, createdMeasurementVeresegyhaz2Gmon);
        
        Localization newLocaction = new Localization(veresegyhazNo2MeasurementCsvFile, createdMeasurementVeresegyhaz2Gmon);
        
        
        Hashtable<String, ArrayList<String>> dataBase = newLocaction.createDataBase();
        System.out.println(dataBase.get("latitude"));
        
        
        
    }
}
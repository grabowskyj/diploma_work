package com.taylor.localization;

import java.io.File;

import com.taylor.measurement.ConvertMeasurementFile;
import com.taylor.simulation.ConvertDatFile;
import com.taylor.tools.Tools;

public class Localization {
    
    public static void main(String[] args) {
        
        /*File datFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\Veresegyhaz_bestserver_bestserver.dat");
        File convFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\veresegyhaz_25m_bestserver.conv");
        File simCsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\veresegyhaz_25m_bestserver.csv");
        File dat2File = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\Veresegyhaz_bestserver_nthserver.dat");
        File conv2File = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\Veresegyhaz_bestserver_nthserver.conv");
        File sim2CsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\Veresegyhaz_bestserver_nthserver.csv");
        
        ConvertDatFile sim = new ConvertDatFile(datFile, convFile, simCsvFile);
        ConvertDatFile sim2 = new ConvertDatFile(dat2File, conv2File, sim2CsvFile);
        
        sim.convertDat2Csv();
        sim2.convertDat2Csv();
        
        File measurementFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\gmon_gsm_rxl_2017_03_25_08_08_06_veresegyhaz.txt");
        File measurementCsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\gmon_gsm_rxl_2017_03_25_08_08_06_veresegyhaz.csv");
        File measurement2File = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\gmon_gsm_rxl_2017_04_01_15_13_03_budapest.txt");
        File measurement2CsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\gmon_gsm_rxl_2017_04_01_15_13_03_budapest.csv");
        File measurement3File = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\gmon_gsm_rxl_2017_03_25_08_49_11_veresegyhaz.txt");
        File measurement3CsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\gmon_gsm_rxl_2017_03_25_08_49_11_veresegyhaz.csv");
        
        File createdMeasurement = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\created_measurement.csv");
        File createdMeasurement2 = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\created_measurement2.csv");
        File createdMeasurement3 = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\created_measurement3.csv");
        File createdMeasurement4 = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\created_measurement4.csv");
        File createdMeasurement5 = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\created_measurement5.csv");
        
        ConvertMeasurementFile measurement = new ConvertMeasurementFile(measurementFile, measurementCsvFile);
        ConvertMeasurementFile measurement2 = new ConvertMeasurementFile(measurement2File, measurement2CsvFile);
        ConvertMeasurementFile measurement3 = new ConvertMeasurementFile(measurement3File, measurement3CsvFile);
        
        measurement.convertMeasurement2Csv();
        measurement2.convertMeasurement2Csv();
        measurement3.convertMeasurement2Csv();
        
        Tools.createTestMeasurementFile(3, simCsvFile, createdMeasurement);
        Tools.createTestMeasurementFile(3, sim2CsvFile, createdMeasurement2);
        Tools.createTestMeasurementFile(3, measurementCsvFile, createdMeasurement3);
        Tools.createTestMeasurementFile(3, measurement2CsvFile, createdMeasurement4);
        Tools.createTestMeasurementFile(3, measurement3CsvFile, createdMeasurement5);*/
    }
}
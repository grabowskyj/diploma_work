package com.taylor.localization;

import java.io.File;

import com.taylor.measurement.ConvertMeasurementFile;
import com.taylor.simulation.ConvertDatFile;

public class Localization {
    
    public static void main(String[] args) {
        
        File datFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\Veresegyhaz_bestserver_bestserver.dat");
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
        
        ConvertMeasurementFile measurement = new ConvertMeasurementFile(measurementFile, measurementCsvFile);
        ConvertMeasurementFile measurement2 = new ConvertMeasurementFile(measurement2File, measurement2CsvFile);
        
        measurement.convertMeasurement2Csv();
        measurement2.convertMeasurement2Csv();
    }
}
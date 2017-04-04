package com.taylor.localization;

import java.io.File;

import com.taylor.simulation.ConvertDatFile;
import com.taylor.tools.Tools;

import org.gdal.osr.*;

public class Localization {
    
    public static void main(String[] args) {
        
        /*File datFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\Veresegyhaz_bestserver_bestserver.dat");
        File convFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\veresegyhaz_25m_bestserver.conv");
        File simCsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\veresegyhaz_25m_bestserver.csv");
        File dat2File = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\sample_25m_bestserver.dat");
        File conv2File = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\sample_25m_bestserver.conv");
        File sim2CsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\sample_25m_bestserver.csv");
        File dat3File = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\Veresegyhaz_bestserver_nthserver.dat");
        File conv3File = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\Veresegyhaz_bestserver_nthserver.conv");
        File sim3CsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\Veresegyhaz_bestserver_nthserver.csv");
        
        File measurementFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\gmon_gsm_rxl_2017_03_25_08_08_06_veresegyhaz.txt");
        File measurementCsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\gmon_gsm_rxl_2017_03_25_08_08_06_veresegyhaz.csv");
        
        ConvertDatFile sim = new ConvertDatFile(datFile, convFile, simCsvFile);
        ConvertDatFile sim2 = new ConvertDatFile(dat2File, conv2File, sim2CsvFile);
        ConvertDatFile sim3 = new ConvertDatFile(dat3File, conv3File, sim3CsvFile);
        ConvertMeasurementFile measurement = new ConvertMeasurementFile(measurementFile, measurementCsvFile);
        
        sim.convertDat2Csv();
        sim2.convertDat2Csv();
        sim3.convertDat2Csv();
        
        measurement.convertMeasurement2Csv();*/
        
        double[] transformToEov = Tools.wgs84ToHd72Eov(47.66645, 19.26039);
        double[] transformToWsg84 = Tools.hd72EovToWgs84(880775, 343725);
        
      //kimeneteket helyesseget ellenorizni
        System.out.println("EOV Coordinate lat: " + transformToEov[0]);
        System.out.println("EOV Coordinate long: " + transformToEov[1]);
        
      //kimeneteket helyesseget ellenorizni
        System.out.println("WSG84 Coordinate lat: " + transformToWsg84[0]);
        System.out.println("WSG84 Coordinate long: " + transformToWsg84[1]);
        
        
    }
}
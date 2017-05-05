package com.taylor.localization;

import java.io.File;

import com.taylor.measurement.ConvertMeasurementFile;
import com.taylor.simulation.ConvertDatFile;
import com.taylor.tools.Tools;

public class GenerateFiles {
    
    public static final String GIT_DIRECTORY = System.getenv("GIT_DIRECTORY") + "\\";
    public static final String MEASUREMENT_DATA = "diploma_work\\test_dir\\measurement_data\\";
    public static final String CONVERTED_DATA = "diploma_work\\test_dir\\converted_data\\";
    public static final String RESULTS = "diploma_work\\test_dir\\measurement_data\\results\\";
    public static final String DECOORDINATED_MEASUREMENT_DATA = "diploma_work\\test_dir\\measurement_data\\decoordinated_data\\";
    
    private static File alle_5m_DCS_nthserver_dat = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "Alle_5m_DCS_nthserver.dat");
    private static File alle_5m_DCS_nthserver_conv = new File(GIT_DIRECTORY + CONVERTED_DATA + "Alle_5m_DCS_nthserver.conv");
    private static File alle_5m_G900_nthserver_dat = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "Alle_5m_G900_nthserver.dat");
    private static File alle_5m_G900_nthserver_conv = new File(GIT_DIRECTORY + CONVERTED_DATA + "Alle_5m_G900_nthserver.conv");
    private static File veresegyh_5m_G900_nthserver_dat = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "veresegyh_5m_G900_nthserver.dat");
    private static File veresegyh_5m_G900_nthserver_conv = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyh_5m_G900_nthserver.conv");
    private static File veresegyh_5m_DCS_nthserver_dat = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "Veresegyh_5m_DCS_nthserver.dat");
    private static File veresegyh_5m_DCS_nthserver_conv = new File(GIT_DIRECTORY + CONVERTED_DATA + "Veresegyh_5m_DCS_nthserver.conv");
    private static File gmon_gsm_veresegyhaz_1TXT = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "gmon_gsm_veresegyhaz_1.txt");
    private static File gmon_gsm_veresegyhaz_2TXT = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "gmon_gsm_veresegyhaz_2.txt");
    private static File gmon_gsm_budapestTXT = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "gmon_gsm_budapest.txt");
    private static File gmon_umts_budapestTXT = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "gmon_umts_budapest.txt");
    private static File gmon_umts_veresegyhazTXT = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "gmon_umts_veresegyhaz.txt");
    
    private static File alle_5m_G900_nthserver_csv = new File(GIT_DIRECTORY + CONVERTED_DATA + "Alle_5m_G900_nthserver.csv");
    private static File alle_5m_DCS_nthserver_csv = new File(GIT_DIRECTORY + CONVERTED_DATA + "Alle_5m_DCS_nthserver.csv");
    private static File veresegyh_5m_G900_nthserver_csv = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyh_5m_G900_nthserver.csv");
    private static File veresegyh_5m_DCS_nthserver_csv = new File(GIT_DIRECTORY + CONVERTED_DATA + "Veresegyh_5m_DCS_nthserver.csv");
    
    public static File budapest_5m_G900_DCS_database = new File(GIT_DIRECTORY + CONVERTED_DATA + "budapest_5m_G900_DCS_nthserver.csv");
    public static File veresegyh_5m_G900_DCS_database = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyh_5m_G900_DCS_nthserver.csv");
    public static File gmon_gsm_veresegyhaz_1_csv = new File(GIT_DIRECTORY + CONVERTED_DATA + "gmon_gsm_veresegyhaz_1.csv");
    public static File gmon_gsm_veresegyhaz_2_csv = new File(GIT_DIRECTORY + CONVERTED_DATA + "gmon_gsm_veresegyhaz_2.csv");
    public static File gmon_umts_veresegyhazCSV = new File(GIT_DIRECTORY + CONVERTED_DATA + "gmon_umts_veresegyhaz.csv");
    public static File gmon_gsm_budapestCSV = new File(GIT_DIRECTORY + CONVERTED_DATA + "gmon_gsm_budapest.csv");
    public static File gmon_umts_budapestCSV = new File(GIT_DIRECTORY + CONVERTED_DATA + "gmon_umts_budapest.csv");
    public static File gmon_database_veresegyhaz = new File(GIT_DIRECTORY + CONVERTED_DATA + "gmon_database_veresegyhaz.csv");
    
    public static void generateDatabase() {

        
        ConvertDatFile alle_5m_G900_nthserver = new ConvertDatFile(alle_5m_G900_nthserver_dat, alle_5m_G900_nthserver_conv, alle_5m_G900_nthserver_csv);
        ConvertDatFile alle_5m_DCS_nthserver = new ConvertDatFile(alle_5m_DCS_nthserver_dat, alle_5m_DCS_nthserver_conv, alle_5m_DCS_nthserver_csv);
        ConvertDatFile veresegyh_5m_G900_nthserver = new ConvertDatFile(veresegyh_5m_G900_nthserver_dat, veresegyh_5m_G900_nthserver_conv, veresegyh_5m_G900_nthserver_csv);
        ConvertDatFile veresegyh_5m_DCS_nthserver = new ConvertDatFile(veresegyh_5m_DCS_nthserver_dat, veresegyh_5m_DCS_nthserver_conv, veresegyh_5m_DCS_nthserver_csv);

        alle_5m_G900_nthserver.convertDat2Csv();
        alle_5m_DCS_nthserver.convertDat2Csv();    
        veresegyh_5m_G900_nthserver.convertDat2Csv();
        veresegyh_5m_DCS_nthserver.convertDat2Csv();
        
        Tools.meltGsmDcs(alle_5m_G900_nthserver_csv, alle_5m_DCS_nthserver_csv, budapest_5m_G900_DCS_database);
        Tools.meltGsmDcs(veresegyh_5m_G900_nthserver_csv, veresegyh_5m_DCS_nthserver_csv, veresegyh_5m_G900_DCS_database);
        alle_5m_G900_nthserver_csv.delete();
        alle_5m_DCS_nthserver_csv.delete();
        veresegyh_5m_G900_nthserver_csv.delete();
        veresegyh_5m_DCS_nthserver_csv.delete();
    }
    
    public static void generateMeasurements() {

        ConvertMeasurementFile gmon_gsm_veresegyhaz_1 = new ConvertMeasurementFile(gmon_gsm_veresegyhaz_1TXT, gmon_gsm_veresegyhaz_1_csv);
        ConvertMeasurementFile gmon_gsm_veresegyhaz_2 = new ConvertMeasurementFile(gmon_gsm_veresegyhaz_2TXT, gmon_gsm_veresegyhaz_2_csv);
        ConvertMeasurementFile gmon_umts_veresegyhaz = new ConvertMeasurementFile(gmon_umts_veresegyhazTXT, gmon_umts_veresegyhazCSV);
        ConvertMeasurementFile gmon_gsm_budapest = new ConvertMeasurementFile(gmon_gsm_budapestTXT, gmon_gsm_budapestCSV);
        ConvertMeasurementFile gmon_umts_budapest = new ConvertMeasurementFile(gmon_umts_budapestTXT, gmon_umts_budapestCSV);        
        
        gmon_gsm_veresegyhaz_1.convertMeasurement2Csv();
        gmon_gsm_veresegyhaz_2.convertMeasurement2Csv();
        gmon_umts_veresegyhaz.convertMeasurement2Csv();
        gmon_gsm_budapest.convertMeasurement2Csv();
        gmon_umts_budapest.convertMeasurement2Csv();
        
        Tools.createDatabaseFromMeasurements(new File[]{gmon_gsm_veresegyhaz_1_csv, gmon_gsm_veresegyhaz_2_csv}, gmon_database_veresegyhaz);
    }
    
}
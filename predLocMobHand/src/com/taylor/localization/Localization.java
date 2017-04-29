package com.taylor.localization;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.taylor.measurement.ConvertMeasurementFile;
import com.taylor.measurement.LocalizationAnalysis;
import com.taylor.simulation.ConvertDatFile;
import com.taylor.localization.HorizontalSearchMethod;
import com.taylor.tools.Tools;

public class Localization {
    /*
     * need to set -Xmx4096m in VM Arguments
     */
    
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        
        String GIT_DIRECTORY = System.getenv("GIT_DIRECTORY") + "\\";
        String MEASUREMENT_DATA = "diploma_work\\test_dir\\measurement_data\\";
        String CONVERTED_DATA = "diploma_work\\test_dir\\converted_data\\";
        String RESULTS = "diploma_work\\test_dir\\measurement_data\\results\\";
               
        File veresegyh_5m_G900_nthserver_dat = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "veresegyh_5m_G900_nthserver.dat");
        File veresegyh_5m_G900_nthserver_conv = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyh_5m_G900_nthserver.conv");
        File veresegyh_5m_G900_nthserver_csv = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyh_5m_G900_nthserver.csv");
        File alle_5m_G900_nthserver_dat = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "Alle_5m_G900_nthserver.dat");
        File alle_5m_G900_nthserver_conv = new File(GIT_DIRECTORY + CONVERTED_DATA + "Alle_5m_G900_nthserver.conv");
        File alle_5m_G900_nthserver_csv = new File(GIT_DIRECTORY + CONVERTED_DATA + "Alle_5m_G900_nthserver.csv");
        File veresegyh_5m_DCS_nthserver_dat = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "Veresegyh_5m_DCS_nthserver.dat");
        File veresegyh_5m_DCS_nthserver_conv = new File(GIT_DIRECTORY + CONVERTED_DATA + "Veresegyh_5m_DCS_nthserver.conv");
        File veresegyh_5m_DCS_nthserver_csv = new File(GIT_DIRECTORY + CONVERTED_DATA + "Veresegyh_5m_DCS_nthserver.csv");
        File alle_5m_DCS_nthserver_dat = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "Alle_5m_DCS_10th_nthserver.dat");
        File alle_5m_DCS_nthserver_conv = new File(GIT_DIRECTORY + CONVERTED_DATA + "Alle_5m_DCS_10th_nthserver.conv");
        File alle_5m_DCS_nthserver_csv = new File(GIT_DIRECTORY + CONVERTED_DATA + "Alle_5m_DCS_10th_nthserver.csv");
                
        //ConvertDatFile veresegyh_5m_G900_nthserver = new ConvertDatFile(veresegyh_5m_G900_nthserver_dat, veresegyh_5m_G900_nthserver_conv, veresegyh_5m_G900_nthserver_csv);
        //ConvertDatFile alle_5m_G900_nthserver = new ConvertDatFile(alle_5m_G900_nthserver_dat, alle_5m_G900_nthserver_conv, alle_5m_G900_nthserver_csv);
        //ConvertDatFile veresegyh_5m_DCS_nthserver = new ConvertDatFile(veresegyh_5m_DCS_nthserver_dat, veresegyh_5m_DCS_nthserver_conv, veresegyh_5m_DCS_nthserver_csv);
        //ConvertDatFile alle_5m_DCS_nthserver = new ConvertDatFile(alle_5m_DCS_nthserver_dat, alle_5m_DCS_nthserver_conv, alle_5m_DCS_nthserver_csv);
        
        //veresegyh_5m_G900_nthserver.convertDat2Csv();
        //alle_5m_G900_nthserver.convertDat2Csv();
        //veresegyh_5m_DCS_nthserver.convertDat2Csv();
        //alle_5m_DCS_nthserver.convertDat2Csv();
        
        //veresegyh_5m_G900_nthserver.regenerateCsvFile();
        //veresegyh_5m_DCS_nthserver.regenerateCsvFile();
        
        File veresegyh_5m_G900_DCS_nthserver = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyh_5m_G900_DCS_nthserver.csv");
        
        //Tools.meltGsmDcs(veresegyh_5m_G900_nthserver_csv, veresegyh_5m_DCS_nthserver_csv, veresegyh_5m_G900_DCS_nthserver);
         
        File gmon_gsm_veresegyhaz_1_txt = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "gmon_gsm_veresegyhaz_1.txt");
        File gmon_gsm_veresegyhaz_1_csv = new File(GIT_DIRECTORY + CONVERTED_DATA + "gmon_gsm_veresegyhaz_1.csv");
        File gmon_gsm_veresegyhaz_2_txt = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "gmon_gsm_veresegyhaz_2.txt");
        File gmon_gsm_veresegyhaz_2_csv = new File(GIT_DIRECTORY + CONVERTED_DATA + "gmon_gsm_veresegyhaz_2.csv");
        File gmon_gsm_budapestTXT = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "gmon_gsm_budapest.txt");
        File gmon_gsm_budapestCSV = new File(GIT_DIRECTORY + CONVERTED_DATA + "gmon_gsm_budapest.csv");
        File gmon_umts_budapestTXT = new File(GIT_DIRECTORY + MEASUREMENT_DATA + "gmon_umts_budapest.txt");
        File gmon_umts_budapestCSV = new File(GIT_DIRECTORY + CONVERTED_DATA + "gmon_umts_budapest.csv");
        
        //ConvertMeasurementFile gmon_gsm_veresegyhaz_1 = new ConvertMeasurementFile(gmon_gsm_veresegyhaz_1_txt, gmon_gsm_veresegyhaz_1_csv);
        //ConvertMeasurementFile gmon_gsm_veresegyhaz_2 = new ConvertMeasurementFile(gmon_gsm_veresegyhaz_2_txt, gmon_gsm_veresegyhaz_2_csv);
        //ConvertMeasurementFile gmon_gsm_budapest = new ConvertMeasurementFile(gmon_gsm_budapestTXT, gmon_gsm_budapestCSV);
        //ConvertMeasurementFile gmon_umts_budapest = new ConvertMeasurementFile(gmon_umts_budapestTXT, gmon_umts_budapestCSV);
        
        //gmon_gsm_veresegyhaz_1.convertMeasurement2Csv();
        //gmon_gsm_veresegyhaz_2.convertMeasurement2Csv();
        //gmon_gsm_budapest.convertMeasurement2Csv();
        //gmon_umts_budapest.convertMeasurement2Csv();
        
        File veresegyhaz_1_gmon_gsm_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyhaz_1_gmon_gsm_created.csv");
        File veresegyhaz_2_gmon_gsm_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyhaz_2_gmon_gsm_created.csv");
        File budapest_gmon_gsm_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "budapest_gmon_gsm_created.csv");
        File budapest_gmon_umts_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "budapest_gmon_umts_created.csv");
        File veresegyh_5m_G900_DCS_nthserver_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "veresegyh_5m_G900_DCS_nthserver_created.csv");
        
        File checkFile_veresegyhaz_1_gmon_gsm_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "checkFile_veresegyhaz_1_gmon_gsm_created.csv");
        File checkFile_veresegyhaz_2_gmon_gsm_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "checkFile_veresegyhaz_2_gmon_gsm_created.csv");
        File checkFile_budapest_gmon_gsm_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "checkFile_budapest_gmon_gsm_created.csv");
        File checkFile_budapest_gmon_umts_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "checkFile_budapest_gmon_umts_created.csv");
        File filtered_veresegyh_5m_G900_DCS_nthserver_created = new File(GIT_DIRECTORY + CONVERTED_DATA + "checkFile_veresegyh_5m_G900_DCS_nthserver_created.csv");

        //Tools.filterDatabaseFile(3, gmon_gsm_veresegyhaz_1_csv, veresegyhaz_1_gmon_gsm_created, checkFile_veresegyhaz_1_gmon_gsm_created, 3);
        //Tools.filterDatabaseFile(3, gmon_gsm_veresegyhaz_2_csv, veresegyhaz_2_gmon_gsm_created, checkFile_veresegyhaz_2_gmon_gsm_created, 3);
        //Tools.filterDatabaseFile(3, gmon_gsm_budapestCSV, budapest_gmon_gsm_created, checkFile_budapest_gmon_gsm_created, 3);
        //Tools.filterDatabaseFile(3, gmon_umts_budapestCSV, budapest_gmon_umts_created, checkFile_budapest_gmon_umts_created, 3);
        Tools.filterDatabaseFile(10, veresegyh_5m_G900_DCS_nthserver, veresegyh_5m_G900_DCS_nthserver_created, filtered_veresegyh_5m_G900_DCS_nthserver_created, 0);
        
        //Tools.decoordinateDatabaseFile(gmon_gsm_veresegyhaz_2_csv, veresegyhaz_2_gmon_gsm_created, checkFile_veresegyhaz_2_gmon_gsm_created);
        
        System.out.println("Localizating ...");
        
        File localization_results_for_VS = new File (GIT_DIRECTORY + RESULTS + "localization_results_VS.csv");
        File localization_error_results_for_VS = new File(GIT_DIRECTORY + RESULTS + "localization_error_results_VS.csv");
        
        /*VerticalSearchMethod newLocationVS = new VerticalSearchMethod(gmon_gsm_veresegyhaz_2_csv, veresegyhaz_2_gmon_gsm_created);
        HashMap<String, ArrayList<String>> database = newLocationVS.createDatabaseForVerticalSearch();
        newLocationVS.getLocation(database, localization_results_for_VS);
        
        LocalizationAnalysis.calculateDistanceError(localization_results_for_VS, checkFile_veresegyhaz_2_gmon_gsm_created, localization_error_results_for_VS);
        
        System.out.println("CERP 95%: " + LocalizationAnalysis.calculateCERP(95, localization_error_results_for_VS));
        System.out.println("CERP 67%: " + LocalizationAnalysis.calculateCERP(67, localization_error_results_for_VS));*/
        
        File localization_results_for_HS = new File (GIT_DIRECTORY + RESULTS + "localization_results_HS.csv");
        File localization_error_results_for_HS = new File(GIT_DIRECTORY + RESULTS + "localization_error_results_HS.csv");
        
        HorizontalSearchMethod newLocactionHS = new HorizontalSearchMethod(filtered_veresegyh_5m_G900_DCS_nthserver_created, veresegyhaz_2_gmon_gsm_created);
        newLocactionHS.getLocation(localization_results_for_HS);
        
        LocalizationAnalysis.calculateDistanceError(localization_results_for_HS, checkFile_veresegyhaz_2_gmon_gsm_created, localization_error_results_for_HS);
        
        System.out.println("CERP 95%: " + LocalizationAnalysis.calculateCERP(95, localization_error_results_for_HS));
        System.out.println("CERP 67%: " + LocalizationAnalysis.calculateCERP(67, localization_error_results_for_HS));
        
        
        
        
        
    }
}
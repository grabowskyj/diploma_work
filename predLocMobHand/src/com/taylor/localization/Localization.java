package com.taylor.localization;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.taylor.measurement.ConvertMeasurementFile;
import com.taylor.measurement.LocalizationAnalysis;
import com.taylor.simulation.ConvertDatFile;
import com.taylor.localization.HorizontalSearchMethod;
import com.taylor.tools.Tools;

/*
 * Need to set -Xmx4096m in VM Arguments to avoid out of memory exceptions
 */

public class Localization {
    
    //Change threadNumber according to the number of required threads (optional number lies between 1-4)
    private static final int threadNumber = 4;
    
    private static ArrayList<ArrayList<String>> createTasklist(File measurementFile, int divider) {
        ArrayList<ArrayList<String>> allTasks = null;
        ArrayList<String> taskSet = null;
        ArrayList<String> measurement = null;
        String task = null;
        String headerRow = null;
        int worker = 1;
        int allTaskNumber = 0;
        
        allTasks = new ArrayList<ArrayList<String>>();
        measurement = Tools.readFileToMemory(measurementFile);
        allTaskNumber = measurement.size() - 1;
        headerRow = measurement.get(0);
        
        try {
            while (worker <= divider) {
                taskSet = new ArrayList<String>();
                taskSet.add(headerRow);
                
                for (int taskNumber = worker; taskNumber <= allTaskNumber; taskNumber = taskNumber + threadNumber) {
                    task = measurement.get(taskNumber);
                    taskSet.add(task);
                }             
                
                allTasks.add(taskSet);
                worker++;
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        
        return allTasks;
    }
    
    public static void main(String[] args) {
       
        System.out.println("Localizating ...");
        
        /*
         * Define file names for differentiate the result files
         */
        
        String measurement_file_name = "m_v2_gsm_full";
        String database_file_name = "m_db_v_gsm_dcs";
        String method_type = "hs";
        
        /*
         * No modification is needed here 
         */
        
        String result_file_name = method_type + "_" + database_file_name + "_" + measurement_file_name;
        String decoordinated_file_name = "decoordinated_" + measurement_file_name + ".csv";
        String check_file_name_for_decoordinated_file = "checkfile_for_" + decoordinated_file_name + ".csv";
        String lowered_database_file_name = "lowered_" + database_file_name + ".csv";
        String decoordinated_lowered_database_file_name = "decoordinated_" + lowered_database_file_name + ".csv";
        String localization_results_file_name = result_file_name + "_localization_results.csv";
        String localization_error_results_file_name = result_file_name + "_localization_error_results.csv";
        String localization_cerp_results_file_name = result_file_name + "_localization_cerp_results.csv";
        
        /*
         * Requested CERP values, change or put numbers into the bracket
         */
        
        int[] cerpValues = new int[]{95,67};
        
        /*
         * Set files for localization
         * No modification is needed here
         */
        
        File localization_results = new File (GenerateFiles.GIT_DIRECTORY + GenerateFiles.RESULTS + localization_results_file_name);
        File localization_error_results = new File(GenerateFiles.GIT_DIRECTORY + GenerateFiles.RESULTS + localization_error_results_file_name);
        File localization_cerp_results = new File(GenerateFiles.GIT_DIRECTORY + GenerateFiles.RESULTS + localization_cerp_results_file_name);
        File decoordinated_file = null;
        File lowered_database_file = new File (GenerateFiles.GIT_DIRECTORY + GenerateFiles.RESULTS + lowered_database_file_name);
        File generatedMeasurementFile = null;
        ArrayList<String> databaseData = null;
        ArrayList<String> measurementData = null;
        
        //Remove the leading comment sign (/* or //) from the beginning of a method if you want to turn it on
        
        /*
         * Convert database files and measurement files
         */
        
        //GenerateFiles.generateDatabase();
        //GenerateFiles.generateMeasurements();
        
        /*
         * Generate measurement file with randomized signal strength values
         * Set arguments according to the javadoc (in this case only the first, second and fifth argument needs to be set)
         */
        
        /*generatedMeasurementFile = Tools.filterDatabaseFile(0, GenerateFiles.gmon_gsm_veresegyhaz_1_csv, 
                new File (GenerateFiles.GIT_DIRECTORY + GenerateFiles.DECOORDINATED_MEASUREMENT_DATA + decoordinated_file_name), 
                new File (GenerateFiles.GIT_DIRECTORY + GenerateFiles.DECOORDINATED_MEASUREMENT_DATA + check_file_name_for_decoordinated_file), 
                0);
        
        /*
         * Create decoordinated measurement file
         * Set arguments according to the javadoc (in this case only the first argument needs to be set)
         */

        /*decoordinated_file = Tools.decoordinate(GenerateFiles.gmon_umts_veresegyhazCSV,
                new File (GenerateFiles.GIT_DIRECTORY + GenerateFiles.DECOORDINATED_MEASUREMENT_DATA + decoordinated_file_name), 
                new File (GenerateFiles.GIT_DIRECTORY + GenerateFiles.DECOORDINATED_MEASUREMENT_DATA + check_file_name_for_decoordinated_file));
       
        /*
         * Lower the database
         * Set arguments according to the javadoc (in this case only the first, second, fourth and fifth argument needs to be set)
         */
        
        /*Tools.filterDatabaseFile(15, GenerateFiles.budapest_5m_G900_DCS_database,
                new File (GenerateFiles.GIT_DIRECTORY + GenerateFiles.DECOORDINATED_MEASUREMENT_DATA + decoordinated_lowered_database_file_name),
                lowered_database_file,
                0);
        
        /*
         * Set database file and measurement file
         * databaseData: Change the file at Tools.readFileToMemory's argument for using different datasource
         * measurementData: Change the file at Tools.readFileToMemory's argument if you want to use generated measurement file instead of decoordinated file
         */
        
        databaseData = Tools.readFileToMemory(GenerateFiles.gmon_umts_veresegyhazCSV);
        measurementData = Tools.readFileToMemory(decoordinated_file);
        
        /*
         *  Use VerticalSearchMethod
         */
        
        /*VerticalSearchMethod newLocationVS = new VerticalSearchMethod(databaseData, measurementData);
        newLocationVS.getLocation(localization_results);
        
        LocalizationAnalysis.calculateDistanceError(localization_results, 
                new File (GenerateFiles.GIT_DIRECTORY + GenerateFiles.DECOORDINATED_MEASUREMENT_DATA + check_file_name_for_decoordinated_file),
                localization_error_results);
        
        LocalizationAnalysis.calculateCERP(cerpValues, localization_error_results, localization_cerp_results);
        
        /*
         * Use HorizontalSearchMethod
         */
        
        /*HorizontalSearchMethod newLocactionHS = new HorizontalSearchMethod(databaseData, measurementData);
        newLocactionHS.getLocation(localization_results);
        
        LocalizationAnalysis.calculateDistanceError(localization_results, 
                new File (GenerateFiles.GIT_DIRECTORY + GenerateFiles.DECOORDINATED_MEASUREMENT_DATA + check_file_name_for_decoordinated_file),
                localization_error_results);
        
        LocalizationAnalysis.calculateCERP(cerpValues, localization_error_results, localization_cerp_results);
        
        /*
         * Use multithreaded HorizontalSearchMethod
         */
        
       /* File resultDirectory = null;
        resultDirectory = new File (SingleWorker.multithreadRunResultsDirectory);
        
        ArrayList<ArrayList<String>> allTasks = null;
        ArrayList<String> taskSet = null;
        int workerNumber = 1;
        
        for (File file : resultDirectory.listFiles()) {
            file.delete();
        }
        
        //Create tasklist for multithread execution
        //Set arguments according to the javadoc (in this case only the first argument needs to be set)
        
        allTasks = createTasklist(decoordinated_file, threadNumber);
        
        ExecutorService executor = Executors.newFixedThreadPool(threadNumber);       
        
        for (int threadCounter = 0; threadCounter < threadNumber; threadCounter++ ) {
            taskSet = allTasks.get(threadCounter);
            
            Runnable worker = new SingleWorker(taskSet, databaseData, workerNumber);
            executor.execute(worker);
            workerNumber++;
        }
        
        executor.shutdown();
        
        while(!executor.isTerminated()) {}
        
        System.out.println("All threads have been finished!");
        
        LocalizationAnalysis.summarizeMultithreadRunResults(resultDirectory, localization_results, measurementData.size());
        LocalizationAnalysis.calculateDistanceError(localization_results,
                new File (GenerateFiles.GIT_DIRECTORY + GenerateFiles.DECOORDINATED_MEASUREMENT_DATA + check_file_name_for_decoordinated_file), 
                localization_error_results);
        
        LocalizationAnalysis.calculateCERP(cerpValues, localization_error_results, localization_cerp_results);
        
        /*
         * 
         */
 
    }
}
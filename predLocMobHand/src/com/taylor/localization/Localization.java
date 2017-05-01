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
 * need to set -Xmx4096m in VM Arguments
 */

public class Localization {

    private static final int threadNumber = 8;
    
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
    
    @SuppressWarnings("unused")
    public static void main(String[] args) {
       
        System.out.println("Localizating ...");
        
        /*
         * Define file names
         */
        
        String decoordinated_file_name = "decoordinated_veresegyhaz_gsm.csv";
        String check_file_name_for_decoordinated_file = "checkfile_for_decoordinated_veresegyhaz_gsm.csv";
        String localization_results_file_name = "localization_results.csv";
        String localization_error_results_file_name = "localization_error_results.csv";
        
        /*
         * Set files for localization
         */
        
        File localization_results = new File (GenerateFiles.GIT_DIRECTORY + GenerateFiles.RESULTS + localization_results_file_name);
        File localization_error_results = new File(GenerateFiles.GIT_DIRECTORY + GenerateFiles.RESULTS + localization_error_results_file_name);
        File decoordinated_file = null;
        ArrayList<String> databaseData = null;
        ArrayList<String> measurementData = null;
        
        /*
         * Convert database files and measurement files
         */
        
        //GenerateFiles.generateDatabase();
        //GenerateFiles.generateMeasurements();
        
        /*
         * Create decoordinated measurement file
         */

        decoordinated_file = Tools.decoordinate(GenerateFiles.gmon_gsm_veresegyhaz_2_csv,
                new File (GenerateFiles.GIT_DIRECTORY + GenerateFiles.DECOORDINATED_MEASUREMENT_DATA + decoordinated_file_name), 
                new File (GenerateFiles.GIT_DIRECTORY + GenerateFiles.DECOORDINATED_MEASUREMENT_DATA + check_file_name_for_decoordinated_file));
        
        /*
         * Set database file and measurement file
         */
        
        databaseData = Tools.readFileToMemory(GenerateFiles.gmon_gsm_veresegyhaz_2_csv);
        measurementData = Tools.readFileToMemory(decoordinated_file);
        
        /*
         *  Use VerticalSearchMethod
         */
        
        /*VerticalSearchMethod newLocationVS = new VerticalSearchMethod(databaseData, measurementData);
        HashMap<String, ArrayList<String>> database = newLocationVS.createDatabaseForVerticalSearch();
        newLocationVS.getLocation(database, localization_results);
        
        LocalizationAnalysis.calculateDistanceError(localization_results, 
                new File (GenerateFiles.GIT_DIRECTORY + GenerateFiles.DECOORDINATED_MEASUREMENT_DATA + check_file_name_for_decoordinated_file),
                localization_error_results);
        
        System.out.println("CERP 95%: " + LocalizationAnalysis.calculateCERP(95, localization_error_results));
        System.out.println("CERP 67%: " + LocalizationAnalysis.calculateCERP(67, localization_error_results));*/
        
        /*
         * Use HorizontalSearchMethod
         */
        
        /*HorizontalSearchMethod newLocactionHS = new HorizontalSearchMethod(databaseData, measurementData);
        newLocactionHS.getLocation(localization_results);
        
        LocalizationAnalysis.calculateDistanceError(localization_results, 
                new File (GenerateFiles.GIT_DIRECTORY + GenerateFiles.DECOORDINATED_MEASUREMENT_DATA + check_file_name_for_decoordinated_file),
                localization_error_results);
        
        System.out.println("CERP 95%: " + LocalizationAnalysis.calculateCERP(95, localization_error_results));
        System.out.println("CERP 67%: " + LocalizationAnalysis.calculateCERP(67, localization_error_results));*/
        
        /*
         * Use multithreaded HorizontalSearchMethod
         */
        
        /*ArrayList<ArrayList<String>> allTasks = null;
        ArrayList<String> taskSet = null;
        int workerNumber = 1; 
        
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
        
        System.out.println("All threads have been finished!");*/
        
        LocalizationAnalysis.summarizeMultithreadRunResults(new File (SingleWorker.multithreadRunResultsDirectory), localization_results, measurementData.size());
        LocalizationAnalysis.calculateDistanceError(localization_results,
                new File (GenerateFiles.GIT_DIRECTORY + GenerateFiles.DECOORDINATED_MEASUREMENT_DATA + check_file_name_for_decoordinated_file), 
                localization_error_results);
        
        System.out.println("CERP 95%: " + LocalizationAnalysis.calculateCERP(95, localization_error_results));
        System.out.println("CERP 67%: " + LocalizationAnalysis.calculateCERP(67, localization_error_results));
 
    }
}
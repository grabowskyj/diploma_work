package com.taylor.localization;

import java.io.File;
import java.util.ArrayList;

import com.taylor.tools.Tools;

public class SingleWorker implements Runnable {
    
    private final ArrayList<String> taskSet;
    private final ArrayList<String> database;
    private final int workerNumber;
    public static final String multithreadRunResultsDirectory = GenerateFiles.GIT_DIRECTORY + GenerateFiles.RESULTS + "\\multithread_run_results\\";
    
    public SingleWorker(ArrayList<String> taskSet, ArrayList<String> database, int workerNumber) {
        this.taskSet = taskSet;
        this.database = database;
        this.workerNumber = workerNumber;
    }
    
    @Override
    public void run(){
        File resultFile = null;
        String resultFileString = "result" + (char) (workerNumber + 64) + ".worker";
        
        resultFile = new File (multithreadRunResultsDirectory + resultFileString);
        Tools.createFile(resultFile);
        
        System.out.println(Thread.currentThread().getName() + " for Worker " + workerNumber + " STARTED");
        HorizontalSearchMethod localization = new HorizontalSearchMethod(database, taskSet);
        localization.getLocation(resultFile);
        System.out.println(Thread.currentThread().getName() + " for Worker " + workerNumber + " FINISHED");    
    }
}
package com.taylor.localization;

import java.io.File;
import java.util.ArrayList;

import com.taylor.tools.Tools;

public class SingleWorker implements Runnable {
    
    private final ArrayList<String> taskSet;
    private final ArrayList<String> database;
    private final int workerNumber;
    
    public SingleWorker(ArrayList<String> taskSet, ArrayList<String> database, int workerNumber) {
        this.taskSet = taskSet;
        this.database = database;
        this.workerNumber = workerNumber;
    }
    
    @Override
    public void run(){
        File resultFile = null;
        String resultFileString = "results" + workerNumber + ".worker";
        
        resultFile = new File(GenerateFiles.GIT_DIRECTORY + GenerateFiles.RESULTS + resultFileString);
        Tools.createFile(resultFile);
        
        System.out.println(Thread.currentThread().getName() + " for Worker " + workerNumber + " STARTED");
        HorizontalSearchMethod localization = new HorizontalSearchMethod(database, taskSet);
        localization.getLocation(resultFile);
        System.out.println(Thread.currentThread().getName() + " for Worker " + workerNumber + " FINISHED");    
    }
}
package com.taylor.measurement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.taylor.tools.Tools;
import com.taylor.tools.Tools.COORDINATES;

public class LocalizationAnalysis {
    
    public static double getDistance(double latitudeOfPoint1, double latitudeOfPoint2, double longitudeOfPoint1, double longitudeOfPoint2, double heightOfPoint1, double heightOfPoint2) {
        final int earthRadius = 6371;
        double distance = 0;
        double height = 0;
        double latitudeDistance = 0;
        double longitudeDistance = 0;
        double a = 0;
        double c = 0;
        
        latitudeDistance = Math.toRadians(latitudeOfPoint2 - latitudeOfPoint1);
        longitudeDistance = Math.toRadians(longitudeOfPoint2 - longitudeOfPoint1);
        a = Math.sin(latitudeDistance / 2) * Math.sin(latitudeDistance / 2)
                + Math.cos(Math.toRadians(latitudeOfPoint1)) * Math.cos(Math.toRadians(latitudeOfPoint2))
                * Math.sin(longitudeDistance / 2) * Math.sin(longitudeDistance / 2);
        c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        distance = earthRadius * c * 1000;
        height = heightOfPoint1 - heightOfPoint2;
        distance = Math.sqrt(Math.pow(distance, 2) + Math.pow(height, 2));

        return distance;
    }
    
    public static void calculateDistanceError(File resultFile, File controlFile, File errorFile) {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        HashMap<String, Double> resultCoordinates = null;
        HashMap<String, Double> controlCoordinates = null;
        List<String> results = null;
        List<String> control = null;
        String[] resultLine = null;
        String[] controlLine = null;
        String errorFileHeader = null;
        String pointName = null;
        String rowToWrite = null;
        double resultLatitude = 0;
        double resultLongitude = 0;
        double controlLatitude = 0;
        double controlLongitude = 0;
        double distance = 0;
        
        Tools.createFile(errorFile);
        errorFileHeader = "point,error,ORIG:" + COORDINATES.LATITUDE.toString() + ",ORIG:" + COORDINATES.LONGITUDE.toString()
                + ",DCM:" + COORDINATES.LATITUDE.toString() + ",DCM:" + COORDINATES.LONGITUDE.toString();
        resultCoordinates = new HashMap<String, Double>();
        controlCoordinates = new HashMap<String, Double>();
        results = new ArrayList<String>();
        control = new ArrayList<String>();
        
        try {
            fileWriter = new FileWriter(errorFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(errorFileHeader);
            bufferedWriter.newLine();
            
            results = Tools.readFileToMemory(resultFile);
            control = Tools.readFileToMemory(controlFile);
            
            for (int lineCounter = 1; lineCounter < results.size(); lineCounter++) {
                resultLine = results.get(lineCounter).trim().split(",");
                controlLine = control.get(lineCounter).trim().split(",");
                
                pointName = resultLine[0];
                resultLatitude = Double.parseDouble(resultLine[1]);
                resultLongitude = Double.parseDouble(resultLine[2]);
                controlLatitude = Double.parseDouble(controlLine[0]);
                controlLongitude = Double.parseDouble(controlLine[1]);
                
                resultCoordinates = Tools.hd72EovToWgs84(resultLatitude, resultLongitude);
                controlCoordinates = Tools.hd72EovToWgs84(controlLatitude, controlLongitude);
                
                distance = getDistance(resultCoordinates.get(COORDINATES.LATITUDE.toString()), controlCoordinates.get(COORDINATES.LATITUDE.toString()), 
                        resultCoordinates.get(COORDINATES.LONGITUDE.toString()), controlCoordinates.get(COORDINATES.LONGITUDE.toString()), 0, 0);
                
                rowToWrite = pointName + "," + distance + "," + controlLatitude + "," + controlLongitude + "," + resultLatitude  + "," + resultLongitude;
                bufferedWriter.write(rowToWrite);
                bufferedWriter.newLine();
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            try {
                bufferedWriter.close();
                fileWriter.close();
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
    }
    
    public static double calculateCERP(int percent, File resultFile) {
        List<String> results = null;
        ArrayList<Double> errorDistances = null;
        int nthElement = 0;
        double errorDistance = 0;
        double cerpValue = 0;
        String[] resultRow = null;
        
        results = new ArrayList<String>();
        errorDistances = new ArrayList<Double>();
        results = Tools.readFileToMemory(resultFile);
        
        for(int resultRowCounter = 1; resultRowCounter < results.size(); resultRowCounter++) {
            resultRow = results.get(resultRowCounter).trim().split(",");
            errorDistance = Double.parseDouble(resultRow[1]);
            errorDistances.add(errorDistance);
        }
        
        Collections.sort(errorDistances);
        nthElement = (int) ((percent * errorDistances.size()) / 100);
        cerpValue = errorDistances.get(nthElement);
        
        return cerpValue;
    }
    
}
package com.taylor.measurement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;

import com.taylor.tools.Tools;
import com.taylor.tools.Tools.COORDINATES;

public class LocalizationAnalysis {
    
    public static void calculateErrorDistance(File resultFile, File controlFile, File errorFile) {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        Hashtable<String, Double> resultCoordinates = null;
        Hashtable<String, Double> controlCoordinates = null;
        ArrayList<String> results = new ArrayList<String>();
        ArrayList<String> control = new ArrayList<String>();
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
        resultCoordinates = new Hashtable<String, Double>();
        controlCoordinates = new Hashtable<String, Double>();
        
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
    
    /*
     * Haversine formula
     */
    public static double getDistance(
            double latitudeOfPoint1, double latitudeOfPoint2,
            double longitudeOfPoint1, double longitudeOfPoint2,
            double heightOfPoint1, double heightOfPoint2) {

        final int R = 6371;

        Double latDistance = Math.toRadians(latitudeOfPoint2 - latitudeOfPoint1);
        Double lonDistance = Math.toRadians(longitudeOfPoint2 - longitudeOfPoint1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latitudeOfPoint1)) * Math.cos(Math.toRadians(latitudeOfPoint2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        double distance = R * c * 1000;

        double height = heightOfPoint1 - heightOfPoint2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

}
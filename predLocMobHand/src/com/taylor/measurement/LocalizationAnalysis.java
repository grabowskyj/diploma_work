package com.taylor.measurement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import org.gdal.ogr.Geometry;
import org.gdal.osr.SpatialReference;

import com.taylor.tools.Tools;
import com.taylor.tools.Tools.COORDINATES;

public class LocalizationAnalysis {
    
    public static void calculateErrorDistance(File resultFile, File controlFile, File errorFile) {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        ArrayList<String> results = new ArrayList<String>();
        ArrayList<String> control = new ArrayList<String>();
        String[] resultLine = null;
        String[] controlLine = null;
        String errorFileHeader = null;
        String pointName = null;
        double resultLatitude = 0;
        double resultLongitude = 0;
        double controlLatitude = 0;
        double controlLongitude = 0;

        
        Tools.createFile(errorFile);
        errorFileHeader = "point,ORIG:" + COORDINATES.LATITUDE.toString() + ",ORIG:" + COORDINATES.LONGITUDE.toString()
                + "DCM:" + COORDINATES.LATITUDE.toString() + ",DCM:" + COORDINATES.LONGITUDE.toString() + ",error";
        
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
}
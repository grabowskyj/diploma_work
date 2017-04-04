package com.taylor.tools;

import java.io.File;
import java.io.IOException;
import org.gdal.osr.*;

public class Tools {
    
    public static void createFile(File file) {
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();  
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static int convertHex2Dec(String[] array) {
        int value = 0;
        String hexString = String.join("", array);
        switch (array.length) {
            case 2:
                value = (short) Integer.parseInt(hexString, 16);
                break;
            case 4:
                value = (int) Long.parseLong(hexString, 16);
                break;
        }     
        
        return value;
    }
    
    public static char convertHex2Char(String hex) {
        int hex2decimal = Integer.parseInt(hex, 16);
        char character = (char) hex2decimal;
        
        return character;
    }
    
    public static String[] reversArray(String[] array) {
        int reverseArrayLength = array.length;
        String[] reversArray = new String[reverseArrayLength];
        for (int elementCounter = 0; elementCounter < array.length; elementCounter++ ) {
            reversArray[--reverseArrayLength] = array[elementCounter];
        }
        
        return reversArray;
    }
    
    public static double[] wgs84ToHd72Eov(double latitude, double longitude) {
        SpatialReference wgs84 = new SpatialReference();
        SpatialReference hd72eov = new SpatialReference();
        wgs84.ImportFromEPSG(4326);
        hd72eov.ImportFromEPSG(23700);
        CoordinateTransformation wgs84ToHd72Eov = new CoordinateTransformation(wgs84, hd72eov);
      //kimeneteket helyesseget ellenorizni
        double[] transformation = wgs84ToHd72Eov.TransformPoint(longitude, latitude);
        
        return transformation;
    }
    
    public static double[] hd72EovToWgs84(double latitude, double longitude) {
        SpatialReference wgs84 = new SpatialReference();
        SpatialReference hd72eov = new SpatialReference();
        wgs84.ImportFromEPSG(4326);
        hd72eov.ImportFromEPSG(23700);
        CoordinateTransformation hd72EovTowgs84 = new CoordinateTransformation(hd72eov, wgs84);
      
        double[] transformation = hd72EovTowgs84.TransformPoint(latitude, longitude);
      //kimeneteket helyesseget ellenorizni
        return transformation;
    }
    
    
}
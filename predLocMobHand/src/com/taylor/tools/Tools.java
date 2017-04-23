package com.taylor.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import org.gdal.osr.*;
import org.rosuda.JRI.Rengine;

public class Tools {
    
    public static enum FILETYPE {BESTSERVER, NTHSERVER, UNDEFINED};
    
    public static enum COORDINATES {
        LATITUDE ("latitude"),
        LONGITUDE ("longitude");
        
        private final String axis;
        
        private COORDINATES(final String axis) {
            this.axis = axis;
        }

        @Override
        public String toString() {
            return axis;
        }
    }
    
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
        for (int elementCounter = 0; elementCounter < array.length; elementCounter++) {
            reversArray[--reverseArrayLength] = array[elementCounter];
        }

        return reversArray;
    }

    public static ArrayList<String> readFileToMemory(File data) {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        ArrayList<String> dataInMemory = new ArrayList<String>();

        try {
            fileReader = new FileReader(data);
            bufferedReader = new BufferedReader(fileReader);
            String readedLine = null;
            
            while ((readedLine = bufferedReader.readLine()) != null) {
                dataInMemory.add(readedLine);
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                fileReader.close();
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }

        return dataInMemory;
    }

    public static HashMap<String, Double> wgs84ToHd72Eov(double y, double x) {
        SpatialReference wgs84 = new SpatialReference();
        SpatialReference hd72eov = new SpatialReference();
        wgs84.ImportFromEPSG(4326);
        hd72eov.ImportFromEPSG(23700);
        CoordinateTransformation wgs84ToHd72Eov = new CoordinateTransformation(wgs84, hd72eov);
        double[] transformation = wgs84ToHd72Eov.TransformPoint(x, y);
        @SuppressWarnings("serial")
        HashMap<String, Double> coordinates = new HashMap<String, Double>() {
            {
                put(COORDINATES.LATITUDE.toString(), transformation[0]);
                put(COORDINATES.LONGITUDE.toString(), transformation[1]);
            }
        };

        return coordinates;
    }

    public static HashMap<String, Double> hd72EovToWgs84(double y, double x) {
        SpatialReference wgs84 = new SpatialReference();
        SpatialReference hd72eov = new SpatialReference();
        CoordinateTransformation hd72EovTowgs84;

        wgs84.ImportFromEPSG(4326);
        hd72eov.ImportFromEPSG(23700);
        hd72EovTowgs84 = new CoordinateTransformation(hd72eov, wgs84);
        double[] transformation = hd72EovTowgs84.TransformPoint(y, x);

        @SuppressWarnings("serial")
        HashMap<String, Double> coordinates = new HashMap<String, Double>() {
            {
                put(COORDINATES.LATITUDE.toString(), transformation[1]);
                put(COORDINATES.LONGITUDE.toString(), transformation[0]);
            }
        };

        return coordinates;
    }

    public static int generateRandomValue(int symmetricRange) {
        Random generateRandomValue = new Random();
        int randomValue = generateRandomValue.nextInt((2 * symmetricRange) + 1) - symmetricRange;

        return randomValue;
    }

    public static File[] createTestMeasurementFile(int nthRow, File inputFile, File derivedMeasurementFile, File checkFile, int range) {
        File[] resultFiles = null;
        FileReader fileReader = null;
        FileWriter fileWriter = null;
        FileWriter checkFileWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        BufferedWriter checkFileBufferedWriter = null;
        FILETYPE typeOfMeasurement = null;
        ArrayList<String> csvData = null;
        ArrayList<String> headerRowArrayList = null;
        int signalStrength = 0;
        int randomValue = 0;
        String rowToWrite = null;
        String[] headerRow = null;
        String[] row = null;
        String[] rowWithoutCoordinates = null;
        String[] headerRowWithoutCoordinates = null;       
        
        resultFiles = new File[]{derivedMeasurementFile, checkFile};
        csvData = new ArrayList<String>();
        createFile(checkFile);

        try {
            fileReader = new FileReader(inputFile);
            bufferedReader = new BufferedReader(fileReader);
            fileWriter = new FileWriter(derivedMeasurementFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            checkFileWriter = new FileWriter(checkFile);
            checkFileBufferedWriter = new BufferedWriter(checkFileWriter);
            String readedLine = null;
            
            while ((readedLine = bufferedReader.readLine()) != null) {
                csvData.add(readedLine);
            }
            
            headerRow = csvData.get(0).trim().split(",");
            headerRowArrayList = new ArrayList<String>(Arrays.asList(headerRow));
            
            if (headerRowArrayList.contains("cellID")) {
                typeOfMeasurement = FILETYPE.BESTSERVER;
            }
            
            if (headerRowArrayList.contains("cellID") && headerRowArrayList.contains("n1cellID")) {
                typeOfMeasurement = FILETYPE.NTHSERVER;
            }
            
            headerRowWithoutCoordinates = Arrays.copyOfRange(headerRow, 2, headerRow.length);
            bufferedWriter.write(String.join(",", headerRowWithoutCoordinates));
            bufferedWriter.newLine();
            checkFileBufferedWriter.write(String.join(",", headerRow));
            checkFileBufferedWriter.newLine();
            
            if (nthRow == 0) {
                nthRow = 1;
            }
            
            for (int rowCounter = 1; rowCounter < csvData.size(); rowCounter++) {
                if (rowCounter % nthRow == 0) {
                    row = csvData.get(rowCounter).trim().split(",");
                    randomValue = generateRandomValue(range);
                    
                    if (typeOfMeasurement == FILETYPE.BESTSERVER) {
                        signalStrength = Integer.parseInt(row[3]);
                        signalStrength = signalStrength + randomValue;
                        row[3] = Integer.toString(signalStrength);
                    } else if (typeOfMeasurement == FILETYPE.NTHSERVER) {
                        for (int rowElement = 3; rowElement < row.length; rowElement = rowElement + 2) {
                            signalStrength = Integer.parseInt(row[rowElement]);
                            signalStrength = signalStrength + randomValue;
                            row[rowElement] = Integer.toString(signalStrength);
                        }
                    } else {
                        for (int rowElement = 3; rowElement < row.length; rowElement = rowElement + 2) {
                            signalStrength = Integer.parseInt(row[rowElement]);
                            signalStrength = signalStrength + randomValue;
                            row[rowElement] = Integer.toString(signalStrength);
                        }
                    }
                    
                    if (row.length > 2) {
                        rowToWrite = String.join(",", row);
                        checkFileBufferedWriter.write(rowToWrite);
                        checkFileBufferedWriter.newLine();
                        rowWithoutCoordinates = Arrays.copyOfRange(row, 2, row.length);
                        rowToWrite = String.join(",", rowWithoutCoordinates);
                        bufferedWriter.write(rowToWrite);
                        bufferedWriter.newLine();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                fileReader.close();
                bufferedWriter.close();
                fileWriter.close();
                checkFileBufferedWriter.close();
                checkFileWriter.close();
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }

        return resultFiles;
    }
    
    public static File[] decoordinateMeasurementFile(File inputFile, File outputFile, File checkFile) {
        File[] outputFiles = null;
        
        outputFiles = createTestMeasurementFile(0, inputFile, outputFile, checkFile, 0);
                
        return outputFiles;
    }
    
    public static HashMap<String, Double> getMeanValueOfCoordinates(Rengine rEngine, ArrayList<String> latitudeCoordinates, ArrayList<String> longitudeCoordinates) {
        HashMap<String, Double> coordinates = new HashMap<String, Double>();
        double latitudeCoordinate = 0;
        double longitudeCoordinate = 0;
        String arrLatitudeVector[] = null;
        String arrLongitudeVector[] = null;
        String latitudeVector = null;
        String longitudeVector = null;
        
        arrLatitudeVector = new String[latitudeCoordinates.size()];
        arrLongitudeVector = new String[longitudeCoordinates.size()];
        
        arrLatitudeVector = latitudeCoordinates.toArray(arrLatitudeVector);
        arrLongitudeVector = longitudeCoordinates.toArray(arrLongitudeVector);
        
        latitudeVector = "c(" + String.join(",", arrLatitudeVector) + ")";
        longitudeVector = "c(" + String.join(",", arrLongitudeVector) + ")";
        
        rEngine.eval("latitudeVector=" + latitudeVector);
        rEngine.eval("longitudeVector=" + longitudeVector);

        rEngine.eval("latitudeVectorMean=mean(latitudeVector)");
        rEngine.eval("longitudeVectorMean=mean(longitudeVector)");
        
        latitudeCoordinate = rEngine.eval("latitudeVectorMean").asDouble();
        longitudeCoordinate = rEngine.eval("longitudeVectorMean").asDouble();
        
        coordinates.put(COORDINATES.LATITUDE.toString(), latitudeCoordinate);
        coordinates.put(COORDINATES.LONGITUDE.toString(), longitudeCoordinate);
        
        return coordinates;
    }
}
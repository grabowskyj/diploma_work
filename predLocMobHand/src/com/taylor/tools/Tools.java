package com.taylor.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;

import org.gdal.osr.*;

public class Tools {
    
    public static enum FILETYPE {BESTSERVER, NTHSERVER, UNDEFINED};
    
    public enum COORDINATES {
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

    public static ArrayList<String> readFileToMemory(File dataBase) {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        ArrayList<String> dataInMemory = new ArrayList<String>();

        try {
            fileReader = new FileReader(dataBase);
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

    public static Hashtable<Object, Object> wgs84ToHd72Eov(double y, double x) {
        SpatialReference wgs84 = new SpatialReference();
        SpatialReference hd72eov = new SpatialReference();
        wgs84.ImportFromEPSG(4326);
        hd72eov.ImportFromEPSG(23700);
        CoordinateTransformation wgs84ToHd72Eov = new CoordinateTransformation(wgs84, hd72eov);
        double[] transformation = wgs84ToHd72Eov.TransformPoint(x, y);
        @SuppressWarnings("serial")
        Hashtable<Object, Object> coordinates = new Hashtable<Object, Object>() {
            {
                put(COORDINATES.LATITUDE, transformation[0]);
                put(COORDINATES.LONGITUDE, transformation[1]);
            }
        };

        return coordinates;
    }

    public static Hashtable<Object, Object> hd72EovToWgs84(double y, double x) {
        SpatialReference wgs84 = new SpatialReference();
        SpatialReference hd72eov = new SpatialReference();
        CoordinateTransformation hd72EovTowgs84;

        wgs84.ImportFromEPSG(4326);
        hd72eov.ImportFromEPSG(23700);
        hd72EovTowgs84 = new CoordinateTransformation(hd72eov, wgs84);
        double[] transformation = hd72EovTowgs84.TransformPoint(y, x);

        @SuppressWarnings("serial")
        Hashtable<Object, Object> coordinates = new Hashtable<Object, Object>() {
            {
                put(COORDINATES.LATITUDE, transformation[1]);
                put(COORDINATES.LONGITUDE, transformation[0]);
            }
        };

        return coordinates;
    }

    public static int generateRandomValue(int symmetricRange) {
        Random generateRandomValue = new Random();
        int randomValue = generateRandomValue.nextInt((2 * symmetricRange) + 1) - symmetricRange;

        return randomValue;
    }

    public static File createTestMeasurementFile(int nthRow, File inputFile, File outputFile, File checkFile, int range) {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        FileWriter checkFileWriter = null;
        BufferedWriter checkFileBufferedWriter = null;
        int signalStrength = 0;
        int randomValue = 0;
        FILETYPE typeOfMeasurement = null;
        String rowToWrite = null;
        String[] headerRow = null;
        String[] row = null;
        String[] rowWithoutCoordinates = null;
        String[] headerRowWithoutCoordinates = null;
        ArrayList<String> csvData = new ArrayList<String>();
        ArrayList<String> headerRowArrayList = null;
        
        createFile(checkFile);

        try {
            fileReader = new FileReader(inputFile);
            bufferedReader = new BufferedReader(fileReader);
            fileWriter = new FileWriter(outputFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            checkFileWriter = new FileWriter(checkFile);
            checkFileBufferedWriter = new BufferedWriter(checkFileWriter);
            String readedLine = null;
            while ((readedLine = bufferedReader.readLine()) != null) {
                csvData.add(readedLine);
            }
            headerRow = csvData.get(0).trim().split(",");
            headerRowArrayList = new ArrayList<String>(Arrays.asList(headerRow));
            if (headerRowArrayList.contains("cellLayerID")) {
                typeOfMeasurement = FILETYPE.BESTSERVER;
            }
            if (headerRowArrayList.contains("cellLayerID") && headerRowArrayList.contains("n1cellLayerID")) {
                typeOfMeasurement = FILETYPE.NTHSERVER;
            }
            headerRowWithoutCoordinates = Arrays.copyOfRange(headerRow, 2, headerRow.length);
            bufferedWriter.write(String.join(",", headerRowWithoutCoordinates));
            bufferedWriter.newLine();
            if (nthRow == 0) {
                nthRow = 1;
            }
            for (int rowCounter = 1; rowCounter < csvData.size(); rowCounter++) {
                if (rowCounter % nthRow == 0) {
                    row = csvData.get(rowCounter).trim().split(",");
                    randomValue = generateRandomValue(range);
                    if (typeOfMeasurement == FILETYPE.BESTSERVER) {
                        signalStrength = Integer.parseInt(row[4]);
                        signalStrength = signalStrength + randomValue;
                        row[4] = Integer.toString(signalStrength);
                    } else if (typeOfMeasurement == FILETYPE.NTHSERVER) {
                        for (int rowElement = 4; rowElement < row.length; rowElement = rowElement + 3) {
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
                    rowToWrite = String.join(",", row);
                    checkFileBufferedWriter.write(rowToWrite);
                    checkFileBufferedWriter.newLine();
                    rowWithoutCoordinates = Arrays.copyOfRange(row, 2, row.length);
                    rowToWrite = String.join(",", rowWithoutCoordinates);
                    bufferedWriter.write(rowToWrite);
                    bufferedWriter.newLine();
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
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }

        return outputFile;
    }

}
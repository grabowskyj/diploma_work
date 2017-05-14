package com.taylor.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import org.gdal.osr.*;
import org.rosuda.JRI.Rengine;

public class Tools {
    
    public static enum FILETYPE {BESTSERVER, NTHSERVER, UNDEFINED};
    public static enum DATATYPE {CELLNAME, SIGNALSTRENGTH};
    
    /**
     * Enumeration for coordinates
     * @author Péter
     *
     */
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
    
    /**
     * Creates file given in parameter, removes if it already exists 
     * @param file to be created
     */
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
    
    /**
     * Converts hexadecimal value to decimal value
     * @param array to be converted
     * @return decimal value of the hexadecimal value
     */
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
    
    /**
     * Converts hexadecimal value to character
     * @param hex to be converted
     * @return char type character of the hexadecimal value
     */
    public static char convertHex2Char(String hex) {
        int hex2decimal = Integer.parseInt(hex, 16);
        char character = (char) hex2decimal;

        return character;
    }
    
    /**
     * Reverses array
     * @param array to be reserved
     * @return reserved array
     */
    public static String[] reversArray(String[] array) {
        int reverseArrayLength = array.length;
        String[] reversArray = new String[reverseArrayLength];
        for (int elementCounter = 0; elementCounter < array.length; elementCounter++) {
            reversArray[--reverseArrayLength] = array[elementCounter];
        }

        return reversArray;
    }
    
    /**
     * Reads file to the memory
     * @param data to be read
     * @return ArrayList type datastructure in memory
     */
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
    
    /**
     * Converts WGS84 to HD72EOV
     * @param y latitude
     * @param x longitude
     * @return converted coordinates
     */
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
    
    /**
     * Converts HD72EOV to WGS84 
     * @param y latitude
     * @param x longitude
     * @return converted coordinates
     */
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
    
    /**
     * Generates random number in symmetric range
     * @param symmetricRange symmetric range value
     * @return generated random value
     */
    public static int generateRandomValue(int symmetricRange) {
        Random generateRandomValue = new Random();
        int randomValue = generateRandomValue.nextInt((2 * symmetricRange) + 1) - symmetricRange;

        return randomValue;
    }
    
    /**
     * Filters datasource
     * @param nthRow every nthRow to be read
     * @param inputFile name of the source file to be filtered
     * @param derivedMeasurementFile name of the filtered result file
     * @param checkFile name of the checkfile for filtered result file
     * @param range modify signal strengths in range
     * @return name of the filtered result file
     */
    public static File filterDatabaseFile(int nthRow, File inputFile, File derivedMeasurementFile, File checkFile, int range) {
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
        
        System.out.println("Filtering measurement file " + derivedMeasurementFile);
        
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

        return derivedMeasurementFile;
    }
    
    /**
     * Decoordinates the given file
     * @param inputFile name of the source file to be decoordinated
     * @param outputFile name of the decoordinated file
     * @param checkFile checkfile for the decoordinated file
     * @return name of the decoordinated file
     */
    public static File decoordinate(File inputFile, File outputFile, File checkFile) {      
        System.out.println("Decoordinating " + inputFile + " file to " + outputFile);
        
        filterDatabaseFile(1, inputFile, outputFile, checkFile, 0);
                
        return outputFile;
    }
    
    /**
     * Calculates the mean value of the coordinates
     * @param rEngine R engine to be used
     * @param latitudeCoordinates ArrayList type list containing the latitude coordinates
     * @param longitudeCoordinates ArrayList type list containing the longitude coordinates
     * @return calculated mean value of the coordinates
     */
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
    
    /**
     * Sorts HashMap
     * @param srcData source HashMap to be sorted
     * @param byData sort by datatype
     * @return sorted HashMap
     */
    public static HashMap<String, Integer> sortHashMap(HashMap<String,Integer> srcData, DATATYPE byData) {
        HashMap<String, Integer> sortedMap = null;
        Set<Entry<String, Integer>> hashmapEntrySet = null;
        List<Entry<String, Integer>> entries = null;
        
        sortedMap = new LinkedHashMap<String, Integer>();
        hashmapEntrySet = srcData.entrySet();
        entries = new LinkedList<>(hashmapEntrySet);
        
        Collections.sort(entries, new Comparator<Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> entry1, Entry<String, Integer> entry2) {
                int result = 0;
                
                if (byData == DATATYPE.SIGNALSTRENGTH) {
                    result = entry2.getValue().compareTo(entry1.getValue());
                } else if (byData == DATATYPE.CELLNAME) {
                    result = entry1.getKey().compareTo(entry2.getKey());
                }
                
                return result;
            }
        
        });
        
        for (Entry<String, Integer> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        
        return sortedMap;
    }
    
    /**
     * Sorts HashMap by the keys of a reference HashMap
     * @param srcData source HashMap to be sorted
     * @param refData reference HashMap
     * @return sorted HashMap
     */
    public static HashMap<String, Integer> sortHashMapByReference(HashMap<String,Integer> srcData, HashMap<String,Integer> refData) {
        HashMap<String, Integer> result = null;
        Set<String> refList = null;
        
        result = new LinkedHashMap<String, Integer>();
        refList = refData.keySet();
        
        for (String refElement : refList) {
            result.put(refElement, srcData.get(refElement));
        }
        
        return result;
    }
    
    /**
     * Create datasource from measurements
     * @param srcFiles list containing the measurements
     * @param outputFile name of the datasource to be created
     * @return name of the datasource file
     */
    public static File createDatabaseFromMeasurements(File[] srcFiles, File outputFile) {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        ArrayList<String> readedFile = null;
        String headerRow = null;
        String rowToWrite = null;
        boolean isHeaderAdded = false;
        
        try {
            fileWriter = new FileWriter(outputFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            
            for (File srcFile : srcFiles) {
                readedFile = readFileToMemory(srcFile);
                
                if (isHeaderAdded == false) {
                    headerRow = readedFile.get(0);
                    rowToWrite = String.join(",", headerRow);
                    isHeaderAdded = true;
                    bufferedWriter.write(rowToWrite);
                    bufferedWriter.newLine();
                }
                
                for (int rowCounter = 1; rowCounter < readedFile.size(); rowCounter++) {
                    rowToWrite = String.join(",", readedFile.get(rowCounter));
                    bufferedWriter.write(rowToWrite);
                    bufferedWriter.newLine();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            try {
                bufferedWriter.close();
                fileWriter.close();
                readedFile = null;
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        
        return outputFile;
    }
    
    /**
     * Melts GSM900 and GSM1800 simulation datasources
     * @param gsmFile file name of the GSM900 file
     * @param dcsFile file name of the GSM1800 file
     * @param outputFile name of the melted simulation datasource to be created
     */
    public static void meltGsmDcs(File gsmFile, File dcsFile, File outputFile) {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        HashMap<String, String> coordinatesAndValues = null;
        HashMap<String, String> filteredCoordinatesAndValues = null;
        Set<Entry<String, String>> filteredCoordinateAndValuesEntries = null; 
        ArrayList<String> data = null;
        String[] splittedDataRow = null;
        String latitude = null;
        String longitude = null;
        String coordinate = null;
        String cellsAndSignals = null;
        String modifiedCellsAndSignal = null;
        String valuesToCoordinate = null;
        String rowToWrite = null;
        String headerRow = null;
        
        System.out.println("Merging GSM and DCS files to " + outputFile);
        
        createFile(outputFile);
        coordinatesAndValues = new LinkedHashMap<String, String>();
        filteredCoordinatesAndValues = new LinkedHashMap<String, String>();
        data = new ArrayList<String>();
        headerRow = "latitude,longitude,cellID,signalStrength";
                
        try {
            fileWriter = new FileWriter(outputFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(headerRow);
            bufferedWriter.newLine();
            data = readFileToMemory(gsmFile);
          
            for (int rowCounter = 1; rowCounter < data.size(); rowCounter++) {
                splittedDataRow = null;
                splittedDataRow = data.get(rowCounter).split(",",3);
                latitude = splittedDataRow[0];
                longitude = splittedDataRow[1];
                cellsAndSignals = splittedDataRow[2];
                coordinate = latitude + "," + longitude;
                coordinatesAndValues.put(coordinate, cellsAndSignals);
            }
            
            data = readFileToMemory(dcsFile);
            
            for (int rowCounter = 1; rowCounter < data.size(); rowCounter++) {
                splittedDataRow = null;
                splittedDataRow = data.get(rowCounter).split(",",3);
                latitude = splittedDataRow[0];
                longitude = splittedDataRow[1];
                cellsAndSignals = splittedDataRow[2];
                coordinate = latitude + "," + longitude;

                if (coordinatesAndValues.containsKey(coordinate)) {
                    valuesToCoordinate = coordinatesAndValues.get(coordinate);
                    modifiedCellsAndSignal =  valuesToCoordinate + "," + cellsAndSignals;
                    filteredCoordinatesAndValues.put(coordinate, modifiedCellsAndSignal);
                }
            }
                      
            filteredCoordinateAndValuesEntries = filteredCoordinatesAndValues.entrySet();
            
            for (Entry<String, String> filteredCoordinateAndCellsEntry : filteredCoordinateAndValuesEntries) {
                rowToWrite = filteredCoordinateAndCellsEntry.getKey() + "," + filteredCoordinateAndCellsEntry.getValue();
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
}
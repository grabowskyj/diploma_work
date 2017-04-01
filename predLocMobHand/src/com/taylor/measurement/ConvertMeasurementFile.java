package com.taylor.measurement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.taylor.simulation.ConvertDatFile;
import com.taylor.tools.*;

public class ConvertMeasurementFile {
    
    private File srcFile;
    private File csvFile;
    
    public ConvertMeasurementFile(File srcFile, File csvFile) {
        this.setSrcFile(srcFile);
        this.setCsvFile(csvFile);
    }
    
    private void setSrcFile(File srcFile) {
        this.srcFile = srcFile;
    }
    
    public File getSrcFile() {
        return srcFile;
    }
    
    private void setCsvFile(File csvFile) {
        this.csvFile = csvFile;
        Tools.createFile(getCsvFile());
    }
    
    public File getCsvFile() {
        return csvFile;
    }

    private ArrayList<String> readMeasurement() {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        ArrayList<String> data = new ArrayList<String>();
        
        try {
            fileReader = new FileReader(getSrcFile());
            bufferedReader = new BufferedReader(fileReader);
            String readedLine;
            while((readedLine = bufferedReader.readLine()) != null){
                data.add(readedLine);
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
        
        return data;
    }
    
    public File convertMeasurement2Csv() {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        ArrayList<String> data = readMeasurement();
        String separator = ";";
        String headerRowToCsv = "latitude,longitude,cellID,signalStrength,n1cellID,n1signalStrength,n2cellID,n2signalStrength,n3cellID,n3signalStrength,n4cellID,n4signalStrength,n5cellID,n5signalStrength,n6cellID,n6signalStrength";
        String dataRowToCsv = null;
        String[] dataRow = null;
        
        try {
            fileWriter = new FileWriter(getCsvFile());
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(headerRowToCsv);
            bufferedWriter.newLine();
            for (int rowCounter = 1; rowCounter < data.size(); rowCounter++ ) {
                dataRow = data.get(rowCounter).trim().split(separator);
                dataRowToCsv = prepareArrayForWrite(dataRow);
                bufferedWriter.write(dataRowToCsv);
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
        
        return getCsvFile();
    }
    
    private String prepareArrayForWrite(String[] array) {
        List<String> setOfData = new ArrayList<String>();
        int[] neededCellOfArray = new int[]{12,13,1,6,19,21,22,24,25,27,28,30,31,33,34,36}; 
        for (int cellNum : neededCellOfArray) {
            if (cellNum <= (array.length - 1)) {
                setOfData.add(array[cellNum]);
            } else {
                break;
            }
        }
        String[] filledArray = new String[setOfData.size()];
        filledArray = setOfData.toArray(filledArray);        
        String rowToWrite = String.join(",", filledArray);
        
        return rowToWrite;
    }

    public static void main(String[] args) {
        
        File datFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\Veresegyhaz_bestserver_bestserver.dat");
        File convFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\veresegyhaz_25m_bestserver.conv");
        File simCsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\veresegyhaz_25m_bestserver.csv");
        File dat2File = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\sample_25m_bestserver.dat");
        File conv2File = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\sample_25m_bestserver.conv");
        File sim2CsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\sample_25m_bestserver.csv");
        //File measurementFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\measurement_data\\gmon_gsm_rxl_2017_03_25_08_08_06.txt");
        //File measurementCsvFile = new File("D:\\Dokumentumok\\GIT\\diploma_work\\test_dir\\gmon_gsm_rxl_2017_03_25_08_08_06.csv");
        
        ConvertDatFile sim = new ConvertDatFile(datFile, convFile, simCsvFile);
        ConvertDatFile sim2 = new ConvertDatFile(dat2File, conv2File, sim2CsvFile);
        //ConvertMeasurementFile measurement = new ConvertMeasurementFile(measurementFile, measurementCsvFile);
        sim2.convertDat2Csv();
        sim.convertDat2Csv();
        
        //measurement.convertMeasurement2Csv();
    }
}

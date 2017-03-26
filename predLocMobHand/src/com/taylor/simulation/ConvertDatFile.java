package com.taylor.simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;

public class ConvertDatFile {
    
    private File datFile;

    ConvertDatFile(File datFile) {
        this.setDatFile(datFile);
    }

    public File getDatFile() {
        return datFile;
    }

    public void setDatFile(File datFile) {
        this.datFile = datFile;
    }
    
    public ArrayList<String> readDatFile() {
        FileInputStream readFile = null;
        ArrayList<String> dataRow = new ArrayList<String>();
        ArrayList<String> headerData = new ArrayList<String>();
        ArrayList<String> bestServerData = new ArrayList<String>();
        ArrayList<String> fileData = new ArrayList<String>();
        String hexNewLine = "a";
        String hexCarriageReturn = "d";
        String readedChar = null;
        int read;
        /*int eastMin = 0;
        int eastMax = 0;
        int northMin = 0;
        int northMax = 0;
        int resolution = 0;
        
        ezek majd feldolgozasnal kellenek*/
        
        try {
            readFile = new FileInputStream(getDatFile());
            int c = 0;
            while((read = readFile.read()) != -1) {
                readedChar = Integer.toHexString(read);
                if (!readedChar.equals(hexCarriageReturn) || !readedChar.equals(hexNewLine)) {
                    dataRow.add(readedChar);
                    if ((headerData.size() == 2) && (dataRow.size() == 10)) {
                        bestServerData.addAll(dataRow);
                        dataRow.clear();
                    }
                }
                if (readedChar.equals(hexCarriageReturn) && (dataRow.size() > 0)) {
                    headerData.addAll(dataRow);
                    dataRow.clear();
                }
                
                System.out.print(Integer.toHexString(read) + " ");
                c++;
                if (c == 100) {
                    System.exit(1);
                }
            }
            fileData.addAll(headerData);
            fileData.addAll(bestServerData);
            readFile.close();           
        } catch(Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        
        return fileData;
    }
    
    public static void main(String[] args) {
        
        File simFile = new File("C:\\Users\\eptrszb\\git\\diploma_work\\minta1_25m_bestserver.dat");
        
        ConvertDatFile simData = new ConvertDatFile(simFile);
        
        simData.readDatFile();    
        
    }
    
}
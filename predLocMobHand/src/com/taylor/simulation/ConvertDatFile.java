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
    
    public void readDatFile() {
        FileInputStream readFile = null;
        int read;
        ArrayList<String> dataRow = new ArrayList<String>();
        ArrayList<String> convertData = new ArrayList<String>();
        String hexNewLine = "a";
        String hexCarriageReturn = "d";
        String readedChar = null;
        int eastMin = 0;
        int eastMax = 0;
        int northMin = 0;
        int northMax = 0;
        int resolution = 0;
        int bestServerDataRowLength = 10;
        
        try {
            readFile = new FileInputStream(getDatFile());
            int c = 0;
            while((read = readFile.read()) != -1) {
                readedChar = Integer.toHexString(read);
                if (!readedChar.equals(hexNewLine) || !readedChar.equals(hexCarriageReturn)) {
                    dataRow.add(readedChar);
                }
                if (readedChar.equals(hexCarriageReturn) && (dataRow.size() > 0)) {
                    convertData.addAll(dataRow);
                }
                System.out.print(Integer.toHexString(read) + " ");
                c++;
                if (c == 100) {
                    System.exit(1);
                }
            }
        } catch(Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        
        File simFile = new File("C:\\Users\\eptrszb\\git\\diploma_work\\minta1_25m_bestserver.dat");
        
        ConvertDatFile simData = new ConvertDatFile(simFile);
        
        simData.readDatFile();    
        
    }
    
}
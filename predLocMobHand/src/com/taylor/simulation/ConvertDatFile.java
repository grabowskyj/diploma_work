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
    
    public ArrayList<ArrayList<String>> readDatFile() {
        FileInputStream readFile = null;
        ArrayList<String> dataRow = new ArrayList<String>();
        ArrayList<ArrayList<String>> headerData = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> bestServerData = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> fileData = new ArrayList<ArrayList<String>>();
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
                if (!readedChar.equals(hexCarriageReturn) && !readedChar.equals(hexNewLine)) {
                    dataRow.add(readedChar);
                    if ((headerData.size() == 2) && (dataRow.size() == 10)) {
                        bestServerData.add(dataRow);
                        dataRow.clear();
                    }
                }
                if (readedChar.equals(hexCarriageReturn) && (dataRow.size() > 0)) {
                    headerData.add(dataRow);
                    for (ArrayList<String> i : headerData) {
                        System.out.print(i + " ");
                    }
                    dataRow.clear();
                }
                
                //System.out.print(Integer.toHexString(read) + " ");
                c++;
                if (c == 150) {
                    //System.exit(1);
                    break;
                }
            }
            readFile.close();
            fileData.addAll(headerData);
            System.out.println("File Data Size: " + fileData.size());
            fileData.addAll(bestServerData);
            System.out.println("File Data Size: " + fileData.size());
            System.out.println("Header Data Size: " + headerData.size());
            System.out.println("Best Server Data Size: " + bestServerData.size());
            System.out.print("Header Data: ");
            for (ArrayList<String> i : headerData) {
                System.out.print(i + " ");
            }
            System.out.println("\n");
            System.out.print("Best Server Data: ");
            for (ArrayList<String> i : bestServerData) {
                System.out.print(i + " ");
            }
            
        } catch(Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        
        return fileData;
    }
    
    public static void main(String[] args) {
        
        File simFile = new File("C:\\Users\\eptrszb\\git\\diploma_work\\minta1_25m_bestserver.dat");
        ArrayList<ArrayList<String>> simDataInList = new ArrayList<ArrayList<String>>();
        ConvertDatFile simData = new ConvertDatFile(simFile);
        
        simDataInList = simData.readDatFile();
    }
    
}
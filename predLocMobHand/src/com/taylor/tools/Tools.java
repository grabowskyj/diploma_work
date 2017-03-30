package com.taylor.tools;

import java.io.File;
import java.io.IOException;

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
    
    public static String[] reversArray(String[] array) {
        int reverseArrayLength = array.length;
        String[] reversArray = new String[reverseArrayLength];
        for (int elementCounter = 0; elementCounter < array.length; elementCounter++ ) {
            reversArray[--reverseArrayLength] = array[elementCounter];
        }
        
        return reversArray;
    }
    
}
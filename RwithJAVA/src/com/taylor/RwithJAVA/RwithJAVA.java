package com.taylor.RwithJAVA;

import org.rosuda.JRI.Rengine;

public class RwithJAVA {

	public static void main(String[] args) {

		// Create an R vector in the form of a string.
        String javaVector = "c(1,2,3,4,5)";

        // Start Rengine.
        Rengine engine = new Rengine(new String[] { "--no-save" }, false, null);

        // The vector that was created in JAVA context is stored in 'rVector' which is a variable in R context.
        engine.eval("rVector=" + javaVector);
        
        //Calculate MEAN of vector using R syntax.
        engine.eval("meanVal=mean(rVector)");
        
        //Retrieve MEAN value
        double mean = engine.eval("meanVal").asDouble();
        
        //Print output values
        System.out.println("Vector: " + javaVector);
        System.out.println("Mean of given vector is: " + mean);

	}

}

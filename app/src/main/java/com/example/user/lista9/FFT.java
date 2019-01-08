package com.example.user.lista9;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class FFT {
    //klasa służąca do obliczania tranformaty Fouriera za pomoca biblioteki Apache Commons Math

    public static double[] computeFFT(double[] input) {
        //metoda obliczająca tranformatę

        FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD); //nowy obiekt do tworzenia tranformaty
        Complex[] complexResults = transformer.transform(input, TransformType.FORWARD); //wykonanie tranformaty z danych (forward - czyli zwykłej a nie odwrotnej)
        double output[] = new double[complexResults.length]; //tablica na wynik
        for (int i = 0; i < complexResults.length; i++) {
            //obliczenie modułu z uzyskanych wartosci rzeczywistych i urojonych
            output[i] = Math.sqrt(Math.pow(complexResults[i].getReal(), 2) + Math.pow(complexResults[i].getImaginary(), 2));

        }
        return output;
    }
}

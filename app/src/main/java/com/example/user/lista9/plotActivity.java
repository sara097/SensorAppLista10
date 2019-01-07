package com.example.user.lista9;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class plotActivity extends Activity {

    //Klasa służąca do obsługiwania aktywności, w której rysowany jest wykres danych zebranych z akcelerometru

    //atrybuty klasy
    //serie danych zbieranych aktualnie
    private XYSeries seriesX;
    private XYSeries seriesY;
    private XYSeries seriesZ;
    //serie danych odczytanych z pliku
    private XYSeries readSeriesX;
    private XYSeries readSeriesY;
    private XYSeries readSeriesZ;
    //elementy potrzebne do wykresu
    private XYMultipleSeriesRenderer mrenderer;
    private LinearLayout chartLayout;
    //elementy GUI
    private EditText fileName;
    private TextView stepText;

    private ArrayList<Double> valuesR; //lista tablicowa na wartosci odczytane z pliku
    protected int steps; //licznik kroków
    private double[] valsC; //tablica na wartosci zebrane przed chwilą

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //metoda onCreate
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);

        steps = 0; //inicjacja licznika kroków
        valuesR = new ArrayList<>(); //inicjacja listy tablicowej, służącej do przechowywania danych odczytanych z pliku

        //pobranie danych przekazanych przez aktywność Acceleration
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            seriesX = (XYSeries) extras.getSerializable("dataX");
            seriesY = (XYSeries) extras.getSerializable("dataY");
            seriesZ = (XYSeries) extras.getSerializable("dataZ");
            valsC = extras.getDoubleArray("steps");
        }

        //utworzenie rendererów serii danych i doprecyzowanie wyglądu serii danych na wykresie
        XYSeriesRenderer rendererX = new XYSeriesRenderer();
        rendererX.setLineWidth(2);
        rendererX.setColor(Color.MAGENTA);
        rendererX.setPointStyle(PointStyle.DIAMOND);
        rendererX.setPointStrokeWidth(6);
        rendererX.setLineWidth(3);

        XYSeriesRenderer rendererY = new XYSeriesRenderer();
        rendererY.setLineWidth(2);
        rendererY.setColor(Color.GREEN);
        rendererY.setPointStyle(PointStyle.DIAMOND);
        rendererY.setPointStrokeWidth(6);
        rendererY.setLineWidth(3);

        XYSeriesRenderer rendererZ = new XYSeriesRenderer();
        rendererZ.setLineWidth(2);
        rendererZ.setColor(Color.BLUE);
        rendererZ.setPointStyle(PointStyle.DIAMOND);
        rendererZ.setPointStrokeWidth(6);
        rendererZ.setLineWidth(3);

        //dodanie otworzonych wczesniej rendererów do listy rendererów i ustawienie maksimów i minimów wykresu
        mrenderer = new XYMultipleSeriesRenderer();
        mrenderer.addSeriesRenderer(rendererX);
        mrenderer.addSeriesRenderer(rendererY);
        mrenderer.addSeriesRenderer(rendererZ);
        mrenderer.setYAxisMax(15);
        mrenderer.setYAxisMin(-10);
        mrenderer.setShowGrid(true);

        //zainicjowanie elementów GUI
        chartLayout = (LinearLayout) findViewById(R.id.chartLayout);
        fileName = (EditText) findViewById(R.id.editTxtFileName);

        stepText = (TextView) findViewById(R.id.stepsTxt);

    }

    public int countSteps(double[] vals) {

        //metoda służąca do obliczania ilości wykonanych kroków
        //parametrem metody jest tablica typu double z wartościami
        steps=0;
        //metoda obliczania ilosci kroków jest bardzo przyblizona.
        //wykres jest piłokształtny, zatem sprawdzam czy punkt jest pikiem biorąc otoczenie az 5 punktów
        //jesli jest on pikiem zwiekszam licznik kroków
        for (int i = 5; i < vals.length - 5; i++) {
            if (vals[i] > vals[i - 5] && vals[i] > vals[i - 3] && vals[i] > vals[i - 1] && vals[i] > vals[i + 1] && vals[i] > vals[i + 3] && vals[i] > vals[i + 5])
                steps++;
        }

        //metoda zwraca ilosc kroków
        return steps;
    }

    public void drawCurrent(View view) {
        //metoda po kliknieciu ktorej rysowany jest wykres z przed chwila zebranych danych

        steps = 0; //zerowanie ilosci krokow
        chartLayout.removeAllViews(); //usuwanie tego co bylo na wykresie
        //dodanie serii danych do wykresu
        XYMultipleSeriesDataset mdataset = new XYMultipleSeriesDataset();
        mdataset.addSeries(seriesX);
        mdataset.addSeries(seriesY);
        mdataset.addSeries(seriesZ);

        //wyswietlenie w TextView ilosci kroków (poprzez wywołanie metody zliczającej kroki)
        stepText.setText(String.valueOf(countSteps(valsC)) + " steps");

        //wyświetlenie wykresu
        GraphicalView chartView = ChartFactory.getLineChartView(this, mdataset, mrenderer);
        chartLayout.addView(chartView);


    }

    public void drawFromFile(View view) {
        //metoda po kliknieciu ktorej rysowany jest wykres z danych z pliku
        steps = 0; //zerowanie ilosci kroków
        chartLayout.removeAllViews(); //usuwanie poprzedniej zawartosci wykresu

        //jesli nie podano nazwy pliku wyswietlany jest odpowiedni komunikat
        if (fileName.getText().toString().equals("")) {
            Toast.makeText(this, "Write file name", Toast.LENGTH_LONG).show();
        } else {
            //jesli nazwa pliku została podana zostaja odczytane wartosci z pliku
            String name = fileName.getText().toString() + ".txt";
            readData(name);

            //nastepnie zostają przepisane z listy tablicowej na tablice, ponieważ do metody obliczajacej kroki konieczna jest tablica
            double[] valsR = new double[valuesR.size()];
            for (int i = 0; i < valsR.length; i++) {
                valsR[i] = valuesR.get(i);
            }

            //wyswietlanie obliczonej ilosci korków
            stepText.setText(String.valueOf(countSteps(valsR)) + " steps");

            //dodanie danych do wykresu
            XYMultipleSeriesDataset mdataset = new XYMultipleSeriesDataset();
            mdataset.addSeries(readSeriesX);
            mdataset.addSeries(readSeriesY);
            mdataset.addSeries(readSeriesZ);

            //wyswietlenie wykresu
            GraphicalView chartView = ChartFactory.getLineChartView(this, mdataset, mrenderer);
            chartLayout.addView(chartView);
            //chartView.repaint();

        }


    }

    public void readData(String name) {

        //metoda służąca do odczytywanie danych z pliku
        int counter = 0;
        double aX = 0;
        double aY = 0;
        double aZ = 0;
        //zainicjowanie serii danych
        readSeriesX = new XYSeries("data X");
        readSeriesY = new XYSeries("data Y");
        readSeriesZ = new XYSeries("data Z");
        valuesR.removeAll(valuesR); //usuniecie tego co poprzednio bylo w tablicy
        try {
            //utworzenie pliku a nastepnie InputStreamReadera i BufferedReadera
            FileInputStream fis = openFileInput(name);
            InputStreamReader reader = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String strLine = null;
            //jesli istnieje linia odczytanie jej zawartosci
            if ((strLine = bufferedReader.readLine()) != null) {

                //rozdzielenie poszczegolnych pomiarów (rozdzielone były wykrzyknikami)
                String[] lines = strLine.split("!");
                String[] line = null;

                for (String line1 : lines) {
                    //wszystkie pomiary mialy po kilka wartosci rozdzielonych sredniakmi, wiec je tez rodzielamy
                    line = line1.split(";");
                    counter = Integer.valueOf(line[0]);
                    aX = Double.valueOf(line[1]);
                    aY = Double.valueOf(line[2]);
                    aZ = Double.valueOf(line[3]);
                    //dodajemy do serii danych wartosci
                    readSeriesX.add(counter, aX);
                    readSeriesY.add(counter, aY);
                    readSeriesZ.add(counter, aZ);
                    //dodajemy wartosci do listy tablicowej, potrzebnej do obliczania ilosci kroków
                    valuesR.add(aZ);
                }
            }


            bufferedReader.close();
            reader.close();
            fis.close();

        } catch (java.io.IOException e) {
            //obsługa wyjątku wraz z wyswietleniem uzytkownikowi komunikatu
            Toast.makeText(this, "Cannot read data", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }


}

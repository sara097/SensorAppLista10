package com.example.user.lista9;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class Gyroscope extends Activity implements SensorEventListener {

    //klasa Gyroscope (rozszerzająca Activity i implementująca SensorEventListener)
    // odpowiada za aktywność aplikacji związaną z pomiarami za pomocą żyroskopu

    //atrybuty klasy
    private static final String TAG = "Gyroscope"; //tag klasy
    //czujniki i powerManager
    private SensorManager mySensorManager;
    private Sensor gyroscope;
    private PowerManager powerManager;
    private PowerManager.WakeLock myWakeLock;

    private boolean isRunning = false; //zmienna okreslajaca czy zbieramy dane czy nie

    private int counter = 0; //licznik (do osi OX wykresu)
    //elementy GUI
    private TextView textViewAx;
    private TextView textViewAy;
    private TextView textViewAz;
    //serie danych do wykresu
    private XYSeries seriesX;
    private XYSeries seriesY;
    private XYSeries seriesZ;
    //elementy potrzebne do wykresu
    private XYMultipleSeriesRenderer mrenderer;
    private LinearLayout chartLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //metoda przy tworzeniu aktywności
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);

        //elementy gui
        textViewAx = (TextView) findViewById(R.id.xTxt);
        textViewAy = (TextView) findViewById(R.id.yTxt);
        textViewAz = (TextView) findViewById(R.id.zTxt);

        //serie danych do wykresu
        seriesX = new XYSeries("X");
        seriesY = new XYSeries("Y");
        seriesZ = new XYSeries("Z");

        //ustawienie czujnika - żyroskopu
        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscope = mySensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mySensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);

        //wake lock zeby aplikacja mogla dzialac, gdy telefon zostanie zablokowany
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        myWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:test");

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //metoda reagujaca na zmianę wartosci rejestrowanej przez czujnik

        if (isRunning) { //gdy pomiar jest wykonywany

            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) { //gdy typ sensora to żyroskop

                //zapisujemy do zmiennych skladowe mierzonej prędkości kątowej oraz czas
                float aX = event.values[0]; //skladowa x
                float aY = event.values[1];
                float aZ = event.values[2];
                float timeStamp = event.timestamp; //czas w nano

                counter++;// zwiekszam licznik
                //licznik jest potrzebny do wykonywania wykresu
                // (kolejne wartosci są bardziej czytelne niż timeStamp w nanosekundach)

                //wyswietlam wartosci skladowej x prędkości kątowej i czasu
                Log.d(TAG, "aX= " + Float.toString(aX) + " timeStamp " + Float.toString(timeStamp));

                //wyświetlanie w okienkach aktualnych wartosci skladowych prędkości kątowej (sformatowanych)
                String x = String.format("%.2f", aX);
                String y = String.format("%.2f", aY);
                String z = String.format("%.2f", aZ);

                textViewAx.setText(x);
                textViewAy.setText(y);
                textViewAz.setText(z);

                //dodanie do serii danych wartosci skladowych prędkości kątowej
                seriesX.add(counter, aX);
                seriesY.add(counter, aY);
                seriesZ.add(counter, aZ);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //nieuzywana metoda ktora trzeba bylo zaimplementować z interfejsu
    }

    public void drawPlot(View view) {
        //metoda po kliknieciu ktorej rysowany jest wykres z przed chwila zebranych danych

        isRunning = false; //zatrzymanie pomiaru

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

        //zainicjowanie layoutu z wykresem
        chartLayout = (LinearLayout) findViewById(R.id.plotLayout);

        chartLayout.removeAllViews();//usuwanie tego co bylo na wykresie
        //dodanie serii danych do wykresu
        XYMultipleSeriesDataset mdataset = new XYMultipleSeriesDataset();
        mdataset.addSeries(seriesX);
        mdataset.addSeries(seriesY);
        mdataset.addSeries(seriesZ);

        //wyświetlenie wykresu
        GraphicalView chartView = ChartFactory.getLineChartView(this, mdataset, mrenderer);
        chartLayout.addView(chartView);

    }

    public void startMeasure(View view) {
        //metoda po kliknieciu start

        Log.d(TAG, "Button pressed");
        isRunning = !isRunning; //zmienna ktora docyduje o tym, czy dokonujemy pomiaru czy tez nie

        if (isRunning) {
            myWakeLock.acquire();  //jesli pomiar ma byc wykonany musimy tez pozwolic aplikacji na pomiary przy zablokowanym telefonie
            //czyścimy serie danych oraz zerujemy licznik
            seriesX.clear();
            seriesY.clear();
            seriesZ.clear();
            counter = 0;

        } else {
            myWakeLock.release(); //gdy zatrzymujemy pomiar zabraniamy zbierania danych przy zablokowanym ekranie

        }
    }
}

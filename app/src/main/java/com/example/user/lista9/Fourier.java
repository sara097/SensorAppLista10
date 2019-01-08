package com.example.user.lista9;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class Fourier extends Activity {

    //serie danych zbieranych aktualnie
    private XYSeries seriesX = new XYSeries("x");
    private XYSeries seriesY = new XYSeries("y");
    private XYSeries seriesZ = new XYSeries("z");
    //elementy potrzebne do wykresu
    private XYMultipleSeriesRenderer mrenderer;
    private LinearLayout chartLayout;
    //tablice z danymi otrzynamymi z poprzedniej aktywnosci
    private double[] x;
    private double[] y;
    private double[] z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourier);

        //pobranie danych z poprzedniej aktywnosci
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            x = extras.getDoubleArray("x");
            y = extras.getDoubleArray("y");
            z = extras.getDoubleArray("z");

        }

        //utworzenie serii danych
        for (int i = 0; i < x.length; i++) {
            seriesX.add(i, x[i]);
            seriesY.add(i, y[i]);
            seriesZ.add(i, z[i]);
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
        //dodanie serii danych do wykresu
        XYMultipleSeriesDataset mdataset = new XYMultipleSeriesDataset();
        mdataset.addSeries(seriesX);
        mdataset.addSeries(seriesY);
        mdataset.addSeries(seriesZ);
        //wyświetlenie wykresu
        GraphicalView chartView = ChartFactory.getLineChartView(this, mdataset, mrenderer);
        chartLayout.addView(chartView);
    }
}

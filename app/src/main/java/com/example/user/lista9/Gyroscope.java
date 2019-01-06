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

    private static final String TAG = "Gyroscope";
    private SensorManager mySensorManager;
    private Sensor gyroscope;
    private PowerManager powerManager;
    private PowerManager.WakeLock myWakeLock;
    private boolean isRunning = false;
    private int counter=0;
    private TextView textViewAx;
    private TextView textViewAy;
    private TextView textViewAz;
    private XYSeries seriesX;
    private XYSeries seriesY;
    private XYSeries seriesZ;
    private XYMultipleSeriesRenderer mrenderer;
    private LinearLayout chartLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);

        textViewAx=(TextView) findViewById(R.id.xTxt);
        textViewAy=(TextView) findViewById(R.id.yTxt);
        textViewAz=(TextView) findViewById(R.id.zTxt);

        seriesX=new XYSeries("X");
        seriesY=new XYSeries("Y");
        seriesZ=new XYSeries("Z");

        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscope = mySensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mySensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        myWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:test");

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (isRunning) {

            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

                float aX = event.values[0]; //skladowa x
                float aY=event.values[1];
                float aZ=event.values[2];
                float timeStamp=event.timestamp; //czas w nano
                counter++;

                Log.d(TAG, "aX= " + Float.toString(aX) +" timeStamp "+Float.toString(timeStamp));

                String x=Float.toString(aX);
                String y=Float.toString(aY);
                String z=Float.toString(aZ);

                if(x.length()<=4){
                    textViewAx.setText(x);
                }else{
                    if(aX<0) textViewAx.setText(x.subSequence(0,6));
                    else textViewAx.setText(x.subSequence(0,5));
                }

                if(y.length()<=4){
                    textViewAy.setText(y);
                }else{
                    if(aY<0) textViewAy.setText(y.subSequence(0,6));
                    else textViewAy.setText(y.subSequence(0,5));
                }

                if(z.length()<=4){
                    textViewAz.setText(z);
                }else{
                    if(aZ<0) textViewAz.setText(z.subSequence(0,6));
                    else textViewAz.setText(z.subSequence(0,5));
                }

                seriesX.add(counter, aX);
                seriesY.add(counter, aY);
                seriesZ.add(counter, aZ);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void drawPlot(View view) {
        isRunning=false;

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

        mrenderer=new XYMultipleSeriesRenderer();
        mrenderer.addSeriesRenderer(rendererX);
        mrenderer.addSeriesRenderer(rendererY);
        mrenderer.addSeriesRenderer(rendererZ);
        mrenderer.setYAxisMax(15);
        mrenderer.setYAxisMin(-10);
        mrenderer.setShowGrid(true);

        chartLayout =(LinearLayout) findViewById(R.id.plotLayout);

        chartLayout.removeAllViews();
        XYMultipleSeriesDataset mdataset=new XYMultipleSeriesDataset();
        mdataset.addSeries(seriesX);
        mdataset.addSeries(seriesY);
        mdataset.addSeries(seriesZ);

        GraphicalView chartView = ChartFactory.getLineChartView(this,mdataset,mrenderer);
        chartLayout.addView(chartView);
        chartView.repaint();

    }

    public void startMeasure(View view) {

        Log.d(TAG, "Button pressed");
        isRunning = !isRunning;

        if (isRunning) {
            myWakeLock.acquire();
            seriesX.clear();
            seriesY.clear();
            seriesZ.clear();
            counter=0;

        }else {
            myWakeLock.release();

        }
    }
}

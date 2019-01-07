package com.example.user.lista9;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity {

    //MainActivity to aktywność odpowiadająca za ekran początkowy aplikacji
    // z tej aktywnosci mozemy przejść do pomiaru akcelerometrem oraz żyroskopem

    //Atrybuty klasy - intencje, żeby otworzyć nowe aktywności
    private Intent accelerometer;
    private Intent gyroscope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //utworzenie nowych intencji
        accelerometer = new Intent(getBaseContext(), Acceleration.class);
        gyroscope = new Intent(getBaseContext(), Gyroscope.class);
    }

    //metoda otwierająca aktywność Accelerometer po kliknięciu przycisku
    public void accelerometerClicked(View view) {
        startActivity(accelerometer);
    }

    //metoda otwierająca aktywność Gyroscope po kliknięciu przycisku
    public void gyroscopeClicked(View view) {
        startActivity(gyroscope);
    }
}

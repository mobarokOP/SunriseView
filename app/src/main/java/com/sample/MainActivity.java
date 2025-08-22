package com.sample;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.sunriseview.SunriseView;

import java.time.LocalTime;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {




    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SunriseView sunView = findViewById(R.id.sunriseView);
        sunView.setRatio(7, 30, 18, 30, /* now */ LocalTime.now());




    }

}


package com.sample;

import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.sunriseview.SunriseView;
import com.sunriseview.oters.SunriseSunsetLabelFormatter;
import com.sunriseview.oters.Time;

import java.time.LocalTime;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {


    private SunriseView mSunriseSunsetView;
    private TextView mSunriseTextView;
    private TextView mSunsetTextView;


    private TimePickerDialog mTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }


    private void initViews() {

        mSunriseTextView = findViewById(R.id.sunrise_time_tv);
        mSunsetTextView = findViewById(R.id.sunset_time_tv);
        Button updateButton = findViewById(R.id.update_btn);

        int sunriseHour = 6;
        int sunriseMinute = 17;
        int sunsetHour = 18;
        int sunsetMinute = 32;

        mSunriseSunsetView = findViewById(R.id.sunriseView);
        mSunriseSunsetView.setLabelFormatter(new SunriseSunsetLabelFormatter() {
            @Override
            public String formatSunriseLabel(@NonNull Time sunrise) {
                return formatLabel(sunrise);
            }

            @Override
            public String formatSunsetLabel(@NonNull Time sunset) {
                return formatLabel(sunset);
            }

            private String formatLabel(Time time) {
                return String.format(Locale.getDefault(), "%02dh %02dm", time.hour, time.minute);
            }
        });
        // initial some custom attributions
        // mSunriseSunsetView.setLabelTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        // mSunriseSunsetView.setLabelTextSize(30);
        // mSunriseSunsetView.setTrackColor(ContextCompat.getColor(this, R.color.amber));
        // mSunriseSunsetView.setSunColor(ContextCompat.getColor(this, R.color.teal));
        // mSunriseSunsetView.setShadowColor(ContextCompat.getColor(this, R.color.indigo));

        refreshSSV(sunriseHour, sunriseMinute, sunsetHour, sunsetMinute);

        mSunriseTextView.setText(String.format("%02d:%02d", sunriseHour, sunriseMinute));
        mSunsetTextView.setText(String.format("%02d:%02d", sunsetHour, sunsetMinute));

        mSunriseTextView.setOnClickListener(new ClickListener(mSunriseTextView));
        mSunsetTextView.setOnClickListener(new ClickListener(mSunsetTextView));

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] srArr = mSunriseTextView.getText().toString().split(":");
                int sunriseHour = Integer.valueOf(srArr[0]);
                int sunriseMinute = Integer.valueOf(srArr[1]);
                String[] ssArr = mSunsetTextView.getText().toString().split(":");
                int sunsetHour = Integer.valueOf(ssArr[0]);
                int sunsetMinute = Integer.valueOf(ssArr[1]);
                refreshSSV(sunriseHour, sunriseMinute, sunsetHour, sunsetMinute);
            }
        });
    }



    private class ClickListener implements View.OnClickListener {

        private TextView mSource;

        ClickListener(TextView source) {
            mSource = source;
        }

        @Override
        public void onClick(View v) {
            showTimePicker();
        }

        private void showTimePicker() {
            String[] timeArr = mSource.getText().toString().split(":");
            int hourOfDay = Integer.parseInt(timeArr[0]);
            int minute = Integer.parseInt(timeArr[1]);

            boolean is24HourMode = true; // চাইলে false দিলে 12-hour AM/PM format হবে

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    MainActivity.this,
                    (view, hour, min) -> {
                        // নতুন টাইম সেট হলে TextView আপডেট হবে
                        mSource.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, min));
                    },
                    hourOfDay,
                    minute,
                    is24HourMode
            );

            timePickerDialog.show();
        }
    }


    private void refreshSSV(int sunriseHour, int sunriseMinute, int sunsetHour, int sunsetMinute) {
        mSunriseSunsetView.setSunriseTime(new Time(sunriseHour, sunriseMinute));
        mSunriseSunsetView.setSunsetTime(new Time(sunsetHour, sunsetMinute));
        mSunriseSunsetView.startAnimate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimePicker != null) {
            mTimePicker.dismiss();
            mTimePicker = null;
        }
    }


}


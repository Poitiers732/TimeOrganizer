package com.example.wojtek.timeorganizer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class StartScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        final Intent i = new Intent(StartScreen.this,MainActivity.class);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startActivity(i);
            }
        };

        Timer startTime = new Timer();
        startTime.schedule(task, 15);

    }

    @Override
    public void onBackPressed() {
    }
}

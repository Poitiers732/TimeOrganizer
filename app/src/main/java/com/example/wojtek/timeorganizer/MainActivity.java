package com.example.wojtek.timeorganizer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
    String today = df.format(Calendar.getInstance().getTime());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void OnButtonClick(View v){
        if(v.getId() == R.id.todoButton){
            Intent i = new Intent(MainActivity.this,ToDo.class);
            startActivity(i);
        }

    }

    @Override
    public void onBackPressed() {
    }
}

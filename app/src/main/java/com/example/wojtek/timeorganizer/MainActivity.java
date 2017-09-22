package com.example.wojtek.timeorganizer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

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

        if(v.getId() == R.id.mealsButton){
            Intent i = new Intent(MainActivity.this,Meals.class);
            startActivity(i);
        }

        if(v.getId() == R.id.trainingButton){
            Intent i = new Intent(MainActivity.this,Training.class);
            startActivity(i);
        }

        if(v.getId() == R.id.closeButton){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
    }
}

package com.example.wojtek.timeorganizer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;



/**
 * Created by Wojtek on 21.09.2017.
 */

public class ToDo extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

    @BindView(R.id.calendarView)
    MaterialCalendarView widget;


    //@BindView(R.id.textViewTime)
    TextView textViewDate;

    AutoCompleteTextView textIn;
    AutoCompleteTextView textDate;
    Button buttonAdd;
    LinearLayout container;
    TextView reList, info;

    private static final String[] NUMBER = new String[] {
            "One", "Two", "Three", "Four", "Five",
            "Six", "Seven", "Eight", "Nine", "Ten"
    };

    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo);
        ButterKnife.bind(this);

        widget.setOnDateChangedListener(this);
        widget.setOnMonthChangedListener(this);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, NUMBER);

        adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, NUMBER);

        textIn = (AutoCompleteTextView)findViewById(R.id.textin);
        textIn.setAdapter(adapter);


        textDate = (AutoCompleteTextView)findViewById(R.id.textin);
        textDate.setAdapter(adapter2);


        buttonAdd = (Button)findViewById(R.id.add);
        container = (LinearLayout) findViewById(R.id.container);
        reList = (TextView)findViewById(R.id.relist);
        reList.setMovementMethod(new ScrollingMovementMethod());
        info = (TextView)findViewById(R.id.info);
        info.setMovementMethod(new ScrollingMovementMethod());
        textViewDate = (TextView)findViewById(R.id.textViewDate) ;



    buttonAdd.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View addView = layoutInflater.inflate(R.layout.row, null);
            AutoCompleteTextView textOut = (AutoCompleteTextView)addView.findViewById(R.id.textout);
            AutoCompleteTextView textDate = (AutoCompleteTextView)addView.findViewById(R.id.textdate);

            textOut.setAdapter(adapter);
            textOut.setText(textIn.getText().toString());

            textDate.setAdapter(adapter2);
            textDate.setText(textViewDate.getText().toString());


            Button buttonRemove = (Button)addView.findViewById(R.id.remove);


            final View.OnClickListener thisListener = new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    info.append("thisListener called:\t" + this + "\n");
                    info.append("Remove addView: " + addView + "\n\n");
                    ((LinearLayout)addView.getParent()).removeView(addView);

                    listAllAddView();
                }
            };

            buttonRemove.setOnClickListener(thisListener);
            container.addView(addView);

            info.append(
                    "thisListener:\t" + thisListener + "\n"
                            + "addView:\t" + addView + "\n\n"
            );

            listAllAddView();
        }
    });
}

    private void listAllAddView(){
        reList.setText("");


        int childCount = container.getChildCount();
        for(int i=0; i<childCount; i++){
            View thisChild = container.getChildAt(i);
            reList.append(thisChild + "\n");

            AutoCompleteTextView childTextView = (AutoCompleteTextView) thisChild.findViewById(R.id.textout);
            String childTextViewValue = childTextView.getText().toString();
            reList.append("= " + childTextViewValue + "\n");
        }
    }



    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        textViewDate.setText(getSelectedDatesString());
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(FORMATTER.format(date.getDate()));

    }

    private String getSelectedDatesString() {
        CalendarDay date = widget.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        return FORMATTER.format(date.getDate());
    }



}


package com.example.wojtek.timeorganizer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.Time;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.wojtek.timeorganizer.R.id.calendarView;
import static com.example.wojtek.timeorganizer.R.id.list_item;
import static com.example.wojtek.timeorganizer.R.id.match_parent;
import static com.example.wojtek.timeorganizer.R.id.taskTextView;
import static com.example.wojtek.timeorganizer.R.id.text;
import static com.example.wojtek.timeorganizer.R.id.wrap_content;
import static com.example.wojtek.timeorganizer.R.string.calendar;


/**
 * Created by Wojtek on 21.09.2017.
 */

public class ToDo extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

    //SQL
    DBAdapter myDB;

    @BindView(R.id.calendarView)
    MaterialCalendarView widget;

    //@BindView(R.id.textViewTime)
    TextView textViewDate;

    AutoCompleteTextView textIn;
    TextView textDate;
    Button buttonAdd, setToday, showAllTasks;
    LinearLayout container;
    TextView reList, info;

    TextView taskTextView;
    TextView itemNumberTextView;
    TextView dateTextView;

    Button buttonEdit;
    Button buttonRemove;
    Button buttonEditDate;

    ListView listViewTasks;
    private ArrayAdapter<String> listAdapter ;

    final Calendar calendar = Calendar.getInstance();

    private static final String[] NUMBER = new String[] {
            "One", "Two", "Three", "Four", "Five",
            "Six", "Seven", "Eight", "Nine", "Ten"
    };

    ArrayAdapter<String> adapter;
   // ArrayAdapter<String> adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo);
        ButterKnife.bind(this);


        widget.setOnDateChangedListener(this);
        widget.setOnMonthChangedListener(this);
        final Calendar calendar = Calendar.getInstance();
        widget.setDateSelected(calendar.getTime(), true);
        widget.setVisibility(View.GONE);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, NUMBER);

        /*
        adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, NUMBER);
                */

        textIn = (AutoCompleteTextView)findViewById(R.id.textin);
        textIn.setAdapter(adapter);

        /*
        textDate = (AutoCompleteTextView)findViewById(R.id.textin);
        textDate.setAdapter(adapter2);
        */

        setToday = (Button)findViewById(R.id.setToday);
        buttonAdd = (Button)findViewById(R.id.add);
        container = (LinearLayout) findViewById(R.id.container);
        reList = (TextView)findViewById(R.id.relist);
        reList.setMovementMethod(new ScrollingMovementMethod());
        info = (TextView)findViewById(R.id.info);
        info.setMovementMethod(new ScrollingMovementMethod());
        textViewDate = (TextView)findViewById(R.id.textViewDate) ;
        textViewDate.setText(getSelectedDatesString());

        taskTextView = (TextView)findViewById(R.id.taskTextView);
        itemNumberTextView = (TextView)findViewById(R.id.itemNumberTextView);
        dateTextView = (TextView)findViewById(R.id.dateTextView);

        buttonEdit = (Button)findViewById(R.id.buttonEdit);
        buttonRemove = (Button)findViewById(R.id.buttonRemove);
        buttonEditDate = (Button)findViewById(R.id.buttonEditDate);

        showAllTasks = (Button)findViewById(R.id.showAllTasks);

        openDB();
        populateList();

        listViewItemClick();



    buttonAdd.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            onClickAddTask(v);
            populateList();

        }
    });


        showAllTasks.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                populateList();

            }
        });

}

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        textViewDate.setText(getSelectedDatesString());

        populateFromDay();
    }

    public void populateFromDay(){
        Cursor cursor = myDB.getTermValues(getSelectedDatesString());
        String[] fromFieldNames = new String[] {DBAdapter.KEY_ROWID,DBAdapter.KEY_TASK,DBAdapter.KEY_DATE};
        int[] toViewIDs = new int[] {R.id.itemNumberTextView,R.id.taskTextView,R.id.dateTextView};
        SimpleCursorAdapter myCursorAdapter;
        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(),R.layout.list_item, cursor, fromFieldNames, toViewIDs,0);
        ListView myList = (ListView) findViewById(R.id.listViewTasks);
        myList.setAdapter(myCursorAdapter);
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        //noinspection ConstantConditions
        //getSupportActionBar().setTitle(FORMATTER.format(date.getDate()));

    }

    private String getSelectedDatesString() {
        CalendarDay date = widget.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        return FORMATTER.format(date.getDate());
    }

    public void showCalendar(View v) {
        if(widget.getVisibility() == View.GONE) {
            widget.setVisibility(View.VISIBLE);
        }
        else{
            widget.setVisibility(View.GONE);
        }
    }

    public void setTodayDate(View v){
        widget.clearSelection();
        widget.setDateSelected(calendar.getTime(), true);
        textViewDate.setText(getSelectedDatesString());
        populateFromDay();
    }

    public void openDB(){
        myDB = new DBAdapter(this);
        myDB.open();
    }



    private void toastMessage (String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    public void onClickAddTask(View v){
        if(!TextUtils.isEmpty(textIn.getText().toString())){
            myDB.insertRow(textIn.getText().toString(),textViewDate.getText().toString());
        }
        populateList();

    }


    private void populateList(){
        Cursor cursor = myDB.getAllRows();
        String[] fromFieldNames = new String[] {DBAdapter.KEY_ROWID,DBAdapter.KEY_TASK,DBAdapter.KEY_DATE};
        int[] toViewIDs = new int[] {R.id.itemNumberTextView,R.id.taskTextView,R.id.dateTextView};
        SimpleCursorAdapter myCursorAdapter;
        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(),R.layout.list_item, cursor, fromFieldNames, toViewIDs,0);
        ListView myList = (ListView) findViewById(R.id.listViewTasks);
        myList.setAdapter(myCursorAdapter);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        closeDB();
    }

    private void closeDB() {
        myDB.close();
    }

    private void showButtons(long id){

        Button buttonEdit=(Button) findViewById(R.id.buttonEdit);
        buttonEdit.setVisibility(View.VISIBLE);
        Button buttonEditDate=(Button) findViewById(R.id.buttonEditDate);
        buttonEditDate.setVisibility(View.VISIBLE);
        Button buttonRemove=(Button) findViewById(R.id.buttonRemove);
        buttonRemove.setVisibility(View.VISIBLE);

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(),"Button clicked",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTask(long id){
        Cursor cursor = myDB.getRow(id);

            if(cursor.moveToFirst()){
                String task = textIn.getText().toString();
                myDB.updateRow(id,task,myDB.getRow(id).getColumnName(2).toString());
            }
            cursor.close();


    }


    private void listViewItemClick(){
        final ListView myList = (ListView) findViewById(R.id.listViewTasks);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long id) {
                // TODO Auto-generated method stub
               // setTextViewEditable(myList);

                showButtons(id);

               // updateTask(id);
               // populateList();
            }
        });
    }



}


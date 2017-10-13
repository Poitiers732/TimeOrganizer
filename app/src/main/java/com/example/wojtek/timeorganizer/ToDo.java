package com.example.wojtek.timeorganizer;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;


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
    RelativeLayout relativeLayoutBottom;
    RelativeLayout editTextWithButton;

    TextView taskTextView;
    TextView itemNumberTextView;
    TextView dateTextView;

    Button buttonEdit;
    Button buttonRemove;
    Button buttonEditDate;
    Button buttonHide;
    Button renameTaskButton;

    EditText editTextView;

    Boolean allTasksPopulated = false;

    String taskNameSuggestion = new String();

    long longid = 0;
    int numberOfrows;
    int repeatedClick = 0;

    RelativeLayout buttons_layout;
    LinearLayout bottomButtons;

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

        relativeLayoutBottom = (RelativeLayout)findViewById(R.id.relativeLayoutBottom);
        editTextWithButton = (RelativeLayout)findViewById(R.id.editTextWithButton);

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
        editTextView = (EditText)findViewById(R.id.editTextView);

        taskTextView = (TextView)findViewById(R.id.taskTextView);
        itemNumberTextView = (TextView)findViewById(R.id.itemNumberTextView);
        dateTextView = (TextView)findViewById(R.id.dateTextView);

        buttonEdit = (Button)findViewById(R.id.buttonEdit);
        buttonRemove = (Button)findViewById(R.id.buttonRemove);
        buttonEditDate = (Button)findViewById(R.id.buttonEditDate);
        buttonHide = (Button) findViewById(R.id.buttonHide);
        renameTaskButton = (Button)findViewById(R.id.renameTaskButton);




        showAllTasks = (Button)findViewById(R.id.showAllTasks);

        bottomButtons = (LinearLayout)findViewById(R.id.bottomButtons);

        openDB();
        populateFromDay();

        listViewItemClick();

        renameTaskButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Cursor cursor = myDB.getRow(longid);
                if (cursor.moveToFirst()){
                    String task = editTextView.getText().toString();
                    myDB.updateRow(longid, task, textViewDate.getText().toString());
                }

                cursor.close();

                if(showAllTasks.getText().toString().equals("Show all tasks")) {
                    populateFromDay();
                }
                else {
                    populateList();
                }

                relativeLayoutBottom.setVisibility(View.GONE);
            }
        });



    buttonAdd.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            onClickAddTask(v);
            populateFromDay();
            allTasksPopulated = false;
            showAllTasks.setText("Show all tasks");

        }
    });


        showAllTasks.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(showAllTasks.getText().toString().equals("Show all tasks")) {
                    populateList();
                    showAllTasks.setText("Show daily tasks");
                    allTasksPopulated = true;
                }
                else {
                    populateFromDay();
                    showAllTasks.setText("Show all tasks");
                    allTasksPopulated = false;
                }

            }
        });


        //buttons litseners

                    buttonEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            editTextWithButton.setVisibility(View.VISIBLE);



                           editTextView.setText( taskNameSuggestion );


                        }
                    });

                    buttonRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getBaseContext(),"Edit",Toast.LENGTH_SHORT).show();

                            myDB.deleteRow(longid);

                            if(allTasksPopulated) {
                                populateList();
                                showAllTasks.setText("Show daily tasks");
                            }
                            else {
                                populateFromDay();
                                showAllTasks.setText("Show all tasks");
                            }

                        }
                    });

                    buttonEditDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getBaseContext(),"Edit",Toast.LENGTH_SHORT).show();
                        }
                    });

                    buttonHide.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            relativeLayoutBottom.setVisibility(View.GONE);

                        }
                    });

}

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        textViewDate.setText(getSelectedDatesString());

        populateFromDay();
        showAllTasks.setText("Show all tasks");
    }

    public void populateFromDay(){
        Cursor cursor = myDB.getTermValues(getSelectedDatesString());
        numberOfrows = cursor.getCount();
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
       // populateList();

    }


    private void populateList(){

        Cursor cursor = myDB.getAllRows();
        numberOfrows = cursor.getCount();
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

                taskNameSuggestion = myDB.getRow(id).getString(1);


                editTextWithButton.setVisibility(View.GONE);


                for(int i=0;i<numberOfrows;i++) {
                    try {
                        View vv = myList.getChildAt(i);
                        vv.setBackgroundColor(Color.WHITE);
                    }catch(Exception e){}
                }



                if(id!=longid || repeatedClick>0){
                    arg1.setBackgroundColor(Color.GREEN);
                    repeatedClick=0;
                    relativeLayoutBottom.setVisibility(View.VISIBLE);


                }else {
                    arg1.setBackgroundColor(Color.WHITE);
                    repeatedClick++;
                    relativeLayoutBottom.setVisibility(View.GONE);


                }


                longid = id;

                if(bottomButtons.getVisibility()==View.GONE){

                    bottomButtons.setVisibility(View.VISIBLE);

                }
                else{
                   // bottomButtons.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showButtons(View lastTouchedView, long id){


        Button buttonEdit = (Button) findViewById(R.id.buttonEdit);
        Button buttonEditDate = (Button) findViewById(R.id.buttonEditDate);
        Button buttonRemove = (Button) findViewById(R.id.buttonRemove);


        if(buttonEdit.getVisibility()==View.GONE){
            buttonEdit.setVisibility(View.VISIBLE);
            buttonEditDate.setVisibility(View.VISIBLE);
            buttonRemove.setVisibility(View.VISIBLE);
        }
        else{
            buttonEdit.setVisibility(View.GONE);
            buttonEditDate.setVisibility(View.GONE);
            buttonRemove.setVisibility(View.GONE);
        }


    }
}


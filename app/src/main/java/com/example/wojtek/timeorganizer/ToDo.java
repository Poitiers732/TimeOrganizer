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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemLongClickListener;


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
    LinearLayout editTextWithButton;

    TextView taskTextView;
    TextView itemNumberTextView;
    TextView isdoneTextView;
    TextView dateTextView;

    Button buttonEdit;
    Button buttonRemove;
    Button buttonEditDate;
    Button buttonHide;
    Button renameTaskButton;
    Button cancelRenameTaskButton;
    Button showDoneTasks;

    ToggleButton toggleButton;

    EditText editTextView;

    Boolean allTasksPopulated = false;

    Boolean populateAllTasks = true;

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
        editTextWithButton = (LinearLayout)findViewById(R.id.editTextWithButton);


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
        isdoneTextView = (TextView)findViewById(R.id.isdoneTextView);
        dateTextView = (TextView)findViewById(R.id.dateTextView);

        buttonEdit = (Button)findViewById(R.id.buttonEdit);
        buttonRemove = (Button)findViewById(R.id.buttonRemove);
        buttonEditDate = (Button)findViewById(R.id.buttonEditDate);
        buttonHide = (Button) findViewById(R.id.buttonHide);
        renameTaskButton = (Button)findViewById(R.id.renameTaskButton);
        cancelRenameTaskButton = (Button)findViewById(R.id.cancelRenameTaskButton);
        showDoneTasks = (Button) findViewById(R.id.showDoneTasks);

        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    populateAllTasks = true;
                } else {
                    populateAllTasks = false;
                }
                populateList();
            }
        });

        showAllTasks = (Button)findViewById(R.id.showAllTasks);

        bottomButtons = (LinearLayout)findViewById(R.id.bottomButtons);

        openDB();
        populateList();

        listViewItemClick();

        showDoneTasks.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

               populateDoneList();

            }
        });

        cancelRenameTaskButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                editTextView.setVisibility(View.GONE);
                editTextWithButton.setVisibility(View.GONE);
                bottomButtons.setVisibility(View.VISIBLE);

            }
        });

        renameTaskButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Cursor cursor = myDB.getRow(longid);
                if (cursor.moveToFirst()){
                    String task = editTextView.getText().toString();
                    myDB.updateRow(longid, task, textViewDate.getText().toString(), "not_done");
                }

                cursor.close();

                    populateList();


                relativeLayoutBottom.setVisibility(View.GONE);
                editTextView.setVisibility(View.GONE);
            }
        });

    buttonAdd.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            onClickAddTask(v);
            populateList();
            allTasksPopulated = false;
            showAllTasks.setText("Show all tasks");
            textIn.setText("");
            textIn.clearFocus();
        }
    });

        showAllTasks.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                    populateList();

            }
        });

                    buttonEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            editTextWithButton.setVisibility(View.VISIBLE);
                            editTextView.setVisibility(View.VISIBLE);

                            editTextView.setText(taskNameSuggestion);
                            bottomButtons.setVisibility(View.GONE);

                            repeatedClick = 0;
                        }
                    });

                    buttonRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getBaseContext(),"Edit",Toast.LENGTH_SHORT).show();

                            myDB.deleteRow(longid);

                                populateList();

                        }
                    });

                    buttonEditDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Cursor cursor = myDB.getRow(longid);
                            if (cursor.moveToFirst()){
                                myDB.updateRowIsDone(longid, "done");
                            }

                            cursor.close();

                        }
                    });

                    buttonHide.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            relativeLayoutBottom.setVisibility(View.GONE);

                                populateList();

                            repeatedClick=0;

                        }
                    });

}

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        textViewDate.setText(getSelectedDatesString());

        toggleButton.setChecked(false);
        allTasksPopulated = false;

        populateList();
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
        populateList();
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
            myDB.insertRow(textIn.getText().toString(),textViewDate.getText().toString(),"not_done");
        }
    }

    private void populateList(){

            Cursor cursor = myDB.getAllRows();
            numberOfrows = cursor.getCount();

        if(!populateAllTasks) {
            cursor = myDB.getTermValues(getSelectedDatesString());
            numberOfrows = cursor.getCount();
        }

        String[] fromFieldNames = new String[] {DBAdapter.KEY_ROWID,DBAdapter.KEY_TASK,DBAdapter.KEY_DATE,DBAdapter.KEY_ISDONE};
        int[] toViewIDs = new int[] {R.id.itemNumberTextView,R.id.taskTextView,R.id.dateTextView,R.id.isdoneTextView};
        SimpleCursorAdapter myCursorAdapter;
        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(),R.layout.list_item, cursor, fromFieldNames, toViewIDs,0);
        ListView myList = (ListView) findViewById(R.id.listViewTasks);
        myList.setAdapter(myCursorAdapter);

        repeatedClick = 0;
    }

    /*
    public void populateFromDay(){
        Cursor cursor = myDB.getTermValues(getSelectedDatesString());
        numberOfrows = cursor.getCount();
        String[] fromFieldNames = new String[] {DBAdapter.KEY_ROWID,DBAdapter.KEY_TASK,DBAdapter.KEY_DATE,DBAdapter.KEY_ISDONE};
        int[] toViewIDs = new int[] {R.id.itemNumberTextView,R.id.taskTextView,R.id.dateTextView,R.id.isdoneTextView};
        SimpleCursorAdapter myCursorAdapter;
        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(),R.layout.list_item, cursor, fromFieldNames, toViewIDs,0);
        ListView myList = (ListView) findViewById(R.id.listViewTasks);
        myList.setAdapter(myCursorAdapter);
    }
    */

    private void populateDoneList(){

        Cursor cursor = myDB.getDoneRows( "done" );
        numberOfrows = cursor.getCount();
        String[] fromFieldNames = new String[] {DBAdapter.KEY_ROWID,DBAdapter.KEY_TASK,DBAdapter.KEY_DATE,DBAdapter.KEY_ISDONE};
        int[] toViewIDs = new int[] {R.id.itemNumberTextView,R.id.taskTextView,R.id.dateTextView,R.id.isdoneTextView};
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
                myDB.updateRow(id,task,myDB.getRow(id).getColumnName(2).toString(),"not_done");
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

        myList.setOnTouchListener(new OnSwipeTouchListener(this) {

            @Override
            public void onSwipeRight(AdapterView<?> arg0, View arg1, int arg2,
                                     long id) {

                if( !myDB.getRow(longid).getString(3).equals("done")) {

                    toastMessage(myDB.getRow(longid).getString(1).toString() + " done!");

                    Cursor cursor = myDB.getRow(longid);
                    if (cursor.moveToFirst()) {
                        myDB.updateRowIsDone(longid, "done");
                    }

                    cursor.close();
                    populateList();
                }

            }

            @Override
            public void onSwipeLeft(AdapterView<?> arg0, View arg1, int arg2,
                                     long id) {

                if( myDB.getRow(longid).getString(3).equals("done")) {

                    toastMessage(myDB.getRow(longid).getString(1).toString() + " now not done!");

                    Cursor cursor = myDB.getRow(longid);
                    if (cursor.moveToFirst()) {
                        myDB.updateRowIsDone(longid, "not_done");
                    }

                    cursor.close();
                    populateDoneList();

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


package com.example.wojtek.timeorganizer;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
    Button buttonAdd, setToday;
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
    Boolean editDateMode = false;
    Boolean showDoneMode = false;

    Boolean populateAllTasks = true;

    String taskNameSuggestion = new String();

    View currentSelectedView = null;

    public long longid = 0;
    int globalPosition = -10;
    int numberOfrows;
    int repeatedClick = 0;

    ArrayList<Item> itemList;
    ItemArrayAdapter itemArrayAdapterGlobal;

    RelativeLayout buttons_layout;
    LinearLayout bottomButtons;

    ListView listViewTasks;

    private ArrayAdapter<String> listAdapter;
    final Calendar calendar = Calendar.getInstance();
  /*  private static final String[] NUMBER = new String[] {
            "One", "Two", "Three", "Four", "Five",
            "Six", "Seven", "Eight", "Nine", "Ten"
    };*/

    ArrayAdapter<String> adapter;

    RecyclerView recyclerView;

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

       /* adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, NUMBER);*/

        textIn = (AutoCompleteTextView)findViewById(R.id.textin);
        textIn.setAdapter(adapter);

        relativeLayoutBottom = (RelativeLayout)findViewById(R.id.relativeLayoutBottom);
        editTextWithButton = (LinearLayout)findViewById(R.id.editTextWithButton);

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

        toggleButton = (ToggleButton) findViewById(R.id.toggleAllDaily);

        bottomButtons = (LinearLayout)findViewById(R.id.bottomButtons);

        itemList = new ArrayList<Item>();

        openDB();
        populateList();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        final ItemArrayAdapter itemArrayAdapter = new ItemArrayAdapter(R.layout.list_item, itemList, new ItemArrayAdapter.OnItemClicked () {

            @Override
            public void onItemClick(View view, int position) {

                editTextView.setVisibility(View.GONE);
                editTextWithButton.setVisibility(View.GONE);

                currentSelectedView = view;

                relativeLayoutBottom.setVisibility(View.VISIBLE);
                bottomButtons.setVisibility(View.VISIBLE);

                //view.setFocusableInTouchMode(true);
                view.requestFocus();
                view.clearFocus();

                if (!view.isSelected()) {
                    relativeLayoutBottom.setVisibility(View.VISIBLE);
                    bottomButtons.setVisibility(View.VISIBLE);
                } else {
                    relativeLayoutBottom.setVisibility(View.GONE);
                    bottomButtons.setVisibility(View.GONE);
                }

                globalPosition = position;
                longid = Integer.valueOf( itemList.get(position).getId() );
            }
        });

        itemArrayAdapterGlobal = itemArrayAdapter;

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                populateAllTasks = isChecked;
                populateList();
                populateRecycleList(itemList, itemArrayAdapter);
                editTextWithButton.setVisibility(View.GONE);
            }
        });

        ///SWIPE

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                populateList();
                return false;
            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();

                if (swipeDir == ItemTouchHelper.RIGHT && !myDB.getRow(longid).getString(3).equals("done")) {

                    itemList.remove(position);
                    itemArrayAdapter.notifyDataSetChanged();

                    toastMessage(myDB.getRow(longid).getString(1).toString() + " done, congrats!");

                    Cursor cursor = myDB.getRow(longid);
                    if (cursor.moveToFirst()) {
                        myDB.updateRowIsDone(longid, "done");
                    }
                    cursor.close();

                }

                if (swipeDir == ItemTouchHelper.LEFT && !myDB.getRow(longid).getString(3).equals("not_done")
                        && myDB.getRow(longid).getString(3).equals("done")) {

                    itemList.remove(position);
                    itemArrayAdapter.notifyDataSetChanged();

                    toastMessage(myDB.getRow(longid).getString(1).toString() + " is now set as not done yet");

                    Cursor cursor = myDB.getRow(longid);
                    if (cursor.moveToFirst()) {
                        myDB.updateRowIsDone(longid, "not_done");
                    }
                    cursor.close();
                }

                else

                populateRecycleList(itemList, itemArrayAdapter);
                populateList();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        ///

        populateRecycleList(itemList, itemArrayAdapter);

            /*final ListView myList = (ListView) findViewById(R.id.listViewTasks);

            myList.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });

            myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                        long id) {

                    showOnlyFirstDate(myList);

                    taskNameSuggestion = myDB.getRow(id).getString(1);
                    editTextWithButton.setVisibility(View.GONE);

                    if(id!=longid || repeatedClick>0  ){
                        //   arg1.setBackgroundColor(Color.GREEN);
                        arg1.setSelected(true);
                        repeatedClick=0;
                        relativeLayoutBottom.setVisibility(View.VISIBLE);
                    }else {
                        //  arg1.setBackgroundColor(Color.WHITE);
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

                        toastMessage(myDB.getRow(longid).getString(1).toString() + " is now set as not done yet");

                        Cursor cursor = myDB.getRow(longid);
                        if (cursor.moveToFirst()) {
                            myDB.updateRowIsDone(longid, "not_done");
                        }
                        cursor.close();
                        populateDoneList();
                    }
                }
            });*/

        showDoneTasks.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                showDoneMode = true;
                populateRecycleList(itemList, itemArrayAdapter);
                populateList();
                showDoneMode = false;

            }
        });

        cancelRenameTaskButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                editTextView.setVisibility(View.GONE);
                editTextWithButton.setVisibility(View.GONE);
                bottomButtons.setVisibility(View.VISIBLE);
                hideKeyboard();
            }
        });

        renameTaskButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Cursor cursor = myDB.getRow(longid);
                    if (cursor.moveToFirst()) {
                        String task = editTextView.getText().toString();
                        myDB.updateRow(longid, task, textViewDate.getText().toString(), "not_done");
                    }
                cursor.close();
                populateList();
                populateRecycleList(itemList, itemArrayAdapter);
                relativeLayoutBottom.setVisibility(View.GONE);
                editTextView.setVisibility(View.GONE);
                hideKeyboard();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                onClickAddTask(v);
                populateList();
                allTasksPopulated = false;
                textIn.setText("");
                textIn.clearFocus();
                populateRecycleList(itemList, itemArrayAdapter);
                editTextWithButton.setVisibility(View.GONE);

                hideKeyboard();
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editTextWithButton.setVisibility(View.VISIBLE);
                editTextView.setVisibility(View.VISIBLE);

                editTextView.setText(taskNameSuggestion);
                bottomButtons.setVisibility(View.GONE);

                view.setSelected(true);
            }
        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDB.deleteRow(longid);

                itemList.remove(globalPosition);
                recyclerView.removeViewAt(globalPosition);
                itemArrayAdapter.notifyItemRemoved(globalPosition);
                itemArrayAdapter.notifyItemRangeChanged(globalPosition, itemList.size());

                populateRecycleList(itemList, itemArrayAdapter);
                relativeLayoutBottom.setVisibility(View.GONE);
                editTextWithButton.setVisibility(View.GONE);
            }
        });

        buttonEditDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                widget.setVisibility(View.VISIBLE);
                widget.requestFocus();
                editDateMode = true;

            }
        });

        buttonHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativeLayoutBottom.setVisibility(View.GONE);

                if(currentSelectedView!=null) {
                    currentSelectedView.setSelected(false);
                }

                populateList();

                repeatedClick = 0;
            }
        });
}

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {

        textViewDate.setText(getSelectedDatesString());
        if(!editDateMode) {
            toggleButton.setChecked(false);
            allTasksPopulated = false;
        }
        else {
            myDB.updateRow(longid, "x" , getSelectedDatesString() , "x" );
            editDateMode = false;
            widget.setVisibility(View.GONE);
        }
        populateList();
        populateRecycleList(itemList, itemArrayAdapterGlobal);
        widget.setVisibility(View.GONE);
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
        populateRecycleList(itemList, itemArrayAdapterGlobal);
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

    private ItemArrayAdapter populateRecycleList(final ArrayList<Item> itemList, final ItemArrayAdapter itemArrayAdapter){

        Cursor cursor;

        if (showDoneMode) {
            cursor = myDB.getDoneRows("done");
        }
        else if(!populateAllTasks) {
            cursor = myDB.getTermValues(getSelectedDatesString());
        }
        else{
            cursor = myDB.getAllRows();
        }
        numberOfrows = cursor.getCount();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemArrayAdapter);

        itemList.clear();

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            itemList.add( new Item( cursor.getString( 1 ), cursor.getString( 2 ), cursor.getString( 0 ) ));
        }

        return itemArrayAdapter;
    }

    private void populateList(){
        repeatedClick = 0;
        bottomButtons.setVisibility(View.GONE);
        editTextView.setVisibility(View.GONE);
    }

    private void populateDoneList(){

        bottomButtons.setVisibility(View.GONE);
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

    public void showOnlyFirstDate(ListView myList){

        Map<Integer,String> ml = new HashMap<>();
        Map<Integer,Integer> mlRep = new HashMap<>();

        String temp;

        for(int i=0;i<numberOfrows;i++){
            try {
                mlRep.put(i, 0);
                temp = ((TextView) myList.getChildAt(i).findViewById(R.id.dateTextView)).getText().toString();
                ml.put(i, temp);
            } catch (Exception e){}
        }

        for(int i=0;i<=numberOfrows;i++){
            try {
                if(ml.get(i).equals(ml.get(i+1))) {
                    myList.getChildAt(i+1).findViewById(R.id.dateTextView).setVisibility(View.GONE);
                }
            }catch (Exception e){}
        }
    }

    private void listViewItemClick(){
        final ListView myList = (ListView) findViewById(R.id.listViewTasks);

        myList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                String temp;
            }
        });

        /*myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long id) {

                //showOnlyFirstDate();

                taskNameSuggestion = myDB.getRow(id).getString(1);
                editTextWithButton.setVisibility(View.GONE);

                if(id!=longid || repeatedClick>0  ){
                 //   arg1.setBackgroundColor(Color.GREEN);
                    arg1.setSelected(true);
                    repeatedClick=0;
                    relativeLayoutBottom.setVisibility(View.VISIBLE);
                }else {
                  //  arg1.setBackgroundColor(Color.WHITE);
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

                    toastMessage(myDB.getRow(longid).getString(1).toString() + " is now set as not done yet");

                    Cursor cursor = myDB.getRow(longid);
                    if (cursor.moveToFirst()) {
                        myDB.updateRowIsDone(longid, "not_done");
                    }
                    cursor.close();
                    populateDoneList();
                }
            }
        });*/
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onBackPressed() {

        if(editTextWithButton.getVisibility()==View.VISIBLE){
            editTextWithButton.setVisibility(View.GONE);
            editTextView.setVisibility(View.GONE);
        }
        else if(relativeLayoutBottom.getVisibility()==View.VISIBLE){
            relativeLayoutBottom.setVisibility(View.GONE);
        }
    }

    public void showHideKeyboard(View view){
        if (view.isSelected()) {
            relativeLayoutBottom.setVisibility(View.VISIBLE);
            bottomButtons.setVisibility(View.VISIBLE);
        } else {
            relativeLayoutBottom.setVisibility(View.GONE);
            bottomButtons.setVisibility(View.GONE);
        }
    }
}


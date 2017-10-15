package com.example.wojtek.timeorganizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//import static android.icu.text.MessagePattern.ArgType.SELECT;

public class DBAdapter {

	private static final String TAG = "DBAdapter"; //used for logging database version changes

	// Field Names:
	public static final String KEY_ROWID = "_id";
	public static final String KEY_TASK = "task";
	public static final String KEY_DATE = "date";
	public static final String KEY_ISDONE = "is_done";


	public static final String[] ALL_KEYS = new String[] {KEY_ROWID, KEY_TASK, KEY_DATE, KEY_ISDONE};

	// Column Numbers for each Field Name:
	public static final int COL_ROWID = 0;
	public static final int COL_TASK = 1;
	public static final int COL_DATE = 2;
	public static final int COL_ISDONE = 3;

	// DataBase info:
	public static final String DATABASE_NAME = "dbToDo";
	public static final String DATABASE_TABLE = "mainToDo";
	public static final int DATABASE_VERSION = 8; // The version number must be incremented each time a change to DB structure occurs.

	//SQL statement to create database
	private static final String DATABASE_CREATE_SQL =
			"CREATE TABLE " + DATABASE_TABLE
					+ " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_TASK + " TEXT NOT NULL, "
					+ KEY_DATE + " TEXT, "
					+ KEY_ISDONE + " TEXT"
					+ ");";

	private final Context context;
	private DatabaseHelper myDBHelper;
	private SQLiteDatabase db;


	public DBAdapter(Context ctx) {
		this.context = ctx;
		myDBHelper = new DatabaseHelper(context);
	}

	// Open the database connection.
	public DBAdapter open() {
		db = myDBHelper.getWritableDatabase();
		return this;
	}

	// Close the database connection.
	public void close() {
		myDBHelper.close();
	}

	// Add a new set of values to be inserted into the database.
	public long insertRow(String task, String date, String is_done) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TASK, task);
		initialValues.put(KEY_DATE, date);
		initialValues.put(KEY_ISDONE, is_done);


		// Insert the data into the database.
		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	// Delete a row from the database, by rowId (primary key)
	public boolean deleteRow(long rowId) {
		String where = KEY_ROWID + "=" + rowId;
		return db.delete(DATABASE_TABLE, where, null) != 0;
	}

	public void deleteAll() {
		Cursor c = getAllRows();
		long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
		if (c.moveToFirst()) {
			do {
				deleteRow(c.getLong((int) rowId));
			} while (c.moveToNext());
		}
		c.close();
	}

	// Return all data in the database.
	public Cursor getAllRows() {
		String where = KEY_ISDONE + "= 'not_done'";
		String ALL_KEYS_ARRAY[] = new String[]{ KEY_ROWID,KEY_DATE,KEY_TASK };
		Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS, where, null, null, null, null, null, null);

		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	// Get a specific row (by rowId)
	public Cursor getRow(long rowId) {
		String where = KEY_ROWID + "=" + rowId;
		String ALL_KEYS_ARRAY[] = new String[]{ KEY_ROWID,KEY_DATE,KEY_TASK };

		Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
				where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	public Cursor getTermValues(String term)
	{
		String where = KEY_DATE + "=?" + " AND " + KEY_ISDONE + "= 'not_done'";
		String whereArgs[] = new String[]{ term };

		String ALL_KEYS_ARRAY[] = new String[]{ KEY_ROWID,KEY_DATE,KEY_TASK };
		Cursor cursor = db.query(DATABASE_TABLE, ALL_KEYS , where, whereArgs, null, null, null, null);
		return cursor;
	}

	public Cursor getDoneRows(String term)
	{
		String where = KEY_ISDONE + "=?";
		String whereArgs[] = new String[]{ term };

		Cursor cursor = db.query(DATABASE_TABLE, ALL_KEYS , where, whereArgs, null, null, null, null);
		return cursor;
	}

	// Change an existing row to be equal to new data.
	public boolean updateRow(long rowId, String task, String date, String is_done) {
		String where = KEY_ROWID + "=" + rowId;
		ContentValues newValues = new ContentValues();
		if(!task.equals("x")) {
			newValues.put(KEY_TASK, task);
		}
		newValues.put(KEY_DATE, date);
		if(!task.equals("x")) {
			newValues.put(KEY_ISDONE, is_done);
		}

		// Insert it into the database.
		return db.update(DATABASE_TABLE, newValues, where, null) != 0;
	}

	public boolean updateRowIsDone(long rowId, String is_done) {
		String where = KEY_ROWID + "=" + rowId;
		ContentValues newValues = new ContentValues();
		//is_done = "done";
		newValues.put(KEY_ISDONE, is_done);


		// Insert it into the database.
		return db.update(DATABASE_TABLE, newValues, where, null) != 0;
	}

	// Change only isdone
	/*
	public boolean updateIsdone(long rowId, String isdone) {
		String where = KEY_ROWID + "=" + rowId;
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_ISDONE, isdone);

		// Insert it into the database.
		return db.update(DATABASE_TABLE, newValues, where, null) != 0;
	}
	*/


	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading application's database from version " + oldVersion
					+ " to " + newVersion + ", which will destroy all old data!");

			// Destroy old database:
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

			// Recreate new database:
			onCreate(_db);
		}
	}


}


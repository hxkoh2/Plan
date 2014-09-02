package com.catchthegivingbug.mylists;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class DatabaseHelper extends SQLiteOpenHelper{
	
	private static DatabaseHelper sInstance = null;
	
	public static final int DATABASE_VERSION=1;
	public static final String DATABASE_NAME="Database.db";
	private static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE Task ("+BaseColumns._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, Items VARCHAR(255), Notes VARCHAR(1000), Parent INT(10), Date VARCHAR(255))";
	private static final String SQL_CREATE_ENTRIES2 = 
			"CREATE TABLE Completed (ID INT(10), Items VARCHAR(255), Notes VARCHAR(1000), Parent INT(10), Date VARCHAR(255))";
	private static final String SQL_CREATE_ENTRIES3 = 
			"CREATE TABLE Lists ("+BaseColumns._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, ListNames VARCHAR(255))";


	public static DatabaseHelper getInstance(Context context){
		if(sInstance==null)
			sInstance = new DatabaseHelper(context.getApplicationContext());
		return sInstance;
	}
	
	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
		db.execSQL(SQL_CREATE_ENTRIES2);
		db.execSQL(SQL_CREATE_ENTRIES3);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}

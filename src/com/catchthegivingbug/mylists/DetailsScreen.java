package com.catchthegivingbug.mylists;

import java.util.Calendar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.CalendarContract.Events;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class DetailsScreen extends FragmentActivity {

	Button done;
	Button cancel;
	Button delete;
	Button addToCalendar; 
	TextView datePicker;
	DatabaseHelper helper;
	SQLiteDatabase database;
	String details;
	String taskName;
	int row;
	int listID;
	String objDate;
	public static final String LISTID = "com.catchthegivingbug.mylists.LISTID";
	protected static final String TAG = DetailsScreen.class.getSimpleName();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details_screen);
		// Show the Up button in the action bar.
		//setupActionBar();
		getActionBar().setDisplayHomeAsUpEnabled(true);

		helper = DatabaseHelper.getInstance(this);
		database = helper.getWritableDatabase();

		Intent intent = getIntent();
		taskName = intent.getStringExtra(ListScreen.TODO);
		row = intent.getIntExtra(ListScreen.ID, -1);
		details = intent.getStringExtra(ListScreen.NOTES);
		listID = intent.getIntExtra(ListScreen.LISTID, -1);
		objDate = intent.getStringExtra(ListScreen.DATE);

		// sets line under "Task" to task you clicked on
		EditText title = (EditText) findViewById(R.id.details_task);
		title.setText(taskName);
		
		// sets space under "Notes" to notes of the task you clicked on
		EditText extraInfo = (EditText) findViewById(R.id.notesText);
		extraInfo.setText(details);
		
		//set date
		TextView dateTextView = (TextView) findViewById(R.id.datePicker);
		dateTextView.setText(objDate);

		// set up done button
		done = (Button) findViewById(R.id.done);
		done.setOnClickListener(save);

		// set up cancel button
		cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(back);
		
		//set up delete button
		delete = (Button) findViewById(R.id.delete);
		delete.setOnClickListener(remove);
		
		//set up add to calendar button
		addToCalendar = (Button) findViewById(R.id.addToCalendar);
		addToCalendar.setOnClickListener(calendar);
		
		//set up date picker
		datePicker = (TextView) findViewById(R.id.datePicker);
		datePicker.setOnClickListener(date);
	}

	OnClickListener save = new OnClickListener() {
		@Override
		public void onClick(View v) {
			try {
				if (v == done) {
					if(row!=-1){
						//update the Task Name
						String where = BaseColumns._ID + "= ?";
						String[] selectionArgs = { String.valueOf(row) };
						
						ContentValues values = new ContentValues();
						EditText editText = (EditText) findViewById(R.id.details_task);
						String task = editText.getText().toString();
						values.put("Items", task);
						database.update("Task", values, where, selectionArgs);	
						
						//update the Notes section
						ContentValues values2 = new ContentValues();
						EditText editText2 = (EditText) findViewById(R.id.notesText);
						String task2 = editText2.getText().toString();
						values2.put("Notes", task2);
						database.update("Task", values2, where, selectionArgs);	
						
					}
					DetailsScreen.this.finish();
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	};

	OnClickListener back = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == cancel) {
				DetailsScreen.this.finish();
			}
		}
	};
	
	OnClickListener remove = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == delete) {
				if(row!=-1){
					String where = BaseColumns._ID + "= ?";
					String[] selectionArgs = { String.valueOf(row) };
					int rowsAffected = database.delete("Task", where, selectionArgs);	
					if(rowsAffected>0)
						ListScreen.activeTasks--;
					else{
						where = "ID= ?";
						database.delete("Completed", where, selectionArgs);
					}
				}
				DetailsScreen.this.finish();
			}
		}
	};
	
	OnClickListener calendar = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == addToCalendar) {
				Intent calendarIntent = new Intent(Intent.ACTION_INSERT, Events.CONTENT_URI);
				EditText name = (EditText) findViewById(R.id.details_task);
				String name2 = name.getText().toString();
				calendarIntent.putExtra(Events.TITLE, name2);
				EditText notes = (EditText) findViewById(R.id.notesText);
				String description = notes.getText().toString();
				calendarIntent.putExtra(Events.DESCRIPTION, description);
				startActivity(calendarIntent);
			}
		}
	};
	
	OnClickListener date = new OnClickListener(){
		@Override
		public void onClick(View v){
			if (v == datePicker){
				DialogFragment newFragment = new MyDatePicker();
			    newFragment.show(getSupportFragmentManager(), "datePicker");
			}
		}
	};
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	/*@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.details_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        Intent upIntent = NavUtils.getParentActivityIntent(this);
		switch (item.getItemId()) {
    	case android.R.id.home:
    		Intent result = new Intent(DetailsScreen.this, ListScreen.class);
			result.putExtra(LISTID, (int)listID);
			setResult(Activity.RESULT_OK, result);
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
    }
	}
	
	public void updateDate(int month, int day, int year) {
		//update the Task Name
		String where = BaseColumns._ID + "= ?";
		String[] selectionArgs = { String.valueOf(row) };
		
		ContentValues values = new ContentValues();
		String date = String.valueOf(month) + "/" + String.valueOf(day) + "/" + String.valueOf(year);
		values.put("Date", date);
		database.update("Task", values, where, selectionArgs);
		
		TextView textview = (TextView) findViewById(R.id.datePicker);
		textview.setText(date);
	}

}

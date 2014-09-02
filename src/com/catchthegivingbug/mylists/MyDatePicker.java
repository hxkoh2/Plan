package com.catchthegivingbug.mylists;

import java.util.Calendar;

import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class MyDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener{
	
	public static final String DATE = "com.catchthegivingbug.mylists.DATE";
	DatabaseHelper helper;
	SQLiteDatabase database;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

	@Override
	public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		// TODO Auto-generated method stub
		Log.v(DATE, "date: " + year + ", " + monthOfYear + ", " + dayOfMonth);
		((DetailsScreen)(getActivity())).updateDate(monthOfYear+1, dayOfMonth, year);
	    dismiss();		
	}
}


package com.catchthegivingbug.mylists;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class MyMainArrayAdapter<T> extends ArrayAdapter<T> {

	Context context;
	List<ListOfListsObjects> results;
	
	
	public MyMainArrayAdapter(Context contexts, List<T> objects) {
		super(contexts,R.layout.main_list_row, objects);
		this.context = contexts;
		this.results = (List<ListOfListsObjects>)objects;
	}

	@Override
	public int getCount(){
		return results.size();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View rowView = inflater.inflate(R.layout.main_list_row, parent, false);
        TextView listItem = (TextView) rowView.findViewById(R.id.rowLists);
        String item = (results.get(position)).listName;
        listItem.setText(item);
        return rowView;
	}
	
}

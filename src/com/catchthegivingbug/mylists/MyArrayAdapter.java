package com.catchthegivingbug.mylists;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class MyArrayAdapter<T> extends ArrayAdapter<T> {
	Context context;
	List<ListObjects> results2;
	private static final int ACTIVE = 0;
	private static final int COMPLETED = 1;
	
	
	public MyArrayAdapter(Context contexts, List<T> objects) {
		super(contexts,R.layout.list_row, objects);
		this.context = contexts;
		this.results2 = (List<ListObjects>)objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = null;
        int type = getItemViewType(position);
        
        switch(type){
        case ACTIVE:
        	rowView = inflater.inflate(R.layout.list_row, parent, false);
        	break;
        case COMPLETED:
        	rowView = inflater.inflate(R.layout.list_row_completed, parent, false);
        	break;
        }
        
        if(rowView!=null){
	        CheckBox checkbox = (CheckBox)rowView.findViewById(R.id.checkbox);
	        TextView listItem = (TextView) rowView.findViewById(R.id.rowText);
	        String item = (results2.get(position)).rowTask;
	        checkbox.setTag((results2.get(position)).id);
	        listItem.setText(item);
        }   
        return rowView;
        
	}
	
	@Override
	public int getCount(){
		return results2.size();
	}
	
	@Override
    public int getItemViewType(int position) {
		if(position>=ListScreen.activeTasks)
			return COMPLETED;
		else
			return ACTIVE; 
			
    }
	
	
	public List<ListObjects> returnList(){
		return results2;
	}
	
}

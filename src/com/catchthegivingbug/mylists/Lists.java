package com.catchthegivingbug.mylists;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.provider.BaseColumns;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Lists extends Activity {

	ListView listView;
	MyMainArrayAdapter<ListOfListsObjects> adapter;
	private SQLiteDatabase database;
	Button addList;
	public static final String ID2 = "com.catchthegvingbug.mylists.ID";
	public static final String TAG = "com.catchthegivingbug.mylists.TAG";
	public static final String TAG2 = "com.catchthegivingbug.mylists.TAG2";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lists);
	    //getActionBar().setDisplayHomeAsUpEnabled(true);
        Log.v(TAG,"onCreate Lists");	

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lists, menu);
		return true;
	}
	
	@Override
	public void onResume(){
		super.onResume();
        Log.v(TAG2,"onStart Lists");	
		DatabaseHelper helper = DatabaseHelper.getInstance(Lists.this);
		database = helper.getWritableDatabase();
		addList = (Button) findViewById(R.id.insertList);
		addList.setOnClickListener(add);
		
    	showLists();
		
		listView = (ListView) findViewById(R.id.mainList);
    	listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    	listView.setItemsCanFocus(true);
    	listView.setAdapter(adapter);
    	listView.setOnItemClickListener(selectList);
	}
	
	public void showLists(){
    	List<ListOfListsObjects> results = new ArrayList<ListOfListsObjects>();
		String[] projection={BaseColumns._ID, "ListNames"};
		String sortOrder = BaseColumns._ID+" ASC";
		Cursor c = database.query("Lists", projection, null, null, null, null, sortOrder, null);
		int idColumnIndex = c.getColumnIndex(BaseColumns._ID);
		int listName = c.getColumnIndex("ListNames");
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			int id = c.getInt(idColumnIndex);
			String name = c.getString(listName);
			ListOfListsObjects obj2 = new ListOfListsObjects(id, name);
			results.add(obj2);			
		}
    	adapter = new MyMainArrayAdapter<ListOfListsObjects>(this, results);
    }
	
	 OnClickListener add = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(v==addList)
		    	{
		    		EditText editText= (EditText) findViewById(R.id.add_list);
		    		String list= editText.getText().toString();
		    		if(!list.isEmpty()){
		    			ContentValues values = new ContentValues();
		    			values.put("ListNames", list);
		    			database.insert("Lists", "ListNames", values);
		    			String[] projection2={BaseColumns._ID};
		    			String where = "ListNames = ?";
						String[] selectionArgs = {list};
		    			String sortOrder2 = BaseColumns._ID+" DESC"; //if there are 2 list items that are the same, get the most recent one (higher ID because incrementing)
		    			Cursor d = database.query("Lists", projection2, where, selectionArgs, null, null, sortOrder2, null);
		    			int columnIndex = d.getColumnIndex(BaseColumns._ID); 
		    			d.moveToFirst();
		    			int id = d.getInt(columnIndex); //getting the ID of the most recent list with the list name of list
		    			ListOfListsObjects create = new ListOfListsObjects(id, list);
		    			adapter.add(create);
		    			adapter.notifyDataSetChanged();
		    			editText.setText("");
		    		}
		    	}
			}
		}; 
		
		OnItemClickListener selectList = new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> aView, View v, int position, long id){
				ListOfListsObjects obj = (ListOfListsObjects) adapter.getItem(position);
				Intent intent = new Intent(Lists.this, ListScreen.class);
				intent.putExtra("ListTitle", obj.listName);
				intent.putExtra(ID2, (int)obj.id);
				startActivity(intent);	
			}
		};
		
}

package com.catchthegivingbug.mylists;

import android.os.Bundle;
import android.provider.BaseColumns;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.view.View;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Comparator;


public class ListScreen extends Activity{
	ListView listView;
	MyArrayAdapter<ListObjects> adapter;
	Button addToList;
	static int activeTasks;
	static boolean showingCompleted = false;
	static int listID;
	
	public static final String TODO = "com.catchthegivingbug.mylists.TODO";
	public static final String ID = "com.catchthegvingbug.mylists.ID";
	public static final String NOTES = "com.catchthegivingbug.mylists.NOTES";
	public static final String TAG = "com.catchthegivingbug.mylists.TAG";
	public static final String TAG2 = "com.catchthegivingbug.mylists.TAG2";
	public static final String TAG3 = "com.catchthegivingbug.mylists.TAG3";	
	public static final String LISTID = "com.catchthegivingbug.mylists.LISTID";
	public static final String FROMLISTS = "com.catchthegivingbug.mylists.FROMLISTS";
	public static final String FROMSAVED = "com.catchthegivingbug.mylists.FROMSAVED";
	public static final String DATE = "com.catchthegivingbug.mylists.DATE";
	private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Intent intent = getIntent();
		listID = intent.getIntExtra(Lists.ID2, -1);
    	String listName = intent.getStringExtra("ListTitle");
    	setTitle(listName);
        super.onCreate(savedInstanceState);
        Log.v(TAG,"onCreate ListScreen");	
		setContentView(R.layout.activity_list_screen);
	    getActionBar().setDisplayHomeAsUpEnabled(true);
		Log.v(FROMLISTS,"ID from lists: " + listID);
    }

    @Override
	public void onPause(){
		super.onPause();
	}
	
	@Override
	public void onResume(){
		super.onResume();
        Log.v(TAG2,"onStart ListScreen");	

	    DatabaseHelper helper = DatabaseHelper.getInstance(ListScreen.this);
	    database=helper.getWritableDatabase();
	    activeTasks=0;
	    
    	//get data from database
	    showActiveTasks();
	    if(showingCompleted)
	    	showCompleted();
        Log.v(TAG3,"Reached");	

        addToList = (Button)findViewById(R.id.plus);
        addToList.setOnClickListener(add);
        
    	listView = (ListView) findViewById(R.id.list);
    	listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    	listView.setItemsCanFocus(true);
    	listView.setAdapter(adapter);
    	listView.setOnItemClickListener(details);
	}
	
	/*@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		
		savedInstanceState.putInt("listID", listID);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
		listID = savedInstanceState.getInt("listID");
		Log.v(FROMSAVED,"ID from saved: " + listID);

	}*/
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_screen_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
        	case android.R.id.home:
        		NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.show_completed:
            	showCompleted();
                return true;
            case R.id.hide_completed:
            	hideCompleted();
                return true;
            case R.id.delete_completed:
            	deleteCompleted();
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void showActiveTasks(){
    	List<ListObjects> results = new ArrayList<ListObjects>();
		String[] projection={"Items","Notes", BaseColumns._ID, "Parent", "Date"};
		String sortOrder = BaseColumns._ID+" ASC";
		Cursor c = database.query("Task", projection, null, null, null, null, sortOrder, null);
		int taskColumnIndex = c.getColumnIndex("Items");
		int idColumnIndex = c.getColumnIndex(BaseColumns._ID);
		int notesColumnIndex = c.getColumnIndex("Notes");
		int parentID = c.getColumnIndex("Parent");
		int dateColumnIndex = c.getColumnIndex("Date");
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			int id2 = c.getInt(idColumnIndex);
			String str = c.getString(taskColumnIndex);
			String details = c.getString(notesColumnIndex);
			int parentID2 = c.getInt(parentID);
			String date = c.getString(dateColumnIndex);
			if(parentID2 == listID){
				ListObjects obj2 = new ListObjects(id2, str, details, parentID2, date);
				results.add(obj2);
				activeTasks++;
			}
		}
    	adapter = new MyArrayAdapter<ListObjects>(this, results);
    }
    
    public void showCompleted(){
    	showingCompleted = true;
		String[] projection={"Items","Notes", "ID", "Parent", "Date"};
		String sortOrder = "ID ASC";
		Cursor c = database.query("Completed", projection, null, null, null, null, sortOrder, null);
		int taskColumnIndex = c.getColumnIndex("Items");
		int idColumnIndex = c.getColumnIndex("ID");
		int notesColumnIndex = c.getColumnIndex("Notes");
		int parentID = c.getColumnIndex("Parent");
		int dateColumnIndex = c.getColumnIndex("Date");
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			int id2 = c.getInt(idColumnIndex);
			String str = c.getString(taskColumnIndex);
			String details = c.getString(notesColumnIndex);	
			int parentID2 = c.getInt(parentID);
			String date = c.getString(dateColumnIndex);
			if(parentID2 == listID){
				ListObjects obj2 = new ListObjects(id2, str, details, parentID2, date);
				if(!itemExistsInAdapter(obj2)){
					adapter.add(obj2);
					adapter.notifyDataSetChanged();
				}
			}
		}	
    }
    
    public void hideCompleted(){
    	showingCompleted = false;
    	while(adapter.getCount()>activeTasks){
    		adapter.remove(adapter.getItem(adapter.getCount()-1));
    	}
    	this.adapter.sort(new Comparator<ListObjects>()
	    {
	        @Override
	        public int compare(ListObjects item1, ListObjects item2)
	        {
	            return item1.id-item2.id;
	        };
	    });
    }
    
    public void deleteCompleted(){
    	showingCompleted = false;
    	database.delete("Completed", null, null);
    	onResume();
    	
    }
    
    public boolean itemExistsInAdapter(ListObjects obj){
    	List<ListObjects> items = adapter.returnList();
    	for(int i=0; i<items.size(); i++){
    		if(obj.id==items.get(i).id)
    			return true;
    	}
    	return false;
    }
    
    OnClickListener add = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v==addToList)
	    	{
	    		EditText editText= (EditText) findViewById(R.id.add_task);
	    		String task= editText.getText().toString();
	    		if(!task.isEmpty()){
	    			ContentValues values = new ContentValues();
	    			values.put("Items", task);
	    			values.put("Parent", listID);
	    			database.insert("Task", "Items", values);
	    			String[] projection2={BaseColumns._ID};
	    			String where = "Items = ?";
					String[] selectionArgs = {task};
	    			String sortOrder2 = BaseColumns._ID+" DESC"; //if there are 2 list items that are the same, get the most recent one (higher ID because incrementing)
	    			Cursor d = database.query("Task", projection2, where, selectionArgs, null, null, sortOrder2, null);
	    			int columnIndex2 = d.getColumnIndex(BaseColumns._ID);
	    			d.moveToFirst();
	    			int id3 = d.getInt(columnIndex2);
	    			ListObjects create = new ListObjects(id3,task, "", listID, "No Date Selected");
	    			adapter.insert(create,activeTasks);
	    			activeTasks++;
	    			adapter.notifyDataSetChanged();
	    			editText.setText("");
	    			//Log.v("Adapter Size", "size: " + adapter.getCount());
	    		}
	    	}
		}
	}; 
	
	OnItemClickListener details = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> aView, View v, int position, long id){
			ListObjects obj = (ListObjects) adapter.getItem(position);
			Intent intent = new Intent(ListScreen.this, DetailsScreen.class);
			intent.putExtra(ID, (int)obj.id);
			intent.putExtra(TODO, (String)obj.rowTask);
			intent.putExtra(NOTES, (String)obj.notes);
			intent.putExtra(LISTID, (int)listID);
			intent.putExtra(DATE, (String) obj.date);
			startActivityForResult(intent,1);	
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
            	listID = data.getIntExtra(DetailsScreen.LISTID, -1);
            }
        }
    }
	
	public void onCheckboxClicked(View view) {
		CheckBox checkbox = (CheckBox) view;
	    boolean checked = checkbox.isChecked();
	    String rowID = checkbox.getTag().toString();
	    
	    if(checked){
	    	checkBoxChecked(rowID);
	    }
	    else{ //!checked
	    	checkBoxNotChecked(rowID);
	    }
	}
	
	private void checkBoxChecked(String rowID){
		String[] selectionArgs = {rowID};
	    Cursor c = database.rawQuery("SELECT * FROM Task WHERE " + BaseColumns._ID+ "=?", selectionArgs);
	    c.moveToFirst();
	    int itemsIndex = c.getColumnIndex("Items");
	    String task = c.getString(itemsIndex);
	    int notesIndex = c.getColumnIndex("Notes");
	    String note = c.getString(notesIndex);
	    int dateIndex = c.getColumnIndex("Date");
	    String date = c.getString(dateIndex);
	    ContentValues values = new ContentValues();
		values.put("Items", task);
		values.put("ID", rowID);
		values.put("Notes", note);
		values.put("Parent", listID);
		values.put("Date", date);
		database.insert("Completed", "Items", values);
		String where = BaseColumns._ID + "= ?";
		String[] selectionArgs2 = {rowID};
		database.delete("Task", where, selectionArgs2);	
	    //adapter.remove(findObjectFromGivenId(Integer.parseInt(rowID)));
	    //activeTasks--;
		onResume();
	}

	private void checkBoxNotChecked(String rowID){
		String[] selectionArgs = {rowID};
	    Cursor c = database.rawQuery("SELECT * FROM Completed WHERE ID=?", selectionArgs);
	    c.moveToFirst();
	    int itemsIndex = c.getColumnIndex("Items");
	    String task = c.getString(itemsIndex);
	    int notesIndex = c.getColumnIndex("Notes");
	    String note = c.getString(notesIndex);
	    int parentIndex = c.getColumnIndex("Parent");
	    int parent = c.getInt(parentIndex);
	    int dateIndex = c.getColumnIndex("Date");
	    String date = c.getString(dateIndex);
	    ContentValues values = new ContentValues();
		values.put("Items", task);
		values.put(BaseColumns._ID, rowID);
		values.put("Notes", note);
		values.put("Parent", parent);
		values.put("Date", date);
		database.insert("Task", "Items", values);
		String where = "ID = ?";
		String[] selectionArgs2 = {rowID};
		database.delete("Completed", where, selectionArgs2);
		//ListObjects transfer = findObjectFromGivenId(Integer.parseInt(rowID));
	    //adapter.remove(transfer);
	    //adapter.insert(transfer, activeTasks);
	    //activeTasks++;
		onResume();
	}
	
	private ListObjects findObjectFromGivenId(int objectID){
		for(int i=0; i<adapter.getCount(); i++){
			if(adapter.getItem(i).id == objectID)
				return adapter.getItem(i);
		}
		return null;
	}
}

 
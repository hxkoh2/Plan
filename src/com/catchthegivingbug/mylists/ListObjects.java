package com.catchthegivingbug.mylists;


public class ListObjects extends Object {

	int id;
	String rowTask;
	String notes;
	int parentID;
	String date;
	
	//constructor
	public ListObjects(int ID, String Task, String details, int parent, String date){
		this.id = ID;
		this.rowTask = Task;
		this.notes = details;
		this.parentID = parent;
		this.date = date;
	}
	
}

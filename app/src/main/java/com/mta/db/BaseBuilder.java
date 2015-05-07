package com.mta.db;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.ContentValues;

public abstract class BaseBuilder{
	
	protected Map<String, Column> columns = new LinkedHashMap<String, Column>();

	protected final Column getColumn(String name){
		if(columns.containsKey(name)){
			return columns.get(name);
		}
		throw new RuntimeException("Column '"+name+"' not found.");
	}
	
	public boolean isSyncDelayed(){
		for (String key : columns.keySet()) {
			if(columns.get(key).getState().equals(STATE.SYNCED)){
				return false;
			}
		}
		return true;
	}
	
	public abstract ContentValues add(ContentValues values);
	
	public abstract ArrayList<ContentValues> get(QueryHolder qh);
	
	public abstract boolean delete(QueryHolder qh);
	
	public abstract ContentValues edit(ContentValues values, QueryHolder qh);

	public final boolean delete(){
		QueryHolder qh = new QueryHolder(this);
		return delete(qh);
	}
	
	public ArrayList<ContentValues> get(){
		QueryHolder qh = new QueryHolder(this);
		return get(qh);
	}

	public final ContentValues getItem(QueryHolder qh){
		qh.setLimit(1);
		ArrayList<ContentValues> result = get(qh);
		if(result != null && 0 < result.size()){
			return result.get(0);
		}
		else{
			return null;
		}
	}

}
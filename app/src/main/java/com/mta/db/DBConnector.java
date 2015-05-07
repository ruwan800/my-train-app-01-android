package com.mta.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.provider.BaseColumns;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBConnector extends ConnectorBuilder implements BaseColumns{

	private BaseHelper helper;
	private SQLiteDatabase db;
	private boolean newlyCreated = false;

	DBConnector(Context context, Map<String, Column> columns, String table){
		this.columns = columns;
		this.helper = new BaseHelper(context, this, table);
	}
	
	public boolean isNewlyCreated(){
		return newlyCreated;
	}

	private void openWritable(){
		Log.i("MTA","opening db connection for writing");
		db = this.helper.getWritableDatabase();
	}

	private void openReadable(){
		Log.i("MTA","opening db connection for reading");
		db = this.helper.getReadableDatabase();
	}

	private void close(){
		Log.i("MTA","closing db connection");
		db.close();
	}

	protected void createTable(SQLiteDatabase db){
		Log.d("EEE","@BaseModel.createTable");// ####
		String query = "CREATE TABLE " + this.getTableName()  + " (" ;
		String[] columnList = columns.keySet().toArray(new String[columns.size()]);
		for (int i = 0; i < columnList.length; i++) {
			String key = columnList[i];
			query += " "+ columns.get(key).getQuerySubString();
			if(i< columns.size()-1){
				query += " , ";
			}
		}
		query += " ) ";
		Log.i("MTA", query);//####
		try{
			db.execSQL(query);
			newlyCreated = true;
		} catch(Exception e){
			Log.w("MTA",e);
		}
	}

	protected void deleteTable(SQLiteDatabase db){
		Log.d("EEE","@BaseModel.deleteTable");// ####
		String query = "DROP TABLE IF EXISTS " + this.getTableName();
		Log.i("MTA", query);//####
		try{
			db.execSQL(query);
		} catch(Exception e){
			Log.w("MTA",e);
		}
	}
	
	@Override
	public final ContentValues add(ContentValues values){
		Log.d("EEE","@BaseModel.insertItem");// ####
		for (String columnKey : columns.keySet()) {
			TYPE type = columns.get(columnKey).getType();
			String key = columns.get(columnKey).getColumnName();
			boolean remoteOnly = columns.get(columnKey).isRemoteOnly();
			if (type.equals(TYPE.TIMESTAMP)) {
				if( values.get(key) == null){
					Date date= new Date();
					long timestamp = date.getTime();
					values.put(key, timestamp);
				}
			}
			if(remoteOnly){
				values.remove(key);
			}
		}
		try{
			openWritable();
			// Insert the new row, returning the primary key value of the new row
			db.insertWithOnConflict( this.getTableName(),	null, values, SQLiteDatabase.CONFLICT_REPLACE);
		} catch(Exception e){
			Log.w("MTA",e);
			return null;
		} finally{
			db.close();
		}
		QueryHolder qh = new QueryHolder(this);
		return getItem(qh);
		
	}

	@Override
	public ArrayList<ContentValues> get(QueryHolder qh){
		Log.d("EEE","@BaseModel.getItems");//####
		ArrayList<ContentValues> result = new ArrayList<ContentValues>();
		
		try{
			openReadable();
			Cursor c = db.query(
				this.getTableName(),  								// The table to query
				qh.getFieldStrings(),                      			// The columns to return
				qh.getWhereConditionString(),              			// The columns for the WHERE clause
				qh.getWhereArgs(),                   				// The values for the WHERE clause
			    null,                                     			// don't group the rows
			    null,                                     			// don't filter by row groups
			    qh.getSortBy(),                                 	// The sort order
			    qh.getLimit()										// Set limit
			    );

			String[] dbColumns = c.getColumnNames();
			Map<String, Integer> dbColumnIndex = new HashMap<String, Integer>();
			for (int i = 0; i < dbColumns.length; i++) {
				dbColumnIndex.put(dbColumns[i], i);
			}
			if (c.moveToFirst()) {
	            while (!c.isAfterLast()) {
	            	ContentValues cv = new ContentValues();
	            	for (Column field : qh.getFields()) {
	            		if(field.isRemoteOnly()){
	            			continue;
	            		}
	            		if( ! dbColumnIndex.containsKey(field.getColumnName())){
							cv.put(field.getName(), "UNDEFINED");
							Log.w("MTA2", "DBConnector.get Field:"+field.getName()+":UNDEFINED");
							continue;
	            		}
	            		int columnIndex = dbColumnIndex.get(field.getColumnName());
	            		switch (field.getType()) {
						case INTEGER:
							cv.put(field.getName(), c.getInt(columnIndex));
							break;
						case DOUBLE:
							cv.put(field.getName(), c.getDouble(columnIndex));
							break;
						case FLOAT:
							cv.put(field.getName(), c.getFloat(columnIndex));
							break;
						case SHORT:
							cv.put(field.getName(), c.getShort(columnIndex));
							break;
						case BOOLEAN:
							cv.put(field.getName(), Boolean.parseBoolean(c.getString(columnIndex)));
							break;
						case REAL:
							cv.put(field.getName(), c.getString(columnIndex));
							break;
						case TIMESTAMP:
							cv.put(field.getName(), c.getInt(columnIndex)); 
							break;
						case TEXT:
							cv.put(field.getName(), c.getString(columnIndex));
							break;
						default:
							cv.put(field.getName(), "UNDEFINED");
							Log.w("MTA2", "DBConnector.get Field:"+field.getName()+":UNDEFINED");
							break;
						}
					}
	                result.add(cv);
	                c.moveToNext();
	            }
	        }
            c.close();
		    return result;
		} catch(Exception e){
			Log.w("MTA",e);
		} finally{
			close();
		}
		return null;
	    
	}
	
	@Override
	public ContentValues edit(ContentValues values, QueryHolder qh){
		Log.d("EEE","@BaseModel.insertItem");//####
		openWritable();
		for (String columnKey : columns.keySet()) {
			TYPE type = columns.get(columnKey).getType();
			String key = columns.get(columnKey).getColumnName();
			boolean remoteOnly = columns.get(columnKey).isRemoteOnly();
			if (type.equals(TYPE.TIMESTAMP)) {
				if( values.get(key) == null){
					Date date= new Date();
					long timestamp = date.getTime();
					values.put(key, timestamp);
				}
				if(remoteOnly){
					values.remove(key);
				}
			}
		}
		try{
			// Insert the new row, returning the primary key value of the new row
			db.updateWithOnConflict (this.getTableName(), values, qh.getWhereConditionString(), qh.getWhereArgs(), SQLiteDatabase.CONFLICT_IGNORE);
		} catch(Exception e){
			Log.w("MTA",e);
			return null;
		} finally{
			db.close();
		}
		return getItem(qh);
	}

	@Override
	public boolean delete(QueryHolder qh){
		Log.d("EEE","@BaseModel.insertItem");//####
		openWritable();
		try{
			// Insert the new row, returning the primary key value of the new row
			db.delete(this.getTableName(), qh.getWhereConditionString(), qh.getWhereArgs());
		} catch(Exception e){
			Log.w("MTA",e);
			return false;
		} finally{
			db.close();
		}
		return true;
	}
}
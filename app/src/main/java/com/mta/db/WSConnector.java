package com.mta.db;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mta.util.Commons;
import com.mta.util.HTTPRequest;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

public class WSConnector extends ConnectorBuilder{

	private final String CREATE = "add";
	private final String READ = "get";
	private final String UPDATE = "edit";
	private final String DELETE = "delete";
	private Context context;

	WSConnector(Context context, Map<String, Column> columns){
		
		Log.d("EEE","@BaseModel.ModelBase");//####
		this.context = context;
		this.columns = columns;
	}

	@Override
	public final ContentValues add(ContentValues values){
		Log.d("EEE","@BaseModel.insertItem");// ####
		String urlstr = HTTPRequest.formatUrl(this.getTableName(), CREATE);
		String respose = HTTPRequest.post(context, urlstr, values);
		
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(respose);
		} catch (Exception e1) {
			Log.w("MTA2", "Error creating JSON respose object at WSConnector:add");
			return null;
		}
		QueryHolder qh = new QueryHolder(this);
		String[] cols = columns.keySet().toArray(new String[columns.size()]);
		qh.setFields(cols);
		ContentValues result = jsonExtract(qh, jsonObject);
		if(result != null){
			return result;
		}
		return null;
	}

	@Override
	public ArrayList<ContentValues> get(QueryHolder qh){
		Log.d("EEE","@BaseModel.getItems");// ####
		ArrayList<ContentValues> result = new ArrayList<ContentValues>();
		ContentValues values = new ContentValues();
		
		String[] fieldStrings = qh.getFieldStrings();
		if(fieldStrings != null){
			String fields = Commons.join(",", fieldStrings);
			values.put("fields[]", fields);
		}
		
		String[] whereFieldStrings = qh.getWhereFields();
		if(whereFieldStrings != null){
			String whereFields = Commons.join(",", whereFieldStrings);
			values.put("where_fields[]", whereFields);
		}
		
		String[] whereConditionStrings = qh.getWhereConditions();
		if(whereConditionStrings != null){
			String whereConditions = Commons.join(",", whereConditionStrings);
			values.put("where_conditions[]", whereConditions);
		}
		
		String[] whereArgStrings = qh.getWhereArgs();
		if(whereArgStrings != null){
			String whereArgs = Commons.join(",", whereArgStrings);
			values.put("where_args[]", whereArgs);
		}
		
		String sortBy = qh.getSortBy();
		if(sortBy != null){
			values.put("sort_by", sortBy);
		}
		
		String limit = qh.getLimit();
		if(limit != null){
			values.put("limit", limit);
		}
		
		String urlstr = HTTPRequest.formatUrl(this.getTableName(), READ);
		String respose = HTTPRequest.post(context, urlstr, values);
		
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(respose);
		} catch (Exception e1) {
			Log.w("MTA2", "Error creating JSON respose array at WSConnector:get");
			return result;
		}
		for (int i = 0; i < jsonArray.length(); i++) {
	    	JSONObject rec;
			try {
				rec = jsonArray.getJSONObject(i);
			} catch (JSONException e) {
				Log.w("MTA2", "Error getting object from JSON array at WSConnector:get");
				continue;
			}
			ContentValues cv = jsonExtract(qh, rec);
			if(cv != null){
				result.add(cv);
			}
    	}
		return result;
	}
	
	@Override
	public ContentValues edit(ContentValues values, QueryHolder qh){
		Log.d("EEE","@BaseModel.insertItem");//####

		String[] whereFieldStrings = qh.getWhereFields();
		if(whereFieldStrings != null){
			String whereFields = Commons.join(",", whereFieldStrings);
			values.put("where_fields[]", whereFields);
		}
		
		String[] whereConditionStrings = qh.getWhereConditions();
		if(whereConditionStrings != null){
			String whereConditions = Commons.join(",", whereConditionStrings);
			values.put("where_conditions[]", whereConditions);
		}
		
		String[] whereArgStrings = qh.getWhereArgs();
		if(whereArgStrings != null){
			String whereArgs = Commons.join(",", whereArgStrings);
			values.put("where_args[]", whereArgs);
		}
		
		String urlstr = HTTPRequest.formatUrl(this.getTableName(), UPDATE);
		String respose = HTTPRequest.post(context, urlstr, values);

    	JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(respose);
		} catch (Exception e1) {
			Log.w("MTA2", "Error creating JSON respose object at WSConnector:edit");
			return null;
		}
		ContentValues result = jsonExtract(qh, jsonObject);
		if(result != null){
			return result;
		}
		return null;
	}

	@Override
	public boolean delete(QueryHolder qh){
		Log.d("EEE","@BaseModel.insertItem");//####
		ContentValues values = new ContentValues();
		
		String[] whereFieldStrings = qh.getWhereFields();
		if(whereFieldStrings != null){
			String whereFields = Commons.join(",", whereFieldStrings);
			values.put("where_fields[]", whereFields);
		}
		
		String[] whereConditionStrings = qh.getWhereConditions();
		if(whereConditionStrings != null){
			String whereConditions = Commons.join(",", whereConditionStrings);
			values.put("where_conditions[]", whereConditions);
		}
		
		String[] whereArgStrings = qh.getWhereArgs();
		if(whereArgStrings != null){
			String whereArgs = Commons.join(",", whereArgStrings);
			values.put("where_args[]", whereArgs);
		}
		
		String urlstr = HTTPRequest.formatUrl(this.getTableName(), DELETE);
		HTTPRequest.post(context, urlstr, values);
		return false;
	}
	
	private ContentValues jsonExtract(QueryHolder qh, JSONObject rec) {
		ContentValues cv = new ContentValues();
		for (Column field : qh.getFields()) {
			try {
				String col = field.getColumn();
				if(field.isLocalOnly()){
					continue;
				}
				if( ! rec.has(col)){
					Log.w("MTA2", "WSConnector:jsonExtract Field:"+col+" not found");
					continue;
				}
				switch (field.getType()) {
				case INTEGER:
					cv.put(field.getName(),rec.getInt(col));
					break;
				case DOUBLE:
					cv.put(field.getName(),rec.getDouble(col));
					break;
				case FLOAT:
					cv.put(field.getName(),rec.getDouble(col));
					break;
				case SHORT:
					cv.put(field.getName(),rec.getInt(col));
					break;
				case BOOLEAN:
					cv.put(field.getName(),rec.getBoolean(col));
					break;
				case REAL:
					cv.put(field.getName(),rec.getString(col));
					break;
				case TIMESTAMP:
					cv.put(field.getName(),rec.getInt(col));
					break;
				case TEXT:
					cv.put(field.getName(),rec.getString(col));
					break;
				default:
					cv.put(field.getName(), "UNDEFINED");
					break;
				}
			} catch (Exception e) {
				cv.put(field.getName(), "ERROR");
				Log.w("MTA2", "WSConnector:jsonExtract Field:"+field.getName()+" not found");
			}
		}
		return cv;
	}

}
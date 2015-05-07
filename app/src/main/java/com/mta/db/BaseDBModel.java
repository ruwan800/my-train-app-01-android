package com.mta.db;

import java.util.ArrayList;

import android.provider.BaseColumns;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

public abstract class BaseDBModel extends ModelBuilder{

	private DBConnector DBC;

	BaseDBModel(Context context, String table){
		Log.d("EEE","@BaseModel.ModelBase");//TODO ####
		this.addColumn("ID", BaseColumns._ID, STATE.LOCAL, TYPE.INTEGER, DEFAULT.NONE, ATTRIBUTE.PRIMARY, ATTRIBUTE.AI);
		tableStructure();
		DBC = new DBConnector(context, this.columns, table);
		DBC.setTableName(table);
	}

	@Override
	public ContentValues add(ContentValues values) {
		DBC.add(values);
		return values;
	}

	@Override
	public ArrayList<ContentValues> get(QueryHolder qh) {
		return DBC.get(qh);
	}
	
	@Override
	public ContentValues edit(ContentValues values, QueryHolder qh) {
		DBC.edit(values, qh);
		return values;
	}

	@Override
	public boolean delete(QueryHolder qh) {
		if(DBC.delete(qh)){
			return true;
		}
		return false;
	}
	
}
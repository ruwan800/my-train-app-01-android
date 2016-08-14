package com.mta.db;

import java.util.ArrayList;

import android.provider.BaseColumns;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

public abstract class BaseWSModel extends ModelBuilder{

	private WSConnector WSC;

	protected BaseWSModel(Context context, String table){
		Log.d("EEE","@BaseModel.ModelBase");//TODO ####
		this.addColumn("ID", BaseColumns._ID, STATE.LOCAL, TYPE.INTEGER, DEFAULT.NONE, ATTRIBUTE.PRIMARY, ATTRIBUTE.AI);
		tableStructure();
		WSC = new WSConnector(context, this.columns);
		WSC.setTableName(table);
	}

	@Override
	public ContentValues add(ContentValues values) {
		return WSC.add(values);
	}

	@Override
	public ArrayList<ContentValues> get(QueryHolder qh) {
		return WSC.get(qh);
	}
	
	@Override
	public ContentValues edit(ContentValues values, QueryHolder qh) {
		return WSC.edit(values, qh);
	}

	@Override
	public boolean delete(QueryHolder qh) {
		return WSC.delete(qh);
	}
	
}
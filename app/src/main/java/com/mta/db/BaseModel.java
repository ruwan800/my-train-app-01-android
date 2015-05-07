package com.mta.db;

import java.util.ArrayList;

import com.mta.util.HTTPRequest;

import android.provider.BaseColumns;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

public abstract class BaseModel extends ModelBuilder{

	private final String SYNCED = "synced";
	private Context context;
	private DBConnector DBC;
	private WSConnector WSC;

	protected BaseModel(Context context, String localTable, String remoteTable){
		Log.d("EEE","@BaseModel.ModelBase");
		this.context = context;
		this.addColumn("ID", BaseColumns._ID, STATE.LOCAL, TYPE.INTEGER, DEFAULT.NONE, ATTRIBUTE.PRIMARY, ATTRIBUTE.AI);
		tableStructure();
		DBC = new DBConnector(context, this.columns, localTable);
		DBC.setTableName(localTable);
		WSC = new WSConnector(context, this.columns);
		WSC.setTableName(remoteTable);
	}

	public void syncTable() {
		Log.d("EEE","@BaseModel.syncTable");//TODO ####
		ArrayList<ContentValues> cvList = WSC.get();
		DBC.delete();
		if( 0 < cvList.size()){
			for(ContentValues cv : cvList){
				DBC.add(cv);
			}
			String urlstr = HTTPRequest.formatUrl(WSC.getTableName(), SYNCED);
			HTTPRequest.get(context, urlstr);
		}
	}

	@Override
	public ContentValues add(ContentValues values) {
		ContentValues newValues = WSC.add(values);
		DBC.add(newValues);
		return newValues;
	}

	@Override
	public ArrayList<ContentValues> get(QueryHolder qh) {
        for (Column column : qh.getFields()) {
            if (column.getState().equals(STATE.REMOTE) || column.getState().equals(STATE.SYNCED)) {
                ArrayList<ContentValues> result = WSC.get(qh);
                for (ContentValues value : result) {
                    DBC.add(value);
                }
                break;
            }
        }
        ArrayList<ContentValues> result = DBC.get(qh);
		if(DBC.isNewlyCreated()){
			syncTable();
			return DBC.get(qh);
		}
		return result;
	}

	@Override
	public ContentValues edit(ContentValues values, QueryHolder qh) {
		ContentValues newValues = WSC.edit(values, qh);
		DBC.edit(newValues, qh);
		return newValues;
	}

	@Override
	public boolean delete(QueryHolder qh) {
		if(WSC.delete(qh)){
			if(DBC.delete(qh)){
				return true;
			}
		}
		return false;
	}
	
	public boolean update(QueryHolder qh){
		ArrayList<ContentValues> cvList = WSC.get();
		for(ContentValues cv : cvList){
			DBC.add(cv);
		}
		return true;
	}
	
}
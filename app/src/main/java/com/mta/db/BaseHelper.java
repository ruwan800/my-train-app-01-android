package com.mta.db;

import com.mta.util.Commons;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class BaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "mta.db";
    public Context context;
    public DBConnector model;
    
    public BaseHelper(Context context, DBConnector model, String table) {
        super(context, Commons.join(".", DATABASE_NAME, table), null, DATABASE_VERSION);
        this.context = context;
        this.model = model;
		Log.d("EEE","@BaseHelper.BaseHelper");//####
    }
    public void onCreate(SQLiteDatabase db) {
		Log.d("EEE","@BaseHelper.onCreate");//####
		model.createTable(db);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("EEE","@BaseHelper.onUpgrade");//####
		model.deleteTable(db);
		onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("EEE","@BaseHelper.onDowngrade");//####
        onUpgrade(db, oldVersion, newVersion);
    }
    



    
    
}
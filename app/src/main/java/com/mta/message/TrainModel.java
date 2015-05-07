package com.mta.message;

import android.content.ContentValues;
import android.content.Context;

import com.mta.db.BaseModel;
import com.mta.db.DEFAULT;
import com.mta.db.QueryHolder;
import com.mta.db.STATE;
import com.mta.db.TYPE;

import java.util.ArrayList;


public class TrainModel extends BaseModel{

	private static final String TRAIN = "train";
    public static final String NAME = "name";
    public static final String INFO = "info";
    public static final String NUMBER = "number";
	
	TrainModel(Context context) {
		super(context, TRAIN, TRAIN);
	}

	@Override
	protected void tableStructure() {
		this.addColumn(NAME, NAME, STATE.SYNC_DELAYED, TYPE.TEXT, DEFAULT.NONE);
		this.addColumn(INFO, INFO, STATE.SYNC_DELAYED, TYPE.TEXT, DEFAULT.NONE);
		this.addColumn(NUMBER, NUMBER, STATE.SYNC_DELAYED, TYPE.INTEGER, DEFAULT.V_0);
	}

    @Override
    public ArrayList<ContentValues> get(){
        QueryHolder qh = new QueryHolder(this);
        qh.setSortBy(NAME, true);
        return get(qh);
    }

}

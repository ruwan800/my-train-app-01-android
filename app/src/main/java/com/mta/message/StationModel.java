package com.mta.message;

import android.content.ContentValues;
import android.content.Context;

import com.mta.db.ATTRIBUTE;
import com.mta.db.BaseModel;
import com.mta.db.DEFAULT;
import com.mta.db.QueryHolder;
import com.mta.db.STATE;
import com.mta.db.TYPE;

import java.util.ArrayList;


public class StationModel extends BaseModel{

	private static final String STATION = "station";
    public static final String NAME = "name";
    public static final String URI = "uri";
    public static final String ACTIVE = "active";
	
	StationModel(Context context) {
		super(context, STATION, STATION);
	}

	@Override
	protected void tableStructure() {
		this.addColumn(NAME, NAME, STATE.SYNC_DELAYED, TYPE.TEXT, DEFAULT.NONE);
		this.addColumn(URI, URI, STATE.SYNC_DELAYED, TYPE.TEXT, DEFAULT.NONE, ATTRIBUTE.UNIQUE);
		this.addColumn(ACTIVE, ACTIVE, STATE.SYNC_DELAYED, TYPE.BOOLEAN, DEFAULT.V_TRUE);
	}

    @Override
    public ArrayList<ContentValues> get(){
        QueryHolder qh = new QueryHolder(this);
        qh.setSortBy(NAME, true);
        return get(qh);
    }

}

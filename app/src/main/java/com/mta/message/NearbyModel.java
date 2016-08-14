package com.mta.message;

import android.content.Context;

import com.mta.db.BaseWSModel;
import com.mta.db.DEFAULT;
import com.mta.db.STATE;
import com.mta.db.TYPE;


public class NearbyModel extends BaseWSModel{

	private static final String NEARBY = "nearby";
    public static final String NAME = "name";
    public static final String C_TYPE = "c_type";
    public static final String INFO = "info";
    public static final String URI = "uri";
	
	NearbyModel(Context context) {
		super(context, NEARBY);
	}

	@Override
	protected void tableStructure() {
		this.addColumn(NAME, NAME, STATE.REMOTE, TYPE.TEXT, DEFAULT.NONE);
		this.addColumn(C_TYPE, C_TYPE, STATE.REMOTE, TYPE.TEXT, DEFAULT.NONE);
		this.addColumn(INFO, INFO, STATE.REMOTE, TYPE.TEXT, DEFAULT.NONE);
		this.addColumn(URI, URI, STATE.REMOTE, TYPE.TEXT, DEFAULT.NONE);
	}

}

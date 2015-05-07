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


public class ContactModel extends BaseModel{

	//TODO
	private static final String CONTACT = "contact";
	public static final String C_TYPE = "c_type";
    public static final String NAME = "name";
    public static final String INFO = "info";
    public static final String THREAD_ID = "thread_id";
    public static final String FAVOURITE = "favourite";
    public static final String USAGE = "usage";
	
	ContactModel(Context context) {
		super(context, CONTACT, CONTACT);
	}

	@Override
	protected void tableStructure() {
		this.addColumn(C_TYPE, C_TYPE, STATE.SYNCED, TYPE.TEXT, DEFAULT.NONE);
        this.addColumn(THREAD_ID, THREAD_ID, STATE.SYNCED, TYPE.TEXT, DEFAULT.NONE, ATTRIBUTE.UNIQUE);
		this.addColumn(NAME, NAME, STATE.SYNCED, TYPE.TEXT, DEFAULT.NONE);
		this.addColumn(INFO, INFO, STATE.SYNCED, TYPE.TEXT, DEFAULT.NONE);
		this.addColumn(FAVOURITE, FAVOURITE, STATE.SYNC_DELAYED, TYPE.BOOLEAN, DEFAULT.V_FALSE);
		this.addColumn(USAGE, USAGE, STATE.SYNC_DELAYED, TYPE.INTEGER, DEFAULT.V_0);
	}

    @Override
    public ArrayList<ContentValues> get(){
        QueryHolder qh = new QueryHolder(this);
        qh.setSortBy(USAGE, false);
        return get(qh);
    }

}

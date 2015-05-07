package com.mta.message;

import java.util.ArrayList;
import java.util.Collections;

import android.content.ContentValues;
import android.content.Context;

import com.mta.db.BaseModel;
import com.mta.db.CONDITION;
import com.mta.db.DEFAULT;
import com.mta.db.QueryHolder;
import com.mta.db.STATE;
import com.mta.db.TYPE;


public class MessageModel extends BaseModel{

	//TODO
	private static final String MESSAGE_TABLE = "message";
    public static final String C_TYPE = "c_type";
	private static final String R_ID = "remote_id";
	private static final String THREAD_ID = "thread_id";
	private static final String SENDER = "sender";
    public static final String MESSAGE = "message";
    public static final String STAR = "star";
    public static final String TIME = "time";
	
	MessageModel(Context context) {
		super(context, MESSAGE_TABLE, MESSAGE_TABLE);
	}

	@Override
	protected void tableStructure() {
        this.addColumn(C_TYPE, C_TYPE, STATE.SYNCED, TYPE.TEXT, DEFAULT.NONE);
        this.addColumn(R_ID, R_ID, STATE.SYNCED, TYPE.INTEGER, DEFAULT.V_0);
        this.addColumn(THREAD_ID, THREAD_ID, STATE.SYNCED, TYPE.TEXT, DEFAULT.NONE);
        this.addColumn(SENDER, SENDER, STATE.SYNCED, TYPE.TEXT, DEFAULT.NONE);
        this.addColumn(MESSAGE, MESSAGE, STATE.SYNCED, TYPE.TEXT, DEFAULT.NONE);
        this.addColumn(STAR, STAR, STATE.SYNCED, TYPE.INTEGER, DEFAULT.V_0);
        this.addColumn(TIME, TIME, STATE.SYNCED, TYPE.TIMESTAMP, DEFAULT.NONE);
    }

    protected ArrayList<ContentValues>getRecentThread(String contact, int limit){
        QueryHolder qh = new QueryHolder(this);
        qh.setLimit(limit);
        qh.addCondition(THREAD_ID, CONDITION.EQ, contact);
        qh.setSortBy(MessageModel.TIME, false);
        ArrayList<ContentValues> result = this.get(qh);
        //Collections.reverse(result);
        return result;
    }
}

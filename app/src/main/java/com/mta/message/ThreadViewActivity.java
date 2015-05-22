package com.mta.message;

import java.util.ArrayList;
import java.util.Date;

import com.mta.db.CONDITION;
import com.mta.db.QueryHolder;
import com.mta.gcm.GcmUpdateManager;
import com.mta.main.R;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;

public class ThreadViewActivity extends ListActivity implements UpdateNotifyHandler {

	String TAG = "MTA";
	private MessageModel threadModel;
	private BaseListAdapter threadAdapter;
	private static final String DEFAULT = "default";
	private static final String MESSAGE = "message";
	private static final String SENDER = "sender";
    public static final String TIME = "time";
    public static final String STAR = "star";
    public static final String THREAD_ID = "thread_id";
	private static final int MESSAGE_LIMIT= 100;
    private ListView listView;
    private String threadId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_thread_view);
        listView = getListView();
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
        threadId = extras.getString(THREAD_ID);
		prepareThread();
	}

	private void prepareThread() {
		threadModel = new MessageModel(this);
		threadAdapter = new BaseListAdapter(this);
        threadAdapter.addViewType(DEFAULT, R.layout.list_item_message);
        //threadModel.addUpdateNotifier(threadAdapter);
        GcmUpdateManager.registerNotifier(this);
		new ThreadDataGetTask().execute();
	}

    @Override
    public void notifyUpdate(String threadId) {
        new ThreadDataUpdateTask().execute(threadId);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_thread_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    public void sendMessage(View view) {
        setProgressBarIndeterminateVisibility(true);
        ContentValues data = new ContentValues();
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        data.put(MESSAGE, message);
        data.put(THREAD_ID, threadId);
        data.put(TIME, new Date().toString());
        new MessageSendTask().execute(data);
    }

    // Uses AsyncTask to create a task away from the main UI thread.
    private class ThreadDataUpdateTask extends AsyncTask<String , Void, ArrayList<ContentValues>> {

		@Override
		protected ArrayList<ContentValues> doInBackground(String... params) {
			return threadModel.getRecentThread(params[0], MESSAGE_LIMIT);
		}
		@Override
		protected void onPostExecute(ArrayList<ContentValues> data) {
            updateThread(data);
		}
	}

    // Uses AsyncTask to create a task away from the main UI thread.
    private class ThreadDataGetTask extends AsyncTask<Void, Void, ArrayList<ContentValues>> {

		@Override
		protected ArrayList<ContentValues> doInBackground(Void... params) {
			return threadModel.getRecentThread(threadId, MESSAGE_LIMIT);
		}
		@Override
		protected void onPostExecute(ArrayList<ContentValues> data) {
            updateThread(data);
		}
	}

    // Uses AsyncTask to create a task away from the main UI thread.
    private class MessageSendTask extends AsyncTask<ContentValues, Void, ArrayList<ContentValues>> {

        @Override
        protected ArrayList<ContentValues> doInBackground(ContentValues... params) {
            threadModel.add(params[0]);
            return threadModel.getRecentThread(threadId, MESSAGE_LIMIT);
        }

        @Override
        protected void onPostExecute(ArrayList<ContentValues> data) {
            updateThread(data);
        }
    }

    private void updateThread(ArrayList<ContentValues> data){
        threadAdapter.setData(data);
        listView.setAdapter(threadAdapter);
        setProgressBarIndeterminateVisibility(false);
    }

	public static String getTitle(Bundle data) {
		String type = (String)data.get("mMessageType");
		if(type.equals("TRAIN") || type.equals("STATION")){
			return (String)data.get("mTarget");
		}
		else if(type.equals("PERSON") || type.equals("GLOBAL")){
			return (String)data.get("mSender");
		}
		return "TA message";
	}

    /*public static void updateThread(String threadId) {
        QueryHolder queryHolder = new QueryHolder(threadModel);
        queryHolder.addCondition(THREAD_ID, CONDITION.EQ, threadId);
        queryHolder.setLimit(100);
        threadModel.update(queryHolder);
        threadAdapter.notifyDataSetChanged();
    }*/



}

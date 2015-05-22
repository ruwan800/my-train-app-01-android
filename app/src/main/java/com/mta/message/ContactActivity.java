package com.mta.message;

import java.util.ArrayList;

import com.mta.db.CONDITION;
import com.mta.db.QueryHolder;
import com.mta.main.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
//import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

public class ContactActivity extends ListActivity {

	private ContactModel contactModel;
	
	private BaseListAdapter contactListAdapter;
	//private enum contactType { NEARBY, STATION, TRAIN	}
	//private contactType current_message_type = contactType.NEARBY;

	private static final String STATION = "station";
	private static final String TRAIN = "train";
    public static final String THREAD_ID = "thread_id";
    public static final String INFO = "info";
	private static final String C_TYPE = ContactModel.C_TYPE;
    private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_contact_select);
        listView = getListView();
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		setProgressBarIndeterminateVisibility(true);
		contactModel = new ContactModel(this);
		contactListAdapter = new BaseListAdapter(this);
		getContactsFromModel();
	}

    @Override
    protected void onResume(){
        super.onResume();
        new retrieveContactList().execute();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_contact_select, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			//NavUtils.navigateUpFromSameTask(this);
            return true;
        case R.id.create_thread:
            createThread();
            return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void getContactsFromModel(){
		contactListAdapter.addViewType(STATION, R.layout.list_item_station);
		contactListAdapter.addViewType(TRAIN, R.layout.list_item_train);
		contactListAdapter.setTypeTargetField(C_TYPE);
	}
	
    // Uses AsyncTask to create a task away from the main UI thread.
    private class retrieveContactList extends AsyncTask<String, Void, ArrayList<ContentValues>> {
	    
    	@Override
		protected ArrayList<ContentValues> doInBackground(String... vals) {
    		return contactModel.get();
    	}
		protected void onPostExecute(ArrayList<ContentValues> result) {
			if(0<result.size()){
		    	contactListAdapter.setData(result);
                //contactListAdapter.notifyDataSetChanged();
                listView.setAdapter(contactListAdapter);
				setProgressBarIndeterminateVisibility(false);
			}
			else{
                createThread();
			}
	   }
	}
    
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
		ContentValues data = (ContentValues) getListView().getItemAtPosition(position);
        new IncrementUsageThread(data).start();
		Intent threadViewIntent = new Intent(this, ThreadViewActivity.class);
		Bundle passData = new Bundle();
		passData.putString(THREAD_ID, data.getAsString(THREAD_ID));
		threadViewIntent.putExtras(passData);
		startActivity(threadViewIntent);
		
		//TODO set button as focusable
		
    }

	public void gotoThreadActivity(ContentValues data) {
		Log.d("MTA", "Initiate thread Activity with data.");//####
		//TODO Initiate thread Activity with data.
    }

    public void createThread(){
        Intent threadCreateIntent = new Intent(ContactActivity.this, ThreadCreateActivity.class);
        startActivity(threadCreateIntent);
    }

    private class IncrementUsageThread extends Thread {
        private ContentValues data;

        IncrementUsageThread(ContentValues data) {
            this.data = data;
        }

        @Override
        public void run() {
            ContentValues newData = new ContentValues();
            String usage = ContactModel.USAGE;
            newData.put(usage, usage+" + "+ 1);
            QueryHolder qh = new QueryHolder(contactModel);
            String threadId = data.getAsString(ContactModel.THREAD_ID);
            qh.addCondition(ContactModel.THREAD_ID, CONDITION.EQ, threadId);
            contactModel.edit(newData, qh);
        }
    }

}

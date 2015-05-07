package com.mta.message;

import java.util.ArrayList;

import com.mta.main.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;

public class ThreadCreateActivity extends ListActivity {

	private NearbyModel nearbyModel;
	private StationModel stationModel;
	private TrainModel trainModel;
	private ContactModel contactModel;
	
	private BaseListAdapter nearbyListAdapter;
	private BaseListAdapter stationListAdapter;
	private BaseListAdapter trainListAdapter;
	
	private enum contactType { NEARBY, STATION, TRAIN	}
	private contactType current_message_type = contactType.NEARBY;

	private static final String STATION = "station";
	private static final String TRAIN = "train";
	private static final String NAME = "name";
    public static final String ID = "ID";
    public static final String THREAD_ID = "thread_id";
    public static final String INFO = "info";
    public static final String NUMBER = "number";
    public static final String DEFAULT = "default";
    private static final String NEARBY_TYPE = NearbyModel.C_TYPE;
    private static final String CONTACT_TYPE = ContactModel.C_TYPE;
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_thread_select);
		listView = getListView();
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setProgressBarIndeterminateVisibility(false);
		
		prepareModels();
		prepareLists();
		
		ImageButton v = (ImageButton) findViewById(R.id.button1);
		setStation(v);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_thread_select, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void prepareModels(){
		nearbyModel = new NearbyModel(this);
		stationModel = new StationModel(this);
		trainModel = new TrainModel(this);
		contactModel = new ContactModel(this);
	}
	
	private void prepareLists(){
		nearbyListAdapter = new BaseListAdapter(this);
        nearbyListAdapter.addViewType(STATION, R.layout.list_item_station);
        nearbyListAdapter.addViewType(TRAIN, R.layout.list_item_train);
		nearbyListAdapter.setTypeTargetField(NEARBY_TYPE);

		stationListAdapter = new BaseListAdapter(this);
		stationListAdapter.addViewType(DEFAULT, R.layout.list_item_station);

		trainListAdapter = new BaseListAdapter(this);
		trainListAdapter.addViewType(DEFAULT, R.layout.list_item_train);
	}
	
	public void setNearby(View v){
		setProgressBarIndeterminateVisibility(true);
		setSelectedButton(v);
		current_message_type = contactType.NEARBY;
		new retrieveNearbyList().execute();
	}
	
	public void setStation(View v){
		setProgressBarIndeterminateVisibility(true);
		setSelectedButton(v);
		current_message_type = contactType.STATION;
		new retrieveStationList().execute();
	}
	
	public void setTrain(View v){
		setProgressBarIndeterminateVisibility(true);
		setSelectedButton(v);
		current_message_type = contactType.TRAIN;
		new retrieveTrainList().execute();
	}
	
	private void setSelectedButton(View v){
		ImageButton b1 = (ImageButton) findViewById(R.id.button1);
		b1.setSelected(false);
		ImageButton b2 = (ImageButton) findViewById(R.id.button2);
		b2.setSelected(false);
		ImageButton b3 = (ImageButton) findViewById(R.id.button3);
		b3.setSelected(false);
		v.setSelected(true);
	}
    
	// Uses AsyncTask to create a task away from the main UI thread.
    private class retrieveNearbyList extends AsyncTask<String, Void, ArrayList<ContentValues>> {
	    
    	@Override
		protected ArrayList<ContentValues> doInBackground(String... vals) {
    		return nearbyModel.get();
    	}
		protected void onPostExecute(ArrayList<ContentValues> result) {
			setNeabyList(result);
	   }
	}
	
    // Uses AsyncTask to create a task away from the main UI thread.
    private class retrieveStationList extends AsyncTask<String, Void, ArrayList<ContentValues>> {
	    
		@Override
		protected ArrayList<ContentValues> doInBackground(String... vals) {
			return stationModel.get();
		}
		@Override
		protected void onPostExecute(ArrayList<ContentValues> result) {
			setStationList(result);
		}
	}
    
	// Uses AsyncTask to create a task away from the main UI thread.
	private class retrieveTrainList extends AsyncTask<String, Void, ArrayList<ContentValues>> {
	    
    	
		@Override
		protected ArrayList<ContentValues> doInBackground(String... vals) {
			return trainModel.get();
		}
		@Override
		protected void onPostExecute(ArrayList<ContentValues> result) {
		   setTrainList(result);
		}
	}
    
    private void setNeabyList(ArrayList<ContentValues> result){
		nearbyListAdapter.setData(result);
		nearbyListAdapter.notifyDataSetChanged();
		listView.setAdapter(nearbyListAdapter);
		setProgressBarIndeterminateVisibility(false);
    }
    
    private void setStationList(ArrayList<ContentValues> result){
		stationListAdapter.setData(result);
		stationListAdapter.notifyDataSetChanged();
		listView.setAdapter(stationListAdapter);
		setProgressBarIndeterminateVisibility(false);
    }
    
	private void setTrainList(ArrayList<ContentValues> result) {
		trainListAdapter.setData(result);
		trainListAdapter.notifyDataSetChanged();
		listView.setAdapter(trainListAdapter);
		setProgressBarIndeterminateVisibility(false);
	}
    
	@Override 
    public void onListItemClick(ListView l, View v, int position, long id) {
		ContentValues data = (ContentValues) getListView().getItemAtPosition(position);
		ContentValues newData = modifyItemToSaveInThread(data);
		setProgressBarIndeterminateVisibility(true);
		new addToThreadTask().execute(newData);
		
    }
	
    private ContentValues modifyItemToSaveInThread(ContentValues data) {
    	ContentValues newData = new ContentValues();
    	String type = getContactType(data);
		newData.put(CONTACT_TYPE, type);
		newData.put(NAME, data.getAsString(NAME));
    	if(type.equals(STATION)){
    		newData.put(THREAD_ID, data.getAsString(ID));
    		newData.put(StationModel.URI, data.getAsString(StationModel.URI));
    	}
    	else if(type.equals(TRAIN)){
    		newData.put(THREAD_ID, data.getAsString(NUMBER));
    		newData.put(NUMBER, data.getAsString(NUMBER));
    	}
    	else{
    		throw new RuntimeException("Invalid type:"+type);
    	}
		return newData;
	}
    
    private String getContactType(ContentValues data) {
		switch (current_message_type) {
		case NEARBY:
			String field = nearbyListAdapter.getTypeTargetField();
			return data.getAsString(field);
		case STATION:
			return STATION;
		case TRAIN:
			return TRAIN;
		default:
			throw new RuntimeException("Current Message Type Is Missing");
		}
	}
    
 // Uses AsyncTask to create a task away from the main UI thread.
    private class addToThreadTask extends AsyncTask<ContentValues, Void, ContentValues> {
		@Override
		protected ContentValues doInBackground(ContentValues... data) {
			ContentValues result = contactModel.add(data[0]);
			return result;
		}
		@Override
		protected void onPostExecute(ContentValues result) {
			setProgressBarIndeterminateVisibility(false);
			gotoThreadActivity(result);
		}
	}

	public void gotoThreadActivity(ContentValues data) {
		Intent ThreadViewIntent = new Intent(this, ThreadViewActivity.class);
		Bundle passData = new Bundle();
		passData.putString(THREAD_ID, data.getAsString(THREAD_ID));
		ThreadViewIntent.putExtras(passData);
		startActivity(ThreadViewIntent);
		this.finish();
		Log.d("MTA", "Initiate thread Activity with data.");//####
		//TODO Initiate thread Activity with data.
    }

}

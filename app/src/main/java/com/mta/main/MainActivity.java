package com.mta.main;

import com.mta.gcm.GCMConnector;
import com.mta.login.Login;
import com.mta.message.ContactActivity;
import com.mta.message.ThreadCreateActivity;
import com.mta.message.ThreadViewActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class MainActivity extends Activity {

	private int workCount = 0;
	private final int MAX_WORKS = 2; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		Login.setCookies(this);
		
		if( ! Login.isRegistered(this)){
			Intent LoginIntent = new Intent(this, LoginActivity.class);
			startActivity(LoginIntent);
		}
		else{
			setProgressBarIndeterminateVisibility(true);
			new CheckLoggedInTask().execute();
			new CheckGCMRegistrationTask().execute();
		}
	}

    @Override
    protected void onResume(){
        super.onResume();
        if( Login.isRegistered(this)){
            setProgressBarIndeterminateVisibility(true);
            new CheckLoggedInTask().execute();
            new CheckGCMRegistrationTask().execute();
        }
    }
    
    private void continueMain(){
		Intent contactIntent = new Intent(this, ContactActivity.class);
		contactIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(contactIntent);
        finish();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	
    // Uses AsyncTask to create a task away from the main UI thread.
    private class CheckLoggedInTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			return Login.isLoggedIn(MainActivity.this);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if(! result){
				//TODO error. shutdown
			}
			setWorkComplete();
	   }
	}
    

    // Uses AsyncTask to create a task away from the main UI thread.
    private class CheckGCMRegistrationTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			GCMConnector gcm = new GCMConnector(MainActivity.this);
			if(! gcm.isRegistered()){
				gcm.register();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void none) {
			setWorkComplete();
	   }
	}

    private void setWorkComplete(){
		workCount++;
    	if(workCount == MAX_WORKS){
    		setProgressBarIndeterminateVisibility(false);
    		continueMain();
    	}
    }
}

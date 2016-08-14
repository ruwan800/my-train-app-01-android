package com.mta.login;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import com.mta.main.R;
import com.mta.util.HTTPRequest;

public class Login {

	private static CookieManager cookieManager = new CookieManager( null, CookiePolicy.ACCEPT_ALL );
	private static Context context=null;
	private static SharedPreferences sharedPreferences;
	private static int loginTimeoutinDays = ((1000*60*60*24)*20);
	
	private static void setSharedPreferences(Context context){
		Login.context = context;
		sharedPreferences = getPreferences(R.string.user_information_file_key);
	}
	
	private static SharedPreferences getPreferences(int resource){
		return Login.context.getSharedPreferences(
				Login.context.getString(resource), Context.MODE_PRIVATE);
	}
	
	private static boolean updateRegistration(Context context) throws NetworkOnMainThreadException{
		setSharedPreferences(context);
		String result = HTTPRequest.get(Login.context, Login.context.getString(R.string.url_user_logincheck));
		if (HTTPRequest.getResultStatus(result)){
			Log.i("MTA","User already logged in");
			setLastLogin();
			updateCookies();
			return true;
		}
		else{
			Log.i("MTA","User not registered");
			updateCookies();
			//register(context);
			//TODO we need to login again. but we did not implemented that.
			return false;
		}
	}

	//set cookies at the beginning of the app.
	public static void setCookies(Context context) {
		Log.d("MTA","@Login.setCookies");
		Login.context = context;
		SharedPreferences cookiePreferences = getPreferences(R.string.http_cookie_file_key);
		String siteUrl =Login.context.getString(R.string.url_site);
		String siteDomain =Login.context.getString(R.string.domain_site);
		URI uri = null;
		try {
			uri = new URI(siteUrl);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	
		for( String key :cookiePreferences.getAll().keySet()){
			HttpCookie cookie = new HttpCookie(key, cookiePreferences.getString(key, null));
			cookie.setDomain(siteDomain);
			cookie.setPath("/");
			cookie.setVersion(0);
			cookieManager.getCookieStore().add(uri, cookie);
		}
		CookieHandler.setDefault( cookieManager );
	}

	private static void updateCookies() {
		Log.d("MTA","@Login.updateCookies");
		SharedPreferences cookiePreferences = getPreferences(R.string.http_cookie_file_key);
		SharedPreferences.Editor editor = cookiePreferences.edit();
		
		String siteUrl =context.getString(R.string.url_site);
		URI uri = null;
		try {
			uri = new URI(siteUrl);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		for(HttpCookie ck : cookieManager.getCookieStore().get(uri)){
			editor.putString(ck.getName(), ck.getValue());
		}
		editor.commit();
	}

	//Register user for the first time.
	public static boolean register(Context context, Map<String,String> userData) throws NetworkOnMainThreadException {
		Log.d("MTA","@Login.register");
		setSharedPreferences(context);
		String result = HTTPRequest.post(context, context.getString(R.string.url_user_register), userData);

		if (HTTPRequest.getResultStatus(result)){
			Log.i("MTA","User Login success");
			updateCookies();
			setLastLogin();
			setRegistered(true);
			setLoggedIn(true);
			return true;
		}
		else{
			setRegistered(false);
			setLoggedIn(false);
			Log.i("MTA","User Login failed");
		}
		return false;
	}

	//check whether user registered. then need to check isLoggeIn.
	public static boolean isRegistered(Context context){
		Log.d("MTA","@Login.isRegistered");
		setSharedPreferences(context);
		return sharedPreferences.getBoolean(Login.context.getString(R.string.user_registration_success), false);
	}
	
	//check whether logged in. check if registered before this. login again if login timed out. 
	public static boolean isLoggedIn(Context context) throws NetworkOnMainThreadException {
		Log.d("MTA","@Login.isLoggedIn");
		setSharedPreferences(context);
		boolean result = sharedPreferences.getBoolean(context.getString(R.string.user_login_success), false);
		if( ! result) {
			return false;
		}
		else if( ! isLoginTimedOut()){
			return true;
		}
		else{
			return updateRegistration(context);
		}
	}
	
	private static void setRegistered(Boolean flag){
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(context.getString(R.string.user_registration_success), flag);
		editor.commit();
	}

	private static void setLoggedIn(Boolean flag){
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(context.getString(R.string.user_login_success), flag);
		editor.commit();
	}
	
	private static long getLastLogin(){
		long defaultDate = (long)loginTimeoutinDays*(1000*60*60*24);
		return sharedPreferences.getLong(context.getString(R.string.user_last_login), defaultDate);
	}
	
	private static void setLastLogin(){
		SharedPreferences.Editor editor = sharedPreferences.edit();
		Date today = new Date();
		long time = today.getTime() - today.getTimezoneOffset()*60*1000;
		editor.putLong(context.getString(R.string.user_last_login), time);
		editor.commit();
	}
	
	private static boolean isLoginTimedOut(){
		long today = new Date().getTime();
		long duration = today - getLastLogin();
		return loginTimeoutinDays < duration ? true : false ;
	}
}


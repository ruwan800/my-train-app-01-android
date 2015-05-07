package com.mta.util;

import android.content.Context;

@Deprecated
public class ContextRetriever {
	private static Context context;
	private static boolean contextSet = false;
	public static void setContext(Context context){
		ContextRetriever.context = context;
		ContextRetriever.contextSet = true;
	}
	public static Context getContext(){
		if(ContextRetriever.contextSet){
			return ContextRetriever.context;
		}
		throw new RuntimeException("Context not assigned in Activity");
	}
}

/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mta.gcm;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mta.main.R;
import com.mta.message.ThreadViewActivity;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    Notification.Builder builder;
    Activity messageActivity;
    public static final String TAG = "MTA";
    public static final String THREAD_ID = "thread_id";

    public GcmIntentService() {
        super("GcmIntentService");
        Log.d("MTA","@GcmIntentService::Default");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    	Log.d("MTA","@GcmIntentService::onHandleIntent");//####
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            	Log.d(TAG,"Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
            	Log.d(TAG,"Deleted messages on server: " + extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            	Log.d(TAG,"Message received:: ");
                handleNotification(extras);
            }else{
            	Log.d(TAG,"Error: " + messageType);
            }
            	
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    /*	Put the message into a notification and post it.
     *	This is just one simple example of what you might choose to do with
     *	a GCM message.
     */
    private void handleNotification(Bundle data) {
    	Log.d("MTA","@GcmIntentService::sendNotification");//####
    	Intent nextIntent;
        nextIntent = new Intent(this, ThreadViewActivity.class);
        String title = (String) (data.containsKey("mthread") ? data.get("mthread"): "Random Chat");
        String message = (String) (data.containsKey("msender") ? data.get("msender"): "Random User");
        String time = (String) (data.containsKey("mtime") ? data.get("mtime"): "Random User");
        String reference = (String) (data.containsKey("mreference") ? data.get("mreference"): null);
        if (reference == null) {
            return;
        }
        String realTitle = title + "  " + time;
        String realMessage = "Message from " + message;


        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        Bundle passData = new Bundle();
        passData.putString(THREAD_ID, reference);
        nextIntent.putExtras(passData);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, nextIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
    	.setContentTitle(realTitle)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(realMessage))
        .setAutoCancel(true)
    	.setContentText(realMessage);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        GcmUpdateManager.notifyThreadUpdate(reference, 1);
        
    }
}
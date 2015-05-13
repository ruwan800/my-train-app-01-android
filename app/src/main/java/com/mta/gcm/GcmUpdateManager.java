package com.mta.gcm;

import com.mta.db.QueryHolder;
import com.mta.message.MessageModel;
import com.mta.message.ThreadViewActivity;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ruwan on 5/11/15.
 */
public class GcmUpdateManager {

    private static Map<String, AtomicInteger> threadUpdates = new ConcurrentHashMap<String, AtomicInteger>();
    private static long lastUpdate;
    private static final int DELAY = 20000;
    private static MessageModel messageModel;

    public static void notifyThreadUpdate(String threadId, int messageCount) {
        if (threadUpdates.containsKey(threadId)) {
            threadUpdates.get(threadId).addAndGet(messageCount);
        } else {
            threadUpdates.put(threadId, new AtomicInteger(0));
        }
        updateMessageModel(threadId);
    }

    private static void updateMessageModel(String threadId) {
        long updateTime = new Date().getTime();
        if (updateTime-lastUpdate < DELAY) {
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ThreadViewActivity.updateThread(threadId);
    }
}

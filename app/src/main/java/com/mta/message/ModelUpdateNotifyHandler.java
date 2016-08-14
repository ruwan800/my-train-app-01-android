package com.mta.message;

import com.mta.db.QueryHolder;

/**
 * Created by ruwan on 5/18/15.
 */
public interface ModelUpdateNotifyHandler {

    public void notifyUpdate(QueryHolder qh);

}

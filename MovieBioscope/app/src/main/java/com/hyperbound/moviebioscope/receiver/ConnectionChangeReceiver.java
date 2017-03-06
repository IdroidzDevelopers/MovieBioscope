package com.hyperbound.moviebioscope.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hyperbound.moviebioscope.volley.VolleyUtil;
import com.lib.utility.util.Logger;

public class ConnectionChangeReceiver extends BroadcastReceiver {
    private static final String TAG = ConnectionChangeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.debug(TAG, "onReceive() called");
        if (null != intent) {
            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                VolleyUtil.syncAnalyticDataIfRequired();
            }
        }
    }
}

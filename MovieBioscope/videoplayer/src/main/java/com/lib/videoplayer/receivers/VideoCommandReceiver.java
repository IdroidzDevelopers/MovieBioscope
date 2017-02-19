package com.lib.videoplayer.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lib.utility.util.CustomIntent;


public class VideoCommandReceiver extends BroadcastReceiver {
    private static final String TAG = "test" + VideoCommandReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive() ");
        if (null != intent) {
            if (CustomIntent.ACTION_VIDEO_DATA_RECEIVED.equals(intent.getAction())) {

            }
        }
    }
}

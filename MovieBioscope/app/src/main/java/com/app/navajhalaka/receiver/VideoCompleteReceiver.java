package com.app.navajhalaka.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import com.app.navajhalaka.util.AppTaskHandler;
import com.lib.utility.util.CustomIntent;
import com.lib.utility.util.Logger;


public class VideoCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = VideoCompleteReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.info(TAG, "onReceive() ");
        if (null != intent) {
            if (CustomIntent.ACTION_MOVIE_COMPLETED.equals(intent.getAction()) || CustomIntent.ACTION_ADV_COMPLETED.equals(intent.getAction())) {
                String id = intent.getStringExtra(CustomIntent.EXTRAS.VIDEO_ID);
                if (null != id) {
                    Logger.info(TAG, "onReceive() :: action " + intent.getAction());
                    Bundle bundle = new Bundle();
                    bundle.putString(CustomIntent.EXTRAS.VIDEO_ID, id);
                    Message message = new Message();
                    message.what = AppTaskHandler.TASK.SYNC_ANALYTIC_DATA;
                    message.setData(bundle);
                    AppTaskHandler.getInstance().sendMessage(message);
                }
            }
        }
    }
}

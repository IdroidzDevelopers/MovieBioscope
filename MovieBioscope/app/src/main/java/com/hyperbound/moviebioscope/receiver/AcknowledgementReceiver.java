package com.hyperbound.moviebioscope.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import com.hyperbound.moviebioscope.util.BusUtil;
import com.lib.utility.util.CustomIntent;
import com.lib.utility.util.Logger;
import com.lib.videoplayer.ui.MovieListDialog;
import com.lib.videoplayer.util.StateMachine;
import com.lib.videoplayer.util.VideoData;
import com.lib.videoplayer.util.VideoTaskHandler;


public class AcknowledgementReceiver extends BroadcastReceiver {
    private static final String TAG = AcknowledgementReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.info(TAG, "onReceive() ");
        if (null != intent) {
            if (CustomIntent.ACTION_ACK_DATA_RECEIVED.equals(intent.getAction())) {
                Bundle lBundle = intent.getBundleExtra("data");
                Logger.debug(TAG, "Bundle data " + lBundle.toString());
                if (null != lBundle) {
                    String fleetId=BusUtil.getFleetId();
                    String transactionId;
                    String assetId;
                    String status;
                    if(lBundle.containsKey("transactionid")) {
                        transactionId=lBundle.getString("transactionid");
                    }
                    if (lBundle.containsKey("assetid")){
                        assetId=lBundle.getString("assetid");
                    }
                    if (lBundle.containsKey("status")){
                        status=lBundle.getString("status");
                    }
                }
            }
        }
    }
}

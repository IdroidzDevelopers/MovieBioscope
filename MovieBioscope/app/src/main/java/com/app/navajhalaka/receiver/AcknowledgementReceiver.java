package com.app.navajhalaka.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.app.navajhalaka.util.BusUtil;
import com.app.navajhalaka.volley.VolleyUtil;
import com.lib.utility.util.CustomIntent;
import com.lib.utility.util.Logger;


public class AcknowledgementReceiver extends BroadcastReceiver {
    private static final String TAG = AcknowledgementReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.info(TAG, "onReceive() ");
        if (null != intent) {
            if (CustomIntent.ACTION_ACK_DATA_RECEIVED.equals(intent.getAction())) {
                Bundle lBundle = intent.getBundleExtra("data");
                if (null != lBundle) {
                    Logger.debug(TAG, "Bundle data " + lBundle.toString());
                    String fleetId = BusUtil.getFleetId();
                    String transactionId = null;
                    String assetId = null;
                    String status = null;
                    if (lBundle.containsKey("transactionid")) {
                        transactionId = lBundle.getString("transactionid");
                    }
                    if (lBundle.containsKey("assetid")) {
                        assetId = lBundle.getString("assetid");
                    }
                    if (lBundle.containsKey("status")) {
                        status = lBundle.getString("status");
                    }
                    VolleyUtil.sendCommandAcknowledgement(fleetId, transactionId, assetId, status);
                }
            }
        }
    }
}

package com.lib.route.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;


public class RouteTaskHandler extends Handler {
    private static final String TAG = RouteTaskHandler.class.getSimpleName();
    public static final String INTENT_ROUTE_CHANGED = "com.lib.location.LOCATION_CHANGED";
    private static Context sContext;

    public interface TASK {
        int UPDATE_DEFAULT_ROUTE = 3;
    }

    public interface KEY {
        String ROUTE_ID = "route_id";
    }

    private static RouteTaskHandler sInstance;


    private RouteTaskHandler(Looper looper) {
        super(looper);
    }

    public static RouteTaskHandler getInstance(Context aContext) {
        sContext = aContext;
        if (null == sInstance) {
            synchronized (RouteTaskHandler.class) {
                if (null == sInstance) {
                    HandlerThread lThread = new HandlerThread(TAG);
                    lThread.start();
                    sInstance = new RouteTaskHandler(lThread.getLooper());
                }
            }
        }
        return sInstance;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Bundle lBundle = msg.getData();
        switch (msg.what) {
            case TASK.UPDATE_DEFAULT_ROUTE:
                if (null != lBundle) {
                    String lRouteId = (String) lBundle.get(KEY.ROUTE_ID);
                    RouteUtil.updateCurrentRoute(sContext, lRouteId);
                    LocalBroadcastManager.getInstance(sContext).sendBroadcast(new Intent(INTENT_ROUTE_CHANGED));
                }
                break;
        }
    }
}

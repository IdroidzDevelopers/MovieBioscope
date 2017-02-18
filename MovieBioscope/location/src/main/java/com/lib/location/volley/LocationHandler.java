package com.lib.location.volley;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lib.location.LocationApplication;
import com.lib.location.model.DistanceData;
import com.lib.location.model.Elements;
import com.lib.location.model.Rows;
import com.lib.location.util.LocationInterface;
import com.lib.location.util.LocationUtil;
import com.lib.utility.util.CustomIntent;

/**
 * Created by Aron on 2/3/2017.
 */

public class LocationHandler extends Handler {

    private static final String TAG = "LocationHandler";

    private static LocationHandler sInstance;

    private LocationHandler(Looper looper) {
        super(looper);
    }

    public static LocationHandler getInstance() {
        if (null == sInstance) {
            synchronized (LocationHandler.class) {
                HandlerThread lThread = new HandlerThread("Network");
                lThread.start();
                if (null == sInstance) {
                    sInstance = new LocationHandler(lThread.getLooper());
                }
                // lThread.quitSafely();
            }
        }
        return sInstance;
    }

    @Override
    public void handleMessage(Message msg) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        switch (msg.what) {
            case LocationInterface.HANDLE_LOCATION_INFO: {
                DistanceData distanceData = gson.fromJson(msg.getData().getString("data"), DistanceData.class);
                if (null != distanceData) {
                    if (distanceData.getStatus().equalsIgnoreCase(LocationInterface.STATUS_OK_KEY)) {
                        Rows[] rows = distanceData.getRows();
                        if (rows.length > 0) {
                            Rows row = rows[0];
                            if (null != row) {
                                Elements[] elements = row.getElements();
                                if (elements.length > 0) {
                                    Elements sourceData = elements[0];
                                    String sourceDistance = sourceData.getDistance().getText();
                                    Elements destinationData = elements[1];
                                    String destinationDistance = destinationData.getDistance().getText();
                                    String trafficTime = destinationData.getDuration_in_traffic().getText();
                                    LocationUtil.updateLocationInfo(sourceDistance,destinationDistance,trafficTime);
                                    LocalBroadcastManager.getInstance(LocationApplication.getLocationContext()).sendBroadcast(new Intent(CustomIntent.ACTION_LOCATION_INFO_CHANGED));
                                    Log.d(TAG, "Success");
                                }
                            }
                        }
                    }
                }
                break;
            }
            case LocationInterface.HANDLE_JOURNEY_INFO: {
                DistanceData distanceData = gson.fromJson(msg.getData().getString("data"), DistanceData.class);
                if (null != distanceData) {
                    if (distanceData.getStatus().equalsIgnoreCase(LocationInterface.STATUS_OK_KEY)) {
                        Rows[] rows = distanceData.getRows();
                        if (rows.length > 0) {
                            Rows row = rows[0];
                            if (null != row) {
                                Elements[] elements = row.getElements();
                                if (elements.length > 0) {
                                    Elements sourceData = elements[0];
                                    String totalDistance = sourceData.getDistance().getText();
                                    String totalTimeTraffic = sourceData.getDuration_in_traffic().getText();
                                    LocationUtil.updateJourneyInfo(totalDistance,totalTimeTraffic);
                                    LocalBroadcastManager.getInstance(LocationApplication.getLocationContext()).sendBroadcast(new Intent(CustomIntent.ACTION_JOURNEY_INFO_CHANGED));
                                    Log.d(TAG, "Success");
                                }
                            }
                        }
                    }
                }
                break;
            }
        }
    }
}

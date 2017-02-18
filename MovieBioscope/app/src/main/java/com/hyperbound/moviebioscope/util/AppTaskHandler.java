package com.hyperbound.moviebioscope.util;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.media.session.PlaybackStateCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyperbound.moviebioscope.app.BioscopeApp;
import com.hyperbound.moviebioscope.firebase.FireBaseManager;
import com.hyperbound.moviebioscope.model.BusDetails;
import com.hyperbound.moviebioscope.model.BusRegData;
import com.hyperbound.moviebioscope.model.DestinationDetails;
import com.hyperbound.moviebioscope.model.Routes;
import com.hyperbound.moviebioscope.model.SourceDetails;
import com.lib.route.util.RouteUtil;
import com.lib.utility.util.CustomIntent;

import java.util.List;


public class AppTaskHandler extends Handler {
    private static final String TAG = AppTaskHandler.class.getSimpleName();

    public interface TASK {
        int SAVE_BUS_DATA = 0;
    }

    public interface KEY {
        String REG_NUMBER = "reg_number";
    }

    private static AppTaskHandler sInstance;


    private AppTaskHandler(Looper looper) {
        super(looper);
    }

    public static AppTaskHandler getInstance() {
        if (null == sInstance) {
            synchronized (AppTaskHandler.class) {
                if (null == sInstance) {
                    HandlerThread lThread = new HandlerThread(TAG);
                    lThread.start();
                    sInstance = new AppTaskHandler(lThread.getLooper());
                }
            }
        }
        return sInstance;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Bundle lBundle = msg.getData();
        switch (msg.what) {
            case TASK.SAVE_BUS_DATA:
                if (null != lBundle) {
                    String lRegNumber = (String) lBundle.get(KEY.REG_NUMBER);
                    BusUtil.saveRegistrationDetail(BioscopeApp.getContext(), lRegNumber);
                }
                break;
            case AppInterface.HANDLE_BUS_DETAILS:
                if (null != lBundle) {
                    BusRegData busData = gson.fromJson(msg.getData().getString("data"), BusRegData.class);
                    if (null != busData && null != busData.getMessage() && busData.getMessage().equalsIgnoreCase("Success")) {
                        BusDetails busDetails[] = busData.getData();
                        if (busDetails.length > 0) {
                            BusDetails busDetail = busDetails[0];
                            if (null != busDetail) {
                                BusUtil.insertBusInfo(busDetail.getFleetID(), busDetail.getRegNo(),
                                        busDetail.getCompany(), busDetail.getCompanyName());
                                Routes busRoutes[] = busDetail.getRoutes();
                                List<String> topicsList=busDetail.getTopics();
                                if(null!=topicsList&&topicsList.size()>0) {
                                    for(String topic:topicsList) {
                                        BusUtil.insertFirebaseTopics(topic);
                                    }
                                }
                                FireBaseManager.getFireBaseToken();
                                FireBaseManager.subscribeFirebaseTopics();
                                if (busRoutes.length > 0) {
                                    for (Routes route : busRoutes) {
                                        if (null != route) {
                                            String routeId = route.getRouteID();
                                            String source = route.getSource();
                                            String destination = route.getDestination();
                                            SourceDetails sourceInfo = route.getSourceDetails();
                                            DestinationDetails destinationInfo = route.getDestinationDetails();
                                            RouteUtil.insertRouteInfo(routeId, source, sourceInfo.getFormatted_address(),
                                                    sourceInfo.getLatitude(), sourceInfo.getLongitude(),
                                                    destination, destinationInfo.getFormatted_address(),
                                                    destinationInfo.getLatitude(), destinationInfo.getLongitude());
                                        }

                                    }
                                    LocalBroadcastManager.getInstance(BioscopeApp.getContext()).sendBroadcast(new Intent(CustomIntent.ACTION_ROUTE_RECEIVED));
                                }
                            }
                        }
                    }
                }
                break;
        }
    }
}

package com.app.navajhalaka.app;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.app.navajhalaka.database.BusProvider;
import com.app.navajhalaka.receiver.AcknowledgementReceiver;
import com.app.navajhalaka.receiver.VideoCompleteReceiver;
import com.lib.firebase.FireBaseApplication;
import com.lib.location.LocationApplication;
import com.lib.location.databases.LocationProvider;
import com.lib.route.RouteApplication;
import com.lib.utility.util.CustomIntent;
import com.lib.videoplayer.VideoApplication;
import com.lib.videoplayer.database.VideoProvider;
import com.lib.videoplayer.receivers.VideoCommandReceiver;
import com.lib.videoplayer.util.AdsSlotConfigUtil;
import com.lib.videoplayer.util.StateMachine;

public class BioscopeApp extends Application {

    private static final String TAG = BioscopeApp.class.getSimpleName();
    private static final String FOLDER_NAME = "movie_bioscope";
    private static final String FIRST_RUN_KEY = "first_run";

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        VideoApplication.setVideoContext(this);
        LocationApplication.setLocationContext(this);
        RouteApplication.setRouteContext(this);
        FireBaseApplication.setFirebaseContext(this);
        registerVideoCommand();
        registerVideoComplete();
        registerAcknowledgementCommand();
        if (isFirstRun()) {
            initSequence();
            initIntroVideo();
            putAdsConfigData();
        }
        setFirstRun(false);
    }

    private boolean isFirstRun() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean(FIRST_RUN_KEY, true);
    }

    private void initIntroVideo() {
        ContentValues lValues1 = new ContentValues();
        lValues1.put(VideoProvider.VIDEO_COLUMNS.VIDEO_ID, "VID3203");
        lValues1.put(VideoProvider.VIDEO_COLUMNS.NAME, "Navajhalaka");
        lValues1.put(VideoProvider.VIDEO_COLUMNS.TYPE, VideoProvider.VIDEO_TYPE.INTRO_VIDEO);
        lValues1.put(VideoProvider.VIDEO_COLUMNS.PATH, "android.resource://" + getContext().getPackageName() + "/" + com.lib.videoplayer.R.raw.navajhalaka_intro);
        lValues1.put(VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME, System.currentTimeMillis());
        lValues1.put(VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED);
        getContext().getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValues1);
    }

    private void initSequence() {
        String[] landingSequence = getContext().getResources().getStringArray(com.lib.videoplayer.R.array.landing_sequence);
        for (int i = 0; i < landingSequence.length; i++) {
            ContentValues values = new ContentValues();
            values.put(VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_TYPE, StateMachine.SEQUENCE_TYPE.LANDING_TYPE);
            values.put(VideoProvider.SEQUENCE_COLUMNS.VIDEO_TYPE, landingSequence[i]);
            values.put(VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_ORDER, i);
            values.put(VideoProvider.SEQUENCE_COLUMNS.UPDATED_TIME, System.currentTimeMillis());
            getContext().getContentResolver().insert(VideoProvider.CONTENT_URI_SEQUENCE_TABLE, values);
        }

        String[] movieInitSequence = getContext().getResources().getStringArray(com.lib.videoplayer.R.array.movie_init_sequence);
        for (int i = 0; i < movieInitSequence.length; i++) {
            ContentValues values = new ContentValues();
            values.put(VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_TYPE, StateMachine.SEQUENCE_TYPE.MOVIE_INIT_TYPE);
            values.put(VideoProvider.SEQUENCE_COLUMNS.VIDEO_TYPE, movieInitSequence[i]);
            values.put(VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_ORDER, i);
            values.put(VideoProvider.SEQUENCE_COLUMNS.UPDATED_TIME, System.currentTimeMillis());
            getContext().getContentResolver().insert(VideoProvider.CONTENT_URI_SEQUENCE_TABLE, values);
        }
    }

    public static Context getContext() {
        return sContext;
    }

    /***********************************************************************************
     * Testing code needs to be removed
     */
    private void putDummyData() {
        ContentValues lValues1 = new ContentValues();
        lValues1.put(VideoProvider.VIDEO_COLUMNS.VIDEO_ID, "dummy01");
        lValues1.put(VideoProvider.VIDEO_COLUMNS.NAME, "Spotlight");
        lValues1.put(VideoProvider.VIDEO_COLUMNS.TYPE, VideoProvider.VIDEO_TYPE.MOVIE);
        lValues1.put(VideoProvider.VIDEO_COLUMNS.PATH, "/storage/emulated/0/movie_bioscope/movie_1.mp4");
        lValues1.put(VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME, System.currentTimeMillis());
        lValues1.put(VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED);
        getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValues1);

        ContentValues lValues2 = new ContentValues();
        lValues2.put(VideoProvider.VIDEO_COLUMNS.VIDEO_ID, "dummy02");
        lValues2.put(VideoProvider.VIDEO_COLUMNS.NAME, "breaking news video");
        lValues2.put(VideoProvider.VIDEO_COLUMNS.TYPE, VideoProvider.VIDEO_TYPE.BREAKING_VIDEO);
        lValues2.put(VideoProvider.VIDEO_COLUMNS.PATH, "/storage/emulated/0/movie_bioscope/breaking_video.mp4");
        lValues2.put(VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME, System.currentTimeMillis());
        lValues2.put(VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED);
        getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValues2);

        ContentValues lValue3 = new ContentValues();
        lValue3.put(VideoProvider.VIDEO_COLUMNS.VIDEO_ID, "dummy03");
        lValue3.put(VideoProvider.VIDEO_COLUMNS.NAME, "Ad1");
        lValue3.put(VideoProvider.VIDEO_COLUMNS.TYPE, VideoProvider.VIDEO_TYPE.ADV);
        lValue3.put(VideoProvider.VIDEO_COLUMNS.PATH, "/storage/emulated/0/movie_bioscope/ad_1.mp4");
        lValue3.put(VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME, System.currentTimeMillis());
        lValue3.put(VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED);
        getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValue3);

        ContentValues lValue4 = new ContentValues();
        lValue4.put(VideoProvider.VIDEO_COLUMNS.VIDEO_ID, "dummy04");
        lValue4.put(VideoProvider.VIDEO_COLUMNS.NAME, "Ad2");
        lValue4.put(VideoProvider.VIDEO_COLUMNS.TYPE, VideoProvider.VIDEO_TYPE.ADV);
        lValue4.put(VideoProvider.VIDEO_COLUMNS.PATH, "/storage/emulated/0/movie_bioscope/ad_2.mp4");
        lValue4.put(VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME, System.currentTimeMillis());
        lValue4.put(VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED);
        getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValue4);

        ContentValues lValue5 = new ContentValues();
        lValue5.put(VideoProvider.VIDEO_COLUMNS.VIDEO_ID, "dummy05");
        lValue5.put(VideoProvider.VIDEO_COLUMNS.NAME, "Breaking news");
        lValue5.put(VideoProvider.VIDEO_COLUMNS.TYPE, VideoProvider.VIDEO_TYPE.BREAKING_NEWS);
        lValue5.put(VideoProvider.VIDEO_COLUMNS.MESSAGE, "Rs 500 and 1000 notes banned from Non 8th .This was announced today by PM Modi");
        lValue5.put(VideoProvider.VIDEO_COLUMNS.PATH, "/storage/emulated/0/movie_bioscope/breaking_image.jpg");
        lValue5.put(VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME, System.currentTimeMillis());
        lValue5.put(VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED);
        getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValue5);

        ContentValues lValue6 = new ContentValues();
        lValue6.put(VideoProvider.VIDEO_COLUMNS.VIDEO_ID, "dummy06");
        lValue6.put(VideoProvider.VIDEO_COLUMNS.NAME, "Traveller");
        lValue6.put(VideoProvider.VIDEO_COLUMNS.TYPE, VideoProvider.VIDEO_TYPE.COMPANY_VIDEO);
        lValue6.put(VideoProvider.VIDEO_COLUMNS.PATH, "/storage/emulated/0/movie_bioscope/traveller.mp4");
        lValue6.put(VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME, System.currentTimeMillis());
        lValue6.put(VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED);
        getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValue6);

        ContentValues lValue7 = new ContentValues();
        lValue7.put(VideoProvider.VIDEO_COLUMNS.VIDEO_ID, "dummy07");
        lValue7.put(VideoProvider.VIDEO_COLUMNS.NAME, "Safety");
        lValue7.put(VideoProvider.VIDEO_COLUMNS.TYPE, VideoProvider.VIDEO_TYPE.SAFETY_VIDEO);
        lValue7.put(VideoProvider.VIDEO_COLUMNS.PATH, "/storage/emulated/0/movie_bioscope/safety.mp4");
        lValue7.put(VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME, System.currentTimeMillis());
        lValue7.put(VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED);
        getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValue7);

    }

    private void putBusDetail() {
        ContentValues lValue5 = new ContentValues();
        lValue5.put(BusProvider.COLUMNS.BUS_NUMBER, "OR07EA2352");
        getContentResolver().insert(BusProvider.CONTENT_URI_BUS_DETAIL_TABLE, lValue5);
    }


    private void putLocationData() {
        ContentValues lValue5 = new ContentValues();
        lValue5.put(LocationProvider.LOCATION_COLUMNS.SOURCE, "Bangalore");
        getContentResolver().insert(LocationProvider.CONTENT_URI_LOCATION_TABLE, lValue5);
    }

    private void putRouteData() {
        /*ContentValues lValue5 = new ContentValues();
        lValue5.put(RouteProvider.COLUMNS.NAME, "Bangalore - Mumbai");
        getContentResolver().insert(RouteProvider.CONTENT_URI_ROUTE_TABLE, lValue5);

        ContentValues lValue6 = new ContentValues();
        lValue6.put(RouteProvider.COLUMNS.NAME, "Bangalore - Mysore");
        getContentResolver().insert(RouteProvider.CONTENT_URI_ROUTE_TABLE, lValue6);

        ContentValues lValue7 = new ContentValues();
        lValue7.put(RouteProvider.COLUMNS.NAME, "Bangalore - Chennai");
        getContentResolver().insert(RouteProvider.CONTENT_URI_ROUTE_TABLE, lValue7);*/
    }

    private void putAdsConfigData() {
        ContentValues lValue5 = new ContentValues();
        lValue5.put(VideoProvider.ADS_SLOTS_CONFIG_COLUMNS.SLOT_TYPE, AdsSlotConfigUtil.SLOT_TYPE.LANDING_SLOT_TYPE);
        lValue5.put(VideoProvider.ADS_SLOTS_CONFIG_COLUMNS.SLOTS_PER_HOUR_COUNT, 30);
        lValue5.put(VideoProvider.ADS_SLOTS_CONFIG_COLUMNS.ADS_PER_SLOT_COUNT, 2);
        getContentResolver().insert(VideoProvider.CONTENT_URI_ADS_SLOTS_CONFIG, lValue5);
    }

    private void checkAdsConfigData(){
        int x=AdsSlotConfigUtil.getAdsPerSlotCount(AdsSlotConfigUtil.SLOT_TYPE.LANDING_SLOT_TYPE);
        int y=AdsSlotConfigUtil.getSlotsPerHourCount(AdsSlotConfigUtil.SLOT_TYPE.LANDING_SLOT_TYPE);
    }

    private void registerVideoCommand() {
        IntentFilter lIntentFilter = new IntentFilter();
        lIntentFilter.addAction(CustomIntent.ACTION_VIDEO_DATA_RECEIVED);
        lIntentFilter.addAction(CustomIntent.ACTION_MOVIE_LIST);
        lIntentFilter.addAction(CustomIntent.ACTION_ROUTE_CHANGED);
        lIntentFilter.addAction(CustomIntent.ACTION_MOVIE_SELECTION_CHANGED);
        lIntentFilter.addAction(CustomIntent.ACTION_ADS_SLOTS_CONFIG_RECEIVED);
        LocalBroadcastManager.getInstance(this).registerReceiver(new VideoCommandReceiver(), lIntentFilter);
    }

    private void registerVideoComplete() {
        IntentFilter lIntentFilter = new IntentFilter();
        lIntentFilter.addAction(CustomIntent.ACTION_MOVIE_COMPLETED);
        lIntentFilter.addAction(CustomIntent.ACTION_ADV_COMPLETED);
        LocalBroadcastManager.getInstance(this).registerReceiver(new VideoCompleteReceiver(), lIntentFilter);
    }

    private void registerAcknowledgementCommand() {
        IntentFilter lIntentFilter = new IntentFilter();
        lIntentFilter.addAction(CustomIntent.ACTION_ACK_DATA_RECEIVED);
        LocalBroadcastManager.getInstance(this).registerReceiver(new AcknowledgementReceiver(), lIntentFilter);
    }

    public void setFirstRun(boolean firstRun) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(FIRST_RUN_KEY, firstRun);
        editor.commit();
    }
}

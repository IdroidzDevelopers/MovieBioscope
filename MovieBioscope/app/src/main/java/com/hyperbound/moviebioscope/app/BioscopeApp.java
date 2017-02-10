package com.hyperbound.moviebioscope.app;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.hyperbound.moviebioscope.database.BusProvider;
import com.lib.cloud.databases.CloudProvider;
import com.lib.location.databases.LocationProvider;
import com.lib.videoplayer.database.VideoProvider;
import com.lib.videoplayer.receivers.VideoCommandReceiver;

import java.io.File;
import java.io.IOException;

public class BioscopeApp extends Application {

    private static final String TAG = BioscopeApp.class.getSimpleName();
    private static final String FOLDER_NAME = "movie_bioscope";
    private static final String FILE_NAME = "read_me.txt";
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        createFolderIfRequired();
        putDummyData();
        // putBusDetail();
        registerVideoCommand();
        putClouddata();
        putLocationData();
    }

    public static Context getContext() {
        return sContext;
    }


    private void createFolderIfRequired() {
        File folder = new File(Environment.getExternalStorageDirectory() + "/" + FOLDER_NAME);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/" + FOLDER_NAME + "/" + FILE_NAME);
        if (!file.exists()) {
            try {
                boolean status = file.createNewFile();
                Log.d(TAG, "createFolderIfRequired() :: status" + status);
                MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, null, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /***********************************************************************************
     * Testing code needs to be removed
     */
    private void putDummyData() {
        ContentValues lValues1 = new ContentValues();
        lValues1.put(VideoProvider.VIDEO_COLUMNS.NAME, "Spotlight");
        lValues1.put(VideoProvider.VIDEO_COLUMNS.TYPE, VideoProvider.VIDEO_TYPE.MOVIE);
        lValues1.put(VideoProvider.VIDEO_COLUMNS.PATH, "/storage/emulated/0/movie_bioscope/movie_1.mp4");
        lValues1.put(VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME, System.currentTimeMillis());
        lValues1.put(VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED);
        getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValues1);

        ContentValues lValues2 = new ContentValues();
        lValues2.put(VideoProvider.VIDEO_COLUMNS.NAME, "Raja Rani");
        lValues2.put(VideoProvider.VIDEO_COLUMNS.TYPE, VideoProvider.VIDEO_TYPE.MOVIE);
        lValues2.put(VideoProvider.VIDEO_COLUMNS.PATH, "/storage/emulated/0/movie_bioscope/movie_2.mp4");
        lValues2.put(VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME, System.currentTimeMillis());
        lValues2.put(VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED);
        getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValues2);

        ContentValues lValue3 = new ContentValues();
        lValue3.put(VideoProvider.VIDEO_COLUMNS.NAME, "Ad1");
        lValue3.put(VideoProvider.VIDEO_COLUMNS.TYPE, VideoProvider.VIDEO_TYPE.ADV);
        lValue3.put(VideoProvider.VIDEO_COLUMNS.PATH, "/storage/emulated/0/movie_bioscope/ad_1.mp4");
        lValue3.put(VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME, System.currentTimeMillis());
        lValue3.put(VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED);
        getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValue3);

        ContentValues lValue4 = new ContentValues();
        lValue4.put(VideoProvider.VIDEO_COLUMNS.NAME, "Ad2");
        lValue4.put(VideoProvider.VIDEO_COLUMNS.TYPE, VideoProvider.VIDEO_TYPE.ADV);
        lValue4.put(VideoProvider.VIDEO_COLUMNS.PATH, "/storage/emulated/0/movie_bioscope/ad_2.mp4");
        lValue4.put(VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME, System.currentTimeMillis());
        lValue4.put(VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED);
        getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValue4);

        ContentValues lValue5 = new ContentValues();
        lValue5.put(VideoProvider.VIDEO_COLUMNS.NAME, "Ad3");
        lValue5.put(VideoProvider.VIDEO_COLUMNS.TYPE, VideoProvider.VIDEO_TYPE.ADV);
        lValue5.put(VideoProvider.VIDEO_COLUMNS.PATH, "/storage/emulated/0/movie_bioscope/ad_3.mp4");
        lValue5.put(VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME, System.currentTimeMillis());
        lValue5.put(VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED);
        getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValue5);

    }

    private void putBusDetail() {
        ContentValues lValue5 = new ContentValues();
        lValue5.put(BusProvider.COLUMNS.NUMBER, "OR07EA2352");
        getContentResolver().insert(BusProvider.CONTENT_URI_BUS_DETAIL_TABLE, lValue5);
    }

    private void putClouddata() {
        ContentValues lValue5 = new ContentValues();
        lValue5.put(CloudProvider.COLUMNS.CLOUD_ID, "12345557");
        getContentResolver().insert(CloudProvider.CONTENT_URI_CLOUD_TABLE, lValue5);
    }

    private void putLocationData() {
        ContentValues lValue5 = new ContentValues();
        lValue5.put(LocationProvider.LOCATION_COLUMNS.SOURCE, "Bangalore");
        getContentResolver().insert(LocationProvider.CONTENT_URI_LOCATION_TABLE, lValue5);
    }

    private void registerVideoCommand() {
        IntentFilter lIntentFilter = new IntentFilter();
        lIntentFilter.addAction("android.intent.action.VIDEO_COMMAND_ACTION");
        LocalBroadcastManager.getInstance(this).registerReceiver(new VideoCommandReceiver(), lIntentFilter);
    }
}

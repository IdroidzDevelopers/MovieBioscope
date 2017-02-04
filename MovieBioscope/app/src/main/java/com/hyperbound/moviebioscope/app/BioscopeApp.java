package com.hyperbound.moviebioscope.app;

import android.app.Application;
import android.content.ContentValues;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import com.lib.videoplayer.database.VideoProvider;

import java.io.File;
import java.io.IOException;

public class BioscopeApp extends Application {

    private static final String TAG = BioscopeApp.class.getSimpleName();
    private static final String FOLDER_NAME = "movie_bioscope";
    private static final String FILE_NAME = "read_me.txt";


    @Override
    public void onCreate() {
        super.onCreate();
        createFolderIfRequired();
        putDummyData();
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
        getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValues1);

        ContentValues lValues2 = new ContentValues();
        lValues2.put(VideoProvider.VIDEO_COLUMNS.NAME, "Raja Rani");
        lValues2.put(VideoProvider.VIDEO_COLUMNS.TYPE, VideoProvider.VIDEO_TYPE.MOVIE);
        lValues2.put(VideoProvider.VIDEO_COLUMNS.PATH, "/storage/emulated/0/movie_bioscope/movie_2.avi");
        lValues2.put(VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME, System.currentTimeMillis());
        getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValues2);

        ContentValues lValue3 = new ContentValues();
        lValue3.put(VideoProvider.VIDEO_COLUMNS.NAME, "Ad1");
        lValue3.put(VideoProvider.VIDEO_COLUMNS.TYPE, VideoProvider.VIDEO_TYPE.ADV);
        lValue3.put(VideoProvider.VIDEO_COLUMNS.PATH, "/storage/emulated/0/movie_bioscope/ad_1.mp4");
        lValue3.put(VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME, System.currentTimeMillis());
        getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValue3);

        ContentValues lValue4 = new ContentValues();
        lValue4.put(VideoProvider.VIDEO_COLUMNS.NAME, "Ad2");
        lValue4.put(VideoProvider.VIDEO_COLUMNS.TYPE, VideoProvider.VIDEO_TYPE.ADV);
        lValue4.put(VideoProvider.VIDEO_COLUMNS.PATH, "/storage/emulated/0/movie_bioscope/ad_2.mp4");
        lValue4.put(VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME, System.currentTimeMillis());
        getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValue4);

        ContentValues lValue5 = new ContentValues();
        lValue5.put(VideoProvider.VIDEO_COLUMNS.NAME, "Ad3");
        lValue5.put(VideoProvider.VIDEO_COLUMNS.TYPE, VideoProvider.VIDEO_TYPE.ADV);
        lValue5.put(VideoProvider.VIDEO_COLUMNS.PATH, "/storage/emulated/0/movie_bioscope/ad_3.mp4");
        lValue5.put(VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME, System.currentTimeMillis());
        getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValue5);

    }
}

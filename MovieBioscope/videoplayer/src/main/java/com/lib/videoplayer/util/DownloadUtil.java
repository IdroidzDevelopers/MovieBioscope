package com.lib.videoplayer.util;


import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.lib.location.util.NetworkUtil;
import com.lib.videoplayer.database.VideoProvider;

public class DownloadUtil {
    private static final String TAG = DownloadUtil.class.getSimpleName();
    private static final boolean DEBUG = true;

    /**
     * Method to start download
     *
     * @param context
     * @param downlodUri
     * @param directory
     * @param name
     * @return
     */
    public static long beginDownload(Context context, String downlodUri, String directory, String name) {
        long downloadId = -1;
        if (NetworkUtil.isInternetAvailable(context)) {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri DownloadUri = Uri.parse(downlodUri);
            DownloadManager.Request request = new DownloadManager.Request(DownloadUri);
            request.setNotificationVisibility(1);
            request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory() + "movie_bioscope", name);
            //Enqueue a new download and same the referenceId
            downloadId = downloadManager.enqueue(request);
        }
        Log.d(TAG, "beginDownload :: downloadId " + downloadId);
        return downloadId;
    }

    /**
     * Method to check the loaded file belongs to our application or not
     *
     * @param context
     * @param downloadId
     * @return
     */
    public static boolean isValid(Context context, long downloadId) {
        String selection = VideoProvider.VIDEO_COLUMNS.DOWNLOADING_ID + " = ?";
        String[] selectionArg = new String[]{"" + downloadId};
        boolean lValue = false;
        Cursor lCursor = null;
        try {
            lCursor = context.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, selection, selectionArg, null);
            if (null != lCursor && lCursor.getCount() > 0) {
                lValue = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception isValid() ", e);
            lValue = false;
        } finally {
            if (null != lCursor && !lCursor.isClosed()) {
                lCursor.close();
            }
        }
        if (DEBUG) Log.d(TAG, "isValid() " + lValue);
        return lValue;
    }

    /**
     * Method to get the downloded file's path
     *
     * @param context
     * @param downloadId
     * @return
     */
    public static String getDownloadedFilePath(Context context, long downloadId) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query myDownloadQuery = new DownloadManager.Query();
        myDownloadQuery.setFilterById(downloadId);
        Cursor cursor = null;
        String downloadedPath = null;
        try {
            cursor = downloadManager.query(myDownloadQuery);
            if (null != cursor) {
                while (cursor.moveToNext()) {
                    downloadedPath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    Log.d(TAG, "getDownloadedFilePath downloadStatus " + downloadStatus);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception getDownloadedFilePath ", e);
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        Log.d(TAG, "getDownloadedFilePath downloadedPath " + downloadedPath);
        return downloadedPath;
    }

    /**
     * method to create uri for different type of video file
     *
     * @param videoType
     * @return
     */
    public static String getDestinationPath(String videoType) {
        //TODO:
        switch (videoType) {
            case VideoProvider.VIDEO_TYPE.TRAVELLER_VIDEO:
            case VideoProvider.VIDEO_TYPE.SAFETY_VIDEO:
            case VideoProvider.VIDEO_TYPE.MOVIE:
            case VideoProvider.VIDEO_TYPE.ADV:
            case VideoProvider.VIDEO_TYPE.BREAKING_VIDEO:
            case VideoProvider.VIDEO_TYPE.BREAKING_NEWS:
                return "/storage/emulated/0/movie_bioscope";

        }
        return null;
    }
}

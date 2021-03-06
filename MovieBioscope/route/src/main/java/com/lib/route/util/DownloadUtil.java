package com.lib.route.util;


import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.lib.route.database.RouteProvider;
import com.lib.route.objects.DownloadData;
import com.lib.utility.util.ExternalStorage;
import com.lib.utility.util.Logger;

import java.io.File;


public class DownloadUtil {
    private static final String TAG = DownloadUtil.class.getSimpleName();
    private static final boolean DEBUG = true;

    /**
     * Method to start download
     *
     * @param context
     * @param downloadUri
     * @param name
     * @return
     */
    public static long beginDownload(Context context, String downloadUri, String name) {
        long downloadId = -1;
        if (NetworkUtil.isInternetAvailable(context)) {
            File ext = ExternalStorage.getPath(context);
            File root = new File(ext + File.separator);
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri DownloadUri = Uri.parse(downloadUri);
            DownloadManager.Request request = new DownloadManager.Request(DownloadUri);
            request.setNotificationVisibility(1);
            FileUtil.createFolderIfRequired(Uri.fromFile(root) + "/movie_bioscope/images/");
            Uri path = Uri.withAppendedPath(Uri.fromFile(root), "/movie_bioscope/images/" + name);
            Log.d(TAG, "--<SD CARD :: TEST > beginDownload :: path " + path);
            request.setDestinationUri(path);
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
        String selection = RouteProvider.ROUTE_IMAGE_COLUMNS.DOWNLOAD_ID + " = ?";
        String[] selectionArg = new String[]{"" + downloadId};
        boolean lValue = false;
        Cursor lCursor = null;
        try {
            lCursor = context.getContentResolver().query(RouteProvider.CONTENT_URI_ROUTE_IMAGE_TABLE, null, selection, selectionArg, null);
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
    public static DownloadData getDownloadedFileData(Context context, long downloadId) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query myDownloadQuery = new DownloadManager.Query();
        myDownloadQuery.setFilterById(downloadId);
        Cursor cursor = null;
        DownloadData data = null;
        try {
            cursor = downloadManager.query(myDownloadQuery);
            if (null != cursor) {
                while (cursor.moveToNext()) {
                    String downloadedPath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                    int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    Logger.debug(TAG, "getDownloadedFileData downloadStatus " + downloadStatus + " downloadedPath " + downloadedPath);
                    data = new DownloadData(downloadedPath, downloadStatus);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception getDownloadedFileData ", e);
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return data;
    }

    /**
     * method to create uri for different type of video file
     *
     * @param videoType
     * @return
     */
    public static String getDestinationPath(String videoType) {
        return "/storage/emulated/0/movie_bioscope/images";

    }
}

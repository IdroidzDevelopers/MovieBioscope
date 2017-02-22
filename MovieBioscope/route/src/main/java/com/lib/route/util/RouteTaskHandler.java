package com.lib.route.util;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;

import com.lib.route.RouteApplication;
import com.lib.route.database.RouteProvider;
import com.lib.route.objects.DownloadData;
import com.lib.utility.util.CustomIntent;


public class RouteTaskHandler extends Handler {
    private static final String TAG = RouteTaskHandler.class.getSimpleName();
    private static Context sContext;

    public interface TASK {
        int UPDATE_DEFAULT_ROUTE = 3;
        int HANDLE_DOWNLOADED_VIDEO = 4;
    }

    public interface KEY {
        String ROUTE_ID = "route_id";
        String DOWNLOAD_ID = "download_id";
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
                    Intent intent = new Intent();
                    intent.putExtra(CustomIntent.EXTRAS.ROUTE_ID, lRouteId);
                    intent.setAction(CustomIntent.ACTION_ROUTE_CHANGED);
                    LocalBroadcastManager.getInstance(sContext).sendBroadcast(intent);
                }
                break;
            case TASK.HANDLE_DOWNLOADED_VIDEO:
                long downloadId = lBundle.getLong(KEY.DOWNLOAD_ID);
                if (DownloadUtil.isValid(sContext, downloadId)) {
                    DownloadData downloadData = DownloadUtil.getDownloadedFileData(sContext, downloadId);
                    String routeId = RouteUtil.getRouteFrom(RouteApplication.getRouteContext(), String.valueOf(downloadId));
                    if (null != routeId && null != downloadData && DownloadManager.STATUS_SUCCESSFUL == downloadData.getDownloadStatus()) {
                        //Successful download
                        String selection = RouteProvider.ROUTE_IMAGE_COLUMNS.DOWNLOAD_ID + " = ?";
                        String[] selectionArg = new String[]{"" + downloadId};
                        ContentValues values = new ContentValues();
                        values.put(RouteProvider.ROUTE_IMAGE_COLUMNS.PATH, downloadData.getPath());
                        values.put(RouteProvider.ROUTE_IMAGE_COLUMNS.STATUS, RouteProvider.DOWNLOAD_STATUS.DOWNLOADED);
                        RouteApplication.getRouteContext().getContentResolver().update(RouteProvider.CONTENT_URI_ROUTE_IMAGE_TABLE, values, selection, selectionArg);
                        Intent intent = new Intent(CustomIntent.ACTION_ROUTE_IMAGE_DOWNLOAD_COMPLETE);
                        intent.putExtra(CustomIntent.EXTRAS.ROUTE_ID, routeId);
                        LocalBroadcastManager.getInstance(sContext).sendBroadcast(intent);
                    } else {
                        //failed download
                        String selection = RouteProvider.ROUTE_IMAGE_COLUMNS.DOWNLOAD_ID + " = ?";
                        String[] selectionArg = new String[]{"" + downloadId};
                        ContentValues values = new ContentValues();
                        values.put(RouteProvider.ROUTE_IMAGE_COLUMNS.PATH, downloadData.getPath());
                        values.put(RouteProvider.ROUTE_IMAGE_COLUMNS.STATUS, RouteProvider.DOWNLOAD_STATUS.FAILED);
                        RouteApplication.getRouteContext().getContentResolver().update(RouteProvider.CONTENT_URI_ROUTE_IMAGE_TABLE, values, selection, selectionArg);
                    }
                }
                break;
        }
    }
}

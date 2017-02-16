package com.lib.videoplayer.receivers;


import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lib.videoplayer.util.DownloadUtil;

public class DownloadCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = DownloadCompleteReceiver.class.getSimpleName();
    private static final boolean DEBUG = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (DEBUG) Log.d(TAG, "onReceive :: " + intent);
        if (null != intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Log.d(TAG, "downloadId " + downloadId);
                if (DownloadUtil.isValid(context, downloadId)) {
                    String lFilePath = DownloadUtil.getDownloadedFilePath(context, downloadId);

                }
            }
        }

    }
}

package com.lib.videoplayer.receivers;


import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.lib.videoplayer.util.VideoTaskHandler;

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
                Message lMessage = new Message();
                lMessage.what = VideoTaskHandler.TASK.HANDLE_DOWNLOADED_VIDEO;
                Bundle lBundle = new Bundle();
                lBundle.putLong(VideoTaskHandler.KEY.DOWNLOAD_ID, downloadId);
                lMessage.setData(lBundle);
                VideoTaskHandler.getInstance(context).sendMessage(lMessage);
            }
        }

    }
}

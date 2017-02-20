package com.lib.videoplayer.util;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;

import com.lib.utility.util.CustomIntent;
import com.lib.videoplayer.database.VideoProvider;
import com.lib.videoplayer.object.Data;
import com.lib.videoplayer.object.DownloadData;


public class VideoTaskHandler extends Handler {
    private static final String TAG = VideoTaskHandler.class.getSimpleName();
    private static Context sContext;

    public interface KEY {
        String ROW_ID = "row_id";
        String DOWNLOAD_ID = "download_id";
    }

    public interface TASK {
        int INIT_VIDEO_DATA = 1;
        int HANDLE_DOWNLOADED_VIDEO = 2;
    }

    private static VideoTaskHandler sInstance;


    private VideoTaskHandler(Looper looper) {
        super(looper);
    }

    public static VideoTaskHandler getInstance(Context aContext) {
        sContext = aContext;
        if (null == sInstance) {
            synchronized (VideoTaskHandler.class) {
                if (null == sInstance) {
                    HandlerThread lThread = new HandlerThread(TAG);
                    lThread.start();
                    sInstance = new VideoTaskHandler(lThread.getLooper());
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
            case TASK.INIT_VIDEO_DATA:
                if (null != lBundle) {
                    String rowId = (String) lBundle.get(KEY.ROW_ID);
                    Data[] dataArr = VideoData.createVideoData(sContext, rowId);
                    for (Data data : dataArr) {
                        long lDownloadId = DownloadUtil.beginDownload(sContext, data.getDownloadUrl(), data.getName()+".mp4");
                        data.setDownloadingId(String.valueOf(lDownloadId));
                        data.setDownloadStatus(VideoProvider.DOWNLOAD_STATUS.DOWNLOADING);
                        VideoData.insertOrUpdateVideoData(sContext, data, VideoProvider.VIDEO_COLUMNS.VIDEO_ID, data.getAssetID());
                    }
                }
                break;
            case TASK.HANDLE_DOWNLOADED_VIDEO:
                long downloadId = lBundle.getLong(KEY.DOWNLOAD_ID);
                if (DownloadUtil.isValid(sContext, downloadId)) {
                    DownloadData downloadData = DownloadUtil.getDownloadedFileData(sContext, downloadId);
                    if (null != downloadData && DownloadManager.STATUS_SUCCESSFUL == downloadData.getDownloadStatus()) {
                        //Successful download
                        Data data = VideoData.getVideoDataFrom(sContext, String.valueOf(downloadId));
                        if (data != null) {
                            data.setDownloadingId(String.valueOf(downloadId));
                            data.setPath(downloadData.getPath());
                            data.setDownloadStatus(VideoProvider.DOWNLOAD_STATUS.DOWNLOADED);
                            data.setLastPlayedTime(String.valueOf(System.currentTimeMillis()));
                            VideoData.insertOrUpdateVideoData(sContext, data, VideoProvider.VIDEO_COLUMNS.VIDEO_ID, data.getAssetID());
                            Intent intent = new Intent(CustomIntent.ACTION_MEDIA_DOWNLOAD_COMPLETE);
                            intent.putExtra(CustomIntent.EXTRAS.VIDEO_ID, data.getAssetID());
                            intent.putExtra(CustomIntent.EXTRAS.TYPE, data.getType());
                            LocalBroadcastManager.getInstance(sContext).sendBroadcast(intent);
                        }
                    }
                } else {
                    //failed download
                    Data data = VideoData.getVideoDataFrom(sContext, String.valueOf(downloadId));
                    if (null != data) {
                        data.setDownloadStatus(VideoProvider.DOWNLOAD_STATUS.FAILED);
                        VideoData.insertOrUpdateVideoData(sContext, data, VideoProvider.VIDEO_COLUMNS.VIDEO_ID, data.getAssetID());
                    }
                }
                break;
        }
    }
}

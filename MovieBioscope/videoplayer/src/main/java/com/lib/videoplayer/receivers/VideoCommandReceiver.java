package com.lib.videoplayer.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lib.utility.util.CustomIntent;
import com.lib.utility.util.Logger;
import com.lib.videoplayer.object.Data;
import com.lib.videoplayer.util.VideoData;


public class VideoCommandReceiver extends BroadcastReceiver {
    private static final String TAG = VideoCommandReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.info(TAG, "onReceive() ");
        if (null != intent) {
            if (CustomIntent.ACTION_VIDEO_DATA_RECEIVED.equals(intent.getAction())) {
                String uri = intent.getStringExtra(CustomIntent.EXTRAS.URI_KEY);
                Logger.debug(TAG, "table uri is " + uri);
                if (null != uri) {
                    String[] content = uri.split("/");
                    if (content.length > 0) {
                        String rowId = content[content.length - 1];
                        Logger.debug(TAG, "rowId " + rowId);
                        Data[] dataArr = VideoData.createVideoData(context, rowId);
                        for (Data data : dataArr) {
                            VideoData.insertOrUpdateVideoData(context, data);
                        }
                    }
                }
            }
        }
    }
}

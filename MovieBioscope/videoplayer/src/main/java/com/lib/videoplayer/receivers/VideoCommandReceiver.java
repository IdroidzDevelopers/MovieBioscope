package com.lib.videoplayer.receivers;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import com.lib.utility.util.CustomIntent;
import com.lib.utility.util.Logger;
import com.lib.videoplayer.ui.MovieDialog;
import com.lib.videoplayer.ui.VideoActivity;
import com.lib.videoplayer.util.VideoData;
import com.lib.videoplayer.util.VideoTaskHandler;


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
                        Message lMessage = new Message();
                        lMessage.what = VideoTaskHandler.TASK.HANDLE_VIDEO_DATA;
                        Bundle lBundle = new Bundle();
                        lBundle.putString(VideoTaskHandler.KEY.ROW_ID, rowId);
                        lMessage.setData(lBundle);
                        VideoTaskHandler.getInstance(context).sendMessage(lMessage);
                    }
                }
            } else if (CustomIntent.ACTION_MOVIE_LIST.equals(intent.getAction())) {
                context.startActivity(new Intent(context, MovieListDialog.class));
                //context.sendBroadcast(new Intent().setAction("android.navajhalka.movielist"));
            }
        }
    }
}

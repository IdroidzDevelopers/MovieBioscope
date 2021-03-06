package com.lib.videoplayer.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import com.lib.utility.util.CustomIntent;
import com.lib.utility.util.Logger;
import com.lib.videoplayer.ui.MovieListActivity;
import com.lib.videoplayer.util.AdsSlotConfigUtil;
import com.lib.videoplayer.util.SequenceUtil;
import com.lib.videoplayer.util.StateMachine;
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
                context.startActivity(new Intent(context, MovieListActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                //context.sendBroadcast(new Intent().setAction("android.navajhalka.movielist"));
            } else if (CustomIntent.ACTION_ROUTE_CHANGED.equals(intent.getAction())) {
                VideoData.resetIntroTravelSafety();
                SequenceUtil.resetSelection(StateMachine.SEQUENCE_TYPE.MOVIE_INIT_TYPE);
            } else if (CustomIntent.ACTION_MOVIE_SELECTION_CHANGED.equals(intent.getAction())) {
                StateMachine.deletePersistState(StateMachine.VIDEO_STATE.MOVIE_AND_ADV);
            } else if (CustomIntent.ACTION_ADS_SLOTS_CONFIG_RECEIVED.equals(intent.getAction())) {
                AdsSlotConfigUtil.deleteAdsSlotsConfigData();
                String uri = intent.getStringExtra(CustomIntent.EXTRAS.URI_KEY);
                Logger.debug(TAG, "table uri is " + uri);
                if (null != uri) {
                    String[] content = uri.split("/");
                    if (content.length > 0) {
                        String rowId = content[content.length - 1];
                        Logger.debug(TAG, "rowId " + rowId);
                        Message lMessage = new Message();
                        lMessage.what = VideoTaskHandler.TASK.HANDLE_ADS_SLOT_CONFIG;
                        Bundle lBundle = new Bundle();
                        lBundle.putString(VideoTaskHandler.KEY.ROW_ID, rowId);
                        lMessage.setData(lBundle);
                        VideoTaskHandler.getInstance(context).sendMessage(lMessage);
                    }
                }
            }
        }
    }
}

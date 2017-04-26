package com.lib.videoplayer.util;


import android.database.Cursor;
import android.util.Log;

import com.lib.videoplayer.VideoApplication;
import com.lib.videoplayer.database.VideoProvider;
import com.lib.videoplayer.object.SequenceData;

public class SequenceUtil {
    private static final String TAG = SequenceUtil.class.getSimpleName();
    private static final int SELECTED = 1;
    private static final int NOT_SELECTED = 0;


    public static SequenceData getCurrentSequence(String sequenceType) {
        SequenceData data = null;
        String selection = VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_TYPE + "= ? AND " + VideoProvider.SEQUENCE_COLUMNS.SELECTED + " = ?";
        String[] selectionArg = {sequenceType, "" + SELECTED};
        Cursor cursor = null;
        try {
            cursor = VideoApplication.getVideoContext().getContentResolver().query(VideoProvider.CONTENT_URI_SEQUENCE_TABLE, null, selection, selectionArg, null);
            while (null != cursor && cursor.moveToNext()) {
                data = new SequenceData();
                data.setSequenceType(cursor.getString(cursor.getColumnIndex(VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_TYPE)));
                data.setVideoType(cursor.getString(cursor.getColumnIndex(VideoProvider.SEQUENCE_COLUMNS.VIDEO_TYPE)));
                data.setSequenceOrder(cursor.getString(cursor.getColumnIndex(VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_ORDER)));
                data.setSelection(cursor.getInt(cursor.getColumnIndex(VideoProvider.SEQUENCE_COLUMNS.SELECTED)));
                break;
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception :: getCurrentSequence() :: ", e);
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return data;
    }


}

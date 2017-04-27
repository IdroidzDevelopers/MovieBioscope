package com.lib.videoplayer.util;


import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.lib.videoplayer.VideoApplication;
import com.lib.videoplayer.database.VideoProvider;
import com.lib.videoplayer.object.SequenceData;

public class SequenceUtil {
    private static final String TAG = SequenceUtil.class.getSimpleName();
    private static final int SELECTED = 1;
    private static final int NOT_SELECTED = 0;


    public static SequenceData getCurrentSequence(int sequenceType) {
        SequenceData data = null;
        String selection = VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_TYPE + "= ? AND " + VideoProvider.SEQUENCE_COLUMNS.SELECTED + " = ?";
        String[] selectionArg = {"" + sequenceType, "" + SELECTED};
        Cursor cursor = null;
        try {
            cursor = VideoApplication.getVideoContext().getContentResolver().query(VideoProvider.CONTENT_URI_SEQUENCE_TABLE, null, selection, selectionArg, null);
            while (null != cursor && cursor.moveToNext()) {
                data = new SequenceData();
                data.setSequenceType(cursor.getString(cursor.getColumnIndex(VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_TYPE)));
                data.setVideoType(cursor.getString(cursor.getColumnIndex(VideoProvider.SEQUENCE_COLUMNS.VIDEO_TYPE)));
                data.setSequenceOrder(cursor.getInt(cursor.getColumnIndex(VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_ORDER)));
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

    public static SequenceData getNextInSequence(int sequenceType, int sequenceOrder) {
        SequenceData data = null;
        String selection = VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_TYPE + "= ? AND " + VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_ORDER + " = ?";
        String[] selectionArg = {"" + sequenceType, "" + ++sequenceOrder};//get the next element
        Cursor cursor = null;
        try {
            cursor = VideoApplication.getVideoContext().getContentResolver().query(VideoProvider.CONTENT_URI_SEQUENCE_TABLE, null, selection, selectionArg, null);
            while (null != cursor && cursor.moveToNext()) {
                data = new SequenceData();
                data.setSequenceType(cursor.getString(cursor.getColumnIndex(VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_TYPE)));
                data.setVideoType(cursor.getString(cursor.getColumnIndex(VideoProvider.SEQUENCE_COLUMNS.VIDEO_TYPE)));
                data.setSequenceOrder(cursor.getInt(cursor.getColumnIndex(VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_ORDER)));
                data.setSelection(cursor.getInt(cursor.getColumnIndex(VideoProvider.SEQUENCE_COLUMNS.SELECTED)));
                break;
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception :: getNextInSequence() :: ", e);
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return data;
    }

    public static long updateSelection(String sequenceType, int sequenceOrder) {
        resetSelection(sequenceType);
        String selection = VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_TYPE + "= ? AND "+VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_ORDER+ " = ?";
        String[] selectionArg = {"" + sequenceType, "" + sequenceOrder};
        ContentValues content = new ContentValues();
        content.put(VideoProvider.SEQUENCE_COLUMNS.SELECTED, SELECTED);
        int count = VideoApplication.getVideoContext().getContentResolver().update(VideoProvider.CONTENT_URI_VIDEO_TABLE, content, selection, selectionArg);
        Log.d(TAG, "updateSelection() :: rows count " + count);
        return count;
    }

    public static int resetSelection(String sequenceType) {
        String selection = VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_TYPE + "= ?";
        String[] selectionArg = {sequenceType};
        ContentValues content = new ContentValues();
        content.put(VideoProvider.SEQUENCE_COLUMNS.SELECTED, NOT_SELECTED);
        int count = VideoApplication.getVideoContext().getContentResolver().update(VideoProvider.CONTENT_URI_VIDEO_TABLE, content, selection, selectionArg);
        Log.d(TAG, "resetSelection() :: rows count " + count);
        return count;
    }


    public static boolean isLast(String sequenceType, int sequenceOrder) {
        String selection = VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_TYPE + "= ?";
        String[] selectionArg = {sequenceType};
        String orderBy = VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_ORDER + " DESC";
        Cursor cursor = null;
        int max = -1;
        try {
            cursor = VideoApplication.getVideoContext().getContentResolver().query(VideoProvider.CONTENT_URI_SEQUENCE_TABLE, null, selection, selectionArg, orderBy);
            while (null != cursor && cursor.moveToNext()) {
                max = cursor.getInt(cursor.getColumnIndex(VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_ORDER));
                break;
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception :: isLast() :: ", e);
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        if (sequenceOrder >= max) {
            return true;
        } else {
            return false;
        }
    }


    public static SequenceData getDefaultSequence(int sequenceType) {
        SequenceData data = null;
        String selection = VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_TYPE + "= ? ";
        String[] selectionArg = {"" + sequenceType};
        String orderBy = VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_ORDER + " ASC";
        Cursor cursor = null;
        try {
            cursor = VideoApplication.getVideoContext().getContentResolver().query(VideoProvider.CONTENT_URI_SEQUENCE_TABLE, null, selection, selectionArg, orderBy);
            while (null != cursor && cursor.moveToNext()) {
                data = new SequenceData();
                data.setSequenceType(cursor.getString(cursor.getColumnIndex(VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_TYPE)));
                data.setVideoType(cursor.getString(cursor.getColumnIndex(VideoProvider.SEQUENCE_COLUMNS.VIDEO_TYPE)));
                data.setSequenceOrder(cursor.getInt(cursor.getColumnIndex(VideoProvider.SEQUENCE_COLUMNS.SEQUENCE_ORDER)));
                data.setSelection(cursor.getInt(cursor.getColumnIndex(VideoProvider.SEQUENCE_COLUMNS.SELECTED)));
                break;
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception :: getDefaultSequence() :: ", e);
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return data;
    }


}

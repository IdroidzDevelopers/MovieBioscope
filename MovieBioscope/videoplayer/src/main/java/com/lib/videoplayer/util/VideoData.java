package com.lib.videoplayer.util;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.lib.videoplayer.database.VideoProvider;
import com.lib.videoplayer.object.Data;

public class VideoData {
    private static final String TAG = VideoData.class.getSimpleName();

    public static Data getRandomMovieUri(Context aContext) {
        Data lData = new Data();
        if (null != aContext) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ?";
            String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.MOVIE, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED};
            String orderBy = VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME + " ASC";
            Cursor lCursor = null;
            try {
                lCursor = aContext.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, orderBy);
                while (null != lCursor && lCursor.moveToNext()) {
                    int lId = lCursor.getInt(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.ID));
                    lData.setId(lId);
                    String lValue = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PATH));
                    lData.setPath(lValue);
                    int lCount = lCursor.getInt(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PLAY_COUNT));
                    lData.setCount(lCount);
                    break;
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception :: getRandomMovieUri() :: ", e);
            } finally {
                if (null != lCursor && !lCursor.isClosed()) {
                    lCursor.close();
                }
            }
        }
        return lData;
    }

    public static Data getRandomAdUri(Context aContext) {
        Data lData = new Data();
        if (null != aContext) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ?";
            String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.ADV, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED};
            String orderBy = VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME + " ASC";
            Cursor lCursor = null;
            try {
                lCursor = aContext.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, orderBy);
                while (null != lCursor && lCursor.moveToNext()) {
                    int lId = lCursor.getInt(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.ID));
                    lData.setId(lId);
                    String lValue = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PATH));
                    lData.setPath(lValue);
                    int lCount = lCursor.getInt(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PLAY_COUNT));
                    lData.setCount(lCount);
                    break;
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception :: getRandomMovieUri() :: ", e);
            } finally {
                if (null != lCursor && !lCursor.isClosed()) {
                    lCursor.close();
                }
            }
        }
        return lData;
    }


    public static Data getTravellerUri(Context aContext) {
        Data lData = new Data();
        if (null != aContext) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ?";
            String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.TRAVELLER_VIDEO, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED};
            String orderBy = VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME + " ASC";
            Cursor lCursor = null;
            try {
                lCursor = aContext.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, orderBy);
                while (null != lCursor && lCursor.moveToNext()) {
                    int lId = lCursor.getInt(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.ID));
                    lData.setId(lId);
                    String lValue = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PATH));
                    lData.setPath(lValue);
                    int lCount = lCursor.getInt(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PLAY_COUNT));
                    lData.setCount(lCount);
                    break;
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception :: getTravellerUri() :: ", e);
            } finally {
                if (null != lCursor && !lCursor.isClosed()) {
                    lCursor.close();
                }
            }
        }
        return lData;
    }


    public static Data getSafetyUri(Context aContext) {
        Data lData = new Data();
        if (null != aContext) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ?";
            String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.SAFETY_VIDEO, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED};
            String orderBy = VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME + " ASC";
            Cursor lCursor = null;
            try {
                lCursor = aContext.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, orderBy);
                while (null != lCursor && lCursor.moveToNext()) {
                    int lId = lCursor.getInt(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.ID));
                    lData.setId(lId);
                    String lValue = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PATH));
                    lData.setPath(lValue);
                    int lCount = lCursor.getInt(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PLAY_COUNT));
                    lData.setCount(lCount);
                    break;
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception :: getSafetyUri() :: ", e);
            } finally {
                if (null != lCursor && !lCursor.isClosed()) {
                    lCursor.close();
                }
            }
        }
        return lData;
    }

    public static long getNextAdTime() {
        return 60 * 1000;//1 min :: static
    }

    public static int updateVideoData(Context aContext, Data lData) {
        String lSelection = VideoProvider.VIDEO_COLUMNS.ID + "= ?";
        String[] lSelectionArg = {"" + lData.getId()};
        ContentValues lValues = new ContentValues();
        lValues.put(VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME, System.currentTimeMillis());
        lValues.put(VideoProvider.VIDEO_COLUMNS.PLAY_COUNT, lData.getCount() + 1);//incremented the play count
        int count = aContext.getContentResolver().update(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValues, lSelection, lSelectionArg);
        Log.d(TAG, "updateVideoData() :: update count " + count);
        return count;
    }
}

package com.lib.videoplayer.util;


import android.app.AlarmManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.lib.videoplayer.database.VideoProvider;
import com.lib.videoplayer.object.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.lib.videoplayer.BuildConfig.DEBUG;

public class VideoData {
    private static final String TAG = VideoData.class.getSimpleName();
    private static final int SLOT_PER_HOUR = 3;
    /*1*60*60*1000*/
    private static final long INTERVAL_FIVE_MINUTES = 5 * 60 * 1000;
    private static final long AVG_TIME_IN_MILLIS = AlarmManager.INTERVAL_HOUR / SLOT_PER_HOUR;
    private static final long MIN_TIME_IN_MILLIS = AVG_TIME_IN_MILLIS - INTERVAL_FIVE_MINUTES;
    private static final long MAX_TIME_IN_MILLIS = AVG_TIME_IN_MILLIS + INTERVAL_FIVE_MINUTES;

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
                    int lId = lCursor.getInt(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.VIDEO_ID));
                    lData.setVideoId(lId);
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
                    int lId = lCursor.getInt(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.VIDEO_ID));
                    lData.setVideoId(lId);
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
                    int lId = lCursor.getInt(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.VIDEO_ID));
                    lData.setVideoId(lId);
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
                    int lId = lCursor.getInt(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.VIDEO_ID));
                    lData.setVideoId(lId);
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
        Random lRandom = new Random();
        long lRandomValue = MIN_TIME_IN_MILLIS +
                (long) (lRandom.nextDouble() * (MAX_TIME_IN_MILLIS - MIN_TIME_IN_MILLIS));
        Log.d(TAG, "--Test-- MIN_TIME_IN_MILLIS " + MIN_TIME_IN_MILLIS + " lRandomValue " + lRandomValue + " MAX_TIME_IN_MILLIS " + MAX_TIME_IN_MILLIS);
        return lRandomValue;//1 min :: static
    }

    public static int updateVideoData(Context aContext, Data lData) {
        String lSelection = VideoProvider.VIDEO_COLUMNS.VIDEO_ID + "= ?";
        String[] lSelectionArg = {"" + lData.getVideoId()};
        ContentValues lValues = new ContentValues();
        lValues.put(VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME, System.currentTimeMillis());
        lValues.put(VideoProvider.VIDEO_COLUMNS.PLAY_COUNT, lData.getCount() + 1);//incremented the play count
        int count = aContext.getContentResolver().update(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValues, lSelection, lSelectionArg);
        Log.d(TAG, "updateVideoData() :: update count " + count);
        return count;
    }

    public static Data getBreakingVideoUri(Context context) {
        Data lData = new Data();
        if (null != context) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ?";
            String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.BREAKING_VIDEO, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED};
            String orderBy = VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME + " ASC";
            Cursor lCursor = null;
            try {
                lCursor = context.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, orderBy);
                while (null != lCursor && lCursor.moveToNext()) {
                    int lId = lCursor.getInt(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.VIDEO_ID));
                    lData.setVideoId(lId);
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

    /**
     * Method to get breaking news data
     *
     * @param context
     * @return
     */
    public static Data getBreakingNews(Context context) {
        Data lData = new Data();
        if (null != context) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ?";
            String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.BREAKING_NEWS, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED};
            String orderBy = VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME + " ASC";
            Cursor lCursor = null;
            try {
                lCursor = context.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, orderBy);
                while (null != lCursor && lCursor.moveToNext()) {
                    int lId = lCursor.getInt(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.VIDEO_ID));
                    lData.setVideoId(lId);
                    String lValue = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PATH));
                    lData.setPath(lValue);
                    String lMessage = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.MESSAGE));
                    lData.setMessage(lMessage);
                    int lCount = lCursor.getInt(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PLAY_COUNT));
                    lData.setCount(lCount);
                    break;
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception :: getBreakingNews() :: ", e);
            } finally {
                if (null != lCursor && !lCursor.isClosed()) {
                    lCursor.close();
                }
            }
        }
        return lData;
    }

    public static List<Data> createVideoData(Context context) {
        List<Data> list = new ArrayList<Data>();
        return list;
    }

    public static void insertOrUpdateVideoData(Data data) {

    }
}

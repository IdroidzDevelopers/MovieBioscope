package com.lib.videoplayer.util;


import android.app.AlarmManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lib.firebase.object.FirebaseData;
import com.lib.firebase.util.FirebaseUtil;
import com.lib.utility.util.Logger;
import com.lib.videoplayer.database.VideoProvider;
import com.lib.videoplayer.object.Data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class VideoData {
    private static final String TAG = VideoData.class.getSimpleName();
    private static final int SLOT_PER_HOUR = 3;
    /*1*60*60*1000*/
    private static final long INTERVAL_FIVE_MINUTES = 5 * 60 * 1000;
    private static final long AVG_TIME_IN_MILLIS = AlarmManager.INTERVAL_HOUR / SLOT_PER_HOUR;
    private static final long MIN_TIME_IN_MILLIS = AVG_TIME_IN_MILLIS - INTERVAL_FIVE_MINUTES;
    private static final long MAX_TIME_IN_MILLIS = AVG_TIME_IN_MILLIS + INTERVAL_FIVE_MINUTES;

    public static Data getRandomMovie(Context aContext) {
        Data lData = null;
        if (null != aContext) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ?";
            String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.MOVIE, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED};
            String orderBy = VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME + " ASC";
            Cursor lCursor = null;
            try {
                lCursor = aContext.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, orderBy);
                while (null != lCursor && lCursor.moveToNext()) {
                    lData = new Data();
                    String lId = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.VIDEO_ID));
                    lData.setAssetID(lId);
                    String lValue = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PATH));
                    lData.setPath(lValue);
                    int lCount = lCursor.getInt(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PLAY_COUNT));
                    lData.setCount(lCount);
                    break;
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception :: getRandomMovie() :: ", e);
            } finally {
                if (null != lCursor && !lCursor.isClosed()) {
                    lCursor.close();
                }
            }
        }
        return lData;
    }

    public static Data getRandomAd(Context aContext) {
        Data lData = null;
        if (null != aContext) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ?";
            String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.ADV, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED};
            String orderBy = VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME + " ASC";
            Cursor lCursor = null;
            try {
                lCursor = aContext.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, orderBy);
                while (null != lCursor && lCursor.moveToNext()) {
                    lData = new Data();
                    String lId = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.VIDEO_ID));
                    lData.setAssetID(lId);
                    String lValue = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PATH));
                    lData.setPath(lValue);
                    int lCount = lCursor.getInt(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PLAY_COUNT));
                    lData.setCount(lCount);
                    break;
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception :: getRandomMovie() :: ", e);
            } finally {
                if (null != lCursor && !lCursor.isClosed()) {
                    lCursor.close();
                }
            }
        }
        return lData;
    }

    public static boolean isAdvExist(Context aContext) {
        boolean flag = false;
        if (null != aContext) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ?";
            String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.ADV, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED};
            String orderBy = VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME + " ASC";
            Cursor lCursor = null;
            try {
                lCursor = aContext.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, orderBy);
                while (null != lCursor && lCursor.moveToNext()) {
                    flag = true;
                    break;
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception :: isAdvExist() :: ", e);
            } finally {
                if (null != lCursor && !lCursor.isClosed()) {
                    lCursor.close();
                }
            }
        }
        return flag;
    }


    public static Data getTravellerVideo(Context aContext) {
        Data lData = null;
        if (null != aContext) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ?";
            String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.TRAVELLER_VIDEO, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED};
            String orderBy = VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME + " ASC";
            Cursor lCursor = null;
            try {
                lCursor = aContext.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, orderBy);
                while (null != lCursor && lCursor.moveToNext()) {
                    lData = new Data();
                    String lId = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.VIDEO_ID));
                    lData.setAssetID(lId);
                    String lValue = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PATH));
                    lData.setPath(lValue);
                    int lCount = lCursor.getInt(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PLAY_COUNT));
                    lData.setCount(lCount);
                    break;
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception :: getTravellerVideo() :: ", e);
            } finally {
                if (null != lCursor && !lCursor.isClosed()) {
                    lCursor.close();
                }
            }
        }
        return lData;
    }


    public static Data getSafetyVideo(Context aContext) {
        Data lData = null;
        if (null != aContext) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ?";
            String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.SAFETY_VIDEO, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED};
            String orderBy = VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME + " ASC";
            Cursor lCursor = null;
            try {
                lCursor = aContext.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, orderBy);
                while (null != lCursor && lCursor.moveToNext()) {
                    lData = new Data();
                    String lId = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.VIDEO_ID));
                    lData.setAssetID(lId);
                    String lValue = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PATH));
                    lData.setPath(lValue);
                    int lCount = lCursor.getInt(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PLAY_COUNT));
                    lData.setCount(lCount);
                    break;
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception :: getSafetyVideo() :: ", e);
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
        return lRandomValue;
    }

    public static int updateVideoData(Context aContext, Data lData) {
        String lSelection = VideoProvider.VIDEO_COLUMNS.VIDEO_ID + "= ?";
        String[] lSelectionArg = {"" + lData.getAssetID()};
        ContentValues lValues = new ContentValues();
        lValues.put(VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME, System.currentTimeMillis());
        lValues.put(VideoProvider.VIDEO_COLUMNS.PLAY_COUNT, lData.getCount() + 1);//incremented the play count
        int count = aContext.getContentResolver().update(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValues, lSelection, lSelectionArg);
        Log.d(TAG, "updateVideoData() :: update count " + count);
        return count;
    }

    public static Data getBreakingVideo(Context context) {
        Data lData = new Data();
        if (null != context) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ?";
            String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.BREAKING_VIDEO, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED};
            String orderBy = VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME + " ASC";
            Cursor lCursor = null;
            try {
                lCursor = context.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, orderBy);
                while (null != lCursor && lCursor.moveToNext()) {
                    String lId = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.VIDEO_ID));
                    lData.setAssetID(lId);
                    String lValue = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PATH));
                    lData.setPath(lValue);
                    int lCount = lCursor.getInt(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PLAY_COUNT));
                    lData.setCount(lCount);
                    break;
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception :: getRandomMovie() :: ", e);
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
                    String lId = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.VIDEO_ID));
                    lData.setAssetID(lId);
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


    /**
     * Method to get breaking news data
     *
     * @param context
     * @return
     */
    public static Data getVideoDataFrom(Context context, String downloadId) {
        Data data = null;
        if (null != context) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.DOWNLOADING_ID + "= ? ";
            String[] lSelectionArg = {"" + downloadId};
            Cursor lCursor = null;
            try {
                lCursor = context.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, null);
                while (null != lCursor && lCursor.moveToNext()) {
                    data = new Data();
                    String videoId = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.VIDEO_ID));
                    data.setAssetID(videoId);
                    String type = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.TYPE));
                    data.setType(type);
                    break;
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception :: getVideoIdFrom() :: ", e);
            } finally {
                if (null != lCursor && !lCursor.isClosed()) {
                    lCursor.close();
                }
            }
        }
        Logger.debug(TAG, "getVideoIdFrom " + data);
        return data;
    }

    public static Data[] createVideoData(Context context, String rowId) {
        FirebaseData firebase = FirebaseUtil.getFireBaseData(context, rowId);
        Logger.debug(TAG, "data is " + firebase.getData());
        JSONObject jsonObject = null;
        Data[] dataArray = null;
        try {
            jsonObject = new JSONObject(firebase.getData());
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            dataArray = gson.fromJson(jsonObject.getString("assets"), Data[].class);
        } catch (JSONException e) {
            Logger.error(TAG, "Exception createVideoData() ", e);
        }
        return dataArray;
    }

    public static void insertOrUpdateVideoData(Context context, Data data, String selectionColumn, String selectionArgValue) {
        String selection = selectionColumn + " = ?";
        String[] selectionArg = new String[]{"" + selectionArgValue};
        ContentValues value = new ContentValues();
        if (null != data.getAssetID()) {
            value.put(VideoProvider.VIDEO_COLUMNS.VIDEO_ID, data.getAssetID());
        }
        if (null != data.getName()) {
            value.put(VideoProvider.VIDEO_COLUMNS.NAME, data.getName());
        }
        if (null != data.getDownloadUrl()) {
            value.put(VideoProvider.VIDEO_COLUMNS.DOWNLOAD_URL, data.getDownloadUrl());
        }
        if (null != data.getType()) {
            value.put(VideoProvider.VIDEO_COLUMNS.TYPE, data.getType());
        }
        if (null != data.getLanguage()) {
            value.put(VideoProvider.VIDEO_COLUMNS.LANGUAGE, data.getLanguage());
        }
        if (null != data.getMessage()) {
            value.put(VideoProvider.VIDEO_COLUMNS.MESSAGE, data.getMessage());
        }
        if (null != data.getDownloadingId()) {
            value.put(VideoProvider.VIDEO_COLUMNS.DOWNLOADING_ID, data.getDownloadingId());
        }
        if (null != data.getDownloadStatus()) {
            value.put(VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS, data.getDownloadStatus());
        }
        if (null != data.getPath()) {
            value.put(VideoProvider.VIDEO_COLUMNS.PATH, data.getPath());
        }
        if (null != data.getLastPlayedTime()) {
            value.put(VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME, data.getLastPlayedTime());
        }
        if (0 != data.getCount()) {
            value.put(VideoProvider.VIDEO_COLUMNS.PLAY_COUNT, data.getCount());
        }
        if (null != data.getCloudId()) {
            value.put(VideoProvider.VIDEO_COLUMNS.CLOUD_ID, data.getCloudId());
        }
        int count = context.getContentResolver().update(VideoProvider.CONTENT_URI_VIDEO_TABLE, value, selection, selectionArg);
        Logger.debug(TAG, "updated count is " + count);
        if (count == 0) {
            context.getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, value);
        }
    }
}

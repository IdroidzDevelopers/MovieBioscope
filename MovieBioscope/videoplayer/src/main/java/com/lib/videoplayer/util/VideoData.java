package com.lib.videoplayer.util;


import android.app.AlarmManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lib.firebase.object.FirebaseData;
import com.lib.firebase.util.FirebaseUtil;
import com.lib.utility.util.CustomIntent;
import com.lib.utility.util.Logger;
import com.lib.videoplayer.VideoApplication;
import com.lib.videoplayer.database.VideoProvider;
import com.lib.videoplayer.object.Data;
import com.lib.videoplayer.object.Movie;
import com.lib.videoplayer.object.MoviesList;
import com.lib.videoplayer.object.PushData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VideoData {
    private static final String TAG = VideoData.class.getSimpleName();
    private static final int SLOT_PER_HOUR = 3;
    /*1*60*60*1000*/
    private static final long INTERVAL_FIVE_MINUTES = 5 * 60 * 1000;
    private static final long AVG_TIME_IN_MILLIS = AlarmManager.INTERVAL_HOUR / SLOT_PER_HOUR;
    private static final long MIN_TIME_IN_MILLIS = AVG_TIME_IN_MILLIS - INTERVAL_FIVE_MINUTES;
    private static final long MAX_TIME_IN_MILLIS = AVG_TIME_IN_MILLIS + INTERVAL_FIVE_MINUTES;


    public static String getDefaultMovie() {
        String id = null;
        String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.SELECTED_STATE + "= ?";
        String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.MOVIE, "" + 1};
        Cursor lCursor = null;
        try {
            lCursor = VideoApplication.getVideoContext().getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, null);
            while (null != lCursor && lCursor.moveToNext()) {
                id = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.VIDEO_ID));
                break;
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception :: getDefaultMovie() :: ", e);
        } finally {
            if (null != lCursor && !lCursor.isClosed()) {
                lCursor.close();
            }
        }
        Logger.debug(TAG, "getDefaultMovie() :: movie id " + id);
        return id;
    }


    public static Data getMovieData(Context aContext) {
        Data lData = null;
        String lSelection;
        String[] lSelectionArg;
        String orderBy = null;
        String defaultSelectedMovieId = null;
        if (null != aContext) {
            defaultSelectedMovieId = getDefaultMovie();
            if (null == defaultSelectedMovieId) {
                Logger.info(TAG, "get movie based on random logic");
                lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ?";
                lSelectionArg = new String[]{"" + VideoProvider.VIDEO_TYPE.MOVIE, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED};
                orderBy = VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME + " ASC";
            } else {
                Logger.info(TAG, "get user selected movie with asset id " + defaultSelectedMovieId);
                lSelection = VideoProvider.VIDEO_COLUMNS.VIDEO_ID + " = ?";
                lSelectionArg = new String[]{"" + defaultSelectedMovieId};
            }
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
                Log.d(TAG, "Exception :: getMovieData() :: ", e);
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
                Log.d(TAG, "Exception :: getMovieData() :: ", e);
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

    public static synchronized boolean isAssetExist(Context aContext, String assetId) {
        boolean flag = false;
        if (null != aContext) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.VIDEO_ID + "= ? ";
            String[] lSelectionArg = {"" + assetId};
            Cursor lCursor = null;
            try {
                lCursor = aContext.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, null);
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
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ? AND " + VideoProvider.VIDEO_COLUMNS.PLAY_COUNT + " = ?";
            String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.COMPANY_VIDEO, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED, "" + 0};
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
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ? AND " + VideoProvider.VIDEO_COLUMNS.PLAY_COUNT + " = ?";
            String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.SAFETY_VIDEO, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED, "" + 0};
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
                Log.d(TAG, "Exception :: getMovieData() :: ", e);
            } finally {
                if (null != lCursor && !lCursor.isClosed()) {
                    lCursor.close();
                }
            }
        }
        return lData;
    }

    /**
     * Method to get companyAd
     *
     * @param context
     * @return
     */
    public static Data getCompanyAd(Context context) {
        Data lData = new Data();
        if (null != context) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ?";
            String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.COMPANY_AD, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED};
            Cursor lCursor = null;
            try {
                lCursor = context.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, null);
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
                Log.d(TAG, "Exception :: getCompanyAd() :: ", e);
            } finally {
                if (null != lCursor && !lCursor.isClosed()) {
                    lCursor.close();
                }
            }
        }
        Logger.debug(TAG, "getCompanyAd() :: data " + lData);
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
     * background search for pending breaking news or breaking video or company ad
     *
     * @param context
     * @return
     */
    public static synchronized void backgroundSearchForBreaking(Context context) {
        if (null != context) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + " IN (?,?,?) AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ? AND " + VideoProvider.VIDEO_COLUMNS.PLAY_COUNT + "= ?";
            String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.BREAKING_NEWS, VideoProvider.VIDEO_TYPE.BREAKING_VIDEO, VideoProvider.VIDEO_TYPE.COMPANY_AD, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED, "" + 0};//0 means never played
            String orderBy = VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME + " ASC";
            Cursor lCursor = null;
            try {
                lCursor = context.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, orderBy);
                while (null != lCursor && lCursor.moveToNext()) {
                    String type = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.TYPE));
                    String id = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.VIDEO_ID));
                    Logger.debug(TAG, "backgroundSearchForBreaking() :: Dude some pending breaking new or video !!! finally time to show");
                    //hacking
                    Intent intent = new Intent(CustomIntent.ACTION_MEDIA_DOWNLOAD_COMPLETE);
                    intent.putExtra(CustomIntent.EXTRAS.TYPE, type);
                    intent.putExtra(CustomIntent.EXTRAS.VIDEO_ID, id);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    break;
                }
                Logger.debug(TAG, "backgroundSearchForBreaking() :: No more pending chill!!!");
            } catch (Exception e) {
                Log.d(TAG, "Exception :: backgroundSearchForBreaking() :: ", e);
            } finally {
                if (null != lCursor && !lCursor.isClosed()) {
                    lCursor.close();
                }
            }
        }
    }


    /**
     * background search for pending breaking news and video
     *
     * @param context
     * @return
     */
    public static synchronized boolean isBreakingNewsStillValid(Context context, String videoId) {
        if (null != context) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.VIDEO_ID + " = ?";
            String[] lSelectionArg = {"" + videoId};
            Cursor lCursor = null;
            try {
                lCursor = context.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, null);
                while (null != lCursor && lCursor.moveToNext()) {
                    String receivedTime = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.RECEIVED_TIME));
                    String cloudTime = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.CLOUD_TIME));
                    if (null != receivedTime && null != cloudTime) {
                        if (!TimeUtil.isMoreThanIntervalTime(Long.valueOf(cloudTime), Long.valueOf(receivedTime), AlarmManager.INTERVAL_HOUR)) {
                            Logger.debug(TAG, "isBreakingNewsStillValid() :: yes still valid go ahead ");
                            return true;
                        } else {
                            Logger.debug(TAG, "isBreakingNewsStillValid() :: u missed breaking news ");
                            return false;
                        }
                    }
                    break;
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception :: backgroundSearchForBreaking() :: ", e);
            } finally {
                if (null != lCursor && !lCursor.isClosed()) {
                    lCursor.close();
                }
            }
        }
        return false;
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

    public static PushData createVideoData(Context context, String rowId) {
        FirebaseData firebase = FirebaseUtil.getFireBaseData(context, rowId);
        PushData pushData = null;
        if (null != firebase) {
            Logger.debug(TAG, "data is " + firebase.getData());
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            pushData = gson.fromJson(firebase.getData(), PushData.class);
            pushData.setCloudTime(firebase.getSentTime());
            pushData.setReceivedTime(firebase.getReceivedTime());
        }
        return pushData;
    }

    public static String getAction(Context context, String rowId) {
        FirebaseData firebase = FirebaseUtil.getFireBaseData(context, rowId);
        JSONObject jsonObject = null;
        String action = null;
        if (null != firebase) {
            Logger.debug(TAG, "data is " + firebase.getData());
            try {
                jsonObject = new JSONObject(firebase.getData());
                action = jsonObject.getString(VideoTaskHandler.CLOUD_JSON.ACTION);
            } catch (JSONException e) {
                Logger.error(TAG, "Exception getAction() ", e);
            }
        }
        return action;
    }

    public static void insertOrUpdateVideoData(Context context, Data data) {
        String selection = VideoProvider.VIDEO_COLUMNS.VIDEO_ID + " = ?";
        String[] selectionArg = new String[]{"" + data.getAssetID()};
        ContentValues value = new ContentValues();
        if (null != data.getAssetID()) {
            value.put(VideoProvider.VIDEO_COLUMNS.VIDEO_ID, data.getAssetID());
        }
        if (null != data.getName()) {
            value.put(VideoProvider.VIDEO_COLUMNS.NAME, data.getName());
        }
        if (null != data.getUrl()) {
            value.put(VideoProvider.VIDEO_COLUMNS.DOWNLOAD_URL, data.getUrl());
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
        if (null != data.getTransactionId()) {
            value.put(VideoProvider.VIDEO_COLUMNS.TRANSACTION_ID, data.getTransactionId());
        }
        if (null != data.getCloudTime()) {
            value.put(VideoProvider.VIDEO_COLUMNS.CLOUD_TIME, data.getCloudTime());
        }
        if (null != data.getReceivedTime()) {
            value.put(VideoProvider.VIDEO_COLUMNS.RECEIVED_TIME, data.getReceivedTime());
        }
        int count = context.getContentResolver().update(VideoProvider.CONTENT_URI_VIDEO_TABLE, value, selection, selectionArg);
        Logger.debug(TAG, "updated count is " + count);

        if (count == 0) {
            if (isSingleTonType(data.getType())) {
                //dirty logic
                //at any time only one breaking new and video
                selection = VideoProvider.VIDEO_COLUMNS.TYPE + " = ?";
                selectionArg = new String[]{"" + data.getType()};
                count = context.getContentResolver().delete(VideoProvider.CONTENT_URI_VIDEO_TABLE, selection, selectionArg);
                Logger.debug(TAG, "deleted count is " + count);
            }
            context.getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, value);
        }
    }

    /**
     * Method to check the video type is single existance
     *
     * @param type
     * @return
     */
    private static boolean isSingleTonType(String type) {
        switch (type) {
            case VideoProvider.VIDEO_TYPE.BREAKING_NEWS:
            case VideoProvider.VIDEO_TYPE.BREAKING_VIDEO:
            case VideoProvider.VIDEO_TYPE.COMPANY_VIDEO:
            case VideoProvider.VIDEO_TYPE.COMPANY_AD:
            case VideoProvider.VIDEO_TYPE.SAFETY_VIDEO:
                return true;
            default:
                return false;


        }
    }

    public static void deleteAllVideoDataExceptCompanyAndSafety() {
        String selection = VideoProvider.VIDEO_COLUMNS.TYPE + " IN (?,?,?,?)";
        String[] selectionArg = new String[]{"" + VideoProvider.VIDEO_TYPE.MOVIE, VideoProvider.VIDEO_TYPE.ADV, VideoProvider.VIDEO_TYPE.BREAKING_NEWS, VideoProvider.VIDEO_TYPE.BREAKING_VIDEO};
        try {
            int videoDeleteCount = VideoApplication.getVideoContext().getContentResolver().delete(VideoProvider.CONTENT_URI_VIDEO_TABLE, selection, selectionArg);
            Log.d(TAG, "Video data Delete Count :: " + videoDeleteCount);
        } catch (Exception e) {
            Log.d(TAG, "Exception :: deleteAllVideoDataExceptCompanyAndSafety() :: ", e);
        }
    }

    public static void deleteRequiredVideoType(String videoType) {
        String selection = VideoProvider.VIDEO_COLUMNS.TYPE + " = ?";
        String[] selectionArg = new String[]{"" + videoType};
        try {
            int videoTypeDeleteCount = VideoApplication.getVideoContext().getContentResolver().delete(VideoProvider.CONTENT_URI_VIDEO_TABLE, selection, selectionArg);
            Log.d(TAG, "deleteRequiredVideoType :: " + videoTypeDeleteCount);
        } catch (Exception e) {
            Log.d(TAG, "Exception :: deleteRequiredVideoType() :: ", e);
            Log.d(TAG, "Exception :: deleteRequiredVideoType() :: ", e);
        }
    }

    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles()) {
                boolean status = child.delete();
            }
    }

    public static void resetTravelSafety() {
        String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + " IN (?, ?)";
        String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.COMPANY_VIDEO, VideoProvider.VIDEO_TYPE.SAFETY_VIDEO};
        ContentValues lValues = new ContentValues();
        lValues.put(VideoProvider.VIDEO_COLUMNS.PLAY_COUNT, 0);//reset so that it will play again
        int count = VideoApplication.getVideoContext().getContentResolver().update(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValues, lSelection, lSelectionArg);
        Logger.debug(TAG, "resetTravelSafety :: count " + count);
    }

    public static List<MoviesList> getMoviesList() {
        Cursor lCursor = null;
        String[] projection = new String[]{"Distinct " + VideoProvider.VIDEO_COLUMNS.LANGUAGE};
        String selection = VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + " = ?";
        String[] selectionArg = new String[]{"" + VideoProvider.DOWNLOAD_STATUS.DOWNLOADED};
        List<MoviesList> mMoviesList = new ArrayList<MoviesList>();
        try {
            lCursor = VideoApplication.getVideoContext().getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, projection, selection, selectionArg, null);
            if (null != lCursor) {
                while (lCursor.moveToNext()) {
                    MoviesList movieList = new MoviesList();
                    String language = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.LANGUAGE));
                    movieList.setLanguage(language);
                    String lSelection = VideoProvider.VIDEO_COLUMNS.LANGUAGE + " = ?";
                    String[] lSelectionArg = new String[]{"" + language};
                    Cursor movieCursor = VideoApplication.getVideoContext().getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, null);
                    if (null != movieCursor) {
                        List<Movie> movies = new ArrayList<>();
                        while (movieCursor.moveToNext()) {
                            Movie movie = new Movie();
                            movie.setMovieId(movieCursor.getString(movieCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.VIDEO_ID)));
                            movie.setMovieName(movieCursor.getString(movieCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.NAME)));
                            movie.setMoviePath(movieCursor.getString(movieCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PATH)));
                            movies.add(movie);
                        }
                        movieList.setMovies(movies);
                    }
                    mMoviesList.add(movieList);
                }

            }
        } catch (Exception e) {
            Log.e(TAG, "Exception getRoutes() ", e);
        } finally {
            if (null != lCursor && !lCursor.isClosed()) {
                lCursor.close();
            }
        }
        Log.d(TAG, "getRoutes() " + mMoviesList);
        return mMoviesList;
    }

    public static boolean updateMovieSelection(String movieId) {
        resetMovieSelection();

        String lSelection = VideoProvider.VIDEO_COLUMNS.VIDEO_ID + " = ?";
        String[] lSelectionArg = new String[]{"" + movieId};
        ContentValues content = new ContentValues();
        content.put(VideoProvider.VIDEO_COLUMNS.SELECTED_STATE, 1);
        int count = VideoApplication.getVideoContext().getContentResolver().update(VideoProvider.CONTENT_URI_VIDEO_TABLE, content, lSelection, lSelectionArg);
        Log.d(TAG, "updateMovieSelection :: updated the new selected movie" + count);
        return count >= 0 ? true : false;

    }

    public static int resetMovieSelection() {
        ContentValues content = new ContentValues();
        content.put(VideoProvider.VIDEO_COLUMNS.SELECTED_STATE, 0);
        int count = VideoApplication.getVideoContext().getContentResolver().update(VideoProvider.CONTENT_URI_VIDEO_TABLE, content, null, null);
        Log.d(TAG, "resetMovieSelection() :: rows count " + count);
        return count;
    }
}

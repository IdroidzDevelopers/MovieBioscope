package com.lib.videoplayer.util;


import android.app.AlarmManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import com.lib.videoplayer.object.AdsSlotsData;
import com.lib.videoplayer.object.Asset;
import com.lib.videoplayer.object.Data;
import com.lib.videoplayer.object.Movie;
import com.lib.videoplayer.object.MoviesList;
import com.lib.videoplayer.object.PushData;
import com.lib.videoplayer.object.SequenceCloudData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class VideoData {
    private static final String TAG = VideoData.class.getSimpleName();
    private static final int VIDEO_PLAYING = 1;
    private static final int VIDEO_COMPLETED = 0;
    private static final int DELETE_STATUS = 1;


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

    /**
     * Method to check landing video exist or not
     *
     * @param aContext
     * @return
     */
    public static boolean isLandingVideoExist(Context aContext) {
        boolean flag = false;
        if (null != aContext) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + " IN( ?, ? , ? , ? , ? ) AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ?";
            String[] lSelectionArg = {VideoProvider.VIDEO_TYPE.TRAILER, VideoProvider.VIDEO_TYPE.COMEDY_SHOW, VideoProvider.VIDEO_TYPE.SERIAL, VideoProvider.VIDEO_TYPE.DEVOTIONAL, VideoProvider.VIDEO_TYPE.SPORTS, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED};
            String orderBy = VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME + " ASC";
            Cursor lCursor = null;
            try {
                lCursor = aContext.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, orderBy);
                while (null != lCursor && lCursor.moveToNext()) {
                    flag = true;
                    break;
                }
            } catch (Exception e) {
                Logger.error(TAG, "Exception :: isLandingVideoExist() :: ", e);
            } finally {
                if (null != lCursor && !lCursor.isClosed()) {
                    lCursor.close();
                }
            }
            Logger.debug(TAG, "isLandingVideoExist() :: " + flag);
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


    public static long getNextAdTime() {
        int SLOT_PER_HOUR = AdsSlotConfigUtil.getSlotsPerHourCount(AdsSlotConfigUtil.SLOT_TYPE.LANDING_SLOT_TYPE);
        long INTERVAL_TWO_MINUTES = 2 * 60 * 1000;
        final long AVG_TIME_IN_MILLIS = AlarmManager.INTERVAL_HOUR / SLOT_PER_HOUR;
        final long MIN_TIME_IN_MILLIS = AVG_TIME_IN_MILLIS - INTERVAL_TWO_MINUTES;
        long MAX_TIME_IN_MILLIS = AVG_TIME_IN_MILLIS + INTERVAL_TWO_MINUTES;
        Random lRandom = new Random();
        long lRandomValue = MIN_TIME_IN_MILLIS +
                (long) (lRandom.nextDouble() * (MAX_TIME_IN_MILLIS - MIN_TIME_IN_MILLIS));
        Log.d(TAG, "--Test-- MIN_TIME_IN_MILLIS " + MIN_TIME_IN_MILLIS + " lRandomValue " + lRandomValue + " MAX_TIME_IN_MILLIS " + MAX_TIME_IN_MILLIS);
        return lRandomValue;
    }

    public static int updateVideoData(Context aContext, Data lData) {
        updateVideoPlayingState(lData.getAssetID());
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
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ? AND " + VideoProvider.VIDEO_COLUMNS.PLAY_COUNT + "= ?";
            String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.BREAKING_VIDEO, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED, "" + 0};
            Cursor lCursor = null;
            try {
                lCursor = context.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, null);
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
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ? AND " + VideoProvider.VIDEO_COLUMNS.PLAY_COUNT + "= ?";
            String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.COMPANY_AD, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED, "" + 0};
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
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ? AND " + VideoProvider.VIDEO_COLUMNS.PLAY_COUNT + "= ?";
            String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.BREAKING_NEWS, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED, "" + 0};
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
                            deleteFileById(videoId);
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
                    String transactionId = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.TRANSACTION_ID));
                    data.setAssetID(videoId);
                    data.setTransactionId(transactionId);
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

    public static AdsSlotsData createAdsSlotsData(Context context, String rowId) {
        FirebaseData firebase = FirebaseUtil.getFireBaseData(context, rowId);
        AdsSlotsData adsSlotsData = null;
        if (null != firebase) {
            Logger.debug(TAG, "data is " + firebase.getData());
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            adsSlotsData = gson.fromJson(firebase.getData(), AdsSlotsData.class);
            //adsSlotsData.setCloudTime(firebase.getSentTime());
            //adsSlotsData.setReceivedTime(firebase.getReceivedTime());
        }
        return adsSlotsData;
    }


    public static Map<String, List<SequenceCloudData>> createSequenceData(String rowId) {
        FirebaseData firebase = FirebaseUtil.getFireBaseData(VideoApplication.getVideoContext(), rowId);
        Map<String, List<SequenceCloudData>> map = new HashMap<String, List<SequenceCloudData>>();
        if (null != firebase) {
            Logger.debug(TAG, "createSequenceData :: data is " + firebase.getData());
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            try {
                JSONObject data = new JSONObject(firebase.getData());
                JSONObject sequence = data.getJSONObject("sequence");
                List<SequenceCloudData> movieList = Arrays.asList(gson.fromJson(sequence.getJSONArray(StateMachine.SEQUENCE_TYPE.MOVIE_INIT_TYPE).toString(), SequenceCloudData[].class));
                List<SequenceCloudData> landingList = Arrays.asList(gson.fromJson(sequence.getJSONArray(StateMachine.SEQUENCE_TYPE.LANDING_TYPE).toString(), SequenceCloudData[].class));
                if (null != movieList && movieList.size() != 0) {
                    map.put(StateMachine.SEQUENCE_TYPE.MOVIE_INIT_TYPE, movieList);
                }
                if (null != landingList && landingList.size() != 0) {
                    map.put(StateMachine.SEQUENCE_TYPE.LANDING_TYPE, landingList);
                }
            } catch (JSONException e) {
                Logger.error(TAG, "Exception createSequenceData() ", e);
            }
        }
        return map;
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
        if (null != data.getPriority()) {
            value.put(VideoProvider.VIDEO_COLUMNS.PRIORITY, data.getPriority());
        }
        int count = context.getContentResolver().update(VideoProvider.CONTENT_URI_VIDEO_TABLE, value, selection, selectionArg);
        Logger.debug(TAG, "updated count is " + count);

        if (count == 0) {
/*            if (isSingleTonType(data.getType())) {
                //at any time only one breaking new and video
                selection = VideoProvider.VIDEO_COLUMNS.TYPE + " = ?";
                selectionArg = new String[]{"" + data.getType()};
                count = context.getContentResolver().delete(VideoProvider.CONTENT_URI_VIDEO_TABLE, selection, selectionArg);
                Logger.debug(TAG, "deleted count is " + count);

            }*/
            context.getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, value);
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

    public static void deleteAllTicker() {
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? ";
            String[] lSelectionArg = {"" + VideoProvider.VIDEO_TYPE.TICKER};
            Cursor lCursor = null;
            try {
                lCursor = VideoApplication.getVideoContext().getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, null);
                while (null != lCursor && lCursor.moveToNext()) {
                    String videoId = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.VIDEO_ID));
                    if(null!=videoId){
                        deleteFileById(videoId);
                    }
                    break;
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception :: deleteAllTicker() :: ", e);
            } finally {
                if (null != lCursor && !lCursor.isClosed()) {
                    lCursor.close();
                }
            }
    }

    public static void deleteFileById(String videoId) {
        String path = getAssetPath(videoId);
        deleteFile(path);
        String selection = VideoProvider.VIDEO_COLUMNS.VIDEO_ID + " = ?";
        String[] selectionArg = new String[]{"" + videoId};
        try {
            int videoTypeDeleteCount = VideoApplication.getVideoContext().getContentResolver().delete(VideoProvider.CONTENT_URI_VIDEO_TABLE, selection, selectionArg);
            Log.d(TAG, "--(debug)-- :: deleteFileById :: " + videoTypeDeleteCount);
        } catch (Exception e) {
            Logger.error(TAG, "Exception :: deleteFileById() :: ", e);
        }
    }

    public static void deleteFileIfNotPlaying(Asset asset) {
        if (null != asset) {
            if (!isVideoPlaying(asset.getAssetID())) {
                try {
                    deleteFileById(asset.getAssetID());
                    Log.d(TAG, "--(debug)-- :: deleteFileIfNotPlaying():: ");
                } catch (Exception e) {
                    Logger.error(TAG, "Exception :: deleteFileIfNotPlaying() ::", e);
                }
            } else {
                updateVideoDeleteState(asset.getAssetID());
            }
        }
    }

    public static synchronized void backgroundSearchForPendingDeleteVideo() {
        String lSelection = VideoProvider.VIDEO_COLUMNS.DELETE_STATUS + " = ? AND " + VideoProvider.VIDEO_COLUMNS.IS_PLAYING + " = ?";
        String[] lSelectionArg = new String[]{"" + 1, "" + 0};
        Cursor lCursor = null;
        try {
            lCursor = VideoApplication.getVideoContext().getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, null);
            while (null != lCursor && lCursor.moveToNext()) {
                String id = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.VIDEO_ID));
                deleteFileById(id);
                Logger.debug(TAG, "backgroundSearchForPendingDeleteVideo() :: Video to delete " + id);
                break;
            }
            Logger.debug(TAG, "backgroundSearchForPendingDeleteVideo() :: No more pending chill!!!");
        } catch (Exception e) {
            Log.d(TAG, "Exception :: backgroundSearchForPendingDeleteVideo() :: ", e);
        } finally {
            if (null != lCursor && !lCursor.isClosed()) {
                lCursor.close();
            }
        }
    }


    public static void deleteRecursive(File fileOrDirectory) {
        if (null != fileOrDirectory) {
            if (fileOrDirectory.isDirectory())
                for (File child : fileOrDirectory.listFiles()) {
                    boolean status = child.delete();
                }
        }
    }

    public static void deleteFile(String filePath) {
        if (null != filePath) {
            try {
                File deleteFile = new File(filePath);
                boolean status = deleteFile.delete();
                Log.d(TAG, "--(debug)-- :: File Delete status:: " + status);
            } catch (Exception e) {
                Logger.error(TAG, "Exception :: deleteFile() ::", e);
            }
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
        Cursor movieCursor = null;
        String[] projection = new String[]{"Distinct " + VideoProvider.VIDEO_COLUMNS.LANGUAGE};
        String selection = VideoProvider.VIDEO_COLUMNS.TYPE + " = ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + " = ?";
        String[] selectionArg = new String[]{VideoProvider.VIDEO_TYPE.MOVIE, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED};
        List<MoviesList> mMoviesList = new ArrayList<MoviesList>();
        try {
            lCursor = VideoApplication.getVideoContext().getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, projection, selection, selectionArg, null);
            if (null != lCursor) {
                while (lCursor.moveToNext()) {
                    MoviesList movieList = new MoviesList();
                    String language = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.LANGUAGE));
                    movieList.setLanguage(language);
                    String lSelection = VideoProvider.VIDEO_COLUMNS.LANGUAGE + " = ? AND " + VideoProvider.VIDEO_COLUMNS.TYPE + " = ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + " = ?";
                    String[] lSelectionArg = new String[]{"" + language, VideoProvider.VIDEO_TYPE.MOVIE, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED};
                    movieCursor = VideoApplication.getVideoContext().getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, null);
                    if (null != movieCursor) {
                        List<Movie> movies = new ArrayList<>();
                        while (movieCursor.moveToNext()) {
                            Movie movie = new Movie();
                            movie.setMovieId(movieCursor.getString(movieCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.VIDEO_ID)));
                            movie.setMovieName(getNameWithoutExtension(movieCursor.getString(movieCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.NAME))));
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
            if (null != movieCursor && !movieCursor.isClosed()) {
                movieCursor.close();
            }
        }
        Log.d(TAG, "getRoutes() " + mMoviesList);
        return mMoviesList;
    }

    private static String getNameWithoutExtension(String name) {
        if (null == name) {
            return "Unknown";
        }
        return name.substring(0, name.lastIndexOf("."));
    }

    public static boolean updateVideoPlayingState(String videoId) {

        String lSelection = VideoProvider.VIDEO_COLUMNS.VIDEO_ID + " = ?";
        String[] lSelectionArg = new String[]{"" + videoId};
        ContentValues content = new ContentValues();
        content.put(VideoProvider.VIDEO_COLUMNS.IS_PLAYING, VIDEO_PLAYING);
        int count = VideoApplication.getVideoContext().getContentResolver().update(VideoProvider.CONTENT_URI_VIDEO_TABLE, content, lSelection, lSelectionArg);
        Log.d(TAG, "updateVideoPlayingState :: updated the video playing state" + count);
        return count >= 0 ? true : false;

    }

    public static boolean updateVideoCompletedState(String videoId) {

        String lSelection = VideoProvider.VIDEO_COLUMNS.VIDEO_ID + " = ?";
        String[] lSelectionArg = new String[]{"" + videoId};
        ContentValues content = new ContentValues();
        content.put(VideoProvider.VIDEO_COLUMNS.IS_PLAYING, VIDEO_COMPLETED);
        content.put(VideoProvider.VIDEO_COLUMNS.HAS_PLAYED_IN_SEQUENCE, 1);
        int count = VideoApplication.getVideoContext().getContentResolver().update(VideoProvider.CONTENT_URI_VIDEO_TABLE, content, lSelection, lSelectionArg);
        Log.d(TAG, "updateVideoCompletedState :: updated the video completed state" + count);
        return count >= 0 ? true : false;

    }

    public static boolean updateVideoDeleteState(String videoId) {

        String lSelection = VideoProvider.VIDEO_COLUMNS.VIDEO_ID + " = ?";
        String[] lSelectionArg = new String[]{"" + videoId};
        ContentValues content = new ContentValues();
        content.put(VideoProvider.VIDEO_COLUMNS.DELETE_STATUS, DELETE_STATUS);
        int count = VideoApplication.getVideoContext().getContentResolver().update(VideoProvider.CONTENT_URI_VIDEO_TABLE, content, lSelection, lSelectionArg);
        Log.d(TAG, "updateVideoPlayingState :: updated the video playing state" + count);
        return count >= 0 ? true : false;

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

    public static void createAndSendAcknowledgementData(String transactionId, String assetId, String status) {
        Bundle lBundle = new Bundle();
        lBundle.putString("transactionid", transactionId);
        lBundle.putString("assetid", assetId);
        lBundle.putString("status", status);
        LocalBroadcastManager.getInstance(VideoApplication.getVideoContext()).
                sendBroadcast(new Intent(CustomIntent.ACTION_ACK_DATA_RECEIVED).
                        putExtra("data", lBundle));
    }

    public static void createAndSendAcknowledgementData(String transactionId, String status) {
        createAndSendAcknowledgementData(transactionId, null, status);

    }

    public static synchronized String getAssetPath(String assetId) {
        String assetPath = null;
        if (null != assetId) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.VIDEO_ID + " = ?";
            String[] lSelectionArg = {"" + assetId};
            Cursor lCursor = null;
            try {
                lCursor = VideoApplication.getVideoContext().getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, null);
                while (null != lCursor && lCursor.moveToNext()) {
                    assetPath = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PATH));
                    if (null != assetPath) {
                        Logger.debug(TAG, "getAssetPath() ::" + assetPath);

                    }
                    break;
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception :: getAssetPath() :: ", e);
            } finally {
                if (null != lCursor && !lCursor.isClosed()) {
                    lCursor.close();
                }
            }
        }
        return assetPath;
    }

    public static synchronized boolean isVideoPlaying(String assetId) {
        boolean isPlaying = false;
        if (null != assetId) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.VIDEO_ID + " = ?";
            String[] lSelectionArg = {"" + assetId};
            Cursor lCursor = null;
            try {
                lCursor = VideoApplication.getVideoContext().getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, lSelection, lSelectionArg, null);
                while (null != lCursor && lCursor.moveToNext()) {
                    int status = lCursor.getInt(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.IS_PLAYING));
                    if (VIDEO_PLAYING == status) {
                        isPlaying = true;
                        Logger.debug(TAG, "isVideoPlaying() ::" + status);
                    }
                    break;
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception :: getAssetPath() :: ", e);
            } finally {
                if (null != lCursor && !lCursor.isClosed()) {
                    lCursor.close();
                }
            }
        }
        return isPlaying;
    }


    /**
     * Method to get the video based on type
     *
     * @param type
     * @return
     */
    public static Data getVideoByType(String type) {
        Data data = null;
        String selection;
        String[] selectionArg;
        String orderBy = null;
        switch (type) {
            case VideoProvider.VIDEO_TYPE.SAFETY_VIDEO:
            case VideoProvider.VIDEO_TYPE.TRAVELER_VIDEO:
                selection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ? AND " + VideoProvider.VIDEO_COLUMNS.PLAY_COUNT + " = ?";
                selectionArg = new String[]{type, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED, "" + 0};
                orderBy = VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME + " ASC";
                break;
            case VideoProvider.VIDEO_TYPE.MOVIE:
                String defaultSelectedMovieId = getDefaultMovie();
                if (null == defaultSelectedMovieId) {
                    Logger.info(TAG, "get movie based on random logic");
                    selection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND "+ VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ?";
                    selectionArg = new String[]{type,"" + 0, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED};
                    orderBy = VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME + " ASC";
                } else {
                    Logger.info(TAG, "get user selected movie with asset id " + defaultSelectedMovieId);
                    selection = VideoProvider.VIDEO_COLUMNS.VIDEO_ID + " = ?";
                    selectionArg = new String[]{"" + defaultSelectedMovieId};
                }
                break;
            default:
                selection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ? AND "+VideoProvider.VIDEO_COLUMNS.HAS_PLAYED_IN_SEQUENCE + "= ? AND " + VideoProvider.VIDEO_COLUMNS.DOWNLOAD_STATUS + "= ?";
                selectionArg = new String[]{type,"" + 0, VideoProvider.DOWNLOAD_STATUS.DOWNLOADED};
                //orderBy = VideoProvider.VIDEO_COLUMNS.PRIORITY + " DESC , " + VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME + " ASC";
                orderBy = VideoProvider.VIDEO_COLUMNS.PRIORITY + " DESC," +VideoProvider.VIDEO_COLUMNS.LAST_PLAYED_TIME + " ASC";
                break;
        }
        Cursor cursor = null;
        try {
            cursor = VideoApplication.getVideoContext().getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE, null, selection, selectionArg, orderBy);
            while (null != cursor && cursor.moveToNext()) {
                data = new Data();
                String value = cursor.getString(cursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.VIDEO_ID));
                data.setAssetID(value);
                value = cursor.getString(cursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.TYPE));
                data.setType(value);
                value = cursor.getString(cursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PATH));
                data.setPath(value);
                int lCount = cursor.getInt(cursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PLAY_COUNT));
                data.setCount(lCount);
                break;
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception :: getVideoByType() :: ", e);
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return data;
    }

    /**
     * Method to reset all videoes of a particular sequence to not played state
     * once all videos has played in a sequence
     *
     * @return count of reset videos
     */
    public static int resetVideoSequencePlayedState(String sequenceType) {
        String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ?";
        String[] lSelectionArg = {"" + sequenceType};
        ContentValues content = new ContentValues();
        content.put(VideoProvider.VIDEO_COLUMNS.HAS_PLAYED_IN_SEQUENCE, 0);
        int count = VideoApplication.getVideoContext().getContentResolver().update(VideoProvider.CONTENT_URI_VIDEO_TABLE, content, lSelection, lSelectionArg);
        Log.d(TAG, "resetVideoSequencePlayedState() :: rows count " + count);
        return count;
    }
}

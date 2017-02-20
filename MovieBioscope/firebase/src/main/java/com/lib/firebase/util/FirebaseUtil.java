package com.lib.firebase.util;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.lib.firebase.database.FirebaseProvider;
import com.lib.firebase.object.FirebaseData;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUtil {
    private static final String TAG = FirebaseUtil.class.getSimpleName();
    private static final boolean DEBUG = true;

    public static synchronized void insertFirebaseTopics(Context context, String topic) {
        ContentValues lLocationContentValue = new ContentValues();
        lLocationContentValue.put(FirebaseProvider.FIREBASECOLUMNS.FIREBASE_TOPIC, topic);
        Uri lUri = context.getContentResolver().insert(FirebaseProvider.CONTENT_URI_FIREBASE_TOPICS_TABLE, lLocationContentValue);
        if (DEBUG)
            Log.d(TAG, "insertFirebaseTopics() :: CONTENT_URI_BUS_DETAIL_TABLE " + lUri);
    }

    public static List<String> getAllFireBaseTopics(Context context) {
        Cursor lCursor = null;
        List<String> mTopicsList = new ArrayList<String>();
        try {
            lCursor = context.getContentResolver().query(FirebaseProvider.CONTENT_URI_FIREBASE_TOPICS_TABLE, null, null, null, null);
            if (null != lCursor) {
                while (lCursor.moveToNext()) {
                    mTopicsList.add(lCursor.getString(lCursor.getColumnIndex(FirebaseProvider.FIREBASECOLUMNS.FIREBASE_TOPIC)));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception getRoutes() ", e);
        } finally {
            if (null != lCursor && !lCursor.isClosed()) {
                lCursor.close();
            }
        }
        if (DEBUG) Log.d(TAG, "getRoutes() " + mTopicsList);
        return mTopicsList;
    }

    public static void deleteAllFirebaseTopics() {

    }

    public static Uri insertFirebaseData(Context context, String appName, String data, long sentTime) {
        ContentValues lLocationContentValue = new ContentValues();
        lLocationContentValue.put(FirebaseProvider.FIREBASEDATACOLUMNS.APP_NAME, appName);
        lLocationContentValue.put(FirebaseProvider.FIREBASEDATACOLUMNS.DATA, data);
        lLocationContentValue.put(FirebaseProvider.FIREBASEDATACOLUMNS.SENT_TIME, sentTime);
        lLocationContentValue.put(FirebaseProvider.FIREBASEDATACOLUMNS.RECEIVED_TIME, System.currentTimeMillis());
        Uri lUri = context.getContentResolver().insert(FirebaseProvider.CONTENT_URI_FIREBASE_DATA_TABLE, lLocationContentValue);
        if (DEBUG)
            Log.d(TAG, "insertFirebaseData() :: CONTENT_URI_BUS_DATA_TABLE " + lUri);
        return lUri;
    }

    public static FirebaseData getFireBaseData(Context context, String rowId) {
        Cursor lCursor = null;
        FirebaseData data = null;
        String selection = FirebaseProvider.FIREBASEDATACOLUMNS.ID + " = ?";
        String[] selectionArg = new String[]{"" + rowId};
        try {
            lCursor = context.getContentResolver().query(FirebaseProvider.CONTENT_URI_FIREBASE_DATA_TABLE, null, selection, selectionArg, null);
            if (null != lCursor) {
                while (lCursor.moveToNext()) {
                    data = new FirebaseData();
                    data.setTransactionId(lCursor.getString(lCursor.getColumnIndex(FirebaseProvider.FIREBASEDATACOLUMNS.APP_NAME)));
                    data.setData(lCursor.getString(lCursor.getColumnIndex(FirebaseProvider.FIREBASEDATACOLUMNS.DATA)));
                    data.setSentTime(lCursor.getString(lCursor.getColumnIndex(FirebaseProvider.FIREBASEDATACOLUMNS.SENT_TIME)));
                    data.setReceivedTime(lCursor.getString(lCursor.getColumnIndex(FirebaseProvider.FIREBASEDATACOLUMNS.RECEIVED_TIME)));
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception getFireBaseData() ", e);
        } finally {
            if (null != lCursor && !lCursor.isClosed()) {
                lCursor.close();
            }
        }
        if (DEBUG) Log.d(TAG, "getFireBaseData() " + data);
        return data;
    }
}

package com.hyperbound.moviebioscope.util;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.hyperbound.moviebioscope.app.BioscopeApp;
import com.hyperbound.moviebioscope.database.BusProvider;

import java.util.ArrayList;
import java.util.List;

public class BusUtil {
    private static final String TAG = BusUtil.class.getSimpleName();
    private static final boolean DEBUG = true;

    public static Uri saveRegistrationDetail(Context aContext, String aRegNumber) {
        ContentValues lValues = new ContentValues();
        lValues.put(BusProvider.COLUMNS.BUS_NUMBER, aRegNumber);
        Uri lUri = aContext.getContentResolver().insert(BusProvider.CONTENT_URI_BUS_DETAIL_TABLE, lValues);
        if (DEBUG) Log.d(TAG, "saveRegistrationDetail :: " + lUri);
        return lUri;
    }

    public static boolean isRegistrationNumberAvailable(Context aContext) {
        boolean lValue = false;
        Cursor lCursor = null;
        try {
            lCursor = aContext.getContentResolver().query(BusProvider.CONTENT_URI_BUS_DETAIL_TABLE, null, null, null, null);
            if (null != lCursor && lCursor.getCount() > 0) {
                lValue = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception isRegistrationNumberAvailable() ", e);
            lValue = false;
        } finally {
            if (null != lCursor && !lCursor.isClosed()) {
                lCursor.close();
            }
        }
        if (DEBUG) Log.d(TAG, "isRegistrationNumberAvailable() " + lValue);
        return lValue;
    }

    public static synchronized void insertBusInfo(String busId, String busNumber,
                                                  String comapanyId, String companyName) {
        ContentValues lLocationContentValue = new ContentValues();
        lLocationContentValue.put(BusProvider.COLUMNS.BUS_ID, busId);
        lLocationContentValue.put(BusProvider.COLUMNS.BUS_NUMBER, busNumber);
        lLocationContentValue.put(BusProvider.COLUMNS.COMPANY_ID, comapanyId);
        lLocationContentValue.put(BusProvider.COLUMNS.COMPANY_NAME, companyName);
        int count = BioscopeApp.getContext().getContentResolver().update(BusProvider.CONTENT_URI_BUS_DETAIL_TABLE, lLocationContentValue, null, null);
        if (DEBUG)
            Log.d(TAG, "insertBusInfo() :: CONTENT_URI_BUS_DETAIL_TABLE rows count " + count);
    }

    public static synchronized void insertFirebaseTopics(String topic) {
        ContentValues lLocationContentValue = new ContentValues();
        lLocationContentValue.put(BusProvider.FIREBASECOLUMNS.FIREBASE_TOPIC, topic);
        int count = BioscopeApp.getContext().getContentResolver().update(BusProvider.CONTENT_URI_FIREBASE_TOPICS_TABLE, lLocationContentValue, null, null);
        if (DEBUG)
            Log.d(TAG, "insertFirebaseTopics() :: CONTENT_URI_BUS_DETAIL_TABLE rows count " + count);
    }

    public static List<String> getAllFireBaseTopics() {
        Cursor lCursor = null;
        List<String> mTopicsList = new ArrayList<String>();
        try {
            lCursor = BioscopeApp.getContext().getContentResolver().query(BusProvider.CONTENT_URI_FIREBASE_TOPICS_TABLE, null, null, null, null);
            if (null != lCursor) {
                while (lCursor.moveToNext()) {
                    mTopicsList.add(lCursor.getString(lCursor.getColumnIndex(BusProvider.FIREBASECOLUMNS.FIREBASE_TOPIC)));
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

    public static void deleteAllFirebaseTopics(){

    }

}

package com.hyperbound.moviebioscope.util;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.hyperbound.moviebioscope.app.BioscopeApp;
import com.hyperbound.moviebioscope.database.BusProvider;
import com.hyperbound.moviebioscope.model.BusDetails;

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
        Uri uri=null;
        ContentValues value = new ContentValues();
        value.put(BusProvider.COLUMNS.BUS_ID, busId);
        value.put(BusProvider.COLUMNS.BUS_NUMBER, busNumber);
        value.put(BusProvider.COLUMNS.COMPANY_ID, comapanyId);
        value.put(BusProvider.COLUMNS.COMPANY_NAME, companyName);
        int updateCount=BioscopeApp.getContext().getContentResolver().update(BusProvider.CONTENT_URI_BUS_DETAIL_TABLE,value,null,null);
        if(updateCount==0) {
            uri = BioscopeApp.getContext().getContentResolver().insert(BusProvider.CONTENT_URI_BUS_DETAIL_TABLE, value);
        }
        if (DEBUG)
            Log.d(TAG, "insertBusInfo() :: CONTENT_URI_BUS_DETAIL_TABLE rows count " + uri);
    }

    public static synchronized BusDetails getBusData() {
        BusDetails data = null;
        Cursor lCursor = null;
        try {
            lCursor = BioscopeApp.getContext().getContentResolver().query(BusProvider.CONTENT_URI_BUS_DETAIL_TABLE, null, null, null, null);
            if (null != lCursor) {
                while (lCursor.moveToNext()) {
                    data = new BusDetails();
                    data.setFleetID(lCursor.getString(lCursor.getColumnIndex(BusProvider.COLUMNS.BUS_ID)));
                    data.setRegNo(lCursor.getString(lCursor.getColumnIndex(BusProvider.COLUMNS.BUS_NUMBER)));
                    data.setCompany(lCursor.getString(lCursor.getColumnIndex(BusProvider.COLUMNS.COMPANY_ID)));
                    data.setRegNo(lCursor.getString(lCursor.getColumnIndex(BusProvider.COLUMNS.BUS_NUMBER)));
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception getBusData() ", e);
        } finally {
            if (null != lCursor && !lCursor.isClosed()) {
                lCursor.close();
            }
        }
        if (DEBUG) Log.d(TAG, "getBusData() " + data);
        return data;
    }

    public static synchronized String getFleetId() {

        Cursor lCursor = null;
        String busId=null;
        try {
            lCursor = BioscopeApp.getContext().getContentResolver().query(BusProvider.CONTENT_URI_BUS_DETAIL_TABLE, null,null,null, null);
            if (null != lCursor) {
                while (lCursor.moveToNext()) {
                    busId=lCursor.getString(lCursor.getColumnIndex(BusProvider.COLUMNS.BUS_ID));
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception getFleetId() ", e);
        } finally {
            if (null != lCursor && !lCursor.isClosed()) {
                lCursor.close();
            }
        }
        if (DEBUG) Log.d(TAG, "getFleetId() " + busId);
        return busId;
    }
}

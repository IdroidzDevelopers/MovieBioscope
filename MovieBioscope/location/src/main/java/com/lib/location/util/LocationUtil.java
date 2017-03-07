package com.lib.location.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.lib.location.LocationApplication;
import com.lib.location.databases.LocationProvider;
import com.lib.location.model.LocationInfo;

/**
 * Created by Aron on 15-02-2017.
 */

public class LocationUtil {
    private static final String TAG = LocationUtil.class.getSimpleName();
    private static final boolean DEBUG = true;

    public static synchronized void insertOrUpdateRouteInfo(String aSource, String aDestination) {
        ContentValues lLocationContentValue = new ContentValues();
        lLocationContentValue.put(LocationProvider.LOCATION_COLUMNS.SOURCE, aSource);
        lLocationContentValue.put(LocationProvider.LOCATION_COLUMNS.DESTINATION, aDestination);
        int count = LocationApplication.getLocationContext().getContentResolver().update(LocationProvider.CONTENT_URI_LOCATION_TABLE, lLocationContentValue, null, null);
        if (DEBUG)
            Log.d(TAG, "insertOrUpdateRouteInfo() :: CONTENT_URI_LOCATION_INFO_TABLE rows count " + count);
        if (count == 0) {
            LocationApplication.getLocationContext().getContentResolver().insert(LocationProvider.CONTENT_URI_LOCATION_TABLE, lLocationContentValue);
        }

    }

    public static LocationInfo getRouteInfo() {
        Cursor lCursor = null;
        LocationInfo locationInfo = null;
        try {
            lCursor = LocationApplication.getLocationContext().getContentResolver().query(LocationProvider.CONTENT_URI_LOCATION_TABLE, null, null, null, null);
            if (null != lCursor) {
                while (lCursor.moveToNext()) {
                    locationInfo = new LocationInfo();
                    locationInfo.setSource(lCursor.getString(lCursor.getColumnIndex(LocationProvider.LOCATION_COLUMNS.SOURCE)));
                    locationInfo.setDestination(lCursor.getString(lCursor.getColumnIndex(LocationProvider.LOCATION_COLUMNS.DESTINATION)));
                }

            }
        } catch (Exception e) {
            Log.e(TAG, "Exception getRouteInfo() ", e);
        } finally {
            if (null != lCursor && !lCursor.isClosed()) {
                lCursor.close();
            }
        }
        if (DEBUG) Log.d(TAG, "getRouteInfo:: " + locationInfo);
        return locationInfo;
    }

    public static synchronized void updateJourneyInfo(String journeyDistance, String journeyTime) {
        ContentValues lLocationContentValue = new ContentValues();
        lLocationContentValue.put(LocationProvider.LOCATION_COLUMNS.TOTAL_DISTANCE, journeyDistance);
        lLocationContentValue.put(LocationProvider.LOCATION_COLUMNS.TOTAL_TIME, journeyTime);
        int count = LocationApplication.getLocationContext().getContentResolver().update(LocationProvider.CONTENT_URI_LOCATION_TABLE, lLocationContentValue, null, null);
        if (DEBUG)
            Log.d(TAG, "updateLocationInfo() :: CONTENT_URI_LOCATION_INFO_TABLE rows count " + count);

    }

    public static LocationInfo getJourneyInfo() {
        Cursor lCursor = null;
        LocationInfo locationInfo = null;
        try {
            lCursor = LocationApplication.getLocationContext().getContentResolver().query(LocationProvider.CONTENT_URI_LOCATION_TABLE, null, null, null, null);
            if (null != lCursor) {
                while (lCursor.moveToNext()) {
                    locationInfo = new LocationInfo();
                    locationInfo.setTotalDistance(lCursor.getString(lCursor.getColumnIndex(LocationProvider.LOCATION_COLUMNS.TOTAL_DISTANCE)));
                    locationInfo.setTotalJourneyTime(lCursor.getString(lCursor.getColumnIndex(LocationProvider.LOCATION_COLUMNS.TOTAL_TIME)));
                }

            }
        } catch (Exception e) {
            Log.e(TAG, "Exception getJourneyInfo() ", e);
        } finally {
            if (null != lCursor && !lCursor.isClosed()) {
                lCursor.close();
            }
        }
        if (DEBUG) Log.d(TAG, "getJourneyInfo:: " + locationInfo);
        return locationInfo;
    }

    public static synchronized void updateLocationInfo(String aDistanceToSource, String aDistanceToDestination, String timeToDestination) {
        ContentValues lLocationContentValue = new ContentValues();
        lLocationContentValue.put(LocationProvider.LOCATION_COLUMNS.DISTANCE_TO_SOURCE, aDistanceToSource);
        lLocationContentValue.put(LocationProvider.LOCATION_COLUMNS.DISTANCE_TO_DESTINATION, aDistanceToDestination);
        lLocationContentValue.put(LocationProvider.LOCATION_COLUMNS.TIME_TO_DESTINATION, timeToDestination);
        lLocationContentValue.put(LocationProvider.LOCATION_COLUMNS.LAST_SYNC_TIME, System.currentTimeMillis());
        int count = LocationApplication.getLocationContext().getContentResolver().update(LocationProvider.CONTENT_URI_LOCATION_TABLE, lLocationContentValue, null, null);
        if (DEBUG)
            Log.d(TAG, "updateLocationInfo() :: CONTENT_URI_LOCATION_INFO_TABLE rows count " + count);

    }

    public static synchronized void updateCurentLocation(String currentLocation,String city) {
        ContentValues lLocationContentValue = new ContentValues();
        lLocationContentValue.put(LocationProvider.LOCATION_COLUMNS.CURRENT_LOCATION, currentLocation);
        lLocationContentValue.put(LocationProvider.LOCATION_COLUMNS.CITY, city);
        int count = LocationApplication.getLocationContext().getContentResolver().update(LocationProvider.CONTENT_URI_LOCATION_TABLE, lLocationContentValue, null, null);
        if (DEBUG)
            Log.d(TAG, "updateCurentLocation() :: CONTENT_URI_LOCATION_INFO_TABLE rows count " + count);

    }

    public static LocationInfo getLocationInfo() {
        Cursor lCursor = null;
        LocationInfo locationInfo = null;
        try {
            lCursor = LocationApplication.getLocationContext().getContentResolver().query(LocationProvider.CONTENT_URI_LOCATION_TABLE, null, null, null, null);
            if (null != lCursor) {
                while (lCursor.moveToNext()) {
                    locationInfo = new LocationInfo();
                    locationInfo.setDistanceToSource(lCursor.getString(lCursor.getColumnIndex(LocationProvider.LOCATION_COLUMNS.DISTANCE_TO_SOURCE)));
                    locationInfo.setDistanceToDestination(lCursor.getString(lCursor.getColumnIndex(LocationProvider.LOCATION_COLUMNS.DISTANCE_TO_DESTINATION)));
                    locationInfo.setTimeToDestination(lCursor.getString(lCursor.getColumnIndex(LocationProvider.LOCATION_COLUMNS.TIME_TO_DESTINATION)));
                    locationInfo.setLastSyncTime(lCursor.getString(lCursor.getColumnIndex(LocationProvider.LOCATION_COLUMNS.LAST_SYNC_TIME)));
                }

            }
        } catch (Exception e) {
            Log.e(TAG, "Exception getLocationInfo() ", e);
        } finally {
            if (null != lCursor && !lCursor.isClosed()) {
                lCursor.close();
            }
        }
        if (DEBUG) Log.d(TAG, "locationInfo:: " + locationInfo);
        return locationInfo;
    }

    public static LocationInfo getCurrentLocation() {
        Cursor lCursor = null;
        LocationInfo locationInfo = null;
        try {
            lCursor = LocationApplication.getLocationContext().getContentResolver().query(LocationProvider.CONTENT_URI_LOCATION_TABLE, null, null, null, null);
            if (null != lCursor) {
                while (lCursor.moveToNext()) {
                    locationInfo = new LocationInfo();
                    locationInfo.setCurrentLocation(lCursor.getString(lCursor.getColumnIndex(LocationProvider.LOCATION_COLUMNS.CURRENT_LOCATION)));
                    locationInfo.setCity(lCursor.getString(lCursor.getColumnIndex(LocationProvider.LOCATION_COLUMNS.CITY)));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception getLocationInfo() ", e);
        } finally {
            if (null != lCursor && !lCursor.isClosed()) {
                lCursor.close();
            }
        }
        if (DEBUG) Log.d(TAG, "locationInfo:: " + locationInfo);
        return locationInfo;
    }

    public static void deleteAllLocationData() {
        try {
            int locationDeleteCount = LocationApplication.getLocationContext().getContentResolver().delete(LocationProvider.CONTENT_URI_LOCATION_TABLE, null, null);
            Log.d(TAG, "Location Data Delete Count :: " + locationDeleteCount);
        } catch (Exception e) {
            Log.d(TAG, "Exception :: deleteAllLocationData() :: ", e);
        }
    }
}

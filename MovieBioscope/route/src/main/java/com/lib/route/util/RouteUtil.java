package com.lib.route.util;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.lib.route.RouteApplication;
import com.lib.route.database.RouteProvider;
import com.lib.route.objects.Route;

import java.util.ArrayList;
import java.util.List;

public class RouteUtil {
    private static final String TAG = RouteUtil.class.getSimpleName();
    private static final boolean DEBUG = true;

    public static synchronized boolean updateCurrentRoute(Context aContext, String routeId) {
        String lSelection = RouteProvider.ROUTECOLUMNS.ROUTE_ID + " = ?";
        String[] lSelectionArg = new String[]{"" + routeId};
        ContentValues lResetContent = new ContentValues();
        lResetContent.put(RouteProvider.ROUTECOLUMNS.CURRENT_SELECTION, 0);

        //reset all the rows for default to 0
        int count = aContext.getContentResolver().update(RouteProvider.CONTENT_URI_BUS_ROUTE_TABLE, lResetContent, null, null);
        if (DEBUG) Log.d(TAG, "updateCurrentRoute() :: reset rows count " + count);
        ContentValues lSelectContent = new ContentValues();
        lSelectContent.put(RouteProvider.ROUTECOLUMNS.CURRENT_SELECTION, 1);
        count = aContext.getContentResolver().update(RouteProvider.CONTENT_URI_BUS_ROUTE_TABLE, lSelectContent, lSelection, lSelectionArg);
        if (DEBUG) Log.d(TAG, "updateCurrentRoute :: updated the new default route" + count);
        return count >= 0 ? true : false;
    }

    public static synchronized void insertRouteInfo(String routeId, String source,
                                                    String sourceAddress, String sourceLatitude,
                                                    String sourceLongitude, String destination,
                                                    String destinationAddress, String destinationLatitude,
                                                    String destinationLongitude) {
        ContentValues lLocationContentValue = new ContentValues();
        lLocationContentValue.put(RouteProvider.ROUTECOLUMNS.ROUTE_ID, routeId);
        lLocationContentValue.put(RouteProvider.ROUTECOLUMNS.SOURCE, source);
        lLocationContentValue.put(RouteProvider.ROUTECOLUMNS.SOURCE_ADDRESS, sourceAddress);
        lLocationContentValue.put(RouteProvider.ROUTECOLUMNS.SOURCE_LATITUDE, sourceLatitude);
        lLocationContentValue.put(RouteProvider.ROUTECOLUMNS.SOURCE_LONGITUDE, sourceLongitude);
        lLocationContentValue.put(RouteProvider.ROUTECOLUMNS.DESTINATION, destination);
        lLocationContentValue.put(RouteProvider.ROUTECOLUMNS.DESTINATION_ADDRESS, destinationAddress);
        lLocationContentValue.put(RouteProvider.ROUTECOLUMNS.DESTINATION_LATITUDE, destinationLatitude);
        lLocationContentValue.put(RouteProvider.ROUTECOLUMNS.DESTINATION_LONGITUDE, destinationLongitude);
        Uri lUri = RouteApplication.getRouteContext().getContentResolver().insert(RouteProvider.CONTENT_URI_BUS_ROUTE_TABLE,lLocationContentValue);
        if (DEBUG)
            Log.d(TAG, "insertRouteInfo() :: CONTENT_URI_BUS_ROUTE_TABLE rows count " + lUri);
    }

    public static List<Route> getRoutes(Context aContext) {
        Cursor lCursor = null;
        List<Route> mRouteList = new ArrayList<Route>();
        try {
            lCursor = aContext.getContentResolver().query(RouteProvider.CONTENT_URI_BUS_ROUTE_TABLE, null, null, null, null);
            if (null != lCursor) {
                while (lCursor.moveToNext()) {
                    Route lRoute = new Route();
                    lRoute.setmRouteId(lCursor.getString(lCursor.getColumnIndex(RouteProvider.ROUTECOLUMNS.ROUTE_ID)));
                    lRoute.setmRouteSource(lCursor.getString(lCursor.getColumnIndex(RouteProvider.ROUTECOLUMNS.SOURCE)));
                    lRoute.setmRouteDestination(lCursor.getString(lCursor.getColumnIndex(RouteProvider.ROUTECOLUMNS.DESTINATION)));
                    lRoute.setmDefault(lCursor.getInt(lCursor.getColumnIndex(RouteProvider.ROUTECOLUMNS.CURRENT_SELECTION)));
                    mRouteList.add(lRoute);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception getRoutes() ", e);
        } finally {
            if (null != lCursor && !lCursor.isClosed()) {
                lCursor.close();
            }
        }
        if (DEBUG) Log.d(TAG, "getRoutes() " + mRouteList);
        return mRouteList;
    }

    public static Route getCurrentRoute(Context aContext) {
        Cursor lCursor = null;
        Route lRoute = null;
        String lSelection = RouteProvider.ROUTECOLUMNS.CURRENT_SELECTION + " = ?";
        String[] lSelectionArg = new String[]{"" + 1};
        try {
            lCursor = aContext.getContentResolver().query(RouteProvider.CONTENT_URI_BUS_ROUTE_TABLE, null, lSelection, lSelectionArg, null);
            if (null != lCursor) {
                while (lCursor.moveToNext()) {
                    lRoute = new Route();
                    lRoute.setmRouteId(lCursor.getString(lCursor.getColumnIndex(RouteProvider.ROUTECOLUMNS.ROUTE_ID)));
                    lRoute.setmRouteSource(lCursor.getString(lCursor.getColumnIndex(RouteProvider.ROUTECOLUMNS.SOURCE)));
                    lRoute.setmRouteDestination(lCursor.getString(lCursor.getColumnIndex(RouteProvider.ROUTECOLUMNS.DESTINATION)));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception getCurrentRoute() ", e);
        } finally {
            if (null != lCursor && !lCursor.isClosed()) {
                lCursor.close();
            }
        }
        if (DEBUG) Log.d(TAG, "getCurrentRoute() " + lRoute);
        return lRoute;
    }

}

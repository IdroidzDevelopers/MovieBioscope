package com.lib.route.util;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.lib.route.database.RouteProvider;
import com.lib.route.objects.Route;

import java.util.ArrayList;
import java.util.List;

public class RouteUtil {
    private static final String TAG = RouteUtil.class.getSimpleName();
    private static final boolean DEBUG = true;

    public static synchronized boolean updateCurrentRoute(Context aContext, String aRoute) {
        String lSelection = RouteProvider.COLUMNS.NAME + " = ?";
        String[] lSelectionArg = new String[]{"" + aRoute};
        ContentValues lResetContent = new ContentValues();
        lResetContent.put(RouteProvider.COLUMNS.CURRENT_SELECTION, 0);

        //reset all the rows for default to 0
        int count = aContext.getContentResolver().update(RouteProvider.CONTENT_URI_ROUTE_TABLE, lResetContent, null, null);
        if (DEBUG) Log.d(TAG, "updateCurrentRoute() :: reset rows count " + count);
        ContentValues lSelectContent = new ContentValues();
        lSelectContent.put(RouteProvider.COLUMNS.CURRENT_SELECTION, 1);
        count = aContext.getContentResolver().update(RouteProvider.CONTENT_URI_ROUTE_TABLE, lSelectContent, lSelection, lSelectionArg);
        if (DEBUG) Log.d(TAG, "updateCurrentRoute :: updated the new default route" + count);
        return count >= 0 ? true : false;
    }

    public static List<Route> getRoutes(Context aContext) {
        Cursor lCursor = null;
        List<Route> mRouteList = new ArrayList<Route>();
        try {
            lCursor = aContext.getContentResolver().query(RouteProvider.CONTENT_URI_ROUTE_TABLE, null, null, null, null);
            if (null != lCursor) {
                while (lCursor.moveToNext()) {
                    Route lRoute = new Route();
                    lRoute.setRouteId(lCursor.getString(lCursor.getColumnIndex(RouteProvider.COLUMNS.ROUTE_ID)));
                    lRoute.setRouteName(lCursor.getString(lCursor.getColumnIndex(RouteProvider.COLUMNS.NAME)));
                    lRoute.setDefault(lCursor.getInt(lCursor.getColumnIndex(RouteProvider.COLUMNS.CURRENT_SELECTION)));
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
        String lSelection = RouteProvider.COLUMNS.CURRENT_SELECTION + " = ?";
        String[] lSelectionArg = new String[]{"" + 1};
        try {
            lCursor = aContext.getContentResolver().query(RouteProvider.CONTENT_URI_ROUTE_TABLE, null, lSelection, lSelectionArg, null);
            if (null != lCursor) {
                while (lCursor.moveToNext()) {
                    lRoute = new Route();
                    lRoute.setRouteId(lCursor.getString(lCursor.getColumnIndex(RouteProvider.COLUMNS.ROUTE_ID)));
                    lRoute.setRouteName(lCursor.getString(lCursor.getColumnIndex(RouteProvider.COLUMNS.NAME)));
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

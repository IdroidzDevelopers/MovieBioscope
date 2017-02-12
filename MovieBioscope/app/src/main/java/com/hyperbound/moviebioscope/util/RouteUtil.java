package com.hyperbound.moviebioscope.util;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.hyperbound.moviebioscope.database.RouteProvider;
import com.hyperbound.moviebioscope.objects.Route;

import java.util.ArrayList;
import java.util.List;

public class RouteUtil {
    private static final String TAG = RouteUtil.class.getSimpleName();
    private static final boolean DEBUG = true;

    public static synchronized void updateDefaultRoute(Context aContext, String aRoute) {
        String lSelection = RouteProvider.COLUMNS.NAME + " = ?";
        String[] lSelectionArg = new String[]{"" + aRoute};
        ContentValues lResetContent = new ContentValues();
        lResetContent.put(RouteProvider.COLUMNS.CURRENT_SELECTION, 0);

        //reset all the rows for default to 0
        int count = aContext.getContentResolver().update(RouteProvider.CONTENT_URI_ROUTE_TABLE, lResetContent, null, null);
        if (DEBUG) Log.d(TAG, "updateDefaultRoute() :: reset rows count " + count);
        ContentValues lSelectContent = new ContentValues();
        lSelectContent.put(RouteProvider.COLUMNS.CURRENT_SELECTION, 1);
        count = aContext.getContentResolver().update(RouteProvider.CONTENT_URI_ROUTE_TABLE, lSelectContent, lSelection, lSelectionArg);
        if (DEBUG) Log.d(TAG, "updateDefaultRoute :: updated the new default route" + count);
    }

    public static List<Route> getRoutes(Context aContext) {
        Cursor lCursor = null;
        List<Route> mRouteList = new ArrayList<Route>();
        try {
            lCursor = aContext.getContentResolver().query(RouteProvider.CONTENT_URI_ROUTE_TABLE, null, null, null, null);
            if (null != lCursor) {
                while (lCursor.moveToNext()) {
                    Route lRoute = new Route();
                    lRoute.setId(lCursor.getInt(lCursor.getColumnIndex(RouteProvider.COLUMNS.ID)));
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


}

package com.hyperbound.moviebioscope.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.hyperbound.moviebioscope.app.BioscopeApp;
import com.hyperbound.moviebioscope.database.AnalyticProvider;
import com.hyperbound.moviebioscope.model.AnalyticData;
import com.hyperbound.moviebioscope.model.BusDetails;
import com.lib.location.ui.TopBannerFragment;
import com.lib.route.objects.Route;
import com.lib.route.util.RouteUtil;
import com.lib.utility.util.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public class AnalyticUtil {

    private static final String TAG = AnalyticUtil.class.getSimpleName();

    public static AnalyticData createAnalyticData(String assetId) {
        AnalyticData data = new AnalyticData();
        data.setAssetId(assetId);
        Route route = RouteUtil.getCurrentRoute(BioscopeApp.getContext());
        if (null != route) {
            data.setRouteId(route.getmRouteId());
        }
        BusDetails detail = BusUtil.getBusData();
        data.setFleetId(detail.getFleetID());
        data.setCompanyId(detail.getCompany());
        return data;
    }

    public static Uri insertAnalytic(AnalyticData data) {
        ContentValues value = new ContentValues();
        if (null != data.getAssetId()) {
            value.put(AnalyticProvider.COLUMNS.ASSET_ID, data.getAssetId());
        }
        if (null != data.getRouteId()) {
            value.put(AnalyticProvider.COLUMNS.ROUTE_ID, data.getRouteId());
        }
        if (null != data.getFleetId()) {
            value.put(AnalyticProvider.COLUMNS.FLEET_ID, data.getFleetId());
        }
        if (null != data.getCompanyId()) {
            value.put(AnalyticProvider.COLUMNS.COMPANY_ID, data.getCompanyId());
        }
        value.put(AnalyticProvider.COLUMNS.PLAYED_TIME, System.currentTimeMillis());
        Uri uri = BioscopeApp.getContext().getContentResolver().insert(AnalyticProvider.CONTENT_URI_ANALYTIC_TABLE, value);
        return uri;
    }


    public static JSONArray getAnalytics() {
        JSONArray jsonArray = new JSONArray();
        Cursor cursor = null;
        try {
            cursor = BioscopeApp.getContext().getContentResolver().query(AnalyticProvider.CONTENT_URI_ANALYTIC_TABLE, null, null, null, null);
            if (null != cursor) {
                while (cursor.moveToNext()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("analyticsID", cursor.getString(cursor.getColumnIndex(AnalyticProvider.COLUMNS.ANALYTIC_ID)));
                    jsonObject.put("assetID", cursor.getString(cursor.getColumnIndex(AnalyticProvider.COLUMNS.ASSET_ID)));
                    jsonObject.put("fleetID", cursor.getString(cursor.getColumnIndex(AnalyticProvider.COLUMNS.FLEET_ID)));
                    jsonObject.put("routeID", cursor.getString(cursor.getColumnIndex(AnalyticProvider.COLUMNS.ROUTE_ID)));
                    jsonObject.put("companyID", cursor.getString(cursor.getColumnIndex(AnalyticProvider.COLUMNS.COMPANY_ID)));
                    jsonObject.put("dateTime", cursor.getString(cursor.getColumnIndex(AnalyticProvider.COLUMNS.PLAYED_TIME)));
                    jsonArray.put(jsonObject);
                }
            }

        } catch (Exception e) {
            Logger.error(TAG, "getAnalytics() :: Exception ", e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        Logger.debug(TAG, "getAnalytics () :: jsonArray " + jsonArray);
        return jsonArray;
    }

    public static int delete(String analyticId) {
        String selection = AnalyticProvider.COLUMNS.ANALYTIC_ID + " = ?";
        String[] selectionArg = new String[]{"" + analyticId};
        int count = BioscopeApp.getContext().getContentResolver().delete(AnalyticProvider.CONTENT_URI_ANALYTIC_TABLE, selection, selectionArg);
        return count;
    }
}

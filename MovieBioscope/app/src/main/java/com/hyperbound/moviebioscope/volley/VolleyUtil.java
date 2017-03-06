package com.hyperbound.moviebioscope.volley;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hyperbound.moviebioscope.app.BioscopeApp;
import com.hyperbound.moviebioscope.util.AnalyticUtil;
import com.hyperbound.moviebioscope.util.AppInterface;
import com.hyperbound.moviebioscope.util.AppTaskHandler;
import com.hyperbound.moviebioscope.util.NetworkUtil;
import com.lib.location.LocationApplication;
import com.lib.location.util.LocationInterface;
import com.lib.location.volley.LocationHandler;
import com.lib.utility.util.CustomIntent;
import com.lib.utility.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aarokiax on 2/15/2017.
 */

public class VolleyUtil {
    private static final String TAG = "VolleyUtil";

    /**
     * Method to get all the data with shopid
     */
    public static void getBusDetails(String busNo) {
        String lUrl = AppInterface.BASE_URL + AppInterface.BUS_REGISTRATION_API;
        try {
            final JSONObject requestJson = new JSONObject();
            requestJson.put(AppInterface.BUS_REG_NO_KEY, busNo);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, lUrl, requestJson, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "getBusDetails() :: onResponse() ::" + response);
                            try {
                                String code = response.getString("code");
                                if (null != code && code.equals("SUCC01")) {
                                    Bundle lBundle = new Bundle();
                                    lBundle.putString("data", response.toString());
                                    Message msg = Message.obtain();
                                    msg.what = AppInterface.HANDLE_BUS_DETAILS;
                                    msg.setData(lBundle);
                                    AppTaskHandler.getInstance().sendMessage(msg);
                                } else {
                                    LocalBroadcastManager.getInstance(BioscopeApp.getContext()).sendBroadcast(new Intent(CustomIntent.ACTION_INVALID_BUS_NUMBER));
                                }
                                Log.i(TAG, "Response : " + response.toString());
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "getBusDetails() :: onErrorResponse() ::" + error);

                        }
                    });
            VolleySingleton.getInstance(LocationApplication.getLocationContext()).addToRequestQueue(jsObjRequest);
        } catch (JSONException je) {
            Log.e(TAG, "JsonException", je);
        }
    }

    /**
     * Method to sync analytics data to cloud
     */
    public static void syncAnalyticDataIfRequired() {
        Log.d(TAG, "syncAnalyticDataIfRequired() :: called");
        if (NetworkUtil.isInternetAvailable(BioscopeApp.getContext())) {
            String lUrl = AppInterface.BASE_URL + AppInterface.ANALYTIC_API;
            JSONArray jsonArray = AnalyticUtil.getAnalytics();
            JsonArrayRequest jsObjRequest = new JsonArrayRequest
                    (Request.Method.POST, lUrl, jsonArray, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d(TAG, "syncAnalyticDataIfRequired() :: onResponse() ::" + response);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "syncAnalyticDataIfRequired() :: onErrorResponse() ::" + error);

                        }
                    });
            VolleySingleton.getInstance(LocationApplication.getLocationContext()).addToRequestQueue(jsObjRequest);
        } else {
            Logger.debug(TAG, "no internet connection ");
        }
    }


    public static void getTravelInfo(String source, String destination) {
        String lUrl = LocationInterface.MATRIX_API_BASE_URL + LocationInterface.ORIGIN_KEY + source
                + LocationInterface.DESTINATION_KEY + destination + LocationInterface.API_KEY + LocationInterface.DISTANCE_MATRIX_API_KEY;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, lUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "getLocationInfo() :: onResponse() ::" + response);
                        try {
                            Bundle lBundle = new Bundle();
                            lBundle.putString("data", response.toString());
                            Message msg = Message.obtain();
                            msg.what = LocationInterface.HANDLE_JOURNEY_INFO;
                            msg.setData(lBundle);
                            LocationHandler.getInstance().sendMessage(msg);
                            Log.i(TAG, "Response : " + response.toString());
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "getAllData() :: onErrorResponse() ::" + error);


                    }
                });
        VolleySingleton.getInstance(LocationApplication.getLocationContext()).addToRequestQueue(jsObjRequest);
    }
}

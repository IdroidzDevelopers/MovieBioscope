package com.app.navajhalaka.volley;

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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.app.navajhalaka.app.BioscopeApp;
import com.app.navajhalaka.model.AnalyticData;
import com.app.navajhalaka.model.AnalyticResponse;
import com.app.navajhalaka.util.AnalyticUtil;
import com.app.navajhalaka.util.AppInterface;
import com.app.navajhalaka.util.AppTaskHandler;
import com.app.navajhalaka.util.NetworkUtil;
import com.lib.location.LocationApplication;
import com.lib.location.util.LocationInterface;
import com.lib.location.volley.LocationHandler;
import com.lib.utility.util.CustomIntent;
import com.lib.utility.util.Logger;
import com.lib.videoplayer.object.PushData;

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
                            try {
                                GsonBuilder gsonBuilder = new GsonBuilder();
                                Gson gson = gsonBuilder.create();
                                AnalyticResponse[] object = gson.fromJson(response.toString(), AnalyticResponse[].class);
                                if (null != object && "SUCC03".equals(object[0].getCode())) {
                                    for (AnalyticData data : object[0].getData()) {
                                        int count = AnalyticUtil.delete(data.getAnalyticsID());
                                        Log.d(TAG, "syncAnalyticDataIfRequired() :: delete() :: count " + count);
                                    }
                                }
                            } catch (Exception e) {
                                Logger.error(TAG, "syncAnalyticDataIfRequired:: onResponse():: exception ", e);
                            }
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

    public static void sendCommandAcknowledgement(String fleetId,String transactionId,String assetId,String status){
        String lUrl = AppInterface.BASE_URL + AppInterface.COMMAND_ACK_API;
        try {
            JSONArray requestArray=new JSONArray();
            final JSONObject requestJson = new JSONObject();
            requestJson.put(AppInterface.FLEET_ID_KEY, fleetId);
            requestJson.put(AppInterface.TRANSACTION_ID_KEY, transactionId);
            if(null!=assetId) {
                requestJson.put(AppInterface.ASSET_ID_KEY, assetId);
            }
            requestJson.put(AppInterface.STATUS_KEY, status);
            requestArray.put(requestJson);
            JsonArrayRequest jsObjRequest = new JsonArrayRequest
                    (Request.Method.POST, lUrl, requestArray, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d(TAG, "sendCommandAcknowledgement() :: onResponse() ::" + response);
                            try {
                                JSONObject responseData=new JSONObject(response.get(0).toString());
                                String code = responseData.getString("code");
                                if (null != code && code.equals("SUCC03")) {
                                    Log.i(TAG,"Acknowledgement success");
                                }
                                Log.i(TAG, "Response : " + response.toString());
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "sendCommandAcknowledgement() :: onErrorResponse() ::" + error);

                        }
                    });
            VolleySingleton.getInstance(BioscopeApp.getContext()).addToRequestQueue(jsObjRequest);
        } catch (JSONException je) {
            Log.e(TAG, "JsonException", je);
        }
    }
}

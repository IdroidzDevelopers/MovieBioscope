package com.lib.location.util;


import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lib.location.R;

public class GoogleMapDistanceMatrix {

    private static final String TAG = GoogleMapDistanceMatrix.class.getSimpleName();

    public static void requestForData(Context aContext, String aSource, String aDest) {
        if (NetworkUtil.isInternetAvailable(aContext)) {
            String lUrl = aContext.getString(R.string.distance_matrix_api) + aSource + "&destinations=" + aDest + "&key=" + aContext.getString(R.string.google_api_key);
            RequestQueue queue = Volley.newRequestQueue(aContext.getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, lUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            Log.i(TAG, "Response is: " + response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG, "error " + error);
                }
            });
// Add the request to the RequestQueue.
            queue.add(stringRequest);
        } else {

        }
    }
}

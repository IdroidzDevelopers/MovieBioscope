package com.lib.location;

import android.content.Context;

/**
 * Created by aarokiax on 2/13/2017.
 */

public class LocationApplication {

    private static Context locationContext;

    public static Context getLocationContext(){
        return locationContext;
    }

    public static void setLocationContext(Context context){
        locationContext=context;
    }
}

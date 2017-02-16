package com.lib.route;

import android.content.Context;

/**
 * Created by aarokiax on 2/17/2017.
 */

public class RouteApplication {

    private static Context routeContext;

    public static Context getRouteContext() {
        return routeContext;
    }

    public static void setRouteContext(Context context) {
        routeContext = context;
    }
}

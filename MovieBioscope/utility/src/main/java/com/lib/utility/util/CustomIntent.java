package com.lib.utility.util;


public class CustomIntent {
    public static final String ACTION_ROUTE_CHANGED = "com.android.action.LOCATION_CHANGED";
    public static final String ACTION_CURRENT_LOCATION_CHANGED = "com.android.action.CURRENT_LOCATION_CHANGED";
    public static final String ACTION_LOCATION_INFO_CHANGED = "com.android.action.LOCATION_INFO_CHANGED";
    public static final String ACTION_JOURNEY_INFO_CHANGED = "com.android.action.JOURNEY_INFO_CHANGED";
    public static final String ACTION_ROUTE_RECEIVED = "com.android.action.ROUTE_RECEIVED";
    public static final String ACTION_INVALID_BUS_NUMBER = "com.android.action.INVALID_BUS_NUMBER";
    public static final String ACTION_VIDEO_DATA_RECEIVED = "com.android.action.VIDEO_DATA_RECEIVED";
    public static final String ACTION_MEDIA_DOWNLOAD_COMPLETE = "com.android.action.MEDIA_DOWNLOAD_COMPLETE";
    public static final String ACTION_ROUTE_IMAGE_DOWNLOAD_COMPLETE = "com.android.action.ROUTE_IMAGE_DOWNLOAD_COMPLETE";
    public static final String ACTION_MOVIE_LIST = "com.android.action.MOVIE_LIST";

    public interface EXTRAS {
        String URI_KEY = "uri";
        String VIDEO_STATE = "video_state";
        String VIDEO_ID = "video_id";
        String TYPE = "type";
        String ROUTE_ID = "route_id";
    }
}

package com.hyperbound.moviebioscope.util;


import android.text.format.DateFormat;
import android.util.Log;

import java.util.Calendar;

public class TimeUtil {
    private static final String TAG = TimeUtil.class.getSimpleName();
    private static final boolean DEBUG = true;

    /**
     * Method to get the time in am/pm
     *
     * @return time
     */
    public static String getTime() {
        String delegate = "hh:mm aaa";
        String lTime = (String) DateFormat.format(delegate, Calendar.getInstance().getTime());
        if (DEBUG) Log.d(TAG, "getTime() " + lTime);
        return lTime;
    }
}

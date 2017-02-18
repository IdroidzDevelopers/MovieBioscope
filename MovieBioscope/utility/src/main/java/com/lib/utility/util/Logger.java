package com.lib.utility.util;


import android.util.Log;

public class Logger {
    private static final boolean DEBUG = true;

    public static void debug(String tag, String message) {
        if (DEBUG) Log.d(tag, message);
    }

    public static void info(String tag, String message) {
        Log.i(tag, message);
    }

    public static void error(String tag, String message) {
        Log.e(tag, message);
    }

    public static void error(String tag, String message, Throwable tr) {
        Log.e(tag, message, tr);
    }
}

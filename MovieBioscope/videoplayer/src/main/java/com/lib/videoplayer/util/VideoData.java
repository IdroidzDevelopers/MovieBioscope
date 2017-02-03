package com.lib.videoplayer.util;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.lib.videoplayer.database.VideoProvider;

public class VideoData {
    private static final String TAG = VideoData.class.getSimpleName();

    public static Uri getRandomMovieUri(Context aContext) {
        Uri lUri = null;
        if (null != aContext) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ?";
            String[] lSelectionArg = {""+VideoProvider.VIDEO_TYPE.MOVIE };
            Cursor lCursor = null;
            try {
                lCursor = aContext.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE,null,lSelection,lSelectionArg,null);
                while (null != lCursor && lCursor.moveToNext()) {
                    String lValue = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PATH));
                    lUri = Uri.parse(lValue);
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception :: getRandomMovieUri() :: ", e);
            } finally {
                if (null != lCursor && !lCursor.isClosed()) {
                    lCursor.close();
                }
            }
        }
        return lUri;
    }

    public static Uri getRandomAdUri(Context aContext) {
        Uri lUri = null;
        if (null != aContext) {
            String lSelection = VideoProvider.VIDEO_COLUMNS.TYPE + "= ?";
            String[] lSelectionArg ={""+VideoProvider.VIDEO_TYPE.ADV };
            Cursor lCursor = null;
            try {
                lCursor = aContext.getContentResolver().query(VideoProvider.CONTENT_URI_VIDEO_TABLE,null,lSelection,lSelectionArg,null);
                while (null != lCursor && lCursor.moveToNext()) {
                    String lValue = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_COLUMNS.PATH));
                    lUri = Uri.parse(lValue);
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception :: getRandomMovieUri() :: ", e);
            } finally {
                if (null != lCursor && !lCursor.isClosed()) {
                    lCursor.close();
                }
            }
        }
        return lUri;
    }

    public static long getNextAdTime() {
        return 30 * 1000;
    }
}

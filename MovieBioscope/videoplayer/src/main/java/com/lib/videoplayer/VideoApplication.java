package com.lib.videoplayer;

import android.content.Context;

/**
 * Created by aarokiax on 2/24/2017.
 */

public class VideoApplication {

    private static Context videoContext;

    public static Context getVideoContext() {
        return videoContext;
    }

    public static void setVideoContext(Context videoContext) {
        VideoApplication.videoContext = videoContext;
    }
}

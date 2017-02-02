package com.lib.videoplayer.util;


import android.net.Uri;
import android.os.Environment;

import java.net.URL;

public class VideoData {
    public static Uri getRandomMovieUri() {
        return Uri.parse(Environment.getExternalStorageDirectory() + "/movie_bioscope/Spotlight.mp4");
    }

    public static Uri getRandomAdUri() {
        return Uri.parse(Environment.getExternalStorageDirectory() + "/movie_bioscope/test_ad.mp4");
    }

}

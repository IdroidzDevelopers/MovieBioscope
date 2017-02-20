package com.lib.videoplayer.util;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.lib.firebase.util.FirebaseUtil;


public class VideoTaskHandler extends Handler {
    private static final String TAG = VideoTaskHandler.class.getSimpleName();
    private static Context sContext;

    public interface TASK {
        int INIT_VIDEO_DATA = 1;
    }

    private static VideoTaskHandler sInstance;


    private VideoTaskHandler(Looper looper) {
        super(looper);
    }

    public static VideoTaskHandler getInstance(Context aContext) {
        sContext = aContext;
        if (null == sInstance) {
            synchronized (VideoTaskHandler.class) {
                if (null == sInstance) {
                    HandlerThread lThread = new HandlerThread(TAG);
                    lThread.start();
                    sInstance = new VideoTaskHandler(lThread.getLooper());
                }
            }
        }
        return sInstance;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case TASK.INIT_VIDEO_DATA:
                break;
        }
    }
}

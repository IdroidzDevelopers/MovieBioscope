package com.hyperbound.moviebioscope.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.hyperbound.moviebioscope.app.BioscopeApp;
import com.lib.route.util.RouteUtil;


public class AppTaskHandler extends Handler {
    private static final String TAG = AppTaskHandler.class.getSimpleName();

    public interface TASK {
        int SAVE_BUS_DATA = 0;
    }

    public interface KEY {
        String REG_NUMBER = "reg_number";
    }

    private static AppTaskHandler sInstance;


    private AppTaskHandler(Looper looper) {
        super(looper);
    }

    public static AppTaskHandler getInstance() {
        if (null == sInstance) {
            synchronized (AppTaskHandler.class) {
                if (null == sInstance) {
                    HandlerThread lThread = new HandlerThread(TAG);
                    lThread.start();
                    sInstance = new AppTaskHandler(lThread.getLooper());
                }
            }
        }
        return sInstance;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Bundle lBundle = msg.getData();
        switch (msg.what) {
            case TASK.SAVE_BUS_DATA:
                if (null != lBundle) {
                    String lRegNumber = (String) lBundle.get(KEY.REG_NUMBER);
                    BusUtil.saveRegistrationDetail(BioscopeApp.getContext(), lRegNumber);
                }
                break;
        }
    }
}

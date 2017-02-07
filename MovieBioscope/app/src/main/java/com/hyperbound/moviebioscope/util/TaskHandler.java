package com.hyperbound.moviebioscope.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.hyperbound.moviebioscope.app.BioscopeApp;


public class TaskHandler extends Handler {
    private static final String TAG = TaskHandler.class.getSimpleName();

    public interface TASK {
        int SAVE_BUS_DATA = 0;
        int SEND_BUS_DATA_TO_CLOUD = 2;
    }

    public interface KEY {
        String REG_NUMBER = "reg_number";
    }

    private static TaskHandler sInstance;


    private TaskHandler(Looper looper) {
        super(looper);
    }

    public static TaskHandler getInstance() {
        if (null == sInstance) {
            synchronized (TaskHandler.class) {
                if (null == sInstance) {
                    HandlerThread lThread = new HandlerThread(TAG);
                    lThread.start();
                    sInstance = new TaskHandler(lThread.getLooper());
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

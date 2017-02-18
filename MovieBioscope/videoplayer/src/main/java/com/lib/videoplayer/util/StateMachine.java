package com.lib.videoplayer.util;


import android.util.Log;

public class StateMachine {
    private static final String TAG = StateMachine.class.getSimpleName();
    private static final boolean DEBUG = true;
    private static StateMachine sInstance;

    public int mVideoState;
    public int mPrevState;
    public int mCurrentState;


    /**
     * states either only adv or movie+adv
     */
    public interface VIDEO_STATE {
        int NONE = 0;
        int ONLY_ADV = 1;
        int MOVIE_AND_ADV = 2;
    }

    /**
     * video playing state
     */
    public interface PLAYING_STATE {
        int NONE = 0;
        int TRAVEL_VIDEO = 1;
        int SAFETY_VIDEO = 2;
        int MOVIE = 3;
        int ADV = 4;
        int BREAKING_VIDEO = 5;
        int BREAKING_TEXT = 6;
        int PAUSED = 7;
    }

    private StateMachine() {
        mVideoState = VIDEO_STATE.NONE;
        mPrevState = PLAYING_STATE.NONE;
        mCurrentState = PLAYING_STATE.NONE;
    }

    /**
     * create a singleton instance
     *
     * @return instance of state machine
     */
    public static StateMachine getInstance() {
        if (null == sInstance) {
            synchronized (StateMachine.class) {
                if (null == sInstance) {
                    sInstance = new StateMachine();
                }
            }
        }
        return sInstance;
    }

    public synchronized void changeState(int aPrevState, int aCurrentState) {
        if (DEBUG)
            Log.d(TAG, "changeState() :: aPrevState:" + aPrevState + " " + getName(aPrevState) + " aCurrentState:" + aCurrentState + " " + getName(aCurrentState));
        mPrevState = aPrevState;
        mCurrentState = aCurrentState;
    }

    public synchronized void setVideoState(int aState) {
        if (DEBUG)
            Log.d(TAG, "setVideoState() :: aState:" + getStateName(aState));
        mVideoState = aState;
    }

    private String getName(int aState) {
        switch (aState) {
            case PLAYING_STATE.NONE:
                return "NONE";
            case PLAYING_STATE.TRAVEL_VIDEO:
                return "TRAVEL_VIDEO";
            case PLAYING_STATE.SAFETY_VIDEO:
                return "SAFETY_VIDEO";
            case PLAYING_STATE.MOVIE:
                return "MOVIE";
            case PLAYING_STATE.ADV:
                return "ADV";
            case PLAYING_STATE.BREAKING_VIDEO:
                return "BREAKING_VIDEO";
            case PLAYING_STATE.BREAKING_TEXT:
                return "BREAKING_NEWS";
            case PLAYING_STATE.PAUSED:
                return "PAUSED";
            default:
                return "UNKNOWN";
        }
    }

    private String getStateName(int aState) {
        switch (aState) {
            case VIDEO_STATE.NONE:
                return "VIDEO_STATE_NONE";
            case VIDEO_STATE.ONLY_ADV:
                return "ONLY_ADV";
            case VIDEO_STATE.MOVIE_AND_ADV:
                return "MOVIE_AND_ADV";
            default:
                return "UNKNOWN";
        }
    }

    public void print() {
        Log.d(TAG, "print :: mVideoState: " + mVideoState + " " + getStateName(mVideoState) + " mPrevState: " + mPrevState + " " + getName(mPrevState) + " mCurrentState: " + mCurrentState + " " + getName(mCurrentState));
    }

    public void reset() {
        mVideoState = VIDEO_STATE.NONE;
        mPrevState = PLAYING_STATE.NONE;
        mCurrentState = PLAYING_STATE.NONE;
    }
}

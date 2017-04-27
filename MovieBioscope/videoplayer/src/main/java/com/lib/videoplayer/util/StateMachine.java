package com.lib.videoplayer.util;


import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.lib.utility.util.Logger;
import com.lib.videoplayer.VideoApplication;
import com.lib.videoplayer.database.VideoProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StateMachine {
    private static final String TAG = StateMachine.class.getSimpleName();
    private static final boolean DEBUG = true;
    private static StateMachine sInstance;
    public VideoInfo videoInfo;

    /**
     * states either only adv or movie+adv
     */
    public interface VIDEO_STATE {
        int NONE = 0;
        int ONLY_ADV = 1;
        int MOVIE_AND_ADV = 2;
    }


    /**
     * states either only adv or movie+adv
     */
    public interface SEQUENCE_TYPE {
        int LANDING_TYPE = 1;
        int MOVIE_INIT_TYPE = 2;
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
        int INTRO_VIDEO = 8;
        int MOVIE_FINISHED = 9;
        int COMPANY_AD = 10;
        int LANDING_VIDEO = 11;
    }


    public int getState(String type) {
        switch (type) {
            case VideoProvider.VIDEO_TYPE.TRAVELER_VIDEO:
                return PLAYING_STATE.TRAVEL_VIDEO;
            case VideoProvider.VIDEO_TYPE.SAFETY_VIDEO:
                return PLAYING_STATE.SAFETY_VIDEO;
            case VideoProvider.VIDEO_TYPE.MOVIE:
                return PLAYING_STATE.MOVIE;
            case VideoProvider.VIDEO_TYPE.ADV:
                return PLAYING_STATE.ADV;
            case VideoProvider.VIDEO_TYPE.BREAKING_VIDEO:
                return PLAYING_STATE.BREAKING_VIDEO;
            case VideoProvider.VIDEO_TYPE.BREAKING_NEWS:
                return PLAYING_STATE.BREAKING_TEXT;
            case VideoProvider.VIDEO_TYPE.INTRO_VIDEO:
                return PLAYING_STATE.INTRO_VIDEO;
            case VideoProvider.VIDEO_TYPE.COMPANY_AD:
                return PLAYING_STATE.COMPANY_AD;
            case VideoProvider.VIDEO_TYPE.LANDING_VIDEO:
                return PLAYING_STATE.LANDING_VIDEO;
            default:
                return PLAYING_STATE.NONE;


        }
    }

    /**
     * Sub class to store the video information
     */
    public static class VideoInfo {
        private String videoId;
        private String prevVideoId;
        private int videoState;
        private int prevState;
        private int currentState;
        private Uri movieUri;
        private int movieSeekTime;
        private Uri otherUri;
        private int otherSeekTime;
        private Uri breakingUri;
        private int breakingSeekTime;
        private List<Integer> mPausedStates = new ArrayList<Integer>();

        public List<Integer> getPausedState() {
            return mPausedStates;
        }

        public void addPausedState(Integer mPausedState) {
            mPausedStates.add(mPausedState);
        }

        public void setPausedState(List<Integer> aPausedStates) {
            this.mPausedStates = aPausedStates;
        }

        public Integer getTopPausedState() {
            if (mPausedStates.size() == 0) {
                return PLAYING_STATE.NONE;
            } else {
                int topState = mPausedStates.get(mPausedStates.size() - 1);
                return topState;
            }
        }

        public void removeTopState() {
            mPausedStates.remove(mPausedStates.size() - 1);
            sInstance.updatePauseState();
        }

        public Uri getBreakingUri() {
            return breakingUri;
        }

        public void setBreakingUri(Uri breakingUri) {
            this.breakingUri = breakingUri;
        }

        public int getBreakingSeekTime() {
            return breakingSeekTime;
        }

        public void setBreakingSeekTime(int breakingSeekTime) {
            this.breakingSeekTime = breakingSeekTime;
        }

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

        public String getPrevVideoId() {
            return prevVideoId;
        }

        public void setPrevVideoId(String prevVideoId) {
            this.prevVideoId = prevVideoId;
        }


        public int getVideoState() {
            synchronized (StateMachine.class) {
                return videoState;
            }
        }

        public void setVideoState(int videoState) {
            synchronized (StateMachine.class) {
                this.videoState = videoState;
            }
        }

        public int getPrevState() {
            synchronized (StateMachine.class) {
                return prevState;
            }
        }

        public void setPrevState(int prevState) {
            synchronized (StateMachine.class) {
                this.prevState = prevState;
            }
        }

        public int getCurrentState() {
            synchronized (StateMachine.class) {
                return currentState;
            }
        }

        public void setCurrentState(int currentState) {
            synchronized (StateMachine.class) {
                this.currentState = currentState;
            }
        }

        public Uri getMovieUri() {
            return movieUri;
        }

        public void setMovieUri(Uri movieUri) {
            this.movieUri = movieUri;
        }

        public int getMovieSeekTime() {
            return movieSeekTime;
        }

        public void setMovieSeekTime(int movieSeekTime) {
            this.movieSeekTime = movieSeekTime;
        }

        public Uri getOtherUri() {
            return otherUri;
        }

        public void setOtherUri(Uri otherUri) {
            this.otherUri = otherUri;
        }

        public int getOtherSeekTime() {
            return otherSeekTime;
        }

        public void setOtherSeekTime(int otherSeekTime) {
            this.otherSeekTime = otherSeekTime;
        }

        @Override
        public String toString() {
            return "VideoInfo{" +
                    "videoState=" + videoState +
                    ", prevState=" + prevState +
                    ", currentState=" + currentState +
                    ", movieUri=" + movieUri +
                    ", movieSeekTime=" + movieSeekTime +
                    ", otherUri=" + otherUri +
                    ", otherSeekTime=" + otherSeekTime +
                    '}';
        }

        public void updateVideoId(String assetID) {
            prevVideoId = videoId;
            videoId = assetID;
        }
    }

    private StateMachine() {
        videoInfo = new VideoInfo();
        videoInfo.videoState = VIDEO_STATE.NONE;
        videoInfo.prevState = PLAYING_STATE.NONE;
        videoInfo.currentState = PLAYING_STATE.NONE;
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
            Log.d(TAG, "--(debug)-- :: changeState() :: aPrevState:" + aPrevState + " " + getName(aPrevState) + " aCurrentState:" + aCurrentState + " " + getName(aCurrentState));
        synchronized (StateMachine.class) {
            videoInfo.prevState = aPrevState;
            videoInfo.currentState = aCurrentState;
        }
    }

    public synchronized void setVideoState(int aState) {
        if (DEBUG)
            Log.d(TAG, "setVideoState() :: aState:" + getStateName(aState));
        synchronized (StateMachine.class) {
            videoInfo.videoState = aState;
        }
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
            case PLAYING_STATE.INTRO_VIDEO:
                return "INTRO_VIDEO";
            case PLAYING_STATE.MOVIE_FINISHED:
                return "MOVIE_FINISHED";
            case PLAYING_STATE.COMPANY_AD:
                return "COMPANY_AD";
            case PLAYING_STATE.LANDING_VIDEO:
                return "LANDING_VIDEO";
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
        Log.d(TAG, "--(debug)-- print :: mVideoState: " + videoInfo.videoState + " " + getStateName(videoInfo.videoState) + " mPrevState: " + videoInfo.prevState + " " + getName(videoInfo.prevState) + " mCurrentState: " + videoInfo.currentState + " " + getName(videoInfo.currentState));
    }

    public void reset() {
        videoInfo.prevState = PLAYING_STATE.NONE;
        videoInfo.currentState = PLAYING_STATE.NONE;
        videoInfo.movieUri = null;
        videoInfo.movieSeekTime = 0;
        videoInfo.otherUri = null;
        videoInfo.otherSeekTime = 0;
        videoInfo.breakingUri = null;
        videoInfo.breakingSeekTime = 0;
        videoInfo.mPausedStates = new ArrayList<Integer>();
    }

    public void updatePauseState() {
        if (0 == videoInfo.mPausedStates.size()) {
            Log.d(TAG, "updatePauseState :: not required ");
            return;
        }
        String selection = VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.VIDEO_STATE + " = ?";
        String[] selectionArg = new String[]{"" + videoInfo.videoState};
        ContentValues lValues = new ContentValues();
        lValues.put(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.PAUSED_STATES, convertListToString(videoInfo.getPausedState()));
        int count = VideoApplication.getVideoContext().getContentResolver().update(VideoProvider.CONTENT_URI_INTERMEDIATE_VIDEO_STATE, lValues, selection, selectionArg);
        Log.d(TAG, "updatePauseState() :: count :: " + count);
    }

    private String convertListToString(List<Integer> list) {
        StringBuilder builder = new StringBuilder();

        for (Integer s : list) {
            builder.append(s);
            builder.append(",");
        }

        String csv = builder.toString();
        if (csv.endsWith(",")) {
            csv = csv.substring(0, csv.length() - ",".length());
        }
        return csv;
    }

    private List<Integer> convertStringToList(String value) {
        List<Integer> list = new ArrayList<Integer>();
        if (null != value) {
            for (String s : Arrays.asList(value.split(","))) {
                list.add(Integer.parseInt(s));
            }
        }
        return list;
    }


    public void persistState(long movieSeekTime, long mOtherSeekTime, long breakingSeekTime) {
        String selection = VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.VIDEO_STATE + " = ?";
        String[] selectionArg = new String[]{"" + videoInfo.videoState};
        ContentValues lValues = new ContentValues();
        lValues.put(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.VIDEO_STATE, videoInfo.videoState);
        lValues.put(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.PREV_STATE, videoInfo.prevState);
        lValues.put(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.CURRENT_STATE, videoInfo.currentState);
        if (null != videoInfo.movieUri) {
            lValues.put(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.MOVIE_URI, videoInfo.movieUri.toString());
        }
        if (0 != movieSeekTime) {
            lValues.put(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.MOVIE_SEEK_TIME, movieSeekTime);
        }
        if (null != videoInfo.otherUri) {
            lValues.put(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.OTHER_URI, videoInfo.otherUri.toString());
        }
        if (0 != mOtherSeekTime) {
            lValues.put(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.OTHER_SEEK_TIME, mOtherSeekTime);
        }
        if (null != videoInfo.breakingUri) {
            lValues.put(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.BREAKING_URI, videoInfo.breakingUri.toString());
        }
        if (0 != breakingSeekTime) {
            lValues.put(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.BREAKING_SEEK_TIME, breakingSeekTime);
        }
        if (0 != videoInfo.mPausedStates.size()) {
            lValues.put(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.PAUSED_STATES, convertListToString(videoInfo.mPausedStates));
        }
        int count = VideoApplication.getVideoContext().getContentResolver().update(VideoProvider.CONTENT_URI_INTERMEDIATE_VIDEO_STATE, lValues, selection, selectionArg);
        if (count == 0) {
            Uri uri = VideoApplication.getVideoContext().getContentResolver().insert(VideoProvider.CONTENT_URI_INTERMEDIATE_VIDEO_STATE, lValues);
            Log.d(TAG, "persistState() :: uri :: " + uri);
        } else {
            Log.d(TAG, "persistState() :: count :: " + count);
        }
    }

    public static void deletePersistState(int videoState) {
        String selection = VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.VIDEO_STATE + " = ?";
        String[] selectionArg = new String[]{"" + videoState};
        try {
            int count = VideoApplication.getVideoContext().getContentResolver().delete(VideoProvider.CONTENT_URI_INTERMEDIATE_VIDEO_STATE, selection, selectionArg);
            Logger.debug(TAG, "deletePersistState() :: Count :: " + count);
        } catch (Exception e) {
            Logger.error(TAG, "Exception :: deletePersistState() :: ", e);
        }
    }


    public void retainPersistenceState(int videoState) {
        String lSelection = VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.VIDEO_STATE + "= ?";
        String[] lSelectionArg = {"" + videoState};
        Cursor lCursor = null;
        try {
            lCursor = VideoApplication.getVideoContext().getContentResolver().query(VideoProvider.CONTENT_URI_INTERMEDIATE_VIDEO_STATE, null, lSelection, lSelectionArg, null);
            if (null != lCursor && lCursor.getCount() > 0) {
                while (lCursor.moveToNext()) {
                    String value = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.VIDEO_STATE));
                    if (null != value) {
                        videoInfo.setVideoState(Integer.parseInt(value));
                    }
                    value = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.PREV_STATE));
                    if (null != value) {
                        videoInfo.setPrevState(Integer.parseInt(value));
                    }
                    value = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.CURRENT_STATE));
                    if (null != value) {
                        videoInfo.setCurrentState(Integer.parseInt(value));
                    }
                    value = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.MOVIE_URI));
                    if (null != value) {
                        videoInfo.setMovieUri(Uri.parse(value));
                    }
                    value = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.MOVIE_SEEK_TIME));
                    if (null != value) {
                        videoInfo.setMovieSeekTime(Integer.parseInt(value));
                    }
                    value = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.OTHER_URI));
                    if (null != value) {
                        videoInfo.setOtherUri(Uri.parse(value));
                    }
                    value = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.OTHER_SEEK_TIME));
                    if (null != value) {
                        videoInfo.setOtherSeekTime(Integer.parseInt(value));
                    }
                    value = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.BREAKING_URI));
                    if (null != value) {
                        videoInfo.setBreakingUri(Uri.parse(value));
                    }
                    value = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.BREAKING_SEEK_TIME));
                    if (null != value) {
                        videoInfo.setBreakingSeekTime(Integer.parseInt(value));
                    }
                    value = lCursor.getString(lCursor.getColumnIndex(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.PAUSED_STATES));
                    if (null != value) {
                        videoInfo.setPausedState(convertStringToList(value));
                    }
                    break;
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception :: retainPersistenceState() :: ", e);
        } finally {
            if (null != lCursor && !lCursor.isClosed()) {
                lCursor.close();
            }

        }
    }
}

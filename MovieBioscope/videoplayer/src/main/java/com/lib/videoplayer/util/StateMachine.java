package com.lib.videoplayer.util;


import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.lib.videoplayer.VideoApplication;
import com.lib.videoplayer.database.VideoProvider;

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
        int INTRO_VIDEO = 8;
        int MOVIE_FINISHED = 9;
        int COMPANY_AD = 10;
    }

    /**
     * Sub class to store the video information
     */
    public static class VideoInfo {
        private String videoId;
        private int videoState;
        private int prevState;
        private int currentState;
        private Uri movieUri;
        private int movieSeekTime;

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

        private Uri otherUri;
        private int otherSeekTime;
        private String prevVideoId;

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
            Log.d(TAG, "changeState() :: aPrevState:" + aPrevState + " " + getName(aPrevState) + " aCurrentState:" + aCurrentState + " " + getName(aCurrentState));
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
            case PLAYING_STATE.PAUSED:
                return "PAUSED";
            case PLAYING_STATE.INTRO_VIDEO:
                return "INTRO_VIDEO";
            case PLAYING_STATE.MOVIE_FINISHED:
                return "MOVIE_FINISHED";
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
        Log.d(TAG, "print :: mVideoState: " + videoInfo.videoState + " " + getStateName(videoInfo.videoState) + " mPrevState: " + videoInfo.prevState + " " + getName(videoInfo.prevState) + " mCurrentState: " + videoInfo.currentState + " " + getName(videoInfo.currentState));
    }

    public void reset() {
        videoInfo.prevState = PLAYING_STATE.NONE;
        videoInfo.currentState = PLAYING_STATE.NONE;
        videoInfo.movieUri = null;
        videoInfo.movieSeekTime = 0;
        videoInfo.otherUri = null;
        videoInfo.otherSeekTime = 0;
    }

    public void persistState(long movieSeekTime, long mOtherSeekTime) {
        String selection = VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.VIDEO_STATE + " = ?";
        String[] selectionArg = new String[]{"" + videoInfo.videoState};
        ContentValues lValues = new ContentValues();
        lValues.put(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.VIDEO_STATE, videoInfo.videoState);
        lValues.put(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.PREV_STATE, videoInfo.prevState);
        lValues.put(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.CURRENT_STATE, videoInfo.currentState);
        if (null != videoInfo.movieUri) {
            lValues.put(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.MOVIE_URI, videoInfo.movieUri.toString());
        }
        lValues.put(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.MOVIE_SEEK_TIME, movieSeekTime);
        if (null != videoInfo.otherUri) {
            lValues.put(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.OTHER_URI, videoInfo.otherUri.toString());
        }
        lValues.put(VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.OTHER_SEEK_TIME, mOtherSeekTime);
        int count = VideoApplication.getVideoContext().getContentResolver().update(VideoProvider.CONTENT_URI_INTERMEDIATE_VIDEO_STATE, lValues, selection, selectionArg);
        Log.d(TAG, "persistState() :: count :: " + count);
        if (count == 0) {
            Uri uri = VideoApplication.getVideoContext().getContentResolver().insert(VideoProvider.CONTENT_URI_INTERMEDIATE_VIDEO_STATE, lValues);
            Log.d(TAG, "persistState() :: uri :: " + uri);
        }
    }

    public static void deletePersistState(int videoState) {
        String selection = VideoProvider.VIDEO_INTERMEDIATE_COLUMNS.VIDEO_STATE + " = ?";
        String[] selectionArg = new String[]{"" + videoState};
        try {
            int count = VideoApplication.getVideoContext().getContentResolver().delete(VideoProvider.CONTENT_URI_INTERMEDIATE_VIDEO_STATE, selection, selectionArg);
            Log.d(TAG, "deletePersistState() :: Count :: " + count);
        } catch (Exception e) {
            Log.d(TAG, "Exception :: deletePersistState() :: ", e);
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
                    break;
                }
            } else {
                reset();
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

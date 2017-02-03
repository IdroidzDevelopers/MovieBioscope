package com.lib.videoplayer.ui;

import android.content.ContentValues;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.lib.videoplayer.R;
import com.lib.videoplayer.database.VideoProvider;
import com.lib.videoplayer.util.VideoData;

public class VideoActivity extends AppCompatActivity implements View.OnTouchListener {
    private static final String TAG = VideoActivity.class.getSimpleName();
    private Context mContext;
    private VideoView mMovieView;
    private VideoView mAdvView;
    private TextView mNoContentView;
    private int mVideoSeekTime;
    private RelativeLayout mHeader;
    private RelativeLayout mFooter;
    private Handler mHandler;

    /******************
     * Listeners
     ******************/
    private AdMediaController mAdMediaController;
    private AdsCompleteListener mAdsCompleteListener;
    private MovieCompleteListener mMovieCompleteListener;
    private MoviePrepareListener mMoviePrepareListener;
    private MovieSeekListener mMovieSeekListener;
    private AdPrepareListener mAdPrepareListener;
    private AdSeekListener mAdSeekListener;
    private View mLoading;
    private int mVideoState = VIDEO_STATE.NONE;
    private int mStopTime;


    public interface VIDEO_STATE {
        int NONE = -1;
        int MOVIE = 0;
        int AD = 1;

    }


    /*********************
     * Task constants
     *********************/

    public interface TASK_EVENT {
        int DISPLAY_LOCATION_INFO = 0;
        int HIDE_LOCATION_INFO = 1;
        int PLAY_MOVIE = 2;
        int PAUSE_MOVIE = 3;
        int PLAY_AD = 4;
        int STOP_AD = 5;
        int PREPARE_FOR_NEXT_AD = 6;

    }

    ;

    public int getVideoState() {
        return mVideoState;
    }

    public void setVideoState(int mVideoState) {
        this.mVideoState = mVideoState;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        //putDummyData();
        mHandler.sendEmptyMessage(TASK_EVENT.PLAY_MOVIE);
        mHandler.sendEmptyMessage(TASK_EVENT.PREPARE_FOR_NEXT_AD);
    }

    private void putDummyData() {
        ContentValues lValues = new ContentValues();
        lValues.put(VideoProvider.VIDEO_COLUMNS.NAME, "Spotlight");
        lValues.put(VideoProvider.VIDEO_COLUMNS.TYPE, VideoProvider.VIDEO_TYPE.MOVIE);
        lValues.put(VideoProvider.VIDEO_COLUMNS.PATH, "/storage/emulated/0/movie_bioscope/Spotlight.mp4");
        getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValues);

        ContentValues lValue = new ContentValues();
        lValue.put(VideoProvider.VIDEO_COLUMNS.NAME, "insipiration");
        lValue.put(VideoProvider.VIDEO_COLUMNS.TYPE, VideoProvider.VIDEO_TYPE.ADV);
        lValue.put(VideoProvider.VIDEO_COLUMNS.PATH, "/storage/emulated/0/movie_bioscope/test_ad.mp4");
        getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValue);
    }


    private void initNextAdTiming() {
        long lNextAdTime = VideoData.getNextAdTime();
        //remove ad if any
        mHandler.removeMessages(TASK_EVENT.PLAY_AD);
        mHandler.sendEmptyMessageDelayed(TASK_EVENT.PLAY_AD, lNextAdTime);
    }


    private void startMovie() {
        Uri lMovieUri = VideoData.getRandomMovieUri(mContext);
        if (null != lMovieUri) {
            mMovieView.setVisibility(View.VISIBLE);
            mMovieView.setVideoURI(lMovieUri);
            mMovieView.setMediaController(null);
            mMovieView.requestFocus();
            mMovieView.seekTo(5000000);
            mMovieView.start();
            setVideoState(VIDEO_STATE.MOVIE);
        } else {
            mNoContentView.setVisibility(View.VISIBLE);
        }
    }

    private void hideMovie() {
        mVideoSeekTime = mMovieView.getCurrentPosition();
        mMovieView.pause();
        mMovieView.setVisibility(View.GONE);
    }

    private void startAd() {
        mAdvView.setVisibility(View.VISIBLE);
        mAdvView.setVideoURI(VideoData.getRandomAdUri(mContext));
        mAdMediaController.show();
        mAdvView.setMediaController(mAdMediaController);
        mAdvView.requestFocus();
        mAdvView.start();
        setVideoState(VIDEO_STATE.AD);
    }

    /**
     * Initialize the layout and get the reference for the views
     */

    private void initView() {
        setContentView(R.layout.video_layout);
        mContext = this;
        mHeader = (RelativeLayout) findViewById(R.id.top_banner_video);
        mFooter = (RelativeLayout) findViewById(R.id.bottom_banner_video);
        mMovieView = (VideoView) findViewById(R.id.movie_view);
        mAdvView = (VideoView) findViewById(R.id.ad_video);
        mNoContentView = (TextView) findViewById(R.id.no_content);
        mLoading = findViewById(R.id.loading);
        mMovieView.setOnTouchListener(this);
        mAdvView.setOnTouchListener(this);
        mHandler = new TaskHandler();
        mMovieCompleteListener = new MovieCompleteListener();
        mAdsCompleteListener = new AdsCompleteListener();
        mMovieView.setOnCompletionListener(mMovieCompleteListener);
        mAdvView.setOnCompletionListener(mAdsCompleteListener);
        mAdMediaController = new AdMediaController(mContext);
        mMoviePrepareListener = new MoviePrepareListener();
        mMovieSeekListener = new MovieSeekListener();
        mAdPrepareListener = new AdPrepareListener();
        mAdSeekListener = new AdSeekListener();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoState != VIDEO_STATE.NONE) {
            mHandler.sendEmptyMessage(TASK_EVENT.PREPARE_FOR_NEXT_AD);
            showLoadingIcon();
            if (getVideoState() == VIDEO_STATE.MOVIE) {
                mMovieView.seekTo(mStopTime);
                mMovieView.start();
                mMovieView.setOnPreparedListener(mMoviePrepareListener);
            } else {
                mAdvView.seekTo(mStopTime);
                mAdvView.start();
                mAdvView.setOnPreparedListener(mAdPrepareListener);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeMessages(TASK_EVENT.PREPARE_FOR_NEXT_AD);
        mStopTime = (getVideoState() == VIDEO_STATE.MOVIE) ? mMovieView.getCurrentPosition() : mAdvView.getCurrentPosition();
        if (getVideoState() == VIDEO_STATE.MOVIE) {
            mMovieView.pause();
        } else {
            mAdvView.pause();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHandler.sendEmptyMessage(TASK_EVENT.DISPLAY_LOCATION_INFO);
                mHandler.sendEmptyMessageDelayed(TASK_EVENT.HIDE_LOCATION_INFO, 3 * 1000);
                break;

        }
        return false;
    }


    public class TaskHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TASK_EVENT.DISPLAY_LOCATION_INFO:
                    showLocationInfo();
                    break;
                case TASK_EVENT.HIDE_LOCATION_INFO:
                    hideLocationInfo();
                    break;
                case TASK_EVENT.PLAY_MOVIE:
                    startMovie();
                    break;
                case TASK_EVENT.PAUSE_MOVIE:

                    break;
                case TASK_EVENT.PLAY_AD:
                    hideMovie();
                    startAd();
                    break;
                case TASK_EVENT.STOP_AD:
                    break;
                case TASK_EVENT.PREPARE_FOR_NEXT_AD:
                    initNextAdTiming();
                    break;
                default:
                    break;

            }

        }
    }

    public class AdsCompleteListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            Log.d(TAG, "onCompletion ");
            showLoadingIcon();
            startSeekingMovie();
            mMovieView.setOnPreparedListener(mMoviePrepareListener);
            mHandler.sendEmptyMessage(TASK_EVENT.PREPARE_FOR_NEXT_AD);
        }
    }

    private void showLoadingIcon() {
        mLoading.setVisibility(View.VISIBLE);
        mLoading.bringToFront();
    }


    private void hideLoadingIcon() {
        mLoading.setVisibility(View.GONE);

    }

    private void startSeekingMovie() {
        mMovieView.setVisibility(View.VISIBLE);
        mMovieView.seekTo(mVideoSeekTime);
        mMovieView.start();
        setVideoState(VIDEO_STATE.MOVIE);

    }

    private void hideAdView() {
        mAdvView.setVisibility(View.GONE);
        mAdMediaController.hide();
    }

    /**
     *
     */
    public class MovieCompleteListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            //TODO:
        }
    }

    public class MoviePrepareListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            Log.d(TAG, "MovieSeekListener :: onPrepared() ");
            mediaPlayer.setOnSeekCompleteListener(mMovieSeekListener);
        }
    }

    public class MovieSeekListener implements OnSeekCompleteListener {

        @Override
        public void onSeekComplete(MediaPlayer mediaPlayer) {
            Log.d(TAG, "MovieSeekListener :: onSeekComplete() ");
            hideAdView();
            hideLoadingIcon();
        }
    }

    public class AdPrepareListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            //mediaPlayer.setOnSeekCompleteListener(mAdSeekListener);
        }
    }

    public class AdSeekListener implements OnSeekCompleteListener {
        @Override
        public void onSeekComplete(MediaPlayer mediaPlayer) {
            //hideLoadingIcon();
        }
    }


    /**
     * Media controller for advertisement
     */
    private class AdMediaController extends MediaController {

        public AdMediaController(Context context) {
            super(context);
        }

        @Override
        public void show() {
            super.show(0);
        }
    }

    /**
     * show location information from screen
     */
    private void showLocationInfo() {
        mHeader.setVisibility(View.VISIBLE);
        mFooter.setVisibility(View.VISIBLE);
        mHeader.bringToFront();
        mFooter.bringToFront();
    }

    /**
     * Hide location information from screen
     */
    private void hideLocationInfo() {
        mHeader.setVisibility(View.GONE);
        mFooter.setVisibility(View.GONE);
    }
}

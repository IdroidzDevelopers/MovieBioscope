package com.lib.videoplayer.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.lib.videoplayer.R;
import com.lib.videoplayer.object.Data;
import com.lib.videoplayer.util.TimeUtil;
import com.lib.videoplayer.util.VideoData;

import java.io.File;

public class VideoActivity extends AppCompatActivity implements View.OnTouchListener {
    private static final String TAG = VideoActivity.class.getSimpleName();
    private static final long BANNER_TIMEOUT = 5 * 1000;//5 secs
    private Context mContext;
    private VideoView mMovieView;
    private VideoView mAdvView;
    private TextView mNoContentView;
    private int mVideoSeekTime;
    private RelativeLayout mHeader;
    private RelativeLayout mFooter;
    private Handler mHandler;
    private View mLoading;
    private int mVideoState = VIDEO_STATE.NONE;
    private int mStopTime;
    private RelativeLayout mNewFeedLayout;
    private TextView mNewsTextView;


    //Header and footer
    private TextView mTimeView;
    private MinuteReceiver mReceiver;

    /******************
     * Listeners
     ******************/
    private AdsCompleteListener mAdsCompleteListener;
    private MovieCompleteListener mMovieCompleteListener;
    private MoviePrepareListener mMoviePrepareListener;
    private MovieSeekListener mMovieSeekListener;
    private AdPrepareListener mAdPrepareListener;
    private AdSeekListener mAdSeekListener;

    /*****************
     * Video state constants
     ****************/


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
        int SHOW_NEWS_FEED = 7;
        int HIDE_NEWS_FEED = 8;

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
        hideNotificationBar();
        initView();
        mHandler.sendEmptyMessage(TASK_EVENT.PLAY_MOVIE);
        mHandler.sendEmptyMessage(TASK_EVENT.PREPARE_FOR_NEXT_AD);
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
        mMoviePrepareListener = new MoviePrepareListener();
        mMovieSeekListener = new MovieSeekListener();
        mAdPrepareListener = new AdPrepareListener();
        mAdSeekListener = new AdSeekListener();
        //news feed
        mNewFeedLayout = (RelativeLayout) findViewById(R.id.news_feed_layout);
        mNewFeedLayout.bringToFront();
        mNewsTextView = (TextView) mNewFeedLayout.findViewById(R.id.news_feed);
        mNewsTextView.setSelected(true);

        //Header and footer
        mTimeView = (TextView) findViewById(R.id.current_time);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoState != VIDEO_STATE.NONE) {
            mHandler.sendEmptyMessage(TASK_EVENT.PREPARE_FOR_NEXT_AD);
            //showLoadingIcon();
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
        mReceiver = new MinuteReceiver();
        IntentFilter lFilter = new IntentFilter();
        lFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mReceiver, lFilter);
        mTimeView.setText(TimeUtil.getTime());
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
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mHeader.getVisibility() != View.VISIBLE && View.VISIBLE != mFooter.getVisibility()) {
                    mHandler.sendEmptyMessage(TASK_EVENT.DISPLAY_LOCATION_INFO);
                    mHandler.sendEmptyMessageDelayed(TASK_EVENT.HIDE_LOCATION_INFO, BANNER_TIMEOUT);
                } else {
                    mHandler.removeMessages(TASK_EVENT.HIDE_LOCATION_INFO);
                    mHandler.sendEmptyMessage(TASK_EVENT.HIDE_LOCATION_INFO);
                }
                break;

        }
        return false;
    }

    /**
     * Method to post advertisement after random time
     */
    private void initNextAdTiming() {
        long lNextAdTime = VideoData.getNextAdTime();
        //remove ad if any
        mHandler.removeMessages(TASK_EVENT.PLAY_AD);
        mHandler.sendEmptyMessageDelayed(TASK_EVENT.PLAY_AD, lNextAdTime);
    }

    /**
     * Method to start movie view
     */
    private void startMovie() {
        Data lData = VideoData.getRandomMovieUri(mContext);
        String lPath = lData.getPath();
        if (null != lPath && isFileExist(lPath)) {
            mMovieView.setVisibility(View.VISIBLE);
            mMovieView.setVideoURI(Uri.parse(lPath));
            mMovieView.setMediaController(null);
            mMovieView.requestFocus();
            mMovieView.start();
            setVideoState(VIDEO_STATE.MOVIE);
            VideoData.updateVideoData(mContext, lData);
        } else {
            mNoContentView.setVisibility(View.VISIBLE);
        }
    }

    private boolean isFileExist(String lPath) {
        File lFile = new File(lPath);
        return lFile.exists();
    }

    /**
     * Method to hide the movie view
     */
    private void hideMovie() {
        mVideoSeekTime = mMovieView.getCurrentPosition();
        mMovieView.pause();
        mMovieView.setVisibility(View.GONE);
    }

    /**
     * Method to start ad view
     */
    private void startAd() {
        Data lData = VideoData.getRandomAdUri(mContext);
        String lPath = lData.getPath();
        if (null != lPath) {
            if (!this.isFinishing()) {// TODO: dirty fix :: need solution
                mAdvView.setVisibility(View.VISIBLE);
                mAdvView.setVideoURI(Uri.parse(lPath));
                mAdvView.requestFocus();
                mAdvView.start();
                setVideoState(VIDEO_STATE.AD);
                VideoData.updateVideoData(mContext, lData);
            }
        }
    }

    /**
     * Method to hide the notification bar
     */
    private void hideNotificationBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Method to hide the loading/buffering icon
     */
    private void showLoadingIcon() {
        mLoading.setVisibility(View.VISIBLE);
        mLoading.bringToFront();
    }

    /**
     * Method to show the loading icon
     */
    private void hideLoadingIcon() {
        mLoading.setVisibility(View.GONE);

    }

    /**
     * Method to start from particular position
     */
    private void startSeekingMovie() {
        mMovieView.setVisibility(View.VISIBLE);
        mMovieView.seekTo(mVideoSeekTime);
        mMovieView.start();
        setVideoState(VIDEO_STATE.MOVIE);

    }

    /**
     * Method to hide the ad view
     */
    private void hideAdView() {
        mAdvView.setVisibility(View.GONE);
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

    private void hideNewsFeed() {
        mNewFeedLayout.setVisibility(View.GONE);
    }

    private void showNewsFeed() {
        mNewFeedLayout.setVisibility(View.VISIBLE);
        mNewFeedLayout.bringToFront();
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
                case TASK_EVENT.SHOW_NEWS_FEED:
                    showNewsFeed();
                    break;
                case TASK_EVENT.HIDE_NEWS_FEED:
                    hideNewsFeed();
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
            mediaPlayer.setOnSeekCompleteListener(mAdSeekListener);
        }
    }

    public class AdSeekListener implements OnSeekCompleteListener {
        @Override
        public void onSeekComplete(MediaPlayer mediaPlayer) {
            hideLoadingIcon();
        }
    }

    private class MinuteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive() :: time changed");
            mTimeView.setText(TimeUtil.getTime());
        }
    }

}

package com.lib.videoplayer.ui;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.lib.location.ui.BottomBannerFragment;
import com.lib.location.ui.TopBannerFragment;
import com.lib.videoplayer.R;
import com.lib.videoplayer.object.Data;
import com.lib.videoplayer.util.StateMachine;
import com.lib.videoplayer.util.VideoData;

import java.io.File;

public class VideoActivity extends AppCompatActivity implements View.OnTouchListener {
    private static final String TAG = VideoActivity.class.getSimpleName();
    private static final long BANNER_TIMEOUT = 5 * 1000;//5 secs
    private static final String ARG_VIDEO_STATE = "video_state";
    private Handler mState;
    private StateMachine mStateMachine;
    private Context mContext;
    private VideoView mMovieView;
    private VideoView mOtherView;
    private TextView mNoContentView;
    private int mMovieStopTime;
    private Handler mTaskHandler;
    private View mLoading;
    private int mOtherStopTime;
    private RelativeLayout mNewFeedLayout;
    private TextView mNewsTextView;

    //Header and Footer
    private Fragment mTopBannerFragment;
    private Fragment mBottomBannerFragment;

    /******************
     * Listeners
     ******************/
    private AdsCompleteListener mAdsCompleteListener;
    private MovieCompleteListener mMovieCompleteListener;
    private MoviePrepareListener mMoviePrepareListener;
    private MovieSeekListener mMovieSeekListener;
    private OtherPrepareListener mOtherPrepareListener;
    private OtherSeekListener mOtherSeekListener;


    public interface EVENT {
        int PLAY_NEXT = 0;
        int PLAY_BREAKING_VIDEO = 1;
        int PLAY_BREAKING_TEXT = 2;
        int RESUME = 3;
        int PLAY_ADV = 4;

    }


    /*********************
     * Task constants
     *********************/

    public interface TASK_EVENT {
        int DISPLAY_LOCATION_INFO = 0;
        int HIDE_LOCATION_INFO = 1;
        int PREPARE_FOR_NEXT_AD = 6;
        int SHOW_NEWS_FEED = 7;
        int HIDE_NEWS_FEED = 8;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNotificationBar();
        initView();
        initState();
    }

    /**
     * initialize the state handler
     */
    private void initState() {
        mStateMachine = StateMachine.getInstance();

        if (null != getIntent() && null != getIntent().getBundleExtra(ARG_VIDEO_STATE)) {
            Bundle lData = getIntent().getBundleExtra(ARG_VIDEO_STATE);
            if (null != lData) {
                int lVideoState = lData.getInt(ARG_VIDEO_STATE, -1);
                if (lVideoState == StateMachine.VIDEO_STATE.ONLY_ADV) {
                    mState = new OnlyAdvState();
                    mStateMachine.setVideoState(StateMachine.VIDEO_STATE.ONLY_ADV);
                } else {
                    mState = new MovieAdvState();
                    mStateMachine.setVideoState(StateMachine.VIDEO_STATE.MOVIE_AND_ADV);
                }
            }
        }
        if (null == mState) {
            //default case
            mState = new MovieAdvState();
            mStateMachine.setVideoState(StateMachine.VIDEO_STATE.MOVIE_AND_ADV);
        }
        mState.sendEmptyMessage(EVENT.PLAY_NEXT);
    }

    /**
     * Initialize the layout and get the reference for the views
     */

    private void initView() {
        setContentView(R.layout.video_layout);
        mContext = this;
        mMovieView = (VideoView) findViewById(R.id.movie_view);
        mOtherView = (VideoView) findViewById(R.id.ad_video);
        mNoContentView = (TextView) findViewById(R.id.no_content);
        mLoading = findViewById(R.id.loading);
        mMovieView.setOnTouchListener(this);
        mOtherView.setOnTouchListener(this);
        mTaskHandler = new TaskHandler();
        mMovieCompleteListener = new MovieCompleteListener();
        mAdsCompleteListener = new AdsCompleteListener();
        mMovieView.setOnCompletionListener(mMovieCompleteListener);
        mOtherView.setOnCompletionListener(mAdsCompleteListener);
        mMoviePrepareListener = new MoviePrepareListener();
        mMovieSeekListener = new MovieSeekListener();
        mOtherPrepareListener = new OtherPrepareListener();
        mOtherSeekListener = new OtherSeekListener();
        //news feed
        mNewFeedLayout = (RelativeLayout) findViewById(R.id.news_feed_layout);
        mNewFeedLayout.bringToFront();
        mNewsTextView = (TextView) mNewFeedLayout.findViewById(R.id.news_feed);
        mNewsTextView.setSelected(true);
        initLocationFragment();
    }

    private void initLocationFragment() {
        mTopBannerFragment = TopBannerFragment.newInstance(TopBannerFragment.TYPE.HOME_ICON_TYPE);
        mBottomBannerFragment = BottomBannerFragment.newInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mTaskHandler.sendEmptyMessage(TASK_EVENT.PREPARE_FOR_NEXT_AD);
        mState.sendEmptyMessage(EVENT.RESUME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mTaskHandler) {
            mTaskHandler.removeMessages(TASK_EVENT.HIDE_LOCATION_INFO);
            mTaskHandler.removeMessages(TASK_EVENT.PREPARE_FOR_NEXT_AD);
        }
        pauseVideo();
    }

    private void pauseVideo() {
        if (mStateMachine.mCurrentState == StateMachine.PLAYING_STATE.MOVIE) {
            mMovieStopTime = mMovieView.getCurrentPosition();
            mMovieView.pause();
        } else {
            mOtherStopTime = mOtherView.getCurrentPosition();
            mOtherView.pause();
        }
        mStateMachine.changeState(mStateMachine.mCurrentState, StateMachine.PLAYING_STATE.PAUSED);

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isLocationInfoVisible()) {
                    mTaskHandler.sendEmptyMessage(TASK_EVENT.DISPLAY_LOCATION_INFO);
                    mTaskHandler.sendEmptyMessageDelayed(TASK_EVENT.HIDE_LOCATION_INFO, BANNER_TIMEOUT);
                } else {
                    mTaskHandler.removeMessages(TASK_EVENT.HIDE_LOCATION_INFO);
                    mTaskHandler.sendEmptyMessage(TASK_EVENT.HIDE_LOCATION_INFO);
                }
                break;

        }
        return false;
    }


    private boolean isLocationInfoVisible() {
        TopBannerFragment lFragment = (TopBannerFragment) getSupportFragmentManager().findFragmentByTag(TopBannerFragment.TAG);
        if (null != lFragment) {
            Log.d(TAG, "lFragment.isVisible() " + lFragment.isVisible());
            return lFragment.isVisible();
        } else {
            return false;
        }
    }

    /**
     * Method to post advertisement after random time
     */
    private void initNextAdTiming() {
        long lNextAdTime = VideoData.getNextAdTime();
        //remove ad if any
        mState.removeMessages(EVENT.PLAY_ADV);
        mState.sendEmptyMessageDelayed(EVENT.PLAY_ADV, lNextAdTime);
    }

    /**
     * Method to start movie view
     */
    private void startMovie() {
        mOtherView.setVisibility(View.GONE);
        Data lData = VideoData.getRandomMovieUri(mContext);
        String lPath = lData.getPath();
        if (null != lPath && isFileExist(lPath)) {
            mMovieView.setVisibility(View.VISIBLE);
            mMovieView.setVideoURI(Uri.parse(lPath));
            mMovieView.setMediaController(null);
            mMovieView.requestFocus();
            mMovieView.start();
            hideLoadingIcon();
            mStateMachine.changeState(mStateMachine.mCurrentState, StateMachine.PLAYING_STATE.MOVIE);
            VideoData.updateVideoData(mContext, lData);
            mTaskHandler.sendEmptyMessage(TASK_EVENT.PREPARE_FOR_NEXT_AD);
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
        mMovieStopTime = mMovieView.getCurrentPosition();
        mMovieView.pause();
        mMovieView.setVisibility(View.GONE);
    }

    /**
     * Method to start ad view
     */
    private void startAd() {
        mMovieView.setVisibility(View.GONE);
        Data lData = VideoData.getRandomAdUri(mContext);
        String lPath = lData.getPath();
        if (null != lPath) {
            if (!this.isFinishing()) {// TODO: dirty fix :: need solution
                mOtherView.setVisibility(View.VISIBLE);
                mOtherView.setVideoURI(Uri.parse(lPath));
                mOtherView.requestFocus();
                mOtherView.start();
                hideLoadingIcon();
                mStateMachine.changeState(mStateMachine.mCurrentState, StateMachine.PLAYING_STATE.ADV);
                VideoData.updateVideoData(mContext, lData);
            }
        }
    }

    /**
     * Method to start traveller view
     */
    private void startTravellerVideo() {
        mMovieView.setVisibility(View.GONE);
        Data lData = VideoData.getTravellerUri(mContext);
        String lPath = lData.getPath();
        if (null != lPath && isFileExist(lPath)) {
            if (!this.isFinishing()) {// TODO: dirty fix :: need solution
                mOtherView.setVisibility(View.VISIBLE);
                mOtherView.setVideoURI(Uri.parse(lPath));
                mOtherView.requestFocus();
                mOtherView.start();
                hideLoadingIcon();
                mStateMachine.changeState(mStateMachine.mCurrentState, StateMachine.PLAYING_STATE.TRAVEL_VIDEO);
                VideoData.updateVideoData(mContext, lData);
            }
        } else {
            mNoContentView.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Method to start safety view
     */
    private void startSafetyVideo() {
        mMovieView.setVisibility(View.GONE);
        Data lData = VideoData.getSafetyUri(mContext);
        String lPath = lData.getPath();
        if (null != lPath && isFileExist(lPath)) {
            if (!this.isFinishing()) {// TODO: dirty fix :: need solution
                mOtherView.setVisibility(View.VISIBLE);
                mOtherView.setVideoURI(Uri.parse(lPath));
                mOtherView.requestFocus();
                mOtherView.start();
                hideLoadingIcon();
                mStateMachine.changeState(mStateMachine.mCurrentState, StateMachine.PLAYING_STATE.SAFETY_VIDEO);
                VideoData.updateVideoData(mContext, lData);
            }
        } else {
            mNoContentView.setVisibility(View.VISIBLE);
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
     * Method to hide the ad view
     */
    private void hideAdView() {
        mOtherView.setVisibility(View.GONE);
    }


    /**
     * show location information from screen
     */
    private void showLocationInfo() {
        FragmentTransaction lTransaction = getSupportFragmentManager().beginTransaction();
        lTransaction.replace(R.id.bottom_container, mBottomBannerFragment, BottomBannerFragment.TAG);
        lTransaction.replace(R.id.top_container, mTopBannerFragment, TopBannerFragment.TAG);
        lTransaction.commitAllowingStateLoss();
    }

    /**
     * Hide location information from screen
     */
    private void hideLocationInfo() {
        FragmentTransaction lTransaction = getSupportFragmentManager().beginTransaction();
        lTransaction.remove(mBottomBannerFragment);
        lTransaction.remove(mTopBannerFragment);
        lTransaction.commitAllowingStateLoss();
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
            mState.sendEmptyMessage(EVENT.PLAY_NEXT);
        }
    }


    public class MovieCompleteListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mState.sendEmptyMessage(EVENT.PLAY_NEXT);
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

    public class OtherPrepareListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mediaPlayer.setOnSeekCompleteListener(mOtherSeekListener);
        }
    }

    public class OtherSeekListener implements OnSeekCompleteListener {
        @Override
        public void onSeekComplete(MediaPlayer mediaPlayer) {
            hideLoadingIcon();
        }
    }

    /**
     * State handler in case of only adv playing
     */
    private class OnlyAdvState extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mStateMachine.print();
            switch (msg.what) {
                case EVENT.PLAY_NEXT:
                    switch (mStateMachine.mCurrentState) {
                        case StateMachine.PLAYING_STATE.NONE:
                        case StateMachine.PLAYING_STATE.TRAVEL_VIDEO:
                        case StateMachine.PLAYING_STATE.SAFETY_VIDEO:
                        case StateMachine.PLAYING_STATE.MOVIE:
                        case StateMachine.PLAYING_STATE.ADV:
                        case StateMachine.PLAYING_STATE.BREAKING_VIDEO:
                        case StateMachine.PLAYING_STATE.BREAKING_TEXT:
                        case StateMachine.PLAYING_STATE.PAUSED:
                            startAd();
                            break;
                    }
                case EVENT.RESUME:
                    switch (mStateMachine.mCurrentState) {
                        case StateMachine.PLAYING_STATE.PAUSED:
                            resumeVideo();
                            break;
                    }
                    break;
            }
        }
    }


    /**
     * State handler in case of both movie and adv playing
     */
    private class MovieAdvState extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mStateMachine.print();
            switch (msg.what) {
                case EVENT.PLAY_NEXT:
                    switch (mStateMachine.mCurrentState) {
                        case StateMachine.PLAYING_STATE.NONE:
                            startTravellerVideo();
                            break;
                        case StateMachine.PLAYING_STATE.TRAVEL_VIDEO:
                            startSafetyVideo();
                            break;
                        case StateMachine.PLAYING_STATE.SAFETY_VIDEO:
                            startMovie();
                            break;
                        case StateMachine.PLAYING_STATE.MOVIE:
                            //TODO: need to go back to home screen
                            break;
                        case StateMachine.PLAYING_STATE.ADV:
                        case StateMachine.PLAYING_STATE.BREAKING_VIDEO:
                        case StateMachine.PLAYING_STATE.BREAKING_TEXT:
                        case StateMachine.PLAYING_STATE.PAUSED:
                            //Forcefully make the previous state as movie and try to resume, because ad , breaking travel having a single video view
                            mStateMachine.mPrevState = StateMachine.PLAYING_STATE.MOVIE;
                            if (mMovieStopTime != 0) {
                                resumeVideo();
                            } else {
                                startMovie();
                            }
                            break;
                    }
                    break;
                case EVENT.RESUME:
                    switch (mStateMachine.mCurrentState) {
                        case StateMachine.PLAYING_STATE.PAUSED:
                            resumeVideo();
                            break;
                    }
                    break;
                case EVENT.PLAY_ADV:
                    switch (mStateMachine.mCurrentState) {
                        case StateMachine.PLAYING_STATE.MOVIE:
                            hideMovie();
                            startAd();
                            break;
                        case StateMachine.PLAYING_STATE.NONE:
                        case StateMachine.PLAYING_STATE.TRAVEL_VIDEO:
                        case StateMachine.PLAYING_STATE.SAFETY_VIDEO:
                        case StateMachine.PLAYING_STATE.ADV:
                        case StateMachine.PLAYING_STATE.BREAKING_VIDEO:
                        case StateMachine.PLAYING_STATE.BREAKING_TEXT:
                            //schedule the next advertisement
                            mTaskHandler.sendEmptyMessage(TASK_EVENT.PREPARE_FOR_NEXT_AD);
                            break;
                    }
                    break;
            }
        }
    }

    private void resumeVideo() {
        showLoadingIcon();
        if (mStateMachine.mPrevState == StateMachine.PLAYING_STATE.MOVIE) {
            mOtherView.setVisibility(View.GONE);
            mMovieView.setVisibility(View.VISIBLE);
            mMovieView.seekTo(mMovieStopTime);
            mMovieView.start();
            mMovieView.setOnPreparedListener(mMoviePrepareListener);
            mTaskHandler.sendEmptyMessage(TASK_EVENT.PREPARE_FOR_NEXT_AD);
            //reset
            mMovieStopTime = 0;
        } else {
            mMovieView.setVisibility(View.GONE);
            mOtherView.setVisibility(View.VISIBLE);
            mOtherView.seekTo(mOtherStopTime);
            mOtherView.start();
            mOtherView.setOnPreparedListener(mOtherPrepareListener);
            //reset
            mOtherStopTime = 0;
        }
        mStateMachine.changeState(mStateMachine.mCurrentState, mStateMachine.mPrevState);
    }
}

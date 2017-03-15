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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.VideoView;

import com.lib.location.ui.BottomBannerFragment;
import com.lib.location.ui.TopBannerFragment;
import com.lib.utility.util.CustomIntent;
import com.lib.utility.util.Logger;
import com.lib.videoplayer.R;
import com.lib.videoplayer.VideoApplication;
import com.lib.videoplayer.database.VideoProvider;
import com.lib.videoplayer.object.Data;
import com.lib.videoplayer.util.FileUtil;
import com.lib.videoplayer.util.StateMachine;
import com.lib.videoplayer.util.VideoData;
import com.lib.videoplayer.util.VideoTaskHandler;

import pl.droidsonroids.gif.GifImageView;

public class VideoActivity extends AppCompatActivity implements View.OnTouchListener {
    private static final String TAG = VideoActivity.class.getSimpleName();
    private static final long BANNER_TIMEOUT = 5 * 1000;//5 secs
    private static final long BREAKING_NEWS_DISPLAY_TIME = 30 * 1000;//30 secs
    private static final long COMPANY_AD_DISPLAY_TIME = 30 * 1000;//30 secs
    private static final long NO_CONTENT_DISPLAY_TIME = 5 * 1000;
    private static final long BACKGROUND_SEARCH_AFTER = 10 * 1000;
    private Handler mState;
    private StateMachine mStateMachine;
    private Context mContext;
    private VideoView mMovieView;
    private VideoView mOtherView;
    private TextView mNoContentView;
    private Handler mTaskHandler;
    private BroadcastReceiver mReceiver;
    private View mLoading;

    //Header , Footer, breaking news
    private Fragment mTopBannerFragment;
    private Fragment mBottomBannerFragment;
    private Fragment mBreakingNewsFragment;
    private Fragment mCompanyAdFragment;

    /******************
     * Listeners
     ******************/
    private AdsCompleteListener mAdsCompleteListener;
    private MovieCompleteListener mMovieCompleteListener;
    private MoviePrepareListener mMoviePrepareListener;
    private MovieSeekListener mMovieSeekListener;
    private OtherPrepareListener mOtherPrepareListener;
    private OtherSeekListener mOtherSeekListener;

    //testing
    private GifImageView mGifImageView;


    public interface EVENT {
        int PLAY_NEXT = 0;
        int PLAY_BREAKING_VIDEO = 1;
        int PLAY_BREAKING_NEWS = 2;
        int RESUME = 3;
        int PLAY_ADV = 4;
        int PLAY_COMPANY_AD = 5;

    }


    /*********************
     * Task constants
     *********************/

    public interface TASK_EVENT {
        int DISPLAY_LOCATION_INFO = 0;
        int HIDE_LOCATION_INFO = 1;
        int PREPARE_FOR_NEXT_AD = 2;
        int REMOVE_BREAKING_NEWS = 3;
        int FINISH_ACTIVITY = 4;
        int REMOVE_COMPANY_AD = 5;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNotificationBar();
        initView();
        initState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CustomIntent.ACTION_MEDIA_DOWNLOAD_COMPLETE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    /**
     * initialize the state handler
     */
    private void initState() {
        mStateMachine = StateMachine.getInstance();
        if (null != getIntent() && null != getIntent().getBundleExtra(CustomIntent.EXTRAS.VIDEO_STATE)) {
            Bundle lData = getIntent().getBundleExtra(CustomIntent.EXTRAS.VIDEO_STATE);
            if (null != lData) {
                int lVideoState = lData.getInt(CustomIntent.EXTRAS.VIDEO_STATE, -1);
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
        mStateMachine.retainPersistenceState(getVideoState());
    }

    /**
     * Initialize the layout and get the reference for the views
     */

    private void initView() {
        setContentView(R.layout.video_layout);
        mTaskHandler = new TaskHandler();
        mReceiver = new Receiver();
        mContext = this;
        mMovieView = (VideoView) findViewById(R.id.movie_view);
        mOtherView = (VideoView) findViewById(R.id.ad_video);
        mNoContentView = (TextView) findViewById(R.id.no_content);
        mLoading = findViewById(R.id.loading);
        mMovieView.setOnTouchListener(this);
        mOtherView.setOnTouchListener(this);
        initListener();
        initFragments();
        //testing
        mGifImageView = (GifImageView) findViewById(R.id.company_logo);
        mGifImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mState.sendEmptyMessage(EVENT.PLAY_BREAKING_NEWS);
            }
        });
    }

    /**
     * initialize the video player listener
     */
    private void initListener() {
        mMovieCompleteListener = new MovieCompleteListener();
        mAdsCompleteListener = new AdsCompleteListener();
        mMovieView.setOnCompletionListener(mMovieCompleteListener);
        mOtherView.setOnCompletionListener(mAdsCompleteListener);
        mMoviePrepareListener = new MoviePrepareListener();
        mMovieSeekListener = new MovieSeekListener();
        mOtherPrepareListener = new OtherPrepareListener();
        mOtherSeekListener = new OtherSeekListener();

    }

    /**
     * create the location related fragment
     */
    private void initFragments() {
        mTopBannerFragment = TopBannerFragment.newInstance(TopBannerFragment.TYPE.HOME_ICON_TYPE);
        mBottomBannerFragment = BottomBannerFragment.newInstance();
        mBreakingNewsFragment = BreakingNewsFragment.newInstance();
        mCompanyAdFragment = CompanyAdFragment.newInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mState.sendEmptyMessage(EVENT.RESUME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //remove the handlers
        hideLocationInfo();
        removeBreakingNews();
        removeCompanyAd();
        if (null != mTaskHandler) {
            mTaskHandler.removeMessages(TASK_EVENT.HIDE_LOCATION_INFO);
            mTaskHandler.removeMessages(TASK_EVENT.PREPARE_FOR_NEXT_AD);
            mTaskHandler.removeMessages(TASK_EVENT.REMOVE_COMPANY_AD);
            mTaskHandler.removeMessages(TASK_EVENT.REMOVE_BREAKING_NEWS);
        }
        pauseVideo();
    }

    /**
     * pause the running video and change the state
     */
    private void pauseVideo() {
        if (mStateMachine.videoInfo.getCurrentState() != StateMachine.PLAYING_STATE.MOVIE_FINISHED) {
            if (mStateMachine.videoInfo.getCurrentState() == StateMachine.PLAYING_STATE.MOVIE) {
                int movieStopTime = mMovieView.getCurrentPosition();
                Logger.debug(TAG, "pauseVideo :: movieSeekTime is " + movieStopTime);
                if (0 != movieStopTime) {
                    mStateMachine.changeState(mStateMachine.videoInfo.getCurrentState(), StateMachine.PLAYING_STATE.PAUSED);
                    mStateMachine.persistState(movieStopTime, 0);
                    mMovieView.pause();
                }
            } else {
                int otherStopTime = mOtherView.getCurrentPosition();
                Logger.debug(TAG, "pauseVideo :: otherStopTime is " + otherStopTime);
                if (0 != otherStopTime) {
                    mStateMachine.changeState(mStateMachine.videoInfo.getCurrentState(), StateMachine.PLAYING_STATE.PAUSED);
                    mStateMachine.persistState(0, otherStopTime);
                    mOtherView.pause();
                }
            }
        } else {
            Logger.debug(TAG, "pauseVideo :: current state is " + mStateMachine.videoInfo.getCurrentState() + " so no need to save");
        }

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

    /**
     * Check if location information visible in the screen
     *
     * @return true or false
     */
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
        mStateMachine.deletePersistState(getVideoState());
        mOtherView.setVisibility(View.GONE);
        Data lData = VideoData.getMovieData(mContext);
        if (null != lData && null != lData.getPath() && FileUtil.isFileExist(lData.getPath())) {
            mMovieView.setVisibility(View.VISIBLE);
            mMovieView.setVideoURI(Uri.parse(lData.getPath()));
            mMovieView.setMediaController(null);
            mMovieView.requestFocus();
            mMovieView.start();
            hideLoadingIcon();
            mStateMachine.videoInfo.updateVideoId(lData.getAssetID());
            mStateMachine.videoInfo.setMovieUri(Uri.parse(lData.getPath()));
            mStateMachine.changeState(mStateMachine.videoInfo.getCurrentState(), StateMachine.PLAYING_STATE.MOVIE);
            VideoData.updateVideoData(mContext, lData);
            mTaskHandler.sendEmptyMessage(TASK_EVENT.PREPARE_FOR_NEXT_AD);
        } else {
            hideLoadingIcon();
            mNoContentView.setVisibility(View.VISIBLE);
            mTaskHandler.sendEmptyMessageDelayed(TASK_EVENT.FINISH_ACTIVITY, NO_CONTENT_DISPLAY_TIME);
        }
    }


    /**
     * Method to hide the movie view
     */
    private void hideMovie() {
        int movieStopTime = mMovieView.getCurrentPosition();
        mStateMachine.persistState(movieStopTime, 0);
        mMovieView.pause();
        mMovieView.setVisibility(View.GONE);
    }

    /**
     * Method to start ad view
     */
    private void startAd() {
        mMovieView.setVisibility(View.GONE);
        Data lData = VideoData.getRandomAd(mContext);
        if (null != lData && null != lData.getPath() && FileUtil.isFileExist(lData.getPath())) {
            if (!this.isFinishing()) {// TODO: dirty fix :: need olution
                mOtherView.setVisibility(View.VISIBLE);
                mOtherView.setVideoURI(Uri.parse(lData.getPath()));
                mOtherView.requestFocus();
                mOtherView.start();
                hideLoadingIcon();
                mStateMachine.videoInfo.updateVideoId(lData.getAssetID());
                mStateMachine.videoInfo.setOtherUri(Uri.parse(lData.getPath()));
                mStateMachine.changeState(mStateMachine.videoInfo.getCurrentState(), StateMachine.PLAYING_STATE.ADV);
                VideoData.updateVideoData(mContext, lData);
            }
        }
    }

    /**
     * Method to start landing video
     */
    private void startLandingVideo() {
        mMovieView.setVisibility(View.GONE);
        Data lData = VideoData.getRandomLandingVideo(mContext);
        if (null != lData && null != lData.getPath() && FileUtil.isFileExist(lData.getPath())) {
            if (!this.isFinishing()) {
                mOtherView.setVisibility(View.VISIBLE);
                mOtherView.setVideoURI(Uri.parse(lData.getPath()));
                mOtherView.requestFocus();
                mOtherView.start();
                hideLoadingIcon();
                mStateMachine.videoInfo.updateVideoId(lData.getAssetID());
                mStateMachine.videoInfo.setOtherUri(Uri.parse(lData.getPath()));
                mStateMachine.changeState(mStateMachine.videoInfo.getCurrentState(), StateMachine.PLAYING_STATE.LANDING_VIDEO);
                VideoData.updateVideoData(mContext, lData);
            }
        }
    }

    private void startBreakingVideo() {
        mMovieView.setVisibility(View.GONE);
        Data lData = VideoData.getBreakingVideo(mContext);
        String lPath = lData.getPath();
        if (null != lPath) {
            if (!this.isFinishing()) {// TODO: dirty fix :: need solution
                mOtherView.setVisibility(View.VISIBLE);
                mOtherView.setVideoURI(Uri.parse(lPath));
                mOtherView.requestFocus();
                mOtherView.start();
                hideLoadingIcon();
                mStateMachine.videoInfo.updateVideoId(lData.getAssetID());
                mStateMachine.videoInfo.setOtherUri(Uri.parse(lData.getPath()));
                mStateMachine.changeState(mStateMachine.videoInfo.getCurrentState(), StateMachine.PLAYING_STATE.BREAKING_VIDEO);
                VideoData.updateVideoData(mContext, lData);
            }
        }
    }

    /**
     * Method to start traveller view
     */
    private void startTravellerVideo() {
        mMovieView.setVisibility(View.GONE);
        Data lData = VideoData.getTravellerVideo(mContext);
        if (null != lData && null != lData.getPath() && FileUtil.isFileExist(lData.getPath())) {
            if (!this.isFinishing()) {// TODO: dirty fix :: need solution
                mOtherView.setVisibility(View.VISIBLE);
                mOtherView.setVideoURI(Uri.parse(lData.getPath()));
                mOtherView.requestFocus();
                mOtherView.start();
                hideLoadingIcon();
                mStateMachine.videoInfo.updateVideoId(lData.getAssetID());
                mStateMachine.videoInfo.setOtherUri(Uri.parse(lData.getPath()));
                mStateMachine.changeState(mStateMachine.videoInfo.getCurrentState(), StateMachine.PLAYING_STATE.TRAVEL_VIDEO);
                VideoData.updateVideoData(mContext, lData);
            }
        } else {
            //still the change the change and process
            mStateMachine.changeState(mStateMachine.videoInfo.getCurrentState(), StateMachine.PLAYING_STATE.TRAVEL_VIDEO);
            mState.sendEmptyMessage(EVENT.PLAY_NEXT);
        }
    }


    /**
     * Method to start safety view
     */
    private void startSafetyVideo() {
        mMovieView.setVisibility(View.GONE);
        Data lData = VideoData.getSafetyVideo(mContext);
        if (null != lData && null != lData.getPath() && FileUtil.isFileExist(lData.getPath())) {
            if (!this.isFinishing()) {// TODO: dirty fix :: need solution
                mOtherView.setVisibility(View.VISIBLE);
                mOtherView.setVideoURI(Uri.parse(lData.getPath()));
                mOtherView.requestFocus();
                mOtherView.start();
                hideLoadingIcon();
                mStateMachine.videoInfo.updateVideoId(lData.getAssetID());
                mStateMachine.videoInfo.setOtherUri(Uri.parse(lData.getPath()));
                mStateMachine.changeState(mStateMachine.videoInfo.getCurrentState(), StateMachine.PLAYING_STATE.SAFETY_VIDEO);
                VideoData.updateVideoData(mContext, lData);
            }
        } else {
            //still the change the change and process
            mStateMachine.changeState(mStateMachine.videoInfo.getCurrentState(), StateMachine.PLAYING_STATE.SAFETY_VIDEO);
            mState.sendEmptyMessage(EVENT.PLAY_NEXT);
        }
    }

    /**
     * Method to start safety view
     */
    private void startIntroVideo() {
        mMovieView.setVisibility(View.GONE);
        if (!this.isFinishing()) {// TODO: dirty fix :: need solution
            mOtherView.setVisibility(View.VISIBLE);
            mOtherView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.navajhalaka_intro));
            mOtherView.requestFocus();
            mOtherView.start();
            hideLoadingIcon();
            mStateMachine.videoInfo.setOtherUri(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.navajhalaka_intro));
            mStateMachine.changeState(mStateMachine.videoInfo.getCurrentState(), StateMachine.PLAYING_STATE.INTRO_VIDEO);
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
        if (null != getSupportFragmentManager()) {
            FragmentTransaction lTransaction = getSupportFragmentManager().beginTransaction();
            if (null != lTransaction) {
                lTransaction.replace(R.id.bottom_container, mBottomBannerFragment, BottomBannerFragment.TAG);
                lTransaction.replace(R.id.top_container, mTopBannerFragment, TopBannerFragment.TAG);
                lTransaction.commitAllowingStateLoss();
            }
        }
    }

    /**
     * Hide location information from screen
     */
    private void hideLocationInfo() {
        if (null != getSupportFragmentManager()) {
            FragmentTransaction lTransaction = getSupportFragmentManager().beginTransaction();
            if (null != lTransaction) {
                lTransaction.remove(mBottomBannerFragment);
                lTransaction.remove(mTopBannerFragment);
                lTransaction.commitAllowingStateLoss();
            }
        }
    }


    /**
     * show breaking news on the screen
     */
    private void showBreakingNews() {
        if (null != getSupportFragmentManager()) {
            FragmentTransaction lTransaction = getSupportFragmentManager().beginTransaction();
            if (null != lTransaction) {
                lTransaction.replace(R.id.breaking_news_container, mBreakingNewsFragment, BreakingNewsFragment.TAG);
                lTransaction.commitAllowingStateLoss();
            }
        }
    }

    /**
     * Hide breaking news from screen
     */
    private void removeBreakingNews() {
        if (null != getSupportFragmentManager()) {
            FragmentTransaction lTransaction = getSupportFragmentManager().beginTransaction();
            if (null != lTransaction) {
                lTransaction.remove(mBreakingNewsFragment);
                lTransaction.commitAllowingStateLoss();
            }
        }
    }


    /**
     * show company ad on the screen
     */
    private void showCompanyAd() {
        if (null != getSupportFragmentManager()) {
            FragmentTransaction lTransaction = getSupportFragmentManager().beginTransaction();
            if (null != lTransaction) {
                lTransaction.replace(R.id.breaking_news_container, mCompanyAdFragment, CompanyAdFragment.TAG);
                lTransaction.commitAllowingStateLoss();
            }
        }
    }

    /**
     * Hide breaking news from screen
     */
    private void removeCompanyAd() {
        if (null != getSupportFragmentManager()) {
            FragmentTransaction lTransaction = getSupportFragmentManager().beginTransaction();
            if (null != lTransaction) {
                lTransaction.remove(mCompanyAdFragment);
                lTransaction.commitAllowingStateLoss();
            }
        }
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
                case TASK_EVENT.REMOVE_BREAKING_NEWS:
                    removeBreakingNews();
                    mStateMachine.retainPersistenceState(getVideoState());
                    resumeVideo();
                    break;
                case TASK_EVENT.REMOVE_COMPANY_AD:
                    removeCompanyAd();
                    mStateMachine.retainPersistenceState(getVideoState());
                    resumeVideo();
                    break;
                case TASK_EVENT.FINISH_ACTIVITY:
                    if (!isFinishing()) {
                        finish();
                    }
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
                    VideoTaskHandler.getInstance(mContext).sendEmptyMessageDelayed(VideoTaskHandler.TASK.BACK_GROUND_BREAKING_NEWS_SEARCH, BACKGROUND_SEARCH_AFTER);
                    startLandingVideo();
                    break;
                case EVENT.RESUME:
                    switch (mStateMachine.videoInfo.getCurrentState()) {
                        case StateMachine.PLAYING_STATE.PAUSED:
                            VideoTaskHandler.getInstance(mContext).sendEmptyMessageDelayed(VideoTaskHandler.TASK.BACK_GROUND_BREAKING_NEWS_SEARCH, BACKGROUND_SEARCH_AFTER);
                            mStateMachine.retainPersistenceState(getVideoState());
                            resumeVideo();
                            break;
                        case StateMachine.PLAYING_STATE.NONE:
                            VideoTaskHandler.getInstance(mContext).sendEmptyMessageDelayed(VideoTaskHandler.TASK.BACK_GROUND_BREAKING_NEWS_SEARCH, BACKGROUND_SEARCH_AFTER);
                            startLandingVideo();
                            break;
                        default:
                            Logger.debug(TAG, "worst case prev :: " + mStateMachine.videoInfo.getPrevState() + " curr " + mStateMachine.videoInfo.getCurrentState());
                            mStateMachine.deletePersistState(getVideoState());
                            mStateMachine.reset();
                            mState.sendEmptyMessage(EVENT.PLAY_NEXT);
                    }
                    break;
                case EVENT.PLAY_BREAKING_VIDEO:
                    switch (mStateMachine.videoInfo.getCurrentState()) {
                        case StateMachine.PLAYING_STATE.LANDING_VIDEO:
                            startBreakingVideo();
                            break;
                    }
                    break;
                case EVENT.PLAY_BREAKING_NEWS:
                    switch (mStateMachine.videoInfo.getCurrentState()) {
                        case StateMachine.PLAYING_STATE.LANDING_VIDEO:
                        case StateMachine.PLAYING_STATE.BREAKING_VIDEO:
                            pauseVideo();
                            showBreakingNews();
                            mTaskHandler.sendEmptyMessageDelayed(TASK_EVENT.REMOVE_BREAKING_NEWS, BREAKING_NEWS_DISPLAY_TIME);
                            break;
                    }
                    break;

                case EVENT.PLAY_COMPANY_AD:
                    switch (mStateMachine.videoInfo.getCurrentState()) {
                        case StateMachine.PLAYING_STATE.LANDING_VIDEO:
                        case StateMachine.PLAYING_STATE.BREAKING_VIDEO:
                            pauseVideo();
                            showCompanyAd();
                            mTaskHandler.sendEmptyMessageDelayed(TASK_EVENT.REMOVE_COMPANY_AD, COMPANY_AD_DISPLAY_TIME);
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
                    switch (mStateMachine.videoInfo.getCurrentState()) {
                        case StateMachine.PLAYING_STATE.NONE:
                            VideoTaskHandler.getInstance(mContext).sendEmptyMessageDelayed(VideoTaskHandler.TASK.BACK_GROUND_BREAKING_NEWS_SEARCH, BACKGROUND_SEARCH_AFTER);
                            startTravellerVideo();
                            break;
                        case StateMachine.PLAYING_STATE.TRAVEL_VIDEO:
                            VideoTaskHandler.getInstance(mContext).sendEmptyMessageDelayed(VideoTaskHandler.TASK.BACK_GROUND_BREAKING_NEWS_SEARCH, BACKGROUND_SEARCH_AFTER);
                            startSafetyVideo();
                            break;
                        case StateMachine.PLAYING_STATE.SAFETY_VIDEO:
                            VideoTaskHandler.getInstance(mContext).sendEmptyMessageDelayed(VideoTaskHandler.TASK.BACK_GROUND_BREAKING_NEWS_SEARCH, BACKGROUND_SEARCH_AFTER);
                            startIntroVideo();
                            break;
                        case StateMachine.PLAYING_STATE.INTRO_VIDEO:
                            VideoTaskHandler.getInstance(mContext).sendEmptyMessageDelayed(VideoTaskHandler.TASK.BACK_GROUND_BREAKING_NEWS_SEARCH, BACKGROUND_SEARCH_AFTER);
                            startMovie();
                            break;
                        case StateMachine.PLAYING_STATE.MOVIE:
                            //once finished , move to home
                            sendCompletedBroadcast();
                            mStateMachine.deletePersistState(getVideoState());
                            VideoData.resetMovieSelection();
                            mStateMachine.changeState(mStateMachine.videoInfo.getCurrentState(), StateMachine.PLAYING_STATE.MOVIE_FINISHED);
                            if (!isFinishing()) {
                                finish();
                            }
                            break;
                        case StateMachine.PLAYING_STATE.ADV:
                            VideoTaskHandler.getInstance(mContext).sendEmptyMessageDelayed(VideoTaskHandler.TASK.BACK_GROUND_BREAKING_NEWS_SEARCH, BACKGROUND_SEARCH_AFTER);
                            //display location after advertisement
                            if (!isLocationInfoVisible()) {
                                mTaskHandler.sendEmptyMessage(TASK_EVENT.DISPLAY_LOCATION_INFO);
                                mTaskHandler.sendEmptyMessageDelayed(TASK_EVENT.HIDE_LOCATION_INFO, BANNER_TIMEOUT);
                            }
                        case StateMachine.PLAYING_STATE.BREAKING_VIDEO:
                        case StateMachine.PLAYING_STATE.BREAKING_TEXT:
                        case StateMachine.PLAYING_STATE.COMPANY_AD:
                            sendCompletedBroadcast();
                        case StateMachine.PLAYING_STATE.PAUSED:
                            //Forcefully make the previous state as movie and try to resume, because ad , breaking travel having a single video view
                            mStateMachine.retainPersistenceState(getVideoState());
                            mStateMachine.videoInfo.setPrevState(StateMachine.PLAYING_STATE.MOVIE);
                            if (0 != mStateMachine.videoInfo.getMovieSeekTime()) {
                                resumeVideo();
                            } else {
                                startMovie();
                            }
                            break;
                    }
                    break;
                case EVENT.RESUME:
                    switch (mStateMachine.videoInfo.getCurrentState()) {
                        case StateMachine.PLAYING_STATE.PAUSED:
                            VideoTaskHandler.getInstance(mContext).sendEmptyMessageDelayed(VideoTaskHandler.TASK.BACK_GROUND_BREAKING_NEWS_SEARCH, BACKGROUND_SEARCH_AFTER);
                            mStateMachine.retainPersistenceState(getVideoState());
                            resumeVideo();
                            break;
                        case StateMachine.PLAYING_STATE.NONE:
                        case StateMachine.PLAYING_STATE.MOVIE_FINISHED:
                            VideoTaskHandler.getInstance(mContext).sendEmptyMessageDelayed(VideoTaskHandler.TASK.BACK_GROUND_BREAKING_NEWS_SEARCH, BACKGROUND_SEARCH_AFTER);
                            mState.sendEmptyMessage(EVENT.PLAY_NEXT);
                            break;
                        default:
                            Logger.debug(TAG, "worst case prev :: " + mStateMachine.videoInfo.getPrevState() + " curr " + mStateMachine.videoInfo.getCurrentState());
                            mStateMachine.deletePersistState(getVideoState());
                            mStateMachine.reset();
                            mState.sendEmptyMessage(EVENT.PLAY_NEXT);
                    }
                    break;
                case EVENT.PLAY_ADV:
                    if (VideoData.isAdvExist(mContext)) {
                        VideoTaskHandler.getInstance(mContext).sendEmptyMessageDelayed(VideoTaskHandler.TASK.BACK_GROUND_BREAKING_NEWS_SEARCH, BACKGROUND_SEARCH_AFTER);
                        switch (mStateMachine.videoInfo.getCurrentState()) {
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
                            case StateMachine.PLAYING_STATE.COMPANY_AD:
                            case StateMachine.PLAYING_STATE.INTRO_VIDEO:

                                //schedule the next advertisement
                                mTaskHandler.sendEmptyMessage(TASK_EVENT.PREPARE_FOR_NEXT_AD);
                                break;
                        }
                        break;
                    } else {
                        //advertisement video not present still schdule for future may be we will get later
                        mTaskHandler.sendEmptyMessage(TASK_EVENT.PREPARE_FOR_NEXT_AD);
                    }

                case EVENT.PLAY_BREAKING_VIDEO:
                    switch (mStateMachine.videoInfo.getCurrentState()) {
                        case StateMachine.PLAYING_STATE.MOVIE:
                            hideMovie();
                        case StateMachine.PLAYING_STATE.NONE:
                        case StateMachine.PLAYING_STATE.TRAVEL_VIDEO:
                        case StateMachine.PLAYING_STATE.SAFETY_VIDEO:
                        case StateMachine.PLAYING_STATE.ADV:
                        case StateMachine.PLAYING_STATE.INTRO_VIDEO:
                        case StateMachine.PLAYING_STATE.BREAKING_VIDEO:
                        case StateMachine.PLAYING_STATE.COMPANY_AD:
                        case StateMachine.PLAYING_STATE.BREAKING_TEXT:
                        case StateMachine.PLAYING_STATE.PAUSED:
                            //no need to hide the movie in this case, otherwise you will loose your persistence data
                            startBreakingVideo();
                            break;
                    }
                    break;
                case EVENT.PLAY_BREAKING_NEWS:
                    switch (mStateMachine.videoInfo.getCurrentState()) {
                        case StateMachine.PLAYING_STATE.TRAVEL_VIDEO:
                        case StateMachine.PLAYING_STATE.SAFETY_VIDEO:
                        case StateMachine.PLAYING_STATE.MOVIE:
                        case StateMachine.PLAYING_STATE.ADV:
                        case StateMachine.PLAYING_STATE.BREAKING_VIDEO:
                        case StateMachine.PLAYING_STATE.INTRO_VIDEO:
                        case StateMachine.PLAYING_STATE.NONE:
                        case StateMachine.PLAYING_STATE.BREAKING_TEXT:
                        case StateMachine.PLAYING_STATE.PAUSED:
                            pauseVideo();
                            showBreakingNews();
                            mTaskHandler.sendEmptyMessageDelayed(TASK_EVENT.REMOVE_BREAKING_NEWS, BREAKING_NEWS_DISPLAY_TIME);
                            break;
                    }
                    break;

                case EVENT.PLAY_COMPANY_AD:
                    switch (mStateMachine.videoInfo.getCurrentState()) {
                        case StateMachine.PLAYING_STATE.TRAVEL_VIDEO:
                        case StateMachine.PLAYING_STATE.SAFETY_VIDEO:
                        case StateMachine.PLAYING_STATE.MOVIE:
                        case StateMachine.PLAYING_STATE.ADV:
                        case StateMachine.PLAYING_STATE.BREAKING_VIDEO:
                        case StateMachine.PLAYING_STATE.INTRO_VIDEO:
                        case StateMachine.PLAYING_STATE.NONE:
                        case StateMachine.PLAYING_STATE.BREAKING_TEXT:
                        case StateMachine.PLAYING_STATE.PAUSED:
                            pauseVideo();
                            showCompanyAd();
                            mTaskHandler.sendEmptyMessageDelayed(TASK_EVENT.REMOVE_COMPANY_AD, COMPANY_AD_DISPLAY_TIME);
                            break;
                    }
                    break;
            }
        }
    }

    private void sendCompletedBroadcast() {
        Logger.debug(TAG, "sendCompletedBroadcast :: called");
        Intent intent = new Intent();
        intent.putExtra(CustomIntent.EXTRAS.VIDEO_ID, mStateMachine.videoInfo.getVideoId());
        switch (mStateMachine.videoInfo.getCurrentState()) {
            case StateMachine.PLAYING_STATE.MOVIE:
                intent.setAction(CustomIntent.ACTION_MOVIE_COMPLETED);
                break;
            default:
                intent.setAction(CustomIntent.ACTION_ADV_COMPLETED);
                break;

        }
        LocalBroadcastManager.getInstance(VideoApplication.getVideoContext()).sendBroadcast(intent);
    }

    private void resumeVideo() {
        showLoadingIcon();
        if (mStateMachine.videoInfo.getPrevState() == StateMachine.PLAYING_STATE.MOVIE) {
            mOtherView.setVisibility(View.GONE);
            mMovieView.setVideoURI(mStateMachine.videoInfo.getMovieUri());
            mMovieView.setVisibility(View.VISIBLE);
            mMovieView.seekTo(mStateMachine.videoInfo.getMovieSeekTime());
            mMovieView.start();
            mMovieView.setOnPreparedListener(mMoviePrepareListener);
            mStateMachine.deletePersistState(getVideoState());
            hideLoadingIcon();
            mTaskHandler.sendEmptyMessage(TASK_EVENT.PREPARE_FOR_NEXT_AD);
            //reset the value so that it wont resume from same place again
        } else {
            mMovieView.setVisibility(View.GONE);
            mOtherView.setVisibility(View.VISIBLE);
            mOtherView.setVideoURI(mStateMachine.videoInfo.getOtherUri());
            mOtherView.seekTo(mStateMachine.videoInfo.getOtherSeekTime());
            mOtherView.start();
            mOtherView.setOnPreparedListener(mOtherPrepareListener);
            hideLoadingIcon();
            if (getVideoState() == StateMachine.VIDEO_STATE.ONLY_ADV) {
                mStateMachine.deletePersistState(getVideoState());
            }
        }
        mStateMachine.changeState(mStateMachine.videoInfo.getCurrentState(), mStateMachine.videoInfo.getPrevState());
    }


    private int getVideoState() {
        if (null != mState) {
            if (mState instanceof OnlyAdvState) {
                return StateMachine.VIDEO_STATE.ONLY_ADV;
            } else if (mState instanceof MovieAdvState) {
                return StateMachine.VIDEO_STATE.MOVIE_AND_ADV;
            } else {
                return StateMachine.VIDEO_STATE.NONE;
            }
        } else {
            return StateMachine.VIDEO_STATE.NONE;
        }
    }

    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                if (CustomIntent.ACTION_MEDIA_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    String videoId = intent.getStringExtra(CustomIntent.EXTRAS.VIDEO_ID);
                    String type = intent.getStringExtra(CustomIntent.EXTRAS.TYPE);
                    Logger.debug(TAG, "onReceive() :: videoId " + videoId + " type " + type + " videoId " + videoId);
                    if (VideoProvider.VIDEO_TYPE.BREAKING_VIDEO.equals(type)) {
                        if (VideoData.isBreakingNewsStillValid(context, videoId)) {
                            mState.sendEmptyMessage(EVENT.PLAY_BREAKING_VIDEO);
                        }
                    } else if (VideoProvider.VIDEO_TYPE.BREAKING_NEWS.equals(type)) {
                        if (VideoData.isBreakingNewsStillValid(context, videoId)) {
                            mState.sendEmptyMessage(EVENT.PLAY_BREAKING_NEWS);
                        }
                    } else if (VideoProvider.VIDEO_TYPE.COMPANY_AD.equals(type)) {
                        mState.sendEmptyMessage(EVENT.PLAY_COMPANY_AD);
                    }
                }
            }
        }
    }
}

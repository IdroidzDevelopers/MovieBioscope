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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.lib.location.ui.BottomBannerFragment;
import com.lib.location.ui.TopBannerFragment;
import com.lib.route.util.RouteUtil;
import com.lib.utility.util.CustomIntent;
import com.lib.utility.util.Logger;
import com.lib.videoplayer.R;
import com.lib.videoplayer.VideoApplication;
import com.lib.videoplayer.database.VideoProvider;
import com.lib.videoplayer.object.Data;
import com.lib.videoplayer.object.SequenceData;
import com.lib.videoplayer.util.AdsSlotConfigUtil;
import com.lib.videoplayer.util.FileUtil;
import com.lib.videoplayer.util.SequenceUtil;
import com.lib.videoplayer.util.StateMachine;
import com.lib.videoplayer.util.VideoData;
import com.lib.videoplayer.util.VideoTaskHandler;

import pl.droidsonroids.gif.GifImageView;

public class VideoActivity extends AppCompatActivity implements View.OnTouchListener {
    private static final String TAG = VideoActivity.class.getSimpleName();
    private static final long BANNER_TIMEOUT = 10 * 1000;//10 secs
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
    private TextView mBrkVideoHeader;
    private RelativeLayout mNewFeedLayout;
    private TextView mNewsTextView;

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

    private GifImageView mTravellerImageView;


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
        int SHOW_TICKER_TEXT = 7;
        int HIDE_TICKER_TEXT = 8;
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
        //reset everything if data is there it will take from database
        mStateMachine.reset();
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
        mBrkVideoHeader = (TextView) findViewById(R.id.breaking_video_header);
        //news feed
        mNewFeedLayout = (RelativeLayout) findViewById(R.id.news_feed_layout);
        mNewFeedLayout.bringToFront();
        mNewsTextView = (TextView) mNewFeedLayout.findViewById(R.id.news_feed);
        mNewsTextView.setSelected(true);
        initTravellerImageIfExist();
    }

    private void initTravellerImageIfExist() {
        mTravellerImageView = (GifImageView) findViewById(R.id.traveller_logo);
        String path = RouteUtil.getTravellerImagePath();
        if (null != path && FileUtil.isFileExist(path)) {
            mTravellerImageView.setImageURI(Uri.parse(path));
            mTravellerImageView.setVisibility(View.VISIBLE);
        }
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
        if (mStateMachine.videoInfo.getVideoState() == StateMachine.VIDEO_STATE.MOVIE_AND_ADV) {
            mTaskHandler.sendEmptyMessage(TASK_EVENT.PREPARE_FOR_NEXT_AD);
        }
        mTaskHandler.sendEmptyMessage(TASK_EVENT.SHOW_TICKER_TEXT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //remove the handlers
        hideLocationInfo();
        removeBreakingNews();
        removeCompanyAd();
        hideBreakingVideoHeaderIfRequired();
        hideTickerText();
        if (null != mTaskHandler) {
            mTaskHandler.removeMessages(TASK_EVENT.HIDE_LOCATION_INFO);
            mTaskHandler.removeMessages(TASK_EVENT.PREPARE_FOR_NEXT_AD);
            mTaskHandler.removeMessages(TASK_EVENT.REMOVE_COMPANY_AD);
            mTaskHandler.removeMessages(TASK_EVENT.REMOVE_BREAKING_NEWS);
            mTaskHandler.removeMessages(TASK_EVENT.HIDE_TICKER_TEXT);
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
                    mStateMachine.videoInfo.addPausedState(mStateMachine.videoInfo.getCurrentState());
                    mStateMachine.persistState(movieStopTime, 0, 0);
                    mMovieView.pause();
                }
                mMovieView.setVisibility(View.GONE);
            } else if (mStateMachine.videoInfo.getCurrentState() == StateMachine.PLAYING_STATE.BREAKING_VIDEO) {
                int otherStopTime = mOtherView.getCurrentPosition();
                Logger.debug(TAG, "pauseVideo :: breaking is " + otherStopTime);
                if (0 != otherStopTime) {
                    mStateMachine.videoInfo.addPausedState(mStateMachine.videoInfo.getCurrentState());
                    mStateMachine.persistState(0, 0, otherStopTime);
                    mOtherView.pause();
                }
                mOtherView.setVisibility(View.GONE);
            } else {
                int otherStopTime = mOtherView.getCurrentPosition();
                Logger.debug(TAG, "pauseVideo :: otherStopTime is " + otherStopTime);
                if (0 != otherStopTime) {
                    mStateMachine.videoInfo.addPausedState(mStateMachine.videoInfo.getCurrentState());
                    mStateMachine.persistState(0, otherStopTime, 0);
                    mOtherView.pause();
                }
                mOtherView.setVisibility(View.GONE);
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
     * Method to start ad view
     */
    private void startAd() {
        mMovieView.setVisibility(View.GONE);
        Data lData = VideoData.getVideoByType(VideoProvider.VIDEO_TYPE.ADV);
        if (null != lData && null != lData.getPath() && FileUtil.isFileExist(lData.getPath())) {
            if (!this.isFinishing()) {// TODO: dirty fix :: need solution
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
        AdsSlotConfigUtil.updateCurrentRunningCount(AdsSlotConfigUtil.SLOT_TYPE.LANDING_SLOT_TYPE);
    }

    /**
     * Method to select video for only ad state
     */
    private void selectOnlyAdVideos() {
        SequenceData sequenceData = SequenceUtil.getCurrentSequence(StateMachine.SEQUENCE_TYPE.LANDING_TYPE);
        if (null == sequenceData) {
            sequenceData = SequenceUtil.getDefaultSequence(StateMachine.SEQUENCE_TYPE.LANDING_TYPE);
            SequenceUtil.updateNewSequence(sequenceData.getSequenceType(), sequenceData.getSequenceOrder());
        } else {
            //get next
            if (!SequenceUtil.isLastForVideoType(sequenceData)) {//not last , just increment the video count
                SequenceUtil.updateCurrentSequence(sequenceData);
            } else if (SequenceUtil.isLast(sequenceData.getSequenceType(), sequenceData.getSequenceOrder())) {
                sequenceData = SequenceUtil.getDefaultSequence(StateMachine.SEQUENCE_TYPE.LANDING_TYPE);
                SequenceUtil.updateNewSequence(sequenceData.getSequenceType(), sequenceData.getSequenceOrder());
            } else {
                sequenceData = SequenceUtil.getNextInSequence(StateMachine.SEQUENCE_TYPE.LANDING_TYPE, sequenceData);
                SequenceUtil.updateNewSequence(sequenceData.getSequenceType(), sequenceData.getSequenceOrder());
            }
        }
        //this steps we have sequenceData what to play
        Data data = VideoData.getVideoByType(sequenceData.getVideoType());
        if (null != data) {
            if (!this.isFinishing()) {
                if (null != data && null != data.getPath() && FileUtil.isFileExist(data.getPath())) {
                    playInOtherView(data);
                } else {
                    selectOnlyAdVideos();
                }
            }
        } else {
            VideoData.resetVideoSequencePlayedState(sequenceData.getVideoType());
            selectOnlyAdVideos();
        }
    }


    /**
     * Method to select video for only ad state
     */
    private void selectMovieAdVideos() {
        SequenceData sequenceData = SequenceUtil.getCurrentSequence(StateMachine.SEQUENCE_TYPE.MOVIE_INIT_TYPE);
        if (null == sequenceData) {
            sequenceData = SequenceUtil.getDefaultSequence(StateMachine.SEQUENCE_TYPE.MOVIE_INIT_TYPE);
            SequenceUtil.updateNewSequence(sequenceData.getSequenceType(), sequenceData.getSequenceOrder());
        } else {
            //get next
            if (!SequenceUtil.isLastForVideoType(sequenceData)) {//not last , just increment the video count
                SequenceUtil.updateCurrentSequence(sequenceData);
            } else if (SequenceUtil.isLast(sequenceData.getSequenceType(), sequenceData.getSequenceOrder())) {
                //all the init videos done
                sequenceData = new SequenceData();
                sequenceData.setVideoType(VideoProvider.VIDEO_TYPE.MOVIE);
            } else {
                sequenceData = SequenceUtil.getNextInSequence(StateMachine.SEQUENCE_TYPE.MOVIE_INIT_TYPE, sequenceData);
                SequenceUtil.updateNewSequence(sequenceData.getSequenceType(), sequenceData.getSequenceOrder());
            }
        }
        //this steps we have sequenceData what to play
        Data data = VideoData.getVideoByType(sequenceData.getVideoType());
        if (!this.isFinishing()) {
            switch (sequenceData.getVideoType()) {
                case VideoProvider.VIDEO_TYPE.MOVIE:
                    playInMovieView(data);
                    break;
                case VideoProvider.VIDEO_TYPE.INTRO_VIDEO:
                    //as this video is local , every time it will be there.So no need of else case
                    if (null != data && null != data.getType()) {
                        playInOtherView(data);
                    }
                    break;
                default:
                    if (null != data && null != data.getPath() && FileUtil.isFileExist(data.getPath())) {
                        playInOtherView(data);
                    } else {
                        selectMovieAdVideos();
                    }
                    break;
            }
        }
    }

    private void playInOtherView(Data data) {
        mMovieView.setVisibility(View.GONE);
        mOtherView.setVisibility(View.VISIBLE);
        mOtherView.setVideoURI(Uri.parse(data.getPath()));
        mOtherView.requestFocus();
        mOtherView.start();
        hideLoadingIcon();
        mStateMachine.videoInfo.updateVideoId(data.getAssetID());
        mStateMachine.videoInfo.setOtherUri(Uri.parse(data.getPath()));
        mStateMachine.changeState(mStateMachine.videoInfo.getCurrentState(), mStateMachine.getState(data.getType()));
        VideoData.updateVideoData(mContext, data);
    }

    private void playInMovieView(Data data) {
        if (null != data && null != data.getPath() && FileUtil.isFileExist(data.getPath())) {
            mMovieView.setVisibility(View.VISIBLE);
            mMovieView.setVideoURI(Uri.parse(data.getPath()));
            mMovieView.setMediaController(null);
            mMovieView.requestFocus();
            mMovieView.start();
            hideLoadingIcon();
            mStateMachine.videoInfo.updateVideoId(data.getAssetID());
            mStateMachine.videoInfo.setMovieUri(Uri.parse(data.getPath()));
            mStateMachine.changeState(mStateMachine.videoInfo.getCurrentState(), mStateMachine.getState(data.getType()));
            VideoData.updateVideoData(mContext, data);
        } else {
            hideLoadingIcon();
            mMovieView.setVisibility(View.GONE);
            mOtherView.setVisibility(View.GONE);
            mNoContentView.setVisibility(View.VISIBLE);
            mTaskHandler.sendEmptyMessageDelayed(TASK_EVENT.FINISH_ACTIVITY, NO_CONTENT_DISPLAY_TIME);
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
                mStateMachine.videoInfo.setBreakingUri(Uri.parse(lData.getPath()));
                mStateMachine.changeState(mStateMachine.videoInfo.getCurrentState(), StateMachine.PLAYING_STATE.BREAKING_VIDEO);
                VideoData.updateVideoData(mContext, lData);
                showBreakingVideoHeader();
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
                mStateMachine.changeState(mStateMachine.videoInfo.getCurrentState(), StateMachine.PLAYING_STATE.BREAKING_TEXT);
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
                mStateMachine.changeState(mStateMachine.videoInfo.getCurrentState(), StateMachine.PLAYING_STATE.COMPANY_AD);
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

    private void hideTickerText() {
        if (null != mNewFeedLayout && mNewFeedLayout.getVisibility() == View.VISIBLE) {
            mNewFeedLayout.setVisibility(View.GONE);
        }
    }

    private void showTickerTextIfExist() {
        String tickerText = VideoData.getTicketText();
        if (null != tickerText && !"".equals(tickerText)) {
            Log.d(TAG, "--(ticker)-- :: length " + tickerText.length());
            mNewsTextView.setText(tickerText);
            mNewFeedLayout.setVisibility(View.VISIBLE);
            mNewFeedLayout.bringToFront();
            long dismissTime = getDismissTime(tickerText.length());
            Log.d(TAG, "--(ticker)-- :: dismissTime " + dismissTime);
            mTaskHandler.sendEmptyMessageDelayed(TASK_EVENT.HIDE_TICKER_TEXT, dismissTime);
        }
    }

    private int getDismissTime(int length) {
        if (length < 50) {
            return 25 * 1000;
        } else {
            return (length / 2) * 1000;
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
                    VideoData.deleteFileById(mStateMachine.videoInfo.getVideoId());
                    mState.sendEmptyMessage(EVENT.PLAY_NEXT);
                    break;
                case TASK_EVENT.REMOVE_COMPANY_AD:
                    removeCompanyAd();
                    VideoData.deleteFileById(mStateMachine.videoInfo.getVideoId());
                    mState.sendEmptyMessage(EVENT.PLAY_NEXT);
                    break;
                case TASK_EVENT.FINISH_ACTIVITY:
                    if (!isFinishing()) {
                        finish();
                    }
                    break;
                case TASK_EVENT.SHOW_TICKER_TEXT:
                    showTickerTextIfExist();
                    break;
                case TASK_EVENT.HIDE_TICKER_TEXT:
                    hideTickerText();
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
            hideBreakingVideoHeaderIfRequired();
            showLoadingIcon();
            VideoData.updateVideoCompletedState(mStateMachine.videoInfo.getVideoId());
            VideoData.backgroundSearchForPendingDeleteVideo();
            //display location after advertisement
            if (mStateMachine.videoInfo.getCurrentState() == StateMachine.PLAYING_STATE.ADV && !isLocationInfoVisible()) {
                mTaskHandler.sendEmptyMessage(TASK_EVENT.DISPLAY_LOCATION_INFO);
                mTaskHandler.sendEmptyMessageDelayed(TASK_EVENT.HIDE_LOCATION_INFO, BANNER_TIMEOUT);
            }
            sendCompletedBroadcast();
            if (isDeleteType(mStateMachine.videoInfo.getCurrentState())) {
                VideoData.deleteFileById(mStateMachine.videoInfo.getVideoId());
            }
            if (mStateMachine.videoInfo.getVideoState() == StateMachine.VIDEO_STATE.MOVIE_AND_ADV && mStateMachine.videoInfo.getCurrentState() == StateMachine.PLAYING_STATE.ADV && !AdsSlotConfigUtil.isLast(AdsSlotConfigUtil.SLOT_TYPE.LANDING_SLOT_TYPE)) {
                startAd();
            } else {
                if (mStateMachine.videoInfo.getVideoState() == StateMachine.VIDEO_STATE.MOVIE_AND_ADV && mStateMachine.videoInfo.getCurrentState() == StateMachine.PLAYING_STATE.ADV) {
                    mTaskHandler.sendEmptyMessage(TASK_EVENT.PREPARE_FOR_NEXT_AD);
                }
                mState.sendEmptyMessage(EVENT.PLAY_NEXT);
            }
        }
    }


    public class MovieCompleteListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            sendCompletedBroadcast();
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

    private void postBackgroundSearch() {
        VideoTaskHandler.getInstance(mContext).removeMessages(VideoTaskHandler.TASK.BACK_GROUND_BREAKING_NEWS_SEARCH);
        VideoTaskHandler.getInstance(mContext).sendEmptyMessageDelayed(VideoTaskHandler.TASK.BACK_GROUND_BREAKING_NEWS_SEARCH, BACKGROUND_SEARCH_AFTER);
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
                case EVENT.RESUME:
                    mStateMachine.retainPersistenceState(getVideoState());
                    switch (mStateMachine.videoInfo.getTopPausedState()) {
                        case StateMachine.PLAYING_STATE.NONE:
                            mStateMachine.deletePersistState(getVideoState());
                            mStateMachine.reset();
                            postBackgroundSearch();
                            selectOnlyAdVideos();
                            break;
                        default:
                            postBackgroundSearch();
                            resumeVideo();
                            break;
                    }
                    break;

/*                case EVENT.PLAY_ADV:
                    AdsSlotConfigUtil.resetCurrentRunningCount(AdsSlotConfigUtil.SLOT_TYPE.LANDING_SLOT_TYPE);
                    if (VideoData.isAdvExist(mContext)) {
                        postBackgroundSearch();
                        pauseVideo();
                        startAd();
                    } else {
                        //advertisement video not present still schdule for future may be we will get later
                        mTaskHandler.sendEmptyMessage(TASK_EVENT.PREPARE_FOR_NEXT_AD);
                    }
                    break;*/

                case EVENT.PLAY_BREAKING_VIDEO:
                    pauseVideo();
                    startBreakingVideo();
                    break;
                case EVENT.PLAY_BREAKING_NEWS:
                    pauseVideo();
                    showBreakingNews();
                    mTaskHandler.sendEmptyMessageDelayed(TASK_EVENT.REMOVE_BREAKING_NEWS, BREAKING_NEWS_DISPLAY_TIME);
                    break;
                case EVENT.PLAY_COMPANY_AD:
                    pauseVideo();
                    showCompanyAd();
                    mTaskHandler.sendEmptyMessageDelayed(TASK_EVENT.REMOVE_COMPANY_AD, COMPANY_AD_DISPLAY_TIME);
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
            switch (msg.what) {
                case EVENT.PLAY_NEXT:
                case EVENT.RESUME:
                    mStateMachine.retainPersistenceState(getVideoState());
                    mStateMachine.print();
                    switch (mStateMachine.videoInfo.getTopPausedState()) {
                        case StateMachine.PLAYING_STATE.TRAVEL_VIDEO:
                        case StateMachine.PLAYING_STATE.SAFETY_VIDEO:
                        case StateMachine.PLAYING_STATE.INTRO_VIDEO:
                        case StateMachine.PLAYING_STATE.MOVIE:
                        case StateMachine.PLAYING_STATE.ADV:
                        case StateMachine.PLAYING_STATE.BREAKING_VIDEO:
                            Logger.debug(TAG, "--(debug)--  :: resuming ");
                            postBackgroundSearch();
                            resumeVideo();
                            break;
                        default:
                            Logger.debug(TAG, "--(debug)--  :: default ");
                            switch (mStateMachine.videoInfo.getCurrentState()) {
                                case StateMachine.PLAYING_STATE.MOVIE:
                                    //once finished , move to home
                                    mStateMachine.deletePersistState(getVideoState());
                                    VideoData.resetMovieSelection();
                                    mStateMachine.changeState(mStateMachine.videoInfo.getCurrentState(), StateMachine.PLAYING_STATE.MOVIE_FINISHED);
                                    if (!isFinishing()) {
                                        finish();
                                    }
                                    break;
                                default:
                                    postBackgroundSearch();
                                    selectMovieAdVideos();
                                    break;
                            }
                            break;
                    }
                    break;
                case EVENT.PLAY_ADV:
                    AdsSlotConfigUtil.resetCurrentRunningCount(AdsSlotConfigUtil.SLOT_TYPE.LANDING_SLOT_TYPE);
                    if (VideoData.isAdvExist(mContext)) {
                        postBackgroundSearch();
                        pauseVideo();
                        startAd();
                    } else {
                        //advertisement video not present still schdule for future may be we will get later
                        mTaskHandler.sendEmptyMessage(TASK_EVENT.PREPARE_FOR_NEXT_AD);
                    }
                    break;

                case EVENT.PLAY_BREAKING_VIDEO:
                    switch (mStateMachine.videoInfo.getCurrentState()) {
                        case StateMachine.PLAYING_STATE.MOVIE:
                        case StateMachine.PLAYING_STATE.TRAVEL_VIDEO:
                        case StateMachine.PLAYING_STATE.SAFETY_VIDEO:
                        case StateMachine.PLAYING_STATE.INTRO_VIDEO:
                        case StateMachine.PLAYING_STATE.ADV:
                            pauseVideo();
                            startBreakingVideo();
                            break;
                        case StateMachine.PLAYING_STATE.COMPANY_AD:
                        case StateMachine.PLAYING_STATE.BREAKING_TEXT:
                        case StateMachine.PLAYING_STATE.BREAKING_VIDEO:
                            Logger.debug(TAG, "--(debug)--  :: ignoring brk video :: play later ");
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
                            pauseVideo();
                            showBreakingNews();
                            mTaskHandler.sendEmptyMessageDelayed(TASK_EVENT.REMOVE_BREAKING_NEWS, BREAKING_NEWS_DISPLAY_TIME);
                            break;
                        case StateMachine.PLAYING_STATE.COMPANY_AD:
                        case StateMachine.PLAYING_STATE.BREAKING_TEXT:
                            Logger.debug(TAG, "--(debug)--  :: ignoring brk image :: play later ");
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
                            pauseVideo();
                            showCompanyAd();
                            mTaskHandler.sendEmptyMessageDelayed(TASK_EVENT.REMOVE_COMPANY_AD, COMPANY_AD_DISPLAY_TIME);
                            break;
                        case StateMachine.PLAYING_STATE.COMPANY_AD:
                        case StateMachine.PLAYING_STATE.BREAKING_TEXT:
                            Logger.debug(TAG, "--(debug)--  :: ignoring company ad :: play later ");
                            break;
                    }
                    break;
            }
        }
    }

    private void sendCompletedBroadcast() {
        Logger.debug(TAG, "sendCompletedBroadcast :: called");
        VideoData.updateVideoCompletedState(mStateMachine.videoInfo.getVideoId());
        VideoData.backgroundSearchForPendingDeleteVideo();
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
        int topState = mStateMachine.videoInfo.getTopPausedState();
        mStateMachine.videoInfo.removeTopState();
        if (topState == StateMachine.PLAYING_STATE.MOVIE) {
            mOtherView.setVisibility(View.GONE);
            mMovieView.setVideoURI(mStateMachine.videoInfo.getMovieUri());
            mMovieView.setVisibility(View.VISIBLE);
            mMovieView.seekTo(mStateMachine.videoInfo.getMovieSeekTime());
            mMovieView.start();
            mMovieView.setOnPreparedListener(mMoviePrepareListener);
            hideLoadingIcon();
            //reset the value so that it wont resume from same place again
        } else if (topState == StateMachine.PLAYING_STATE.BREAKING_VIDEO) {
            mMovieView.setVisibility(View.GONE);
            mOtherView.setVisibility(View.VISIBLE);
            mOtherView.setVideoURI(mStateMachine.videoInfo.getBreakingUri());
            mOtherView.seekTo(mStateMachine.videoInfo.getBreakingSeekTime());
            mOtherView.start();
            mOtherView.setOnPreparedListener(mOtherPrepareListener);
            hideLoadingIcon();
            showBreakingVideoHeader();
        } else {
            mMovieView.setVisibility(View.GONE);
            mOtherView.setVisibility(View.VISIBLE);
            mOtherView.setVideoURI(mStateMachine.videoInfo.getOtherUri());
            mOtherView.seekTo(mStateMachine.videoInfo.getOtherSeekTime());
            mOtherView.start();
            mOtherView.setOnPreparedListener(mOtherPrepareListener);
            hideLoadingIcon();
        }
        //anything change the current playing to present state
        mStateMachine.changeState(mStateMachine.videoInfo.getCurrentState(), topState);
        if (mStateMachine.videoInfo.getTopPausedState() == StateMachine.VIDEO_STATE.NONE) {
            mStateMachine.deletePersistState(getVideoState());
        }
    }

    /**
     * Method to check the video type is single existance
     *
     * @param playingType
     * @return
     */
    private boolean isDeleteType(int playingType) {
        switch (playingType) {
            case StateMachine.PLAYING_STATE.BREAKING_TEXT:
            case StateMachine.PLAYING_STATE.BREAKING_VIDEO:
            case StateMachine.PLAYING_STATE.COMPANY_AD:
                return true;
            default:
                return false;


        }
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

    private void showBreakingVideoHeader() {
        if (null != mBrkVideoHeader && mBrkVideoHeader.getVisibility() != View.VISIBLE) {
            mBrkVideoHeader.setVisibility(View.VISIBLE);
        }
    }

    private void hideBreakingVideoHeaderIfRequired() {
        if (null != mBrkVideoHeader && mBrkVideoHeader.getVisibility() == View.VISIBLE) {
            mBrkVideoHeader.setVisibility(View.GONE);
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
                    } else if (VideoProvider.VIDEO_TYPE.TICKER.equals(type)) {
                        mTaskHandler.sendEmptyMessage(TASK_EVENT.SHOW_TICKER_TEXT);
                    }
                }
            }
        }
    }


}

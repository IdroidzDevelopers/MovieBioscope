package com.lib.videoplayer.ui;

import android.content.ContentValues;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.lib.videoplayer.R;
import com.lib.videoplayer.database.VideoProvider;
import com.lib.videoplayer.util.VideoData;

public class VideoActivity extends AppCompatActivity implements View.OnTouchListener {
    private static final String TAG = VideoActivity.class.getSimpleName();
    private VideoView mMovieView;
    private VideoView mAdvView;
    private int videoTime;
    private RelativeLayout mHeader;
    private RelativeLayout mFooter;
    private Handler mHandler;
    private AdsCompleteListener mAdsCompleteListener;
    private MovieCompleteListener mMovieCompleteListener;

    /*********************
     * Task constants
     */

    public interface TASK_EVENT {
        int DISPLAY_LOCATION_INFO = 0;
        int HIDE_LOCATION_INFO = 1;

    }

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        startMovie();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                videoTime = mMovieView.getCurrentPosition();
                mMovieView.pause();
                mMovieView.setVisibility(View.GONE);
                startAd();

            }
        }, 10000);
    }

    private void startMovie() {
        mMovieView.setVideoURI(VideoData.getRandomMovieUri());
        mMovieView.setMediaController(null);
        mMovieView.requestFocus();
        mMovieView.start();
    }

    private void startAd() {
        mAdvView.setVisibility(View.VISIBLE);
        mAdvView.setVideoURI(VideoData.getRandomAdUri());
        mAdvView.setMediaController(null);
        mAdvView.requestFocus();
        mAdvView.start();
    }

    /**
     * Initialize the layout and get the reference for the views
     */

    private void initView() {
        setContentView(R.layout.video_layout);
        mHeader = (RelativeLayout) findViewById(R.id.top_banner_video);
        mFooter = (RelativeLayout) findViewById(R.id.bottom_banner_video);
        mMovieView = (VideoView) findViewById(R.id.movie_view);
        mAdvView = (VideoView) findViewById(R.id.ad_video);
        mMovieView.setOnTouchListener(this);
        mAdvView.setOnTouchListener(this);
        mHandler = new TaskHandler();
        mMovieCompleteListener = new MovieCompleteListener();
        mAdsCompleteListener = new AdsCompleteListener();
        mMovieView.setOnCompletionListener(mMovieCompleteListener);
        mAdvView.setOnCompletionListener(mAdsCompleteListener);

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

    private void putDummyData() {
        ContentValues lValues = new ContentValues();
        lValues.put(VideoProvider.VIDEO_COLUMNS.NAME, "hello");
        getContentResolver().insert(VideoProvider.CONTENT_URI_VIDEO_TABLE, lValues);
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

            }

        }
    }

    public class AdsCompleteListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mMovieView.setVisibility(View.VISIBLE);
            mMovieView.seekTo(videoTime);
            mMovieView.start();
            mAdvView.setVisibility(View.GONE);

            //Todo:test
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    videoTime = mMovieView.getCurrentPosition();
                    mMovieView.pause();
                    startAd();
                    mMovieView.setVisibility(View.GONE);

                }
            }, 10000);
        }
    }

    public class MovieCompleteListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {

        }
    }

    private void showLocationInfo() {
        mHeader.setVisibility(View.VISIBLE);
        mFooter.setVisibility(View.VISIBLE);
        mHeader.bringToFront();
        mFooter.bringToFront();
    }

    private void hideLocationInfo() {
        mHeader.setVisibility(View.GONE);
        mFooter.setVisibility(View.GONE);
    }
}

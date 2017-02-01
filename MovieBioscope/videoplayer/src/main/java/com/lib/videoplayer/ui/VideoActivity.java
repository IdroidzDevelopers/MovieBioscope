package com.lib.videoplayer.ui;

import android.content.ContentValues;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.lib.videoplayer.R;
import com.lib.videoplayer.database.VideoDatabaseProvider;

import java.io.File;

public class VideoActivity extends AppCompatActivity implements View.OnTouchListener {

    private VideoView mMovieView;
    private VideoView mAdvView;
    private int videoTime;
    private RelativeLayout mHeader;
    private RelativeLayout mFooter;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_layout);
        mHeader = (RelativeLayout) findViewById(R.id.top_banner_video);
        mFooter = (RelativeLayout) findViewById(R.id.bottom_banner_video);
        mMovieView = (VideoView) findViewById(R.id.movie_view);
        mAdvView = (VideoView) findViewById(R.id.ad_video);
        mMovieView.setVideoURI(Uri.parse(Environment.getExternalStorageDirectory() + "/movie_bioscope/Spotlight.mp4"));
        mMovieView.setMediaController(null);
        mMovieView.requestFocus();
        mMovieView.start();
        mMovieView.setOnTouchListener(this);
        mHandler = new Handler();
        mAdvView.setVideoURI(Uri.parse(Environment.getExternalStorageDirectory() + "/movie_bioscope/ad1.mp4"));
        mAdvView.setMediaController(null);

        mMovieView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        int time = mMovieView.getCurrentPosition();
                        mMovieView.requestFocus();
                        mMovieView.start();
                    }
                });

            }
        });

        mAdvView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMovieView.setVisibility(View.VISIBLE);
                mMovieView.seekTo(videoTime);
            }
        });
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
        lValues.put(VideoDatabaseProvider.VIDEO_COLUMNS.NAME, "hello");
        getContentResolver().insert(VideoDatabaseProvider.CONTENT_URI_VIDEO_TABLE, lValues);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                showHeaderFooter();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mHeader.setVisibility(View.GONE);
                        mFooter.setVisibility(View.GONE);
                    }
                }, 3 * 1000);
                break;

        }
        return false;
    }

    private void showHeaderFooter(){
        mHeader.setVisibility(View.VISIBLE);
        mFooter.setVisibility(View.VISIBLE);
        mHeader.bringToFront();
        mFooter.bringToFront();
    }
}

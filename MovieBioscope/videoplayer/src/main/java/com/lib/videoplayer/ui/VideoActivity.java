package com.lib.videoplayer.ui;

import android.content.ContentValues;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.lib.videoplayer.R;
import com.lib.videoplayer.database.VideoDatabaseProvider;

public class VideoActivity extends AppCompatActivity {

    private VideoView mMovieView;
    private VideoView mAdvView;
    private int videoTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.video_layout);
        putDummyData();
        mMovieView = (VideoView) findViewById(R.id.movie_view);
        mAdvView = (VideoView) findViewById(R.id.ad_video);
        mMovieView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample));
        mMovieView.setMediaController(new MediaController(this));
        mMovieView.requestFocus();
        mMovieView.start();

        mAdvView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ad));
        mAdvView.setMediaController(new MediaController(this));

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

    private void fullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void putDummyData() {
        ContentValues lValues = new ContentValues();
        lValues.put(VideoDatabaseProvider.VIDEO_COLUMNS.NAME, "hello");
        getContentResolver().insert(VideoDatabaseProvider.CONTENT_URI_VIDEO_TABLE, lValues);
    }
}

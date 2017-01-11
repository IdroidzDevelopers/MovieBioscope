package com.hyperbound.moviebioscope;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    VideoView videoView1;
    VideoView videoView2;
    Button Hide,Show;
    int videoTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView1=(VideoView)findViewById(R.id.video1);
        videoView2=(VideoView)findViewById(R.id.video2);
        Hide=(Button)findViewById(R.id.hide);
        Hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView1.pause();
                videoTime=videoView1.getCurrentPosition();
                videoView1.setVisibility(View.INVISIBLE);
            }
        });
        Show=(Button)findViewById(R.id.show);
        Show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView1.pause();
                videoTime=videoView1.getCurrentPosition();
                videoView1.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(),"can seek "+videoView1.canSeekForward(),Toast.LENGTH_SHORT).show();
                videoView2.requestFocus();
                videoView2.start();
                //videoView1.seekTo(videoTime);
            }
        });
        videoView1.setVideoURI(Uri.parse("android.resource://" +getPackageName()+ "/"+R.raw.sample));
        videoView1.setMediaController(new MediaController(this));
        videoView1.requestFocus();
        videoView1.start();

        videoView2.setVideoURI(Uri.parse("android.resource://" +getPackageName()+ "/"+R.raw.ad));
        videoView2.setMediaController(new MediaController(this));

        videoView1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        int time=videoView1.getCurrentPosition();
                        videoView1.requestFocus();
                        videoView1.start();
                    }
                });

            }
        });

        videoView2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView1.setVisibility(View.VISIBLE);
                videoView1.seekTo(videoTime);
            }
        });


    }
}

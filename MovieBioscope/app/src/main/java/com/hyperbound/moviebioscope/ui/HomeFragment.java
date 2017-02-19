package com.hyperbound.moviebioscope.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.hyperbound.moviebioscope.R;
import com.lib.videoplayer.ui.VideoActivity;
import com.lib.videoplayer.util.DownloadUtil;
import com.lib.videoplayer.util.StateMachine;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private static final long WAITING_TIME = 30 * 1000;
    private static final String ARG_VIDEO_STATE = "video_state";
    private ImageButton mPlayBottom;
    private View mRootView;
    private Handler mHandler;
    private StartVideoRunnable mRunnable;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.landing_fragment_layout, container, false);
        initView();
        return mRootView;
    }

    private void initView() {
        mPlayBottom = (ImageButton) mRootView.findViewById(R.id.play);
        mPlayBottom.setOnClickListener(this);
        mHandler = new Handler();
        mRunnable = new StartVideoRunnable();

        //testing
        DownloadUtil.beginDownload(getActivity(), "http:\\/\\/139.59.62.165\\/download\\/95b27ab225fd33f6c3137bcc07dac266", null, "Sample 2");
        //GoogleMapDistanceMatrix.requestForData(getActivity(),"Bangalore","Mumbai");
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        StartVideoTimer();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play:
                Intent lIntent = new Intent(getActivity(), VideoActivity.class);
                Bundle lBundle = new Bundle();
                lBundle.putInt(ARG_VIDEO_STATE, StateMachine.VIDEO_STATE.MOVIE_AND_ADV);
                lIntent.putExtra(ARG_VIDEO_STATE, lBundle);
                startActivity(lIntent);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
    }


    private void StartVideoTimer() {
        mHandler.postDelayed(mRunnable, WAITING_TIME);
    }

    private class StartVideoRunnable implements Runnable {

        @Override
        public void run() {
            Intent lIntent = new Intent(getActivity(), VideoActivity.class);
            Bundle lBundle = new Bundle();
            lBundle.putInt(ARG_VIDEO_STATE, StateMachine.VIDEO_STATE.ONLY_ADV);
            lIntent.putExtra(ARG_VIDEO_STATE, lBundle);
            startActivity(lIntent);
        }
    }

}
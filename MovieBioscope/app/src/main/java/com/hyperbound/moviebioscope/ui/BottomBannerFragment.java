package com.hyperbound.moviebioscope.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyperbound.moviebioscope.R;
import com.lib.videoplayer.util.TimeUtil;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link BottomBannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomBannerFragment extends Fragment {
    private static final String TAG = BottomBannerFragment.class.getSimpleName();
    private View mRootView;
    private TextView mCurrentTime;
    private MinuteReceiver mReceiver;

    public BottomBannerFragment() {
    }

    public static Fragment newInstance() {
        BottomBannerFragment fragment = new BottomBannerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.bottom_banner, container, false);
        initView();
        return mRootView;
    }

    private void initView() {
        mCurrentTime = (TextView) mRootView.findViewById(R.id.current_time);
        mReceiver = new MinuteReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentTime.setText(TimeUtil.getTime());
        IntentFilter lFilter = new IntentFilter();
        lFilter.addAction(Intent.ACTION_TIME_TICK);
        getActivity().registerReceiver(mReceiver, lFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
    }

    private class MinuteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive() :: time changed");
            mCurrentTime.setText(TimeUtil.getTime());
        }
    }
}

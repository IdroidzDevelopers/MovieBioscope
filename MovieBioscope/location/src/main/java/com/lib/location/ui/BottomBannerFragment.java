package com.lib.location.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lib.location.R;
import com.lib.location.util.TimeUtil;
import com.lib.route.objects.Route;
import com.lib.route.util.RouteTaskHandler;
import com.lib.route.util.RouteUtil;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link BottomBannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomBannerFragment extends Fragment {
    public static final String TAG = BottomBannerFragment.class.getSimpleName();
    private View mRootView;
    private TextView mSource;
    private TextView mDestination;

    private TextView mCurrentTime;
    private BroadcastReceiver mReceiver;

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
        mReceiver = new BroadcastReceiver();
        mSource = (TextView) mRootView.findViewById(R.id.source);
        mDestination = (TextView) mRootView.findViewById(R.id.destination);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter lFilter = new IntentFilter();
        lFilter.addAction(Intent.ACTION_TIME_TICK);
        getActivity().registerReceiver(mReceiver, lFilter);

        IntentFilter lLocalFilter = new IntentFilter();
        lLocalFilter.addAction(RouteTaskHandler.INTENT_LOCATION_CHANGED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, lLocalFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
        mCurrentTime.setText(TimeUtil.getTime());
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }

    private class BroadcastReceiver extends android.content.BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                    Log.d(TAG, "onReceive() :: time changed");
                    mCurrentTime.setText(TimeUtil.getTime());
                } else if (RouteTaskHandler.INTENT_LOCATION_CHANGED.equals(intent.getAction())) {
                    Log.d(TAG, "onReceive() :: route changed");
                    updateView();
                }
            }
        }
    }

    private void updateView() {
        Route lRoute = RouteUtil.getCurrentRoute(getActivity());
        String[] arr = null;
        if (null != lRoute && null != lRoute.getRouteName()) {
            arr = lRoute.getRouteName().split("-");
        }
        if (null != arr && arr.length > 1) {
            mSource.setText(arr[0].trim());
            mDestination.setText(arr[1].trim());
        }
    }
}

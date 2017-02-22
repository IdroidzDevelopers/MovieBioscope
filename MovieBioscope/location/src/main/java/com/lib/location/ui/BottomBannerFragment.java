package com.lib.location.ui;

import android.content.BroadcastReceiver;
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
import com.lib.location.model.LocationInfo;
import com.lib.location.util.LocationInterface;
import com.lib.location.util.LocationUtil;
import com.lib.location.util.TimeUtil;
import com.lib.location.volley.VolleyUtil;
import com.lib.route.objects.Route;
import com.lib.route.util.RouteTaskHandler;
import com.lib.route.util.RouteUtil;
import com.lib.utility.util.CustomIntent;


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
    private TextView mCurrentLocation;
    private TextView mDistanceToSource;
    private TextView mDistanceToDest;
    private TextView mTimeToDest;


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
        mReceiver = new Receiver();
        mSource = (TextView) mRootView.findViewById(R.id.source);
        mDestination = (TextView) mRootView.findViewById(R.id.destination);
        mCurrentLocation = (TextView) mRootView.findViewById(R.id.current_location);
        mDistanceToSource = (TextView) mRootView.findViewById(R.id.source_distance);
        mDistanceToDest = (TextView) mRootView.findViewById(R.id.destination_distance);
        mTimeToDest = (TextView) mRootView.findViewById(R.id.time_left);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter lFilter = new IntentFilter();
        lFilter.addAction(Intent.ACTION_TIME_TICK);
        getActivity().registerReceiver(mReceiver, lFilter);

        IntentFilter lLocalFilter = new IntentFilter();
        lLocalFilter.addAction(CustomIntent.ACTION_ROUTE_CHANGED);
        lLocalFilter.addAction(CustomIntent.ACTION_CURRENT_LOCATION_CHANGED);
        lLocalFilter.addAction(CustomIntent.ACTION_LOCATION_INFO_CHANGED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, lLocalFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRouteInfo();
        mCurrentTime.setText(TimeUtil.getTime());
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }

    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                    Log.d(TAG, "onReceive() :: time changed");
                    mCurrentTime.setText(TimeUtil.getTime());
                } else if (CustomIntent.ACTION_ROUTE_CHANGED.equals(intent.getAction())) {
                    Log.d(TAG, "onReceive() :: route changed");
                    updateRouteInfo();
                } else if (CustomIntent.ACTION_LOCATION_INFO_CHANGED.equals(intent.getAction())) {
                    Log.d(TAG, "onReceive() :: location info changed");
                    updateLocationInfo();
                } else if (CustomIntent.ACTION_CURRENT_LOCATION_CHANGED.equals(intent.getAction())) {
                    Log.d(TAG, "onReceive() :: ACTION_CURRENT_LOCATION_CHANGED");
                    updateCurrentLocation();
                }

            }
        }
    }

    private void updateLocationInfo() {
        LocationInfo locationInfo = LocationUtil.getLocationInfo();
        if (null != locationInfo) {
            mDistanceToSource.setText(locationInfo.getDistanceToSource());
            mDistanceToDest.setText(locationInfo.getDistanceToDestination());
            mTimeToDest.setText(locationInfo.getTimeToDestination());
        }
    }

    private void updateCurrentLocation() {
        LocationInfo locationInfo = LocationUtil.getCurrentLocation();
        if (null != locationInfo) {
            mCurrentLocation.setText(locationInfo.getCurrentLocation());
        }
    }

    private void updateRouteInfo() {
        Route lRoute = RouteUtil.getCurrentRoute(getActivity());
        if (null != lRoute && null != lRoute.getmRouteSource()&& null!=lRoute.getmRouteDestination()) {
            mSource.setText(lRoute.getmRouteSource());
            mDestination.setText(lRoute.getmRouteDestination());
            LocationUtil.insertOrUpdateRouteInfo(lRoute.getmRouteSource(), lRoute.getmRouteDestination());
            com.lib.location.LocationManager lm = new com.lib.location.LocationManager();
            lm.getCurrentLocation();
            VolleyUtil.getTravelInfo(lRoute.getmRouteSource(), lRoute.getmRouteDestination());
        }
    }
}

package com.hyperbound.moviebioscope.ui;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.hyperbound.moviebioscope.R;
import com.hyperbound.moviebioscope.objects.Route;
import com.hyperbound.moviebioscope.util.AppTaskHandler;
import com.hyperbound.moviebioscope.util.RouteUtil;
import com.hyperbound.moviebioscope.util.TimeUtil;
import com.lib.videoplayer.ui.VideoActivity;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private static final long WAITING_TIME = 30 * 1000;
    private ImageButton mPlayBottom;
    private View mRootView;
    private Handler mHandler;
    private StartVideoRunnable mRunnable;
    //Header and Footer
    private TextView mCurrentTime;
    private ImageView mRoute;

    //location
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationRequest mLocationRequest;
    private MinuteReceiver mReceiver;
    private Dialog lRouteDialog;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.landing_fragment_layout, container, false);
        initView();
        return mRootView;
    }

    private void initView() {
        mCurrentTime = (TextView) mRootView.findViewById(R.id.current_time);
        mPlayBottom = (ImageButton) mRootView.findViewById(R.id.play);
        mPlayBottom.setOnClickListener(this);
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();
        mReceiver = new MinuteReceiver();
        mRoute = (ImageView) mRootView.findViewById(R.id.route);
        mRoute.setOnClickListener(this);
        mHandler = new Handler();
        mRunnable = new StartVideoRunnable();

        //GoogleMapDistanceMatrix.requestForData(getActivity(),"Bangalore","Mumbai");
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (null != mGoogleApiClient) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "mGoogleApiClient " + mGoogleApiClient);
        mGoogleApiClient.disconnect();
        if (null != lRouteDialog) {
            lRouteDialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentTime.setText(TimeUtil.getTime().toLowerCase());
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
        IntentFilter lFilter = new IntentFilter();
        lFilter.addAction(Intent.ACTION_TIME_TICK);
        getActivity().registerReceiver(mReceiver, lFilter);
        StartVideoTimer();
    }

    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play:
                startActivity(new Intent(getActivity(), VideoActivity.class));
                Log.d("test" + TAG, "sending broadcast");
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent().setAction("android.intent.action.VIDEO_COMMAND_ACTION"));
                break;
            case R.id.route:
                showDialog();
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Log.d(TAG, "location " + mLocation);
        if (mLocation != null) {
            Log.d(TAG, "location " + mLocation.getLongitude());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "location " + location);
    }

    private class MinuteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive() :: time changed");
            mCurrentTime.setText(TimeUtil.getTime());
        }
    }

    private void showDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        List<Route> lRouteList = RouteUtil.getRoutes(getActivity());
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.route_row, R.id.route_text);
        for (Route route : lRouteList) {
            arrayAdapter.add(route.getRouteName());
        }
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String lRoute = arrayAdapter.getItem(which);
                Log.d(TAG, "onClick() " + lRoute);
                updateDefaultRoute(lRoute);
            }
        });
        lRouteDialog = builderSingle.create();
        lRouteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_color)));
        lRouteDialog.show();
    }

    private void updateDefaultRoute(String lRouteName) {
        if (null != lRouteName) {
            Message lMessage = new Message();
            lMessage.what = AppTaskHandler.TASK.UPDATE_DEFAULT_ROUTE;
            Bundle lBundle = new Bundle();
            lBundle.putString(AppTaskHandler.KEY.ROUTE_NAME, lRouteName);
            lMessage.setData(lBundle);
            AppTaskHandler.getInstance().sendMessage(lMessage);
        }
    }

    private void StartVideoTimer() {
        mHandler.postDelayed(mRunnable, WAITING_TIME);
    }

    private class StartVideoRunnable implements Runnable {

        @Override
        public void run() {
            startActivity(new Intent(getActivity(), VideoActivity.class));
        }
    }

}


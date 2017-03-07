package com.hyperbound.moviebioscope.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.hyperbound.moviebioscope.R;
import com.hyperbound.moviebioscope.app.BioscopeApp;
import com.hyperbound.moviebioscope.util.ImagePagerAdapter;
import com.hyperbound.moviebioscope.util.NetworkUtil;
import com.lib.route.RouteApplication;
import com.lib.route.objects.Route;
import com.lib.route.util.RouteUtil;
import com.lib.utility.util.CustomIntent;
import com.lib.utility.util.Logger;
import com.lib.videoplayer.ui.VideoActivity;
import com.lib.videoplayer.util.StateMachine;
import com.lib.videoplayer.util.VideoData;

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
    private ImageButton mPlayBottom;
    private View mRootView;
    private Handler mHandler;
    private StartVideoRunnable mRunnable;
    private ViewPager mViewPager;
    private ImagePagerAdapter mImagePagerAdapter;
    private BroadcastReceiver mReceiver;

    private Handler mSlideHandler;
    public static final int DELAY = 3 * 1000;
    private int page = 0;
    private AlertDialog mInternetDialog;
    private AlertDialog mGpsDialog;

    Runnable mSlideRunnable = new Runnable() {
        public void run() {
            if (mImagePagerAdapter.getCount() - 1 == page) {
                page = 0;
            } else {
                page++;
            }
            Log.d(TAG, "Page :: " + page);
            mViewPager.setCurrentItem(page, true);
            mSlideHandler.postDelayed(this, DELAY);
        }
    };

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
        Route defaultRoute = RouteUtil.getCurrentRoute(RouteApplication.getRouteContext());
        String routeId = null;
        if (null != defaultRoute) {
            routeId = defaultRoute.getmRouteId();
        }
        mImagePagerAdapter = new ImagePagerAdapter(getActivity(), RouteUtil.getImagesForRoute(routeId));
        mViewPager = (ViewPager) mRootView.findViewById(R.id.pager);
        mViewPager.setAdapter(mImagePagerAdapter);
        mPlayBottom = (ImageButton) mRootView.findViewById(R.id.play);
        mPlayBottom.setOnClickListener(this);
        mHandler = new Handler();
        mSlideHandler = new Handler();
        mRunnable = new StartVideoRunnable();
        mReceiver = new Receiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CustomIntent.ACTION_ROUTE_IMAGE_DOWNLOAD_COMPLETE);
        intentFilter.addAction(CustomIntent.ACTION_ROUTE_CHANGED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
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
        mSlideHandler.postDelayed(mSlideRunnable, DELAY);
        StartVideoTimer();
        if (!NetworkUtil.isInternetAvailable(getActivity())) {
            showInternetDialog();
        } else if (!NetworkUtil.isGPSEnabled(getActivity())) {
            showGPSDisabledAlertToUser();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play:
                Intent lIntent = new Intent(getActivity(), VideoActivity.class);
                Bundle lBundle = new Bundle();
                lBundle.putInt(CustomIntent.EXTRAS.VIDEO_STATE, StateMachine.VIDEO_STATE.MOVIE_AND_ADV);
                lIntent.putExtra(CustomIntent.EXTRAS.VIDEO_STATE, lBundle);
                startActivity(lIntent);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
        mSlideHandler.removeCallbacks(mSlideRunnable);
        if (null != mInternetDialog && mInternetDialog.isShowing()) {
            mInternetDialog.dismiss();
        }
        if (null != mGpsDialog && mGpsDialog.isShowing()) {
            mGpsDialog.dismiss();
        }
    }


    public void StartVideoTimer() {
        mHandler.removeCallbacks(mSlideRunnable);
        mHandler.postDelayed(mRunnable, WAITING_TIME);
    }

    private class StartVideoRunnable implements Runnable {

        @Override
        public void run() {
            if (!isDialogVisible() && VideoData.isAdvExist(BioscopeApp.getContext())) {
                Intent lIntent = new Intent(getActivity(), VideoActivity.class);
                Bundle lBundle = new Bundle();
                lBundle.putInt(CustomIntent.EXTRAS.VIDEO_STATE, StateMachine.VIDEO_STATE.ONLY_ADV);
                lIntent.putExtra(CustomIntent.EXTRAS.VIDEO_STATE, lBundle);
                startActivity(lIntent);
            } else {
                StartVideoTimer();
            }
        }
    }

    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                switch (intent.getAction()) {
                    case CustomIntent.ACTION_ROUTE_CHANGED:
                        Route route = RouteUtil.getCurrentRoute(RouteApplication.getRouteContext());
                        if (null != route) {
                            mImagePagerAdapter = new ImagePagerAdapter(getActivity(), RouteUtil.getImagesForRoute(route.getmRouteId()));
                            mViewPager.setAdapter(mImagePagerAdapter);
                        }
                        break;
                    case CustomIntent.ACTION_ROUTE_IMAGE_DOWNLOAD_COMPLETE:
                        String newRoute = intent.getStringExtra(CustomIntent.EXTRAS.ROUTE_ID);
                        Route defaultRoute = RouteUtil.getCurrentRoute(RouteApplication.getRouteContext());
                        Log.d(TAG, "ACTION_ROUTE_IMAGE_DOWNLOAD_COMPLETE :: newRoute " + newRoute + " defaultRouteId " + defaultRoute);
                        if (null != defaultRoute && null != newRoute && newRoute.equals(defaultRoute.getmRouteId())) {
                            mImagePagerAdapter = new ImagePagerAdapter(getActivity(), RouteUtil.getImagesForRoute(defaultRoute.getmRouteId()));
                            mViewPager.setAdapter(mImagePagerAdapter);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(getString(R.string.gps_dialog_text))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.setting),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        mGpsDialog = alertDialogBuilder.create();
        mGpsDialog.show();
    }


    private void showInternetDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(getString(R.string.internet_dialog_text))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.setting),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        mInternetDialog = alertDialogBuilder.create();
        mInternetDialog.show();
    }

    private boolean isDialogVisible() {
        if (null != mGpsDialog && mGpsDialog.isShowing()) {
            Logger.debug(TAG, "isDialogVisible : true");
            return true;
        } else if (null != mInternetDialog && mInternetDialog.isShowing()) {
            Logger.debug(TAG, "isDialogVisible : true");
            return true;
        } else {
            Logger.debug(TAG, "isDialogVisible : false");
            return false;
        }
    }
}
package com.lib.location.ui;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lib.location.R;
import com.lib.location.model.LocationInfo;
import com.lib.location.util.LocationInterface;
import com.lib.location.util.LocationUtil;
import com.lib.location.util.RouteAdapter;
import com.lib.route.objects.Route;
import com.lib.route.util.RouteTaskHandler;
import com.lib.route.util.RouteUtil;
import com.lib.utility.util.CustomIntent;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link TopBannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopBannerFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = TopBannerFragment.class.getSimpleName();
    private View mRootView;
    private ImageView mRoute;
    private ImageView mHome;
    private ImageView mMovie;
    private static final String ARG_PARAM1 = "param1";
    private Dialog mRouteDialog;
    private String mType;
    private BroadcastReceiver mReceiver;

    private TextView mJourneyTime;
    private TextView mJourneyDistance;

    public interface TYPE {
        String HOME_ICON_TYPE = "home_icon_type";
        String NORMAL_TYPE = "normal_type";

    }

    ;

    public TopBannerFragment() {
    }

    public static Fragment newInstance(String aType) {
        TopBannerFragment fragment = new TopBannerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, aType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            mType = getArguments().getString(ARG_PARAM1);
        }
        if (TYPE.HOME_ICON_TYPE.equals(mType)) {
            mRootView = inflater.inflate(R.layout.top_banner_video, container, false);
        } else {
            mRootView = inflater.inflate(R.layout.top_banner, container, false);
        }
        initView();
        return mRootView;
    }

    private void initView() {
        mReceiver = new Receiver();
        mRoute = (ImageView) mRootView.findViewById(R.id.route);
        mJourneyTime = (TextView) mRootView.findViewById(R.id.journey_hours);
        mJourneyDistance = (TextView) mRootView.findViewById(R.id.total_distance);
        mRoute.setOnClickListener(this);
        if (mType.equals(TYPE.HOME_ICON_TYPE)) {
            mHome = (ImageView) mRootView.findViewById(R.id.home);
            mHome.setOnClickListener(this);
        }
        mMovie=(ImageView)mRootView.findViewById(R.id.movie_list);
        mMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent().setAction(CustomIntent.ACTION_MOVIE_LIST));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter lLocalFilter = new IntentFilter();
        lLocalFilter.addAction(CustomIntent.ACTION_JOURNEY_INFO_CHANGED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, lLocalFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateJourneyInfoView();
    }


    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        if (null != mRouteDialog) {
            mRouteDialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.route) {
            showDialog();
        } else if (i == R.id.home) {
            getActivity().finish();
        }
    }

    private void showDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        List<Route> lRouteList = RouteUtil.getRoutes(getActivity());
        final RouteAdapter arrayAdapter = new RouteAdapter(getActivity(), R.layout.route_row, lRouteList);
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Route lRoute = arrayAdapter.getItem(which);
                Log.d(TAG, "onClick() " + lRoute.getmRouteId());
                updateDefaultRoute(lRoute.getmRouteId());
            }
        });
        mRouteDialog = builderSingle.create();
        mRouteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_color)));
        mRouteDialog.show();
    }


    private void updateDefaultRoute(String lRouteId) {
        if (null != lRouteId) {
            Message lMessage = new Message();
            lMessage.what = RouteTaskHandler.TASK.UPDATE_DEFAULT_ROUTE;
            Bundle lBundle = new Bundle();
            lBundle.putString(RouteTaskHandler.KEY.ROUTE_ID, lRouteId);
            lMessage.setData(lBundle);
            RouteTaskHandler.getInstance(getActivity()).sendMessage(lMessage);
        }
    }

    private class Receiver extends android.content.BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                if (CustomIntent.ACTION_JOURNEY_INFO_CHANGED.equals(intent.getAction())) {
                    Log.d(TAG, "onReceive() :: ACTION_JOURNEY_INFO_CHANGED");
                    updateJourneyInfoView();
                }

            }
        }
    }

    private void updateJourneyInfoView() {
        LocationInfo info = LocationUtil.getJourneyInfo();
        if (null != info) {
            mJourneyDistance.setText(info.getTotalDistance());
            mJourneyTime.setText(info.getTotalJourneyTime());
        }
    }
}

package com.hyperbound.moviebioscope.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.hyperbound.moviebioscope.R;
import com.hyperbound.moviebioscope.objects.Route;
import com.hyperbound.moviebioscope.util.AppTaskHandler;
import com.hyperbound.moviebioscope.util.RouteUtil;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link TopBannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopBannerFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = TopBannerFragment.class.getSimpleName();
    private View mRootView;
    private ImageView mRoute;
    private Dialog mRouteDialog;

    public TopBannerFragment() {
    }

    public static Fragment newInstance() {
        TopBannerFragment fragment = new TopBannerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.top_banner, container, false);
        initView();
        return mRootView;
    }

    private void initView() {
        mRoute = (ImageView) mRootView.findViewById(R.id.route);
        mRoute.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (null != mRouteDialog) {
            mRouteDialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.route:
                showDialog();
                break;
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
        mRouteDialog = builderSingle.create();
        mRouteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_color)));
        mRouteDialog.show();
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
}

package com.lib.location.ui;

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

import com.lib.location.R;
import com.lib.route.objects.Route;
import com.lib.route.util.RouteTaskHandler;
import com.lib.route.util.RouteUtil;

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
    private static final String ARG_PARAM1 = "param1";
    private Dialog mRouteDialog;
    private String mType;

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
        mRoute = (ImageView) mRootView.findViewById(R.id.route);
        mRoute.setOnClickListener(this);
        if (mType.equals(TYPE.HOME_ICON_TYPE)) {
            mHome = (ImageView) mRootView.findViewById(R.id.home);
            mHome.setOnClickListener(this);
        }
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
            lMessage.what = RouteTaskHandler.TASK.UPDATE_DEFAULT_ROUTE;
            Bundle lBundle = new Bundle();
            lBundle.putString(RouteTaskHandler.KEY.ROUTE_NAME, lRouteName);
            lMessage.setData(lBundle);
            RouteTaskHandler.getInstance(getActivity()).sendMessage(lMessage);
        }
    }
}

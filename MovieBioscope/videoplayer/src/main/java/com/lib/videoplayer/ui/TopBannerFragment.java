package com.lib.videoplayer.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.lib.videoplayer.R;

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
    private Dialog mRouteDialog;

    public TopBannerFragment() {
        super();
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
        mRootView = inflater.inflate(R.layout.top_banner_video, container, false);
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
        int i = view.getId();
        if (i == R.id.route) {
            Toast.makeText(getActivity(), "onClick called", Toast.LENGTH_LONG).show();

        }
    }

   /* private void showDialog() {
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
    }*/
}

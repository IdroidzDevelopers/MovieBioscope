package com.hyperbound.moviebioscope.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hyperbound.moviebioscope.R;
import com.hyperbound.moviebioscope.util.TimeUtil;
import com.lib.videoplayer.ui.VideoActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private ImageButton mPlayBottom;
    private View mRootView;
    private TextView mCurrentTime;

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
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentTime.setText(TimeUtil.getTime().toLowerCase());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play:
                startActivity(new Intent(getActivity(), VideoActivity.class));
                Log.d("test" + TAG, "sending broadcast");
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent().setAction("android.intent.action.VIDEO_COMMAND_ACTION"));
                break;
        }
    }
}

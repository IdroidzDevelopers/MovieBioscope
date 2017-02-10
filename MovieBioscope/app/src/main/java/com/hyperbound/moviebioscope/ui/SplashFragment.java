package com.hyperbound.moviebioscope.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyperbound.moviebioscope.R;
import com.hyperbound.moviebioscope.util.BusUtil;
import com.hyperbound.moviebioscope.util.NetworkUtil;
import com.hyperbound.moviebioscope.util.TaskHandler;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SplashFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplashFragment extends Fragment {
    private static final String TAG = SplashFragment.class.getSimpleName();
    private static final long SPLASH_SCREEN_TIME_OUT = 3 * 1000;
    private View mRootView;
    private Handler mHandler;

    public SplashFragment() {
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
        SplashFragment fragment = new SplashFragment();
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
        mRootView = inflater.inflate(R.layout.splash_layout, container, false);
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                moveToNextPage();
            }
        }, SPLASH_SCREEN_TIME_OUT);
        return mRootView;
    }


    private void moveToNextPage() {
        FragmentTransaction lTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        if (BusUtil.isRegistrationNumberAvailable(getActivity())) {
            lTransaction.replace(R.id.container, HomeFragment.newInstance());
        } else {
            lTransaction.replace(R.id.container, RegistrationFragment.newInstance());
        }
        lTransaction.commit();
    }


}
package com.app.navajhalaka.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.navajhalaka.R;
import com.app.navajhalaka.util.BusUtil;
import com.lib.location.ui.BottomBannerFragment;
import com.lib.location.ui.TopBannerFragment;
import com.lib.utility.util.Logger;

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
    private Runnable mMoveNextRunnable;

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
        mMoveNextRunnable = new MovePageRunnable();
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mHandler && null != mMoveNextRunnable) {
            mHandler.postDelayed(mMoveNextRunnable, SPLASH_SCREEN_TIME_OUT);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != mHandler && null != mMoveNextRunnable) {
            mHandler.removeCallbacks(mMoveNextRunnable);
        }
    }

    private class MovePageRunnable implements Runnable {

        @Override
        public void run() {
            moveToNextPage();
        }
    }


    private void moveToNextPage() {
        FragmentTransaction lTransaction = null;
        if (null != getActivity() && null != getActivity().getSupportFragmentManager()) {
            lTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        }
        if (null != lTransaction) {
            if (BusUtil.isRegistrationNumberAvailable(getActivity())) {
                lTransaction.replace(R.id.container, HomeFragment.newInstance());
                lTransaction.replace(R.id.bottom_container, BottomBannerFragment.newInstance());
                lTransaction.replace(R.id.top_container, TopBannerFragment.newInstance(TopBannerFragment.TYPE.NORMAL_TYPE));
            } else {
                lTransaction.replace(R.id.container, RegistrationFragment.newInstance());
            }
            lTransaction.commit();
        } else {
            Logger.debug(TAG, "moveToNextPage :: lTransaction is null " );
        }
    }


}

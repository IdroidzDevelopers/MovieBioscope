package com.lib.videoplayer.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lib.videoplayer.R;
import com.lib.videoplayer.object.Data;
import com.lib.videoplayer.util.StateMachine;
import com.lib.videoplayer.util.VideoData;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link CompanyAdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompanyAdFragment extends Fragment {
    public static final String TAG = CompanyAdFragment.class.getSimpleName();
    private View mRootView;
    private ImageView mCompanyAdImage;

    public CompanyAdFragment() {
    }

    public static Fragment newInstance() {
        CompanyAdFragment fragment = new CompanyAdFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.company_ad, container, false);
        initView();
        return mRootView;
    }

    private void initView() {
        mCompanyAdImage = (ImageView) mRootView.findViewById(R.id.company_ad_image);
        Data data = VideoData.getCompanyAd(getActivity());
        if (null != data && null != data.getPath()) {
            StateMachine.getInstance().videoInfo.setVideoId(data.getAssetID());
            mCompanyAdImage.setImageURI(Uri.parse(data.getPath()));
            VideoData.updateVideoData(getActivity(), data);
        }
    }

}

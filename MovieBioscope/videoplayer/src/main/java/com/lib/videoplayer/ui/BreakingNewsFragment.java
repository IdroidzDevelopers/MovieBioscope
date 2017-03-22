package com.lib.videoplayer.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lib.videoplayer.R;
import com.lib.videoplayer.object.Data;
import com.lib.videoplayer.util.StateMachine;
import com.lib.videoplayer.util.VideoData;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link BreakingNewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BreakingNewsFragment extends Fragment {
    public static final String TAG = BreakingNewsFragment.class.getSimpleName();
    private View mRootView;
    private ImageView mBreakingImage;
    private TextView mBreakingMessage;

    public BreakingNewsFragment() {
    }

    public static Fragment newInstance() {
        BreakingNewsFragment fragment = new BreakingNewsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.breaking_news, container, false);
        initView();
        return mRootView;
    }

    private void initView() {
        mBreakingImage = (ImageView) mRootView.findViewById(R.id.breaking_new_image);
        mBreakingMessage = (TextView) mRootView.findViewById(R.id.breaking_news_message);
        Data data = VideoData.getBreakingNews(getActivity());
        if (null != data && null != data.getPath()) {
            StateMachine.getInstance().videoInfo.setVideoId(data.getAssetID());
            mBreakingImage.setImageURI(Uri.parse(data.getPath()));
            VideoData.updateVideoData(getActivity(), data);
        }
        if (null != data && null != data.getMessage()) {
            mBreakingMessage.setText(data.getMessage());
        }
    }

}

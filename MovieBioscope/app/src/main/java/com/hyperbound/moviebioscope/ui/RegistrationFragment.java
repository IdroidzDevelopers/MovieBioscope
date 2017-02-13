package com.hyperbound.moviebioscope.ui;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hyperbound.moviebioscope.R;
import com.hyperbound.moviebioscope.util.AppTaskHandler;
import com.lib.location.ui.BottomBannerFragment;
import com.lib.location.ui.TopBannerFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = RegistrationFragment.class.getSimpleName();
    private View mRootView;
    private Button mSubmitButton;
    private EditText mRegNumber;

    public RegistrationFragment() {
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
        RegistrationFragment fragment = new RegistrationFragment();
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
        mRootView = inflater.inflate(R.layout.registration_layout, container, false);
        initView();
        return mRootView;
    }

    private void initView() {
        mSubmitButton = (Button) mRootView.findViewById(R.id.submit_button);
        mSubmitButton.setOnClickListener(this);
        mRegNumber = (EditText) mRootView.findViewById(R.id.registration_number);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_button:
                //if (NetworkUtil.isInternetAvailable(getActivity())) {
                    String lRegNumber = mRegNumber.getText().toString().trim();
                    if (!TextUtils.isEmpty(lRegNumber)) {
                        moveToNextPage();
                        //put entry in the table and send it to cloud
                        saveDataInBackgroundThread(lRegNumber);
                        //TODO: sending to cloud

                    } else {
                        mRegNumber.setError(getString(R.string.empty_reg_text));
                    }
               /* } else {
                    Toast.makeText(getActivity(), getString(R.string.no_internet_text), Toast.LENGTH_LONG).show();
                }*/
                break;
        }
    }

    private void saveDataInBackgroundThread(String lRegNumber) {
        if (null != lRegNumber) {
            Message lMessage = new Message();
            lMessage.what = AppTaskHandler.TASK.SAVE_BUS_DATA;
            Bundle lBundle = new Bundle();
            lBundle.putString(AppTaskHandler.KEY.REG_NUMBER, lRegNumber);
            lMessage.setData(lBundle);
            AppTaskHandler.getInstance().sendMessage(lMessage);
        }
    }

    private void moveToNextPage() {
        FragmentTransaction lTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        lTransaction.replace(R.id.container, HomeFragment.newInstance());
        lTransaction.replace(R.id.top_container, TopBannerFragment.newInstance(TopBannerFragment.TYPE.NORMAL_TYPE));
        lTransaction.replace(R.id.bottom_container, BottomBannerFragment.newInstance());
        lTransaction.commit();
    }


}

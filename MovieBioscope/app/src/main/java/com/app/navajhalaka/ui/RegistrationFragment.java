package com.app.navajhalaka.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.navajhalaka.R;
import com.app.navajhalaka.app.BioscopeApp;
import com.app.navajhalaka.util.AppTaskHandler;
import com.app.navajhalaka.util.NetworkUtil;
import com.app.navajhalaka.volley.VolleyUtil;
import com.lib.location.ui.BottomBannerFragment;
import com.lib.location.ui.TopBannerFragment;
import com.lib.utility.util.CustomIntent;

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
    private Receiver mReceiver;
    private String mRegistrationNumber;
    private ProgressDialog mProgressDialog;
    private Dialog mDialog;

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

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CustomIntent.ACTION_ROUTE_RECEIVED);
        intentFilter.addAction(CustomIntent.ACTION_INVALID_BUS_NUMBER);
        LocalBroadcastManager.getInstance(BioscopeApp.getContext()).registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(BioscopeApp.getContext()).unregisterReceiver(mReceiver);
        dismissProgressDialog();
        dismissDialog();
    }

    private void initView() {
        mSubmitButton = (Button) mRootView.findViewById(R.id.submit_button);
        mSubmitButton.setOnClickListener(this);
        mRegNumber = (EditText) mRootView.findViewById(R.id.registration_number);
        mReceiver = new Receiver();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_button:
                if (NetworkUtil.isInternetAvailable(getActivity())) {
                    mRegistrationNumber = mRegNumber.getText().toString();
                    if (!TextUtils.isEmpty(mRegistrationNumber)) {
                        showProgressDialog();
                        VolleyUtil.getBusDetails(mRegistrationNumber);
                    } else {
                        mRegNumber.setError(getString(R.string.empty_reg_text));
                    }
                } else {
                    showDialog(getString(R.string.no_internet_text), getString(R.string.ok_text));
                }
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

    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                if (CustomIntent.ACTION_ROUTE_RECEIVED.equals(intent.getAction())) {
                    dismissProgressDialog();
                    //saveDataInBackgroundThread(mRegistrationNumber);
                    moveToNextPage();
                } else if (CustomIntent.ACTION_INVALID_BUS_NUMBER.equals(intent.getAction())) {
                    dismissProgressDialog();
                    showDialog(getString(R.string.invalid_bus_number_text), getString(R.string.ok_text));
                }
            }
        }
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(getString(R.string.validating_text));

        // Set the progress dialog background color
        if (null != mProgressDialog && null != mProgressDialog.getWindow()) {
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
        }
        mProgressDialog.setIndeterminate(false);
        // Finally, show the progress dialog
        mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void showDialog(String message, String buttonText) {
        mDialog = new Dialog(getActivity());
        mDialog.setContentView(R.layout.dialog_layout);
        TextView messageTextView = (TextView) mDialog.findViewById(R.id.message);
        messageTextView.setText(message);
        Button positiveButton = (Button) mDialog.findViewById(R.id.positive_button);
        positiveButton.setText(buttonText);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog();
            }
        });
        mDialog.show();
    }

    private void dismissDialog() {
        if (null != mDialog && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

}

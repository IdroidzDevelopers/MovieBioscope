package com.app.navajhalaka.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.app.navajhalaka.R;
import com.app.navajhalaka.util.BusUtil;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNotificationBar();
        addLayout();

    }

    private void addLayout() {
        setContentView(R.layout.main_layout);
        FragmentTransaction lTransaction = getSupportFragmentManager().beginTransaction();
        lTransaction.add(R.id.container, SplashFragment.newInstance());
        lTransaction.commit();
    }


    private void hideNotificationBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

}

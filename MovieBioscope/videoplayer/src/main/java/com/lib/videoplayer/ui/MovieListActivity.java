package com.lib.videoplayer.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.view.WindowManager;

import com.lib.utility.util.Logger;
import com.lib.videoplayer.R;
import com.lib.videoplayer.util.MovieRVAdapter;

/**
 * Created by aarokiax on 2/28/2017.
 */

public class MovieListActivity extends FragmentActivity implements MovieRVAdapter.MovieSelectCallback {
    private static final String TAG = MovieListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNotificationBar();
        initLayout();
    }

    private void initLayout() {
        setContentView(R.layout.movielist_layout);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.movie_list_container, MovieListFragment.newInstance());
        transaction.commit();
    }

    private void hideNotificationBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    @Override
    public void onMovieSelected() {
        if (!isFinishing()) {
            finish();
        }
    }
}

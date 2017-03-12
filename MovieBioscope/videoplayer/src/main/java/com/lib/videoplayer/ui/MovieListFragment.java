package com.lib.videoplayer.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lib.utility.util.Logger;
import com.lib.videoplayer.R;
import com.lib.videoplayer.object.MoviesList;
import com.lib.videoplayer.util.MovieListRVAdapter;
import com.lib.videoplayer.util.VideoData;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MovieListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieListFragment extends Fragment {
    private static final String TAG = MovieListFragment.class.getSimpleName();
    private View mRootView;
    private MovieListRVAdapter adapter;
    private RecyclerView moviesRecyclerView;
    private List<MoviesList> movieList;
    private TextView emptyView;
    private ProgressBar mProgressBar;

    public MovieListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoadingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieListFragment newInstance() {
        MovieListFragment fragment = new MovieListFragment();
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
        mRootView = inflater.inflate(R.layout.movie_list_fragment, container, false);
        emptyView = (TextView) mRootView.findViewById(R.id.empty_view);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.loading);
        moviesRecyclerView = (RecyclerView) mRootView.findViewById(R.id.movies_language_list);
        moviesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        moviesRecyclerView.setLayoutManager(llm);
        movieList = new ArrayList<MoviesList>();
        new LoadDataAsync().execute();
        return mRootView;
    }


    private class LoadDataAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            movieList = VideoData.getMoviesList();
            adapter = new MovieListRVAdapter(getActivity(), movieList);
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            moviesRecyclerView.setAdapter(adapter);
             mProgressBar.setVisibility(View.GONE);
            if (movieList.isEmpty()) {
                moviesRecyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                moviesRecyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
        }
    }
}

package com.lib.videoplayer.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.lib.videoplayer.R;
import com.lib.videoplayer.object.Movie;
import com.lib.videoplayer.object.MoviesList;
import com.lib.videoplayer.util.MovieListRVAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aarokiax on 2/22/2017.
 */

public class MovieDialog extends DialogFragment {

    Button mButton;
    EditText mEditText;
    onSubmitListener mListener;
    String text = "";

    private MovieListRVAdapter adapter;
    RecyclerView moviesRecyclerView;
    List<MoviesList> movieList;

    interface onSubmitListener {
        void setOnSubmitListener(String arg);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        if (null != dialog.getWindow()) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        dialog.setContentView(R.layout.movies_dialog);
        dialog.show();
        moviesRecyclerView = (RecyclerView) dialog.findViewById(R.id.movies_language_list);
        moviesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        moviesRecyclerView.setLayoutManager(llm);
        movieList = new ArrayList<MoviesList>();
        putDummyData();
        adapter = new MovieListRVAdapter(getActivity(), movieList);
        moviesRecyclerView.setAdapter(adapter);
        return dialog;
    }

    private void putDummyData() {

        MoviesList data = new MoviesList();
        data.setLanguage("Hindi");
        List<Movie> list = new ArrayList<Movie>();
        for (int i = 0; i < 10; i++) {
            Movie movie = new Movie();
            movie.setMovieId(i + "x");
            movie.setMovieName("Dhangal" + i);
            list.add(movie);
        }
        data.setMovies(list);

        MoviesList data1 = new MoviesList();
        data1.setLanguage("Tamil");
        List<Movie> list1 = new ArrayList<Movie>();
        for (int i = 0; i < 10; i++) {
            Movie movie = new Movie();
            movie.setMovieId(i + "x");
            movie.setMovieName("Thupakki" + i);
            list1.add(movie);
        }
        data1.setMovies(list1);

        MoviesList data2 = new MoviesList();
        data2.setLanguage("English");
        List<Movie> list2 = new ArrayList<Movie>();
        for (int i = 0; i < 10; i++) {
            Movie movie = new Movie();
            movie.setMovieId(i + "x");
            movie.setMovieName("Dragon" + i);
            list2.add(movie);
        }
        data2.setMovies(list2);

        MoviesList data3 = new MoviesList();
        data3.setLanguage("Odiya");
        List<Movie> list3 = new ArrayList<Movie>();
        for (int i = 0; i < 10; i++) {
            Movie movie = new Movie();
            movie.setMovieId(i + "x");
            movie.setMovieName("Dragon" + i);
            list3.add(movie);
        }
        data3.setMovies(list3);

        MoviesList data4 = new MoviesList();
        data4.setLanguage("Odiya");
        List<Movie> list4 = new ArrayList<Movie>();
        for (int i = 0; i < 10; i++) {
            Movie movie = new Movie();
            movie.setMovieId(i + "x");
            movie.setMovieName("Dragon" + i);
            list4.add(movie);
        }
        data4.setMovies(list4);

        MoviesList data5 = new MoviesList();
        data5.setLanguage("Odiya");
        List<Movie> list5 = new ArrayList<Movie>();
        for (int i = 0; i < 10; i++) {
            Movie movie = new Movie();
            movie.setMovieId(i + "x");
            movie.setMovieName("Dragon" + i);
            list5.add(movie);
        }
        data5.setMovies(list5);

        MoviesList data6 = new MoviesList();
        data6.setLanguage("Odiya");
        List<Movie> list6 = new ArrayList<Movie>();
        for (int i = 0; i < 10; i++) {
            Movie movie = new Movie();
            movie.setMovieId(i + "x");
            movie.setMovieName("Dragon" + i);
            list6.add(movie);
        }
        data6.setMovies(list6);

        movieList.add(data);
        movieList.add(data1);
        movieList.add(data2);
        movieList.add(data3);
        movieList.add(data4);
        movieList.add(data5);
        movieList.add(data6);
    }
}

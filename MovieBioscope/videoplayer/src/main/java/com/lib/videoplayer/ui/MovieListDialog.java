package com.lib.videoplayer.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lib.utility.util.CustomIntent;
import com.lib.videoplayer.R;
import com.lib.videoplayer.object.Movie;
import com.lib.videoplayer.object.MoviesList;
import com.lib.videoplayer.util.MovieListRVAdapter;
import com.lib.videoplayer.util.MovieRVAdapter;
import com.lib.videoplayer.util.VideoData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aarokiax on 2/28/2017.
 */

public class MovieListDialog extends Activity implements MovieRVAdapter.MovieSelectCallback
{
    private static final String TAG = "MovieListDialog";
    static final String ACTION = "android.navajhalka.movielist";

    private MovieListRVAdapter adapter;
    RecyclerView moviesRecyclerView;
    List<MoviesList> movieList;
    private TextView emptyView;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayAlert();
    }

    private void displayAlert()
    {
        dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.movies_dialog);
        emptyView = (TextView) dialog.findViewById(R.id.empty_view);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                finish();
            }
        });
        moviesRecyclerView =(RecyclerView) dialog.findViewById(R.id.movies_language_list);
        moviesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        moviesRecyclerView.setLayoutManager(llm);
        movieList =new ArrayList<MoviesList>();
        movieList= VideoData.getMoviesList();
        //putDummyData();
        adapter = new MovieListRVAdapter(this, movieList);
        moviesRecyclerView.setAdapter(adapter);
        if (movieList.isEmpty()) {
            moviesRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            moviesRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
        dialog.show();
    }


    private void putDummyData(){

        MoviesList data=new MoviesList();
        data.setLanguage("Hindi");
        List<Movie> list=new ArrayList<Movie>();
        for(int i=0;i<10;i++){
            Movie movie=new Movie();
            movie.setMovieId(i+"x");
            movie.setMovieName("Dhangal"+i);
            list.add(movie);
        }
        data.setMovies(list);

        MoviesList data1=new MoviesList();
        data1.setLanguage("Tamil");
        List<Movie> list1=new ArrayList<Movie>();
        for(int i=0;i<10;i++){
            Movie movie=new Movie();
            movie.setMovieId(i+"x");
            movie.setMovieName("Thupakki"+i);
            list1.add(movie);
        }
        data1.setMovies(list1);

        MoviesList data2=new MoviesList();
        data2.setLanguage("English");
        List<Movie> list2=new ArrayList<Movie>();
        for(int i=0;i<10;i++){
            Movie movie=new Movie();
            movie.setMovieId(i+"x");
            movie.setMovieName("Dragon"+i);
            list2.add(movie);
        }
        data2.setMovies(list2);

        MoviesList data3=new MoviesList();
        data3.setLanguage("Odiya");
        List<Movie> list3=new ArrayList<Movie>();
        for(int i=0;i<10;i++){
            Movie movie=new Movie();
            movie.setMovieId(i+"x");
            movie.setMovieName("Dragon"+i);
            list3.add(movie);
        }
        data3.setMovies(list3);

        MoviesList data4=new MoviesList();
        data4.setLanguage("Odiya");
        List<Movie> list4=new ArrayList<Movie>();
        for(int i=0;i<10;i++){
            Movie movie=new Movie();
            movie.setMovieId(i+"x");
            movie.setMovieName("Dragon"+i);
            list4.add(movie);
        }
        data4.setMovies(list4);

        MoviesList data5=new MoviesList();
        data5.setLanguage("Odiya");
        List<Movie> list5=new ArrayList<Movie>();
        for(int i=0;i<10;i++){
            Movie movie=new Movie();
            movie.setMovieId(i+"x");
            movie.setMovieName("Dragon"+i);
            list5.add(movie);
        }
        data5.setMovies(list5);

        MoviesList data6=new MoviesList();
        data6.setLanguage("Odiya");
        List<Movie> list6=new ArrayList<Movie>();
        for(int i=0;i<10;i++){
            Movie movie=new Movie();
            movie.setMovieId(i+"x");
            movie.setMovieName("Dragon"+i);
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

    @Override
    public void onMovieSelected() {
        if(dialog.isShowing())
            dialog.dismiss();
    }
}

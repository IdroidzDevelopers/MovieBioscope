package com.lib.videoplayer.util;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.lib.videoplayer.R;
import com.lib.videoplayer.object.MoviesList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aarokiax on 2/20/2017.
 */

public class MovieListRVAdapter extends RecyclerView.Adapter<MovieListRVAdapter.SimpleViewHolder> {

    private static Context mContext;
    private static List<MoviesList> mData;
    private static RecyclerView movieList;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        private MovieRVAdapter horizontalAdapter;

        public SimpleViewHolder(View view) {
            super(view);
            Context context = itemView.getContext();
            title = (TextView) view.findViewById(R.id.language_text);
            movieList = (RecyclerView) itemView.findViewById(R.id.movie_list);
            movieList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            horizontalAdapter = new MovieRVAdapter(mContext);
            movieList.setAdapter(horizontalAdapter);
        }
    }

    public MovieListRVAdapter(Context context, List<MoviesList> data) {
        mContext = context;
        if (data != null)
            mData = new ArrayList<MoviesList>(data);
        else mData = new ArrayList<MoviesList>();
    }

    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.movies_lang_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {
        holder.title.setText(mData.get(position).getLanguage());
        holder.horizontalAdapter.setData(mData.get(position).getMovies()); // List of movies
        holder.horizontalAdapter.setRowIndex(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}

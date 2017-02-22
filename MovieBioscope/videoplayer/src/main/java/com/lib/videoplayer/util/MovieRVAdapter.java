package com.lib.videoplayer.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.videoplayer.R;
import com.lib.videoplayer.object.Movie;

import java.util.List;

/**
 * Created by aarokiax on 2/20/2017.
 */

public class MovieRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Movie> mDataList;
    private int mRowIndex = -1;

    public MovieRVAdapter() {
    }

    public void setData(List<Movie> data) {
        if (mDataList != data) {
            mDataList = data;
            notifyDataSetChanged();
        }
    }

    public void setRowIndex(int index) {
        mRowIndex = index;
    }




    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView text;

        public ItemViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.movie_item_text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "Clicked = " + mDataList.get(getPosition()).getMovieName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);
        ItemViewHolder holder = new ItemViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position) {
        ItemViewHolder holder = (ItemViewHolder) rawHolder;
        holder.text.setText(mDataList.get(position).getMovieName());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


}

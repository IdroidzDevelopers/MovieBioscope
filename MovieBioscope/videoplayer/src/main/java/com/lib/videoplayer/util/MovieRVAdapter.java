package com.lib.videoplayer.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.utility.util.CustomIntent;
import com.lib.videoplayer.R;
import com.lib.videoplayer.VideoApplication;
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
        private ImageView movieIcon;

        public ItemViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.movie_item_text);
            movieIcon = (ImageView) itemView.findViewById(R.id.movieicon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("Video","Selected Movie ::"+mDataList.get(getPosition()).getMovieName());
            VideoData.updateMovieSelection(mDataList.get(getPosition()).getMovieId());
            LocalBroadcastManager.getInstance(VideoApplication.getVideoContext()).sendBroadcast(new Intent(CustomIntent.ACTION_MOVIE_SELECTION_CHANGED));
            //Toast.makeText(view.getContext(), "Clicked = " + mDataList.get(getPosition()).getMovieName(), Toast.LENGTH_SHORT).show();
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
        Bitmap bp= ThumbnailUtils.createVideoThumbnail(mDataList.get(position).getMoviePath(), MediaStore.Images.Thumbnails.MINI_KIND);
        holder.movieIcon.setImageBitmap(bp);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


}

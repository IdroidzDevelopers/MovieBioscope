package com.app.navajhalaka.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.navajhalaka.R;
import com.lib.route.RouteApplication;

import java.io.File;
import java.util.List;

/**
 * Created by aarokiax on 2/18/2017.
 */

public class ImagePagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<String> mBitmapResources;


    public ImagePagerAdapter(Context context, List<String> resources) {
        mContext = context;
        mBitmapResources = resources;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (mBitmapResources.size() != 0) {
            return mBitmapResources.size();
        } else {
            return 1;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        if (mBitmapResources.size() != 0) {
            File imageFile = new File(mBitmapResources.get(position));
            imageView.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
        } else {
            imageView.setImageBitmap(BitmapFactory.decodeResource(RouteApplication.getRouteContext().getResources(),
                    R.drawable.default_landing_background));
        }
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        ImageView imgView = (ImageView) view.findViewById(R.id.imageView);
        BitmapDrawable bmpDrawable = (BitmapDrawable) imgView.getDrawable();
        if (bmpDrawable != null && bmpDrawable.getBitmap() != null) {
            // This is the important part
            bmpDrawable.getBitmap().recycle();
        }
        ((ViewPager) container).removeView(view);
        view = null;
    }
}

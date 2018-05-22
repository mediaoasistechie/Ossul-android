package com.ossul.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ossul.R;
import com.ossul.activities.FullImageActivity;
import com.ossul.view.WrapViewPager;

import java.util.ArrayList;

/***
 * Adapter to set data on landing page
 ***/
public class ViewPageAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<String> images;
    private int count;

    private int mCurrentPosition = -1;

    public ViewPageAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images = images;
        count = images.size();
    }

    @Override
    public int getCount() {
        return count;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public View instantiateItem(final ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_pager_item, container, false);
        ImageView banner = (ImageView) view.findViewById(R.id.image_view);
        banner.setVisibility(View.VISIBLE);
        final String imageUrl = images.get(position);
        ImageLoader.getInstance().displayImage(imageUrl + "", banner, new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.color_grey)
                .showImageOnFail(R.drawable.color_grey)
                .showStubImage(R.drawable.color_grey)
                .cacheOnDisc(true)
                .cacheInMemory(true)
                .build());

        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fullViewIntent = new Intent(context, FullImageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("data", null);
                bundle.putString("image_url", imageUrl);
                fullViewIntent.putExtra("bundle", bundle);
                context.startActivity(fullViewIntent);
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (position != mCurrentPosition) {
            View view = (View) object;
            WrapViewPager pager = (WrapViewPager) container;
            if (view != null) {
                mCurrentPosition = position;
                pager.measureCurrentView(view);
            }
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}

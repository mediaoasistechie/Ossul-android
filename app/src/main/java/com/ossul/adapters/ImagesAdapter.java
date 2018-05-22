package com.ossul.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ossul.R;
import com.ossul.listener.OnItemClickListener;

import java.util.ArrayList;


public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesHolder> {
    private ArrayList<String> lists;
    private Context mActivity;
    private OnItemClickListener listener;

    public ImagesAdapter(Context activity, ArrayList<String> lists) {
        this.mActivity = activity;
        this.lists = lists;


    }

    @Override
    public ImagesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ImagesHolder(inflater.inflate(R.layout.adapter_images, parent, false));
    }

    @Override
    public void onBindViewHolder(ImagesHolder holder, int position) {
        ImagesHolder propertiesHolder = (ImagesHolder) holder;
        propertiesHolder.setData(lists.get(position) + "", position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return lists != null ? lists.size() : 0;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ImagesHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        public ImagesHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.iv_image);
        }

        void setData(final String response, final int position) {
            Log.d("images utils", "" + response);
            ImageLoader.getInstance().displayImage(response, image, new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.color_grey)
                    .showImageOnFail(R.drawable.color_grey)
                    .showStubImage(R.drawable.color_grey)
                    .cacheOnDisc(true)
                    .cacheInMemory(true)
                    .build());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

}

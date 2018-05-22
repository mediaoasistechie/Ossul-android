package com.ossul.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ossul.R;
import com.ossul.adapters.ImagesAdapter;
import com.ossul.listener.OnItemClickListener;
import com.ossul.view.TouchImageView;

import java.util.ArrayList;

/**
 * Created by ${Rajan}
 */
public class FullImageActivity extends AppCompatActivity implements View.OnClickListener {

    private TouchImageView mImageTIV;
    private TextView mTitleTV;
    private RelativeLayout mCloseRL;
    private ArrayList<String> imageList;
    private RecyclerView mImagesRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        initViews();
        initVariables();
    }

    private void initViews() {
        mImageTIV = (TouchImageView) findViewById(R.id.tiv_image);

        mTitleTV = (TextView) findViewById(R.id.tv_title);
        mCloseRL = (RelativeLayout) findViewById(R.id.rl_close);
        mImagesRV = (RecyclerView) findViewById(R.id.rv_images);

    }

    private void initVariables() {
        if (getIntent().getBundleExtra("bundle") != null) {
            Bundle bundle = getIntent().getBundleExtra("bundle");
            String imageUrl = bundle.getString("image_url");
            imageList = bundle.getStringArrayList("data");
            if (imageUrl != null) {
                ImageLoader.getInstance().displayImage(imageUrl, mImageTIV, new DisplayImageOptions.Builder()
                        .showImageForEmptyUri(R.drawable.color_grey)
                        .showImageOnFail(R.drawable.color_grey)
                        .showStubImage(R.drawable.color_grey)
                        .cacheOnDisc(true)
                        .cacheInMemory(true)
                        .build());
            }
        }
        findViewById(R.id.tv_close).setOnClickListener(this);
        if (imageList != null && imageList.size() > 0) {
            mImagesRV.setVisibility(View.VISIBLE);
            final LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            mImagesRV.setLayoutManager(layout);
            mImagesRV.setHasFixedSize(true);

            ImagesAdapter imagesAdapter = new ImagesAdapter(this, imageList);
            mImagesRV.setAdapter(imagesAdapter);
            imagesAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if (imageList.get(position) != null)
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    ImageLoader.getInstance().displayImage(imageList.get(position), mImageTIV, new DisplayImageOptions.Builder()
                            .showImageForEmptyUri(R.drawable.color_grey)
                            .showImageOnFail(R.drawable.color_grey)
                            .showStubImage(R.drawable.color_grey)
                            .cacheOnDisc(true)
                            .cacheInMemory(true)
                            .build());
                    mImageTIV.resetZoom();

                }
            });
        } else {
            mImagesRV.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_close:
                finish();
                break;
        }
    }
}

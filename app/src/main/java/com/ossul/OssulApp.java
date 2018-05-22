package com.ossul;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.ossul.activities.BaseActivity;
import com.ossul.apppreferences.AppPreferences;

import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.io.File;

/**
 * Created by Rajan on 13-Dec-16.
 */

@ReportsCrashes(formKey = "", mailTo = "tiwari.rajan2013@gmail.com", mode = ReportingInteractionMode.TOAST, resToastText = R.string.app_name)
public class OssulApp extends Application {
    public static final int MAX_SIZE = 4 * 1024 * 1024;

    @Override
    public void onCreate() {
//        if (BuildConfig.DEBUG)
//            ACRA.init(this);
        super.onCreate();
        AppPreferences.initAppPreferences(getApplicationContext());
        initImageLoader(getApplicationContext());
//        Stetho.initializeWithDefaults(this);
    }

    private void initImageLoader(Context context) {
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.color_default)
                .showImageOnFail(R.drawable.color_default)
                .showStubImage(R.drawable.color_default)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();
        File cacheDir = StorageUtils.getCacheDirectory(context);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(MAX_SIZE))
                .defaultDisplayImageOptions(imageOptions)
                .discCache(new UnlimitedDiscCache(cacheDir))
//                .writeDebugLogs()
                .build();

        ImageLoader.getInstance().init(config);
        BaseActivity.getDisplayOption();
    }

}

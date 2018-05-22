package com.ossul.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ossul.R;
import com.ossul.apppreferences.AppPreferences;


/**
 * @author Rajan Tiwari, Base activity for all the activity defined in this app will
 *         extend this class this is the base class for all activity
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private static DisplayImageOptions displayImageOptions;
    public AppPreferences mAppPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppPreferences.initAppPreferences(getApplicationContext());
        mAppPreferences = AppPreferences.get();
    }

    protected void initData() {
        initViews();
        initVariables();
    }

    /**
     * used to initialize views
     *
     * @return void
     */
    protected abstract void initViews();

    /**
     * used to initialize variables
     *
     * @return void
     */
    protected abstract void initVariables();

    @Override
    public void onClick(View v) {

    }

    //To display images
    public static DisplayImageOptions getDisplayOption() {
        if (displayImageOptions == null)
            displayImageOptions = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.color_default)
                    .showImageOnFail(R.drawable.color_default)
                    .showStubImage(R.drawable.color_default)
                    .cacheOnDisc(true)
                    .cacheInMemory(true)
                    .build();
        return displayImageOptions;
    }
}

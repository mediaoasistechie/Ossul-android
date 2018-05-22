package com.ossul.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.ossul.activities.BaseActivity;
import com.ossul.apppreferences.AppPreferences;


/**
 * * Base class for the base fragment
 */
public abstract class BaseFragment extends Fragment {

    private static final String TAG = BaseFragment.class.getSimpleName();
    public BaseActivity mActivity;
    public AppPreferences mAppPreferences;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (BaseActivity) activity;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (BaseActivity) getActivity();
        AppPreferences.initAppPreferences(mActivity);
        mAppPreferences = AppPreferences.get();
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
    public void onResume() {
        super.onResume();
    }


    public void killMe() {
        if (mActivity != null) {
            mActivity.getSupportFragmentManager().popBackStack();
        }
    }

}

package com.ossul.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ossul.R;
import com.ossul.activities.BaseActivity;
import com.ossul.activities.EconomicNewsActivity;
import com.ossul.activities.LoginActivity;
import com.ossul.activities.MainActivity;
import com.ossul.activities.RealEstateNewsActivity;
import com.ossul.activities.SpecialOffersActivity;
import com.ossul.apppreferences.AppPreferences;
import com.ossul.utility.FragmentUtils;
import com.ossul.view.HeaderHandler;

public class HomeFragment extends BaseFragment implements View.OnClickListener {
    private View view;
    private ImageView mBanner1IV, mRealEstateNewsIV, mBanner3IV, mBanner4IV, mBanner5IV, mBanner6IV;

    public static void showFragment(int i, BaseActivity mActivity) {
        if (mActivity.getSupportFragmentManager() == null)
            return;
        switch (i) {
            case 3:
                SpecialOfferFragment specialOfferFragment = new SpecialOfferFragment();
                FragmentUtils.replaceFragmentWithSlide(mActivity.getSupportFragmentManager(), specialOfferFragment.getClass().getSimpleName(), true, R.id.container, specialOfferFragment);

                break;
            case 6:
                SubmitRequestFragment submitRequestFragment = new SubmitRequestFragment();
                FragmentUtils.replaceFragmentWithSlide(mActivity.getSupportFragmentManager(), submitRequestFragment.getClass().getSimpleName(), true, R.id.container, submitRequestFragment);

                break;
            case 5:
                MyRequestFragment myRequestFragment = new MyRequestFragment();
                FragmentUtils.replaceFragmentWithSlide(mActivity.getSupportFragmentManager(), myRequestFragment.getClass().getSimpleName(), true, R.id.container, myRequestFragment);

                break;
            case 4:
                SubmitOfferFragment submitOfferFragment = new SubmitOfferFragment();
                FragmentUtils.replaceFragmentWithSlide(mActivity.getSupportFragmentManager(), submitOfferFragment.getClass().getSimpleName(), true, R.id.container, submitOfferFragment);

                break;
            default:
                if (mActivity.getSupportFragmentManager() != null)
                    FragmentUtils.clearStack(mActivity.getSupportFragmentManager());
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAppPreferences = AppPreferences.get();
        ((MainActivity) mActivity).setHeaderVisibility(HeaderHandler.KEY_EMPTY);
    }

    @Override
    protected void initViews() {
        mBanner1IV = (ImageView) view.findViewById(R.id.iv_economic_news);
        mRealEstateNewsIV = (ImageView) view.findViewById(R.id.iv_real_estate_news);
        mBanner3IV = (ImageView) view.findViewById(R.id.iv_special_offers);
        mBanner4IV = (ImageView) view.findViewById(R.id.iv_requests);
        mBanner5IV = (ImageView) view.findViewById(R.id.iv_my_requests);
        mBanner6IV = (ImageView) view.findViewById(R.id.iv_submit_request);

        mBanner1IV.setOnClickListener(this);
        mRealEstateNewsIV.setOnClickListener(this);
        mBanner3IV.setOnClickListener(this);
        mBanner4IV.setOnClickListener(this);
        mBanner5IV.setOnClickListener(this);
        mBanner6IV.setOnClickListener(this);
    }

    @Override
    protected void initVariables() {
        mAppPreferences = AppPreferences.get();
        changeLabels();
    }

    public void changeLabels() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_economic_news:
                Intent intent = new Intent(mActivity, EconomicNewsActivity.class);
                startActivityForResult(intent, 103);
                break;
            case R.id.iv_real_estate_news:
                Intent realEstateNewsActivity = new Intent(mActivity, RealEstateNewsActivity.class);
                startActivityForResult(realEstateNewsActivity, 100);
                break;
            case R.id.iv_special_offers:
                if (AppPreferences.get().isLogin()) {
                    Intent specialOfferIntent = new Intent(mActivity, SpecialOffersActivity.class);
                    startActivityForResult(specialOfferIntent, 105);
                } else {
                    Intent intentLogin = new Intent(mActivity, LoginActivity.class);
                    mActivity.startActivityForResult(intentLogin, 11001);
                }
                break;
            case R.id.iv_requests:

                if (AppPreferences.get().isLogin()) {
                    showFragment(4, mActivity);

                } else {
                    Intent intentLogin = new Intent(mActivity, LoginActivity.class);
                    mActivity.startActivityForResult(intentLogin, 11001);
                }
                break;
            case R.id.iv_my_requests:
                if (AppPreferences.get().isLogin()) {
                    showFragment(5, mActivity);
                } else {
                    Intent intentLogin = new Intent(mActivity, LoginActivity.class);
                    mActivity.startActivityForResult(intentLogin, 11001);
                }
                break;
            case R.id.iv_submit_request:
                if (AppPreferences.get().isLogin()) {
                    showFragment(6, mActivity);
                } else {
                    Intent intentLogin = new Intent(mActivity, LoginActivity.class);
                    mActivity.startActivityForResult(intentLogin, 11001);
                }
                break;
        }
    }
}

package com.ossul.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.appcompat.BuildConfig;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.ossul.R;
import com.ossul.adapters.ImagesAdapter;
import com.ossul.adapters.ViewPageAdapter;
import com.ossul.appconstant.AppConstants;
import com.ossul.apppreferences.AppPreferences;
import com.ossul.dialog.CustomDialogFragment;
import com.ossul.dialog.DialogManager;
import com.ossul.interfaces.INetworkEvent;
import com.ossul.listener.OnItemClickListener;
import com.ossul.network.NetworkService;
import com.ossul.network.constants.AppNetworkConstants;
import com.ossul.network.model.NetworkModel;
import com.ossul.network.model.response.EconomicNewsList;
import com.ossul.network.model.response.GetEcoNewsResponse;
import com.ossul.network.model.response.GetNewsResponse;
import com.ossul.network.model.response.RealEstateNewsList;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.Validator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;


public class NewsDetailsActivity extends BaseActivity implements View.OnClickListener, INetworkEvent {

    private ViewPager viewPager;
    private TextView mTitleTV, mDescriptionTV, mPublishedByTV, mPublishedDateTV;
    private JSONObject jsonObject = new JSONObject();
    private TextView mDateTV;
    private RecyclerView mImagesRV;
    private TextView mByTV;
    private TextView mHeaderTitleTV;
    private String economicNewsStr = "Economic News", realEstateNewsStr = "Real Estate News";
    private String API_GET_ECONOMIC_NEWS_BY_ID = "";
    private String API_GET_REAL_ESTATE_NEWS_BY_ID = "";
    private String TAG = NewsDetailsActivity.class.getSimpleName();
    private DialogManager mProgressDialog;

    public void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup) child.getParent();
        if (null != parent) {
            parent.removeView(child);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    parent.addView(child);
                }
            }, 500);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        initData();

    }

    protected void initData() {
        initViews();
        initVariables();
    }

    @Override
    protected void initViews() {
        mHeaderTitleTV = (TextView) findViewById(R.id.tv_header_title);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        mImagesRV = (RecyclerView) findViewById(R.id.rv_images);

        mTitleTV = (TextView) findViewById(R.id.tv_title);
        mDescriptionTV = (TextView) findViewById(R.id.tv_description);
        mDescriptionTV = (TextView) findViewById(R.id.tv_description);
        mPublishedByTV = (TextView) findViewById(R.id.tv_published);
        mByTV = (TextView) findViewById(R.id.tv_by);
        mPublishedDateTV = (TextView) findViewById(R.id.tv_published_date);
        mDateTV = (TextView) findViewById(R.id.tv_date);
        findViewById(R.id.iv_close).setOnClickListener(this);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                sendViewToBack(mImagesRV);
                sendViewToBack(mDescriptionTV);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initVariables() {
        mProgressDialog = new DialogManager(this);
        final LinearLayoutManager layout = new LinearLayoutManager(this, android.support.v7.widget.LinearLayoutManager.HORIZONTAL, false);
        mImagesRV.setLayoutManager(layout);
        mImagesRV.setHasFixedSize(true);

        if (!Validator.isEmptyString(AppPreferences.get().getLabels())) {
            try {
                jsonObject = new JSONObject(AppPreferences.get().getLabels());
                if (jsonObject != null && jsonObject.length() != 0) {
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Real Estate News")))
                        realEstateNewsStr = (AppUtilsMethod.getValueFromKey(jsonObject, "Real Estate News"));

                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Economic News")))
                        economicNewsStr = (AppUtilsMethod.getValueFromKey(jsonObject, "Economic News"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (getIntent() != null && getIntent().getBundleExtra("bundle") != null && getIntent().getBundleExtra("bundle").getParcelable("data") != null) {
            if (getIntent().getStringExtra("for").equalsIgnoreCase("real")) {
                final ArrayList<String> images = new ArrayList<>();
                if (getIntent().getStringArrayListExtra("images") != null && getIntent().getStringArrayListExtra("images").size() > 0) {
                    images.addAll(getIntent().getStringArrayListExtra("images"));
                }
                final RealEstateNewsList realEstateNewsList = (RealEstateNewsList) getIntent().getBundleExtra("bundle").getParcelable("data");
                setRealEstateNewsData(realEstateNewsList, images);
            } else {

                EconomicNewsList realEstateNewsList = (EconomicNewsList) getIntent().getBundleExtra("bundle").getParcelable("data");
                final ArrayList<String> images = new ArrayList<>();
                if (getIntent().getStringArrayListExtra("images") != null && getIntent().getStringArrayListExtra("images").size() > 0) {
                    images.addAll(getIntent().getStringArrayListExtra("images"));
                }
                setEconomicData(realEstateNewsList, images);
            }
        }
        if (getIntent() != null && !Validator.isEmptyString(getIntent().getStringExtra("news_id"))) {
            if (!Validator.isEmptyString(getIntent().getStringExtra("for")) && getIntent().getStringExtra("for").equalsIgnoreCase("real")) {
                getRealEstateNewsById(getIntent().getStringExtra("news_id"));
            } else if (!Validator.isEmptyString(getIntent().getStringExtra("for")) && getIntent().getStringExtra("for").equalsIgnoreCase("economic")) {
                getEconomicNewsById(getIntent().getStringExtra("news_id"));
            }
        }

    }

    private void setRealEstateNewsData(RealEstateNewsList realEstateNewsList, final ArrayList<String> images) {
        mHeaderTitleTV.setText(realEstateNewsStr);
        if (realEstateNewsList == null)
            return;
        mTitleTV.setText(realEstateNewsList.title + "");

        if (!Validator.isEmptyString(realEstateNewsList.description)) {
            mDescriptionTV.setVisibility(View.VISIBLE);
            mDescriptionTV.setText(Html.fromHtml(realEstateNewsList.description));
        } else {
            mDescriptionTV.setVisibility(View.GONE);
        }

        String publishBy;
        if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Published By")))
            publishBy = (AppUtilsMethod.getValueFromKey(jsonObject, "Published By"));
        else publishBy = "Published By";
        mPublishedByTV.setText(publishBy);
        String publishDate;
        if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Date")))
            publishDate = (AppUtilsMethod.getValueFromKey(jsonObject, "Date"));
        else
            publishDate = "Date";

        mPublishedDateTV.setText(publishDate + ": ");
        mByTV.setText(" :" + realEstateNewsList.displayName);
        mDateTV.setText(AppUtilsMethod.formattedDateTimeToDisplay(realEstateNewsList.creationDate));

        if (images != null && images.size() > 0) {
            mImagesRV.setVisibility(View.VISIBLE);
            viewPager.setAdapter(new ViewPageAdapter(this, images));
            final ImagesAdapter imagesAdapter = new ImagesAdapter(this, images);
            mImagesRV.setAdapter(imagesAdapter);
            imagesAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if (images.size() > position && !Validator.isEmptyString(images.get(position))) {
                        viewPager.setCurrentItem(position);
                    }
                }
            });
        }
    }

    private void setEconomicData(EconomicNewsList realEstateNewsList, final ArrayList<String> images) {
        mHeaderTitleTV.setText(economicNewsStr);
        if (realEstateNewsList == null)
            return;
        mTitleTV.setText(realEstateNewsList.title + "");
        if (!Validator.isEmptyString(realEstateNewsList.description)) {
            mDescriptionTV.setVisibility(View.VISIBLE);
            mDescriptionTV.setText(Html.fromHtml(realEstateNewsList.description));
        } else {
            mDescriptionTV.setVisibility(View.GONE);
        }

        String publishBy;
        if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Published By")))
            publishBy = (AppUtilsMethod.getValueFromKey(jsonObject, "Published By"));
        else publishBy = "Published By";
        mPublishedByTV.setText(publishBy);
        String publishDate;
        if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Date")))
            publishDate = (AppUtilsMethod.getValueFromKey(jsonObject, "Date"));
        else
            publishDate = "Date";

        mPublishedDateTV.setText(publishDate + ": ");
        mByTV.setText(" :" + realEstateNewsList.displayName);
        mDateTV.setText(AppUtilsMethod
                .formattedDateTimeToDisplay(realEstateNewsList.creationDate));

        if (images != null && images.size() > 0) {
            mImagesRV.setVisibility(View.VISIBLE);
            viewPager.setAdapter(new ViewPageAdapter(this, images));

            final ImagesAdapter imagesAdapter = new ImagesAdapter(this, images);
            mImagesRV.setAdapter(imagesAdapter);
            imagesAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if (images.size() > position && !Validator.isEmptyString(images.get(position))) {
                        viewPager.setCurrentItem(position);
                    }
                }
            });
        }
    }

    private void getRealEstateNewsById(String newsId) {
        if (Validator.isConnectedToInternet(getApplicationContext())) {
            NetworkModel networkModel = new NetworkModel();
            API_GET_REAL_ESTATE_NEWS_BY_ID = AppNetworkConstants.BASE_URL + "user/news.php?action=real-estate-news-by-id&realestate_news_id=" + newsId;
            NetworkService service = new NetworkService(API_GET_REAL_ESTATE_NEWS_BY_ID, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }


    private void getEconomicNewsById(String newsId) {
        if (Validator.isConnectedToInternet(getApplicationContext())) {
            NetworkModel networkModel = new NetworkModel();
            API_GET_ECONOMIC_NEWS_BY_ID = AppNetworkConstants.BASE_URL + "user/news.php?action=economic-news-by-id&economic_news_id=" + newsId;
            NetworkService service = new NetworkService(API_GET_ECONOMIC_NEWS_BY_ID, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                onBackPressed();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_out_up, R.anim.slide_out_down);
    }

    @Override
    public void onNetworkCallInitiated(String service) {
        if (mProgressDialog != null) {
            mProgressDialog.show();
        }
    }

    @Override
    public void onNetworkCallCompleted(String service, String response) {
        Log.e(TAG, " success " + response);
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        if (service.equalsIgnoreCase(API_GET_REAL_ESTATE_NEWS_BY_ID)) {
            GetNewsResponse newsParentResponse = GetNewsResponse.fromJson(response);
            if (newsParentResponse != null && newsParentResponse.success && newsParentResponse.data != null) {
                ArrayList<String> images = new ArrayList<>();
                JsonArray jsonElements = newsParentResponse.data.images;
                for (int i = 0; i < jsonElements.size(); i++) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonElements.get(i) + "");
                        if (jsonObject != null) {
                            images.add(AppUtilsMethod.getValueFromKey(jsonObject, "storage_path"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (images != null && images.size() > 0)
                    Collections.reverse(images);
                setRealEstateNewsData(newsParentResponse.data, images);


            } else if (newsParentResponse != null && newsParentResponse.error && !Validator.isEmptyString(newsParentResponse.errorMessage)) {
                CustomDialogFragment.getInstance(this, null, newsParentResponse.errorMessage, null, AppConstants.OK, null, 1, null);
            } else {
                if (BuildConfig.DEBUG)
                    CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
            }

        } else if (service.equalsIgnoreCase(API_GET_ECONOMIC_NEWS_BY_ID)) {
            GetEcoNewsResponse newsParentResponse = GetEcoNewsResponse.fromJson(response);
            if (newsParentResponse != null && newsParentResponse.success && newsParentResponse.data != null) {
                JsonArray jsonElements = newsParentResponse.data.images;
                ArrayList<String> images = new ArrayList<>();
                for (int i = 0; i < jsonElements.size(); i++) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonElements.get(i) + "");
                        if (jsonObject != null) {
                            images.add(AppUtilsMethod.getValueFromKey(jsonObject, "storage_path"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Collections.reverse(images);
                setEconomicData(newsParentResponse.data, images);

            } else if (newsParentResponse != null && newsParentResponse.error && !Validator.isEmptyString(newsParentResponse.errorMessage)) {

                CustomDialogFragment.getInstance(this, null, newsParentResponse.errorMessage, null, AppConstants.OK, null, 1, null);
            } else {
                if (BuildConfig.DEBUG)
                    CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
            }

        }
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();

    }
}

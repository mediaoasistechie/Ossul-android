package com.ossul.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.appcompat.BuildConfig;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.ossul.R;
import com.ossul.adapters.EconomicNewsAdapter;
import com.ossul.adapters.SpinnerListAdapter;
import com.ossul.appconstant.AppConstants;
import com.ossul.dialog.CustomDialogFragment;
import com.ossul.dialog.DialogManager;
import com.ossul.interfaces.INetworkEvent;
import com.ossul.listener.EndlessRecyclerOnScrollListener;
import com.ossul.network.NetworkService;
import com.ossul.network.constants.AppNetworkConstants;
import com.ossul.network.model.NetworkModel;
import com.ossul.network.model.response.CountriesList;
import com.ossul.network.model.response.EconomicNewsList;
import com.ossul.network.model.response.GetCountryListResponse;
import com.ossul.network.model.response.NewsParentResponse;
import com.ossul.network.parser.ParserKeys;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.Validator;
import com.ossul.view.CustomSwipeRefreshLayout;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class EconomicNewsActivity extends BaseActivity implements INetworkEvent, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = EconomicNewsActivity.class.getSimpleName();
    private DialogManager mProgressDialog;
    private JSONObject jsonObject = new JSONObject();
    private TextView mHeaderTitleTV;
    private RecyclerView mRealEstateNewsRV;
    private Spinner mCountrySPN;
    private EconomicNewsAdapter mEconomicNewsAdapter;
    private CustomSwipeRefreshLayout mRefreshLayout;
    private String API_GET_REAL_ESTATE_NEWS_LIST = "";
    private String API_COUNTRY_LIST = "";
    private ArrayList<CountriesList> mCountryList = new ArrayList<>();
    private ArrayList<String> mCountyNameList = new ArrayList<>();

    private String mCountryId = "";
    private String mCityId = "";
    private ArrayList<EconomicNewsList> mNewsList = new ArrayList<>();
    private String countryStr = "Country";
    private PopupMenu popupMenu;
    private String API_DELETE_NEWS = "";
    private RelativeLayout mLoaderRL;
    private EndlessRecyclerOnScrollListener recyclerOnScrollListener;
    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            recyclerOnScrollListener.resetPage();
            if (mCountryList != null && mCountryList.size() > position && mCountryList.get(position) != null) {
                mCountryId = mCountryList.get(position).countryId;
                getEconomicNewsList(1, mCountryId, mCityId);
            } else {
                mCountryId = "";
                mCityId = "";
                getEconomicNewsList(1, mCountryId, mCityId);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_economic_news);
        initData();
    }

    @Override
    protected void initViews() {
        mHeaderTitleTV = (TextView) findViewById(R.id.tv_header_title);
        mRealEstateNewsRV = (RecyclerView) findViewById(R.id.rv_real_estate_news);
        mCountrySPN = (Spinner) findViewById(R.id.spn_country);
        mRefreshLayout = (CustomSwipeRefreshLayout) findViewById(R.id.customRefreshLayout);

        mLoaderRL = (RelativeLayout) findViewById(R.id.rl_loader);
        mLoaderRL.setVisibility(View.GONE);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_add_news).setOnClickListener(this);
        findViewById(R.id.rl_country).setOnClickListener(this);

    }

    @Override
    protected void initVariables() {
        mProgressDialog = new DialogManager(this);
        final LinearLayoutManager layout = new LinearLayoutManager(this);
        mRealEstateNewsRV.setLayoutManager(layout);
        mRealEstateNewsRV.setHasFixedSize(true);
        mEconomicNewsAdapter = new EconomicNewsAdapter(this, mNewsList, jsonObject);
        mRealEstateNewsRV.setAdapter(mEconomicNewsAdapter);
        updateLabels();
        getCountryListAPI();
        getEconomicNewsList(1, mCountryId, mCityId);
        recyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layout) {
            @Override
            public void onLoadMore(int current_page) {
                getEconomicNewsList(current_page, mCountryId, mCityId);
                if (current_page >= 2)
                    mLoaderRL.setVisibility(View.VISIBLE);
            }
        };
        mRealEstateNewsRV.addOnScrollListener(recyclerOnScrollListener);
        mRefreshLayout.setOnRefreshListener(this);
        recyclerOnScrollListener.resetPage();
    }

    private void getCountryListAPI() {
        if (Validator.isConnectedToInternet(getApplicationContext())) {
            NetworkModel networkModel = new NetworkModel();
            API_COUNTRY_LIST = AppNetworkConstants.BASE_URL + "user/request.php?action=country-list";
            NetworkService service = new NetworkService(API_COUNTRY_LIST, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }

    @Override
    public void onRefresh() {
        recyclerOnScrollListener.resetPage();
        getEconomicNewsList(1, mCountryId, mCityId);
    }

    private void updateLabels() {
        if (!Validator.isEmptyString(mAppPreferences.getLabels())) {
            try {
                jsonObject = new JSONObject(mAppPreferences.getLabels());
                if (jsonObject != null && jsonObject.length() != 0) {
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Economic News")))
                        mHeaderTitleTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Economic News"));
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Country")))
                        countryStr = (AppUtilsMethod.getValueFromKey(jsonObject, "Country"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getEconomicNewsList(int page, String countryId, String cityId) {
        if (Validator.isConnectedToInternet(getApplicationContext())) {
            if (page == 1)
                mRefreshLayout.setRefreshing(true);
            NetworkModel networkModel = new NetworkModel();
            API_GET_REAL_ESTATE_NEWS_LIST = AppNetworkConstants.BASE_URL + "user/news.php?action=economic-news-list&page=" + page + "&country_id=" + countryId + "&city_id=" + cityId;
            NetworkService service = new NetworkService(API_GET_REAL_ESTATE_NEWS_LIST, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_add_news:
                Intent intent = new Intent(this, SubmitEconomicNewsActivity.class);
                startActivityForResult(intent, 102);
                break;
            case R.id.rl_country:
                mCountrySPN.performClick();
                break;

            case R.id.iv_back:
                onBackPressed();
                break;

        }

    }

    @Override
    public void onNetworkCallInitiated(String service) {
        if (mProgressDialog != null && !mRefreshLayout.isRefreshing() && !service.equalsIgnoreCase(API_GET_REAL_ESTATE_NEWS_LIST)) {
            mProgressDialog.show();
        }
    }

    @Override
    public void onNetworkCallCompleted(String service, String response) {
        Log.e(TAG, " success " + response);
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        mRefreshLayout.setRefreshing(false);
        if (service.equalsIgnoreCase(API_GET_REAL_ESTATE_NEWS_LIST)) {
            NewsParentResponse baseResponse = NewsParentResponse.fromJson(response);
            if (baseResponse != null && baseResponse.success) {
                if (mNewsList != null && API_GET_REAL_ESTATE_NEWS_LIST.contains("page=1"))
                    mNewsList.clear();
                JsonArray data = baseResponse.data;
                if (data != null && data.size() > 0) {
                    for (int i = 0; i < data.size(); i++) {
                        String newsListStr = data.get(i).toString();
                        if (EconomicNewsList.fromJson(newsListStr) != null)
                            mNewsList.add(EconomicNewsList.fromJson(newsListStr));
                    }
                }
                mLoaderRL.setVisibility(View.GONE);
                mRealEstateNewsRV.setVisibility(View.VISIBLE);
                if (mEconomicNewsAdapter != null) {
                    mEconomicNewsAdapter.notifyDataSetChanged();
                }
            } else if (baseResponse != null && baseResponse.error && !Validator.isEmptyString(baseResponse.errorMessage)) {
                CustomDialogFragment.getInstance(this, null, baseResponse.errorMessage, null, AppConstants.OK, null, 1, null);
            } else {
                if (BuildConfig.DEBUG)
                    CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
            }
        } else if (service.equalsIgnoreCase(API_COUNTRY_LIST)) {
            GetCountryListResponse getCountryListResponse = GetCountryListResponse.fromJson(response);
            if (getCountryListResponse != null && getCountryListResponse.data != null) {
                if (getCountryListResponse.data.size() > 0) {
                    mCountryList.clear();
                    mCountyNameList.clear();
                    mCountyNameList.add(countryStr);
                    mCountryList = getCountryListResponse.data;
                    for (CountriesList list : mCountryList) {
                        mCountyNameList.add(list.countryName);
                    }

                    mCountryList.add(0, null);
                    SpinnerListAdapter adapter = new SpinnerListAdapter(this, mCountyNameList);
                    mCountrySPN.setAdapter(adapter);
                    mCountrySPN.setEnabled(true);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mCountrySPN != null && onItemSelectedListener != null)
                                mCountrySPN.setOnItemSelectedListener(onItemSelectedListener);
                        }
                    }, 2000);
                }
            }
        }
    }


    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        mLoaderRL.setVisibility(View.GONE);
        mRefreshLayout.setRefreshing(false);
        if (BuildConfig.DEBUG)
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102 && resultCode == RESULT_OK) {
            onRefresh();
        }
    }

    public void deleteNews(String economicNewsId, int position) {
        try {
            mNewsList.remove(position);
            mEconomicNewsAdapter.notifyDataSetChanged();
        } catch (Exception e) {

        }
        if (economicNewsId != null) {
            API_DELETE_NEWS = AppNetworkConstants.BASE_URL + "user/news.php";
            NetworkService serviceCall = new NetworkService(API_DELETE_NEWS, AppConstants.METHOD_POST, this);
            MultipartBuilder multipartBuilder = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart(ParserKeys.action.toString(), AppNetworkConstants.ACTION_REMOVE_ECONOMIC_NEWS)
                    .addFormDataPart(ParserKeys.economic_news_id.toString(), economicNewsId);
            RequestBody requestBody = (RequestBody) multipartBuilder.build();
            serviceCall.setRequestBody(requestBody);
            serviceCall.call(new NetworkModel());
        }
    }
}


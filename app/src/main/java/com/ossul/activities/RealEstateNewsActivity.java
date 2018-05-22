package com.ossul.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.appcompat.BuildConfig;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.ossul.R;
import com.ossul.adapters.RealEstateNewsAdapter;
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
import com.ossul.network.model.response.GetCityListResponse;
import com.ossul.network.model.response.GetCountryListResponse;
import com.ossul.network.model.response.NewsParentResponse;
import com.ossul.network.model.response.RealEstateNewsList;
import com.ossul.network.parser.ParserKeys;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.Validator;
import com.ossul.view.CustomSwipeRefreshLayout;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class RealEstateNewsActivity extends BaseActivity implements INetworkEvent, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = RealEstateNewsActivity.class.getSimpleName();
    private DialogManager mProgressDialog;
    private JSONObject jsonObject = new JSONObject();
    private TextView mHeaderTitleTV;
    private RecyclerView mRealEstateNewsRV;
    private Spinner mCountrySPN;
    private Spinner mCitySPN;
    private RealEstateNewsAdapter mRealEstateNewsAdapter;
    private CustomSwipeRefreshLayout mRefreshLayout;
    private String API_GET_REAL_ESTATE_NEWS_LIST = "";
    private String API_COUNTRY_LIST = "";
    private String API_CITY_LIST = "";
    private ArrayList<GetCityListResponse.CityList> mCityList = new ArrayList<>();
    private ArrayList<String> mCityNameList = new ArrayList<>();
    private String mCityId = "";
    private ArrayList<CountriesList> mCountryList = new ArrayList<>();
    private ArrayList<String> mCountyNameList = new ArrayList<>();

    private String mCountryId = "";
    private SpinnerListAdapter cityAdapter;
    private ArrayList<RealEstateNewsList> mNewsList = new ArrayList<>();
    private RelativeLayout mCountryRL, mCityRL;
    private String API_DELETE_NEWS = "";
    private EndlessRecyclerOnScrollListener recyclerOnScrollListener;
    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            recyclerOnScrollListener.resetPage();
            if (mCityList != null && mCityList.size() > position && mCityList.get(position) != null) {
                mCityId = mCityList.get(position).cityId;
                getRealEstateNewsList(1, mCountryId, mCityId);
            } else {
                mCityId = "";
                if (mCitySPN.isEnabled())
                    getRealEstateNewsList(1, mCountryId, mCityId);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private RelativeLayout mLoaderRL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_estate_news);
        initData();
    }

    @Override
    protected void initViews() {
        mHeaderTitleTV = (TextView) findViewById(R.id.tv_header_title);
        mRealEstateNewsRV = (RecyclerView) findViewById(R.id.rv_real_estate_news);
        mCountrySPN = (Spinner) findViewById(R.id.spn_country);
        mCitySPN = (Spinner) findViewById(R.id.spn_city);

        mCountryRL = (RelativeLayout) findViewById(R.id.rl_country);
        mCityRL = (RelativeLayout) findViewById(R.id.rl_city);
        mLoaderRL = (RelativeLayout) findViewById(R.id.rl_loader);
        mLoaderRL.setVisibility(View.GONE);

        mRefreshLayout = (CustomSwipeRefreshLayout) findViewById(R.id.customRefreshLayout);

        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_add_news).setOnClickListener(this);
        mCityRL.setOnClickListener(this);
        mCountryRL.setOnClickListener(this);

    }

    @Override
    protected void initVariables() {
        mProgressDialog = new DialogManager(this);

        final LinearLayoutManager layout = new LinearLayoutManager(this);
        mRealEstateNewsRV.setLayoutManager(layout);
        mRealEstateNewsRV.setHasFixedSize(true);
        mRealEstateNewsAdapter = new RealEstateNewsAdapter(this, mNewsList, jsonObject);
        mRealEstateNewsRV.setAdapter(mRealEstateNewsAdapter);
        updateLabels();
        getCountryListAPI();
        recyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layout) {
            @Override
            public void onLoadMore(int current_page) {
                getRealEstateNewsList(current_page, mCountryId, mCityId);
                if (current_page >= 2)
                    mLoaderRL.setVisibility(View.VISIBLE);
            }
        };
        mRealEstateNewsRV.addOnScrollListener(recyclerOnScrollListener);
        recyclerOnScrollListener.resetPage();
        mRefreshLayout.setOnRefreshListener(this);
        mCountrySPN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recyclerOnScrollListener.resetPage();
                if (mCountryList != null && mCountryList.size() > position && mCountryList.get(position) != null) {
                    mCountryId = mCountryList.get(position).countryId;
                    getCityListAPI(mCountryId);
                    getRealEstateNewsList(1, mCountryId, mCityId);
                } else {
                    mCityId = "";
                    mCountryId = "";
                    mCityList.clear();
                    mCityNameList.clear();
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "City")))
                        mCityNameList.add(AppUtilsMethod.getValueFromKey(jsonObject, "City"));
                    else
                        mCityNameList.add("City");
                    mCityList.add(0, null);
                    if (cityAdapter != null) {
                        cityAdapter.notifyDataSetChanged();
                        mCitySPN.setEnabled(false);
                    }
                    getRealEstateNewsList(1, mCountryId, mCityId);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cityAdapter = new SpinnerListAdapter(this, mCityNameList);
        mCitySPN.setAdapter(cityAdapter);
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

    private void getCityListAPI(String countryId) {
        if (Validator.isConnectedToInternet(getApplicationContext())) {
            NetworkModel networkModel = new NetworkModel();
            API_CITY_LIST = AppNetworkConstants.BASE_URL + "user/request.php?action=city-list&country_id=" + countryId;
            NetworkService service = new NetworkService(API_CITY_LIST, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }


    @Override
    public void onRefresh() {
        recyclerOnScrollListener.resetPage();
        getRealEstateNewsList(1, mCountryId, mCityId);
    }

    private void updateLabels() {
        if (!Validator.isEmptyString(mAppPreferences.getLabels())) {
            try {
                jsonObject = new JSONObject(mAppPreferences.getLabels());
                if (jsonObject != null && jsonObject.length() != 0) {

                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Real Estate News")))
                        mHeaderTitleTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Real Estate News"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getRealEstateNewsList(int page, String countryId, String cityId) {
        if (Validator.isConnectedToInternet(getApplicationContext())) {
            if (page == 1)
                mRefreshLayout.setRefreshing(true);
            NetworkModel networkModel = new NetworkModel();
            API_GET_REAL_ESTATE_NEWS_LIST = AppNetworkConstants.BASE_URL + "user/news.php?action=real-estate-news-list&page=" + page + "&country_id=" + countryId + "&city_id=" + cityId;
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
                Intent intent = new Intent(this, SubmitRealEstateNewsActivity.class);
                startActivityForResult(intent, 101);
                break;
            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.rl_country:
                mCountrySPN.performClick();
                break;
            case R.id.rl_city:
                mCitySPN.performClick();
                break;

        }

    }

    @Override
    public void onNetworkCallInitiated(String service) {
        if (mProgressDialog != null && !mRefreshLayout.isRefreshing() && !service.equalsIgnoreCase(API_GET_REAL_ESTATE_NEWS_LIST))
            mProgressDialog.show();
    }

    @Override
    public void onNetworkCallCompleted(String service, String response) {
        Log.e(TAG, " success " + response);
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        mRefreshLayout.setRefreshing(false);
        if (service.equalsIgnoreCase(API_GET_REAL_ESTATE_NEWS_LIST)) {
            NewsParentResponse newsParentResponse = NewsParentResponse.fromJson(response);
            mLoaderRL.setVisibility(View.GONE);
            if (newsParentResponse != null && newsParentResponse.success) {
                if (mNewsList != null && API_GET_REAL_ESTATE_NEWS_LIST.contains("page=1"))
                    mNewsList.clear();
                JsonArray data = newsParentResponse.data;
                if (data != null && data.size() > 0) {
                    for (int i = 0; i < data.size(); i++) {
                        String newsListStr = data.get(i).toString();
                        if (RealEstateNewsList.fromJson(newsListStr) != null)
                            mNewsList.add(RealEstateNewsList.fromJson(newsListStr));
                    }


                }
                mRealEstateNewsRV.setVisibility(View.VISIBLE);
                if (mRealEstateNewsAdapter != null) {
                    mRealEstateNewsAdapter.notifyDataSetChanged();
                }
            } else if (newsParentResponse != null && newsParentResponse.error && !Validator.isEmptyString(newsParentResponse.errorMessage)) {
                CustomDialogFragment.getInstance(this, null, newsParentResponse.errorMessage, null, AppConstants.OK, null, 1, null);
            } else {
                if (BuildConfig.DEBUG)
                    CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
            }
        } else if (service.equalsIgnoreCase(API_CITY_LIST)) {
            GetCityListResponse getCityListResponse = GetCityListResponse.fromJson(response);
            if (getCityListResponse != null && getCityListResponse.data != null) {
                if (getCityListResponse.data.size() > 0) {
                    mCityList.clear();
                    mCityNameList.clear();
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "City")))
                        mCityNameList.add(AppUtilsMethod.getValueFromKey(jsonObject, "City"));
                    else
                        mCityNameList.add("City");
                    mCityList = getCityListResponse.data;
                    for (GetCityListResponse.CityList list : mCityList) {
                        mCityNameList.add(list.cityName);
                    }

                    mCityList.add(0, null);
                    cityAdapter = new SpinnerListAdapter(this, mCityNameList);
                    mCitySPN.setAdapter(cityAdapter);
                    mCitySPN.setOnItemSelectedListener(null);
                    mCitySPN.setEnabled(true);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mCitySPN != null && onItemSelectedListener != null)
                                mCitySPN.setOnItemSelectedListener(onItemSelectedListener);
                        }
                    }, 2000);
                }
            }
        } else if (service.equalsIgnoreCase(API_COUNTRY_LIST)) {
            GetCountryListResponse getCountryListResponse = GetCountryListResponse.fromJson(response);
            if (getCountryListResponse != null && getCountryListResponse.data != null) {
                if (getCountryListResponse.data.size() > 0) {
                    mCountryList.clear();
                    mCountyNameList.clear();
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Country")))
                        mCountyNameList.add(AppUtilsMethod.getValueFromKey(jsonObject, "Country"));
                    else
                        mCountyNameList.add("Country");
                    mCountryList = getCountryListResponse.data;
                    for (CountriesList list : mCountryList) {
                        mCountyNameList.add(list.countryName);
                    }

                    mCountryList.add(0, null);
                    SpinnerListAdapter adapter = new SpinnerListAdapter(this, mCountyNameList);
                    mCountrySPN.setAdapter(adapter);
                    mCountrySPN.setEnabled(true);
                }
            }
        }
    }


    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        mRefreshLayout.setRefreshing(false);
        mLoaderRL.setVisibility(View.GONE);
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
        if (requestCode == 101 && resultCode == RESULT_OK) {
            onRefresh();
        }
    }

    public void deleteNews(String economicNewsId, int position) {
        try {
            mNewsList.remove(position);
            mRealEstateNewsAdapter.notifyDataSetChanged();
        } catch (Exception e) {

        }
        if (economicNewsId != null) {
            API_DELETE_NEWS = AppNetworkConstants.BASE_URL + "user/news.php";
            NetworkService serviceCall = new NetworkService(API_DELETE_NEWS, AppConstants.METHOD_POST, this);
            MultipartBuilder multipartBuilder = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart(ParserKeys.action.toString(), AppNetworkConstants.ACTION_REMOVE_REAL_ESTATE_NEWS)
                    .addFormDataPart(ParserKeys.realestate_news_id.toString(), economicNewsId);
            RequestBody requestBody = (RequestBody) multipartBuilder.build();
            serviceCall.setRequestBody(requestBody);
            serviceCall.call(new NetworkModel());
        }
    }
}

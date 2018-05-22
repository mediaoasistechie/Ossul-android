package com.ossul.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ossul.R;
import com.ossul.adapters.SpecialOfferAdapter;
import com.ossul.adapters.SpinnerListAdapter;
import com.ossul.appconstant.AppConstants;
import com.ossul.dialog.CustomDialogFragment;
import com.ossul.dialog.DialogManager;
import com.ossul.interfaces.INetworkEvent;
import com.ossul.network.NetworkService;
import com.ossul.network.constants.AppNetworkConstants;
import com.ossul.network.model.NetworkModel;
import com.ossul.network.model.response.CategoryList;
import com.ossul.network.model.response.CountriesList;
import com.ossul.network.model.response.CountryCitySettingResponse;
import com.ossul.network.model.response.GetCategoryListResponse;
import com.ossul.network.model.response.GetCityListResponse;
import com.ossul.network.model.response.GetCountryListResponse;
import com.ossul.network.model.response.GetSpecialPropertyParentRes;
import com.ossul.network.model.response.RealEstateOfficeList;
import com.ossul.network.model.response.SpecialOfferProperty;
import com.ossul.network.parser.ParserKeys;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.Validator;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * * Fragment for special offers list
 */
public class SpecialOffersActivity extends BaseActivity implements INetworkEvent, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = SpecialOffersActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeContainer;
    private View view;
    private DialogManager mProgressDialog;
    private String API_GET_STORE_LIST = "";
    private String API_GET_CATEGORY_LIST = "";
    private Spinner mCategoriesSPN;
    private RecyclerView mOffersRV;
    private TextView mNoDataTV;
    private String mCategoryId = "";
    private ArrayList<RealEstateOfficeList> mStoreList;
    private ArrayList<CategoryList> mCategoryList;
    private String API_GET_PROPERTY_LIST = "";
    private String mStoreId = "";
    private String API_COUNTRY_CITY_SETTING = "";

    private String API_COUNTRY_LIST = "";
    private LinearLayout filterLL;
    private boolean isCountryVisible;
    private boolean mCityVisible = false;
    private RelativeLayout cityRL, countryRL;
    private Spinner mCitySPN;
    private Spinner mCountrySPN;
    private ArrayList<GetCityListResponse.CityList> mCityList = new ArrayList<>();
    private ArrayList<String> mCityNameList = new ArrayList<>();
    private String mCityId = "";
    private ArrayList<CountriesList> mCountryList = new ArrayList<>();
    private ArrayList<String> mCountyNameList = new ArrayList<>();
    private String mCountryId = "4";
    private String API_CITY_LIST = "";
    private JSONObject jsonObject = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_special_offer);
        initData();
    }

    @Override
    protected void initViews() {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.customRefreshLayout);
//        mStoresSPN = (Spinner) findViewById(R.id.spn_stores);
        mCategoriesSPN = (Spinner) findViewById(R.id.spn_categories);
        mOffersRV = (RecyclerView) findViewById(R.id.rv_special_offer);
        mNoDataTV = (TextView) findViewById(R.id.tv_no_data);

        mCountrySPN = (Spinner) findViewById(R.id.spn_country);
        mCitySPN = (Spinner) findViewById(R.id.spn_city);
        filterLL = (LinearLayout) findViewById(R.id.ll_search_filter);
        countryRL = (RelativeLayout) findViewById(R.id.rl_country);
        cityRL = (RelativeLayout) findViewById(R.id.rl_city);


        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_add_news).setVisibility(View.GONE);


        final LinearLayoutManager layout = new LinearLayoutManager(this);
        mOffersRV.setLayoutManager(layout);
        mOffersRV.setHasFixedSize(true);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_add_news:
//                Intent intent = new Intent(this, SubmitRealEstateNewsActivity.class);
//                startActivityForResult(intent, 101);
                break;

            case R.id.iv_back:
                onBackPressed();
                break;
        }

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

    private void getStoreListAPI() {
        if (Validator.isConnectedToInternet(this)) {
            NetworkModel networkModel = new NetworkModel();
            API_GET_STORE_LIST = AppNetworkConstants.BASE_URL + "store/SpecialOffers.php?action=get-store-list";
            NetworkService service = new NetworkService(API_GET_STORE_LIST, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }

    private void getCategoryListAPI() {
        if (Validator.isConnectedToInternet(this)) {
            NetworkModel networkModel = new NetworkModel();
            API_GET_CATEGORY_LIST = AppNetworkConstants.BASE_URL + "store/SpecialOffers.php?action=get-category-list";
            NetworkService service = new NetworkService(API_GET_CATEGORY_LIST, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }

    @Override
    protected void initVariables() {
        mProgressDialog = new DialogManager(this);
        swipeContainer.setOnRefreshListener(this);
        mStoreList = new ArrayList<>();
        mCategoryList = new ArrayList<>();
        mStoreId = "";
        mCategoryId = "";
        getCountryCitySettingsAPI();
//        getStoreListAPI();
        getCategoryListAPI();

        if (!Validator.isEmptyString(mAppPreferences.getLabels())) {
            try {
                if (mAppPreferences.getLabels() != null && mAppPreferences.getLabels().length() != 0) {
                    jsonObject = new JSONObject(mAppPreferences.getLabels());
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "No data found")))
                        mNoDataTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "No data found"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mCategoriesSPN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mCategoryList != null && mCategoryList.size() > position) {
                    mCategoryId = mCategoryList.get(position).categoryId;
                    getPropertyListAPI(mStoreId, mCategoryId, mCityId, 1);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /*mStoresSPN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mStoreList != null && mStoreList.size() > position) {
                    mStoreId = mStoreList.get(position).storeId;
                    getPropertyListAPI(mStoreId, mCategoryId, mCityId, 1);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        mCountrySPN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mCountryList != null && mCountryList.size() > position && mCountryList.get(position) != null) {
                    mCountryId = mCountryList.get(position).countryId;
                    getCityListAPI(mCountryId);
                } else {
                    mCityList.clear();
                    mCityNameList.clear();
                    mCityNameList.add(AppUtilsMethod.getValueFromKey(jsonObject, "All City"));
                    mCityList.add(0, null);
                    SpinnerListAdapter cityAdapter = new SpinnerListAdapter(SpecialOffersActivity.this, mCityNameList);
                    mCitySPN.setAdapter(cityAdapter);
                    mCitySPN.setEnabled(false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mCitySPN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mCityList != null && mCityList.size() > position && mCityList.get(position) != null) {
                    mCityId = mCityList.get(position).cityId;
                    getPropertyListAPI(mStoreId, mCategoryId, mCityId, 1);
                } else {
                    mCityId = "";
                    if (mCitySPN.isEnabled())
                        getPropertyListAPI(mStoreId, mCategoryId, mCityId, 1);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getCountryCitySettingsAPI() {
        if (Validator.isConnectedToInternet(this.getApplicationContext())) {
            NetworkModel networkModel = new NetworkModel();
            API_COUNTRY_CITY_SETTING = AppNetworkConstants.BASE_URL + "user/manage.php?action=get-country-city-setting&screen_name=Offers";
            NetworkService service = new NetworkService(API_COUNTRY_CITY_SETTING, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }


    private void getCountryListAPI() {
        if (Validator.isConnectedToInternet(this.getApplicationContext())) {
            NetworkModel networkModel = new NetworkModel();
            API_COUNTRY_LIST = AppNetworkConstants.BASE_URL + "user/request.php?action=country-list";
            NetworkService service = new NetworkService(API_COUNTRY_LIST, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }

    private void getCityListAPI(String countryId) {
        if (Validator.isConnectedToInternet(this.getApplicationContext())) {
            NetworkModel networkModel = new NetworkModel();
            API_CITY_LIST = AppNetworkConstants.BASE_URL + "user/request.php?action=city-list&country_id=" + countryId;
            NetworkService service = new NetworkService(API_CITY_LIST, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }


    private void getPropertyListAPI(String mStoreId, String mCategoryId, String cityId, int page) {
        if (page == 1)
            swipeContainer.setRefreshing(true);

        API_GET_PROPERTY_LIST = AppNetworkConstants.BASE_URL + "store/SpecialOffers.php";
        NetworkService serviceCall = new NetworkService(API_GET_PROPERTY_LIST, AppConstants.METHOD_GET, this);
        MultipartBuilder multipartBuilder = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart(ParserKeys.action.toString(), AppNetworkConstants.ACTION_GET_PROPERTY)
                .addFormDataPart(ParserKeys.store_id.toString(), mStoreId + "")
                .addFormDataPart(ParserKeys.category_id.toString(), mCategoryId);
        if (!Validator.isEmptyString(cityId)) {
            multipartBuilder.addFormDataPart(ParserKeys.city_id.toString(), cityId + "");
            multipartBuilder.addFormDataPart(ParserKeys.country_id.toString(), mCountryId);
        }

        RequestBody requestBody = (RequestBody) multipartBuilder.build();
        serviceCall.setRequestBody(requestBody);
        serviceCall.call(new NetworkModel());
    }

    @Override
    public void onRefresh() {
        getPropertyListAPI(mStoreId, mCategoryId, mCityId, 1);
    }

    @Override
    public void onNetworkCallInitiated(String service) {
        if (swipeContainer != null)
            swipeContainer.setRefreshing(true);
    }

    @Override
    public void onNetworkCallCompleted(String service, String response) {
        Log.e(TAG, " success " + response);
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        swipeContainer.setRefreshing(false);
      /*  if (service.equalsIgnoreCase(API_GET_STORE_LIST)) {
            GetStoreListResponse getStoreListResponse = GetStoreListResponse.fromJson(response);
            if (getStoreListResponse != null && getStoreListResponse.data != null && getStoreListResponse.data.size() > 0) {
                mStoreList.clear();
                mStoreList = getStoreListResponse.data;
                ArrayList<String> categoryList = new ArrayList<>();
                for (RealEstateOfficeList list : getStoreListResponse.data) {
                    categoryList.add(list.storeName);
                }
                SpinnerListAdapter adapter = new SpinnerListAdapter(this, getStoreListResponse.data, categoryList);
                mStoresSPN.setAdapter(adapter);
            }
        } else */
        if (service.equalsIgnoreCase(API_GET_CATEGORY_LIST)) {
            GetCategoryListResponse getStoreListResponse = GetCategoryListResponse.fromJson(response);
            if (getStoreListResponse != null && getStoreListResponse.data != null && getStoreListResponse.data.size() > 0) {
                mCategoryList.clear();
                mCategoryList = getStoreListResponse.data;
                ArrayList<String> categoryList = new ArrayList<>();
                for (CategoryList list : getStoreListResponse.data) {
                    if (!Validator.isEmptyString(list.categoryName))
                        categoryList.add(list.categoryName);
                    else if (list.categoryId.equalsIgnoreCase("0"))
                        categoryList.add(this.getResources().getString(R.string.all));
                }
                SpinnerListAdapter adapter = new SpinnerListAdapter(this, getStoreListResponse.data, categoryList);
                mCategoriesSPN.setAdapter(adapter);
            }
        } else if (service.equalsIgnoreCase(API_GET_PROPERTY_LIST)) {
            GetSpecialPropertyParentRes getSpecialPropertyParentRes = GetSpecialPropertyParentRes.fromJson(response);
            if (getSpecialPropertyParentRes != null && getSpecialPropertyParentRes.data != null && getSpecialPropertyParentRes.data.size() > 0) {
                ArrayList<SpecialOfferProperty> list =new ArrayList<>();
                for (SpecialOfferProperty property : getSpecialPropertyParentRes.data) {
                    if (AppUtilsMethod.convertDateTimeInMillis(property.productOfferTo) >= System.currentTimeMillis()) {
                        list.add(property);
                    }
                }
                final SpecialOfferAdapter specialOfferAdapter = new SpecialOfferAdapter(this, list, jsonObject);
                mOffersRV.setAdapter(specialOfferAdapter);
                mOffersRV.setVisibility(View.VISIBLE);
                mNoDataTV.setVisibility(View.GONE);
            } else {
                mOffersRV.setVisibility(View.GONE);
                mNoDataTV.setVisibility(View.VISIBLE);
            }
        } else if (service.equalsIgnoreCase(API_CITY_LIST)) {
            GetCityListResponse getCityListResponse = GetCityListResponse.fromJson(response);
            if (getCityListResponse != null && getCityListResponse.data != null) {
                if (getCityListResponse.data.size() > 0) {
                    mCityList.clear();
                    mCityNameList.clear();
                    mCityNameList.add(AppUtilsMethod.getValueFromKey(jsonObject, "All City"));
                    mCityList = getCityListResponse.data;
                    for (GetCityListResponse.CityList list : mCityList) {
                        mCityNameList.add(list.cityName);
                    }

                    mCityList.add(0, null);
                    SpinnerListAdapter cityAdapter = new SpinnerListAdapter(this, mCityNameList);
                    mCitySPN.setAdapter(cityAdapter);
                    mCitySPN.setEnabled(true);
                }
            }
        } else if (service.equalsIgnoreCase(API_COUNTRY_LIST)) {
            GetCountryListResponse getCountryListResponse = GetCountryListResponse.fromJson(response);
            if (getCountryListResponse != null && getCountryListResponse.data != null) {
                if (getCountryListResponse.data.size() > 0) {
                    mCountryList.clear();
                    mCountyNameList.clear();
                    mCountyNameList.add(AppUtilsMethod.getValueFromKey(jsonObject, "All Country"));
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
        } else if (service.equalsIgnoreCase(API_COUNTRY_CITY_SETTING)) {
            CountryCitySettingResponse countryCitySettingResponse = CountryCitySettingResponse.fromJson(response);
            if (countryCitySettingResponse != null && countryCitySettingResponse.data != null) {
                if (countryCitySettingResponse.data.city == 1) {
                    cityRL.setVisibility(View.VISIBLE);
                    filterLL.setVisibility(View.VISIBLE);
                    mCityVisible = true;
                } else {
                    cityRL.setVisibility(View.GONE);
                    mCityVisible = false;
                }
                if (countryCitySettingResponse.data.country == 1) {
                    countryRL.setVisibility(View.VISIBLE);
                    filterLL.setVisibility(View.VISIBLE);
                    getCountryListAPI();
                    isCountryVisible = true;
                } else {
                    countryRL.setVisibility(View.GONE);
                    isCountryVisible = false;
                }

                if (!isCountryVisible && mCityVisible) {
                    cityRL.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                    getCityListAPI("4");
                }
                if (isCountryVisible && !mCityVisible) {
                    countryRL.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                if (!isCountryVisible && !mCityVisible) {
                    filterLL.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        swipeContainer.setRefreshing(false);
        CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
    }
}

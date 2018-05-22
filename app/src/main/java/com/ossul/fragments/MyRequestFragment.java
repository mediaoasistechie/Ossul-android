package com.ossul.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ossul.R;
import com.ossul.activities.MainActivity;
import com.ossul.adapters.MyRequestAdapter;
import com.ossul.adapters.SpinnerListAdapter;
import com.ossul.appconstant.AppConstants;
import com.ossul.dialog.CustomDialogFragment;
import com.ossul.dialog.DialogManager;
import com.ossul.interfaces.INetworkEvent;
import com.ossul.listener.OnItemClickListener;
import com.ossul.network.NetworkService;
import com.ossul.network.constants.AppNetworkConstants;
import com.ossul.network.model.NetworkModel;
import com.ossul.network.model.response.BaseResponse;
import com.ossul.network.model.response.CountriesList;
import com.ossul.network.model.response.CountryCitySettingResponse;
import com.ossul.network.model.response.GetCityListResponse;
import com.ossul.network.model.response.GetCountryListResponse;
import com.ossul.network.model.response.SubmitOfferParentResponse;
import com.ossul.network.model.response.SubmitOfferResponse;
import com.ossul.network.parser.ParserKeys;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.FragmentUtils;
import com.ossul.utility.Validator;
import com.ossul.view.HeaderHandler;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * * Fragment for offices list
 */
public class MyRequestFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, INetworkEvent, OnItemClickListener {
    private static final String TAG = MyRequestFragment.class.getSimpleName();
    private SwipeRefreshLayout swipeContainer;
    private View view;
    private DialogManager mProgressDialog;
    private EditText mSearchET;
    private String API_REMOVE_REQUEST = "";
    private RecyclerView mRequestsRV;
    private String API_MY_REQUEST = "";
    private ArrayList<SubmitOfferResponse> mRequestList;
    private TextView mNoDataTV;
    private MyRequestAdapter mRequestAdapter;
    private JSONObject jsonObject = new JSONObject();

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
    private String API_COUNTRY_CITY_SETTING = "";
    private AdapterView.OnItemSelectedListener onItemselectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (mCityList != null && mCityList.size() > position && mCityList.get(position) != null) {
                mCityId = mCityList.get(position).cityId;
                getMyRequestAPI(mCityId);
            } else {
                mCityId = "";
                if (mCitySPN.isEnabled())
                    getMyRequestAPI(mCityId);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private boolean showLoader = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_my_request, container, false);
        }
        return view;
    }

    @Override
    protected void initViews() {
        mSearchET = (EditText) view.findViewById(R.id.et_search);
        mRequestsRV = (RecyclerView) view.findViewById(R.id.rv_list);
        mNoDataTV = (TextView) view.findViewById(R.id.tv_no_data);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayout);

        mCountrySPN = (Spinner) view.findViewById(R.id.spn_country);
        mCitySPN = (Spinner) view.findViewById(R.id.spn_city);
        filterLL = (LinearLayout) view.findViewById(R.id.ll_filter);
        countryRL = (RelativeLayout) view.findViewById(R.id.rl_country);
        cityRL = (RelativeLayout) view.findViewById(R.id.rl_city);

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) mActivity).setHeaderVisibility(HeaderHandler.MY_REQUEST);

    }

    @Override
    protected void initVariables() {
        showLoader = false;
        mRequestList = new ArrayList<>();
        mProgressDialog = new DialogManager(mActivity);
        swipeContainer.setOnRefreshListener(this);
        mRequestsRV.setHasFixedSize(true);
        final LinearLayoutManager layout = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        mRequestsRV.setLayoutManager(layout);
        if (!Validator.isEmptyString(mAppPreferences.getLabels())) {
            try {
                jsonObject = new JSONObject(mAppPreferences.getLabels());
                if (jsonObject != null && jsonObject.length() != 0) {
                    mSearchET.setHint(AppUtilsMethod.getValueFromKey(jsonObject, "Search"));
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "No offer found")))
                        mNoDataTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "No offer found"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        getCountryCitySettingsAPI();
        getMyRequestAPI(mCityId);
        mSearchET.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Validator.isEmptyString(mSearchET.getText().toString().trim()) && mRequestAdapter != null)
                    mRequestAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mCountrySPN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mCountryList != null && mCountryList.size() > position && mCountryList.get(position) != null) {
                    mCountryId = mCountryList.get(position).countryId;
                    showLoader = true;
                    getCityListAPI(mCountryId);

                } else {
                    mCityList.clear();
                    mCityNameList.clear();
                    mCityNameList.add(AppUtilsMethod.getValueFromKey(jsonObject, "All City"));
                    mCityList.add(0, null);
                    SpinnerListAdapter cityAdapter = new SpinnerListAdapter(mActivity, mCityNameList);
                    mCitySPN.setAdapter(cityAdapter);
                    mCitySPN.setEnabled(false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void getCountryListAPI() {
        if (Validator.isConnectedToInternet(mActivity.getApplicationContext())) {
            NetworkModel networkModel = new NetworkModel();
            API_COUNTRY_LIST = AppNetworkConstants.BASE_URL + "user/request.php?action=country-list";
            NetworkService service = new NetworkService(API_COUNTRY_LIST, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(mActivity, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }

    private void getCityListAPI(String countryId) {
        if (Validator.isConnectedToInternet(mActivity.getApplicationContext())) {
            NetworkModel networkModel = new NetworkModel();
            API_CITY_LIST = AppNetworkConstants.BASE_URL + "user/request.php?action=city-list&country_id=" + countryId;
            NetworkService service = new NetworkService(API_CITY_LIST, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(mActivity, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }


    private void getMyRequestAPI(String cityId) {
        if (Validator.isConnectedToInternet(mActivity.getApplicationContext())) {
            NetworkModel networkModel = new NetworkModel();
            if (Validator.isEmptyString(cityId))
                API_MY_REQUEST = AppNetworkConstants.BASE_URL + "user/request.php?action=my-request";
            else
                API_MY_REQUEST = AppNetworkConstants.BASE_URL + "user/request.php?action=my-request&country_id=" + mCountryId + "&city_id=" + cityId;
            NetworkService service = new NetworkService(API_MY_REQUEST, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(mActivity, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }

    @Override
    public void onRefresh() {
        getMyRequestAPI(mCityId);
    }

    @Override
    public void onNetworkCallInitiated(String service) {
        if (mProgressDialog != null && !swipeContainer.isRefreshing() && (service.equalsIgnoreCase(API_MY_REQUEST) || showLoader || service.equalsIgnoreCase(API_REMOVE_REQUEST)))
            mProgressDialog.show();

    }

    @Override
    public void onNetworkCallCompleted(String service, String response) {
        Log.e(TAG, " success " + response);
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        swipeContainer.setRefreshing(false);
        if (service.equalsIgnoreCase(API_MY_REQUEST)) {
            SubmitOfferParentResponse submitOfferParentResponse = SubmitOfferParentResponse.fromJson(response);
            if (submitOfferParentResponse != null && submitOfferParentResponse.data != null) {
                mRequestList.clear();
                if (submitOfferParentResponse.data.size() > 0) {
                    mRequestList = submitOfferParentResponse.data;
                    if (((MainActivity) mActivity).mRequestCountTV != null)
                        ((MainActivity) mActivity).mRequestCountTV.setText(submitOfferParentResponse.data.size() + "");

                    mNoDataTV.setVisibility(View.GONE);
                    mRequestsRV.setVisibility(View.VISIBLE);
                    mRequestAdapter = new MyRequestAdapter(mActivity, mRequestList, jsonObject, this);
                    mRequestsRV.setAdapter(mRequestAdapter);
                    mRequestAdapter.setOnItemClickListener(this);
                } else {
                    mNoDataTV.setVisibility(View.VISIBLE);
                    mRequestsRV.setVisibility(View.GONE);
                }
            } else
                CustomDialogFragment.getInstance(mActivity, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
        } else if (service.equalsIgnoreCase(API_REMOVE_REQUEST)) {
            BaseResponse baseResponse = BaseResponse.fromJson(response);
            if (baseResponse != null && baseResponse.success) {
                if (!Validator.isEmptyString(baseResponse.successMessage))
                    CustomDialogFragment.getInstance(mActivity, null, baseResponse.successMessage, null,AppConstants.OK, null, 1, null);
            } else
                CustomDialogFragment.getInstance(mActivity, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
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
                    SpinnerListAdapter cityAdapter = new SpinnerListAdapter(mActivity, mCityNameList);
                    mCitySPN.setAdapter(cityAdapter);
                    mCitySPN.setOnItemSelectedListener(null);
                    mCitySPN.setEnabled(true);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mCitySPN != null && onItemselectedListener != null)
                                mCitySPN.setOnItemSelectedListener(onItemselectedListener);
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
                    mCountyNameList.add(AppUtilsMethod.getValueFromKey(jsonObject, "All Country"));
                    mCountryList = getCountryListResponse.data;
                    for (CountriesList list : mCountryList) {
                        mCountyNameList.add(list.countryName);
                    }

                    mCountryList.add(0, null);
                    SpinnerListAdapter adapter = new SpinnerListAdapter(mActivity, mCountyNameList);
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


    private void getCountryCitySettingsAPI() {
        if (Validator.isConnectedToInternet(mActivity.getApplicationContext())) {
            NetworkModel networkModel = new NetworkModel();
            API_COUNTRY_CITY_SETTING = AppNetworkConstants.BASE_URL + "user/manage.php?action=get-country-city-setting&screen_name=My Request";
            NetworkService service = new NetworkService(API_COUNTRY_CITY_SETTING, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(mActivity, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }


    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        swipeContainer.setRefreshing(false);
        CustomDialogFragment.getInstance(mActivity, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);

    }

    @Override
    public void onItemClick(int position) {
        if (mRequestList != null && mRequestList.size() > position) {
            SubmitRequestFragment submitRequestFragment = SubmitRequestFragment.newInstance(mRequestList.get(position));
            FragmentUtils.replaceFragment(mActivity.getSupportFragmentManager(), submitRequestFragment.getClass().getSimpleName() + "edit", true, R.id.container, submitRequestFragment);

        }
    }

    public void callRemoveRequestAPI(String requestId, String userId) {
        API_REMOVE_REQUEST = AppNetworkConstants.BASE_URL + "user/request.php";
        NetworkService serviceCall = new NetworkService(API_REMOVE_REQUEST, AppConstants.METHOD_POST, this);
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart(ParserKeys.action.toString(), AppNetworkConstants.ACTION_REMOVE_REQUEST)
                .addFormDataPart(ParserKeys.user_id.toString(), userId)
                .addFormDataPart(ParserKeys.request_id.toString(), requestId)
                .build();
        serviceCall.setRequestBody(requestBody);
        serviceCall.call(new NetworkModel());
    }

    public void changeLabels() {
        ((MainActivity) mActivity).setHeaderVisibility(HeaderHandler.MY_REQUEST);
    }
}

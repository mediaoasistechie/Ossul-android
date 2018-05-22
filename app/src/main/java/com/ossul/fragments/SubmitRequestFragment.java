package com.ossul.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ossul.R;
import com.ossul.activities.MainActivity;
import com.ossul.adapters.CustomArrayAdapter;
import com.ossul.adapters.ZoneTypeListAdapter;
import com.ossul.appconstant.AppConstants;
import com.ossul.appconstant.ErrorConstant;
import com.ossul.dialog.CustomDialogFragment;
import com.ossul.dialog.DialogManager;
import com.ossul.interfaces.INetworkEvent;
import com.ossul.interfaces.IValidationResult;
import com.ossul.listener.OnItemClickListener;
import com.ossul.network.NetworkService;
import com.ossul.network.constants.AppNetworkConstants;
import com.ossul.network.model.NetworkModel;
import com.ossul.network.model.response.BaseResponse;
import com.ossul.network.model.response.GetCityListResponse;
import com.ossul.network.model.response.GetSubmitRequestData;
import com.ossul.network.model.response.PropertyFields;
import com.ossul.network.model.response.SubmitOfferResponse;
import com.ossul.network.parser.ParserKeys;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.AppValidationChecker;
import com.ossul.utility.FragmentUtils;
import com.ossul.utility.Validator;
import com.ossul.view.HeaderHandler;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import org.florescu.android.rangeseekbar.RangeSeekBar;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * * Fragment for offices list
 */
public class SubmitRequestFragment extends BaseFragment implements INetworkEvent, OnItemClickListener, IValidationResult {
    private static final String TAG = SubmitRequestFragment.class.getSimpleName();
    private View view;
    private Dialog mProgressDialog;
    private String API_SUBMIT_REQUEST_DATA = "";
    private Spinner mCountrySPN, mCitySPN, mSaleSPN, mPropertySPN;
    private RecyclerView mZoneRV;
    private EditText mDescriptionET /*mMinET, mMaxET*/;
    private ScrollView scrollView;
    private ArrayList<GetSubmitRequestData.Data.CountryList> mCountryList;
    private ArrayList<GetSubmitRequestData.Data.SaleType> mSaleTypeList;
    private ArrayList<GetSubmitRequestData.Data.ZoneType> mZoneTypeList;
    private ArrayList<GetSubmitRequestData.Data.PropertyType> mPropertyTypeList;
    private ArrayList<String> mCountyNameList;

    private ArrayList<String> mSaleTypeNameList;
    private ArrayList<String> mCityNameList;
    private ArrayList<String> mPropertyTypeNameList;
    private String API_CITY_LIST;
    private ArrayList<GetCityListResponse.CityList> mCityList;
    private ArrayList<String> mZoneIds = new ArrayList<>();
    private String API_SUBMIT_REQUEST = "";
    private SubmitOfferResponse offerResponse;
    private JSONObject jsonObject = new JSONObject();
    private TextView mPriceRangeTV;
    private TextView mSubmitTV;
    private TextView mMaxTV, mMinTV;
    private RelativeLayout mCountryRL, mCityRL;
    private boolean isCountryVisible = false;
    private boolean mCityVisible = false;
    private String country = "Country", cityLabel = "City";
    private RangeSeekBar<Long> mRangeSeekBar;
    private long mMinValue = 0, mMaxValue = 0;

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (mCountyNameList != null && mCountryList != null && position > 0) {
                String countryId = "";
                String countryName = mCountyNameList.get(position);
                for (GetSubmitRequestData.Data.CountryList list : mCountryList) {
                    if (!Validator.isEmptyString(countryName) && list != null && countryName.equalsIgnoreCase(list.countryName)) {
                        countryId = list.countryId;
                    }
                }
                if (!Validator.isEmptyString(countryId)) {
                    getCityListAPI(countryId);
                }
            }
            if (position == 0 && mCityNameList != null && mCityList != null) {
                mCityNameList.clear();
                mCityNameList.add(AppUtilsMethod.getValueFromKey(jsonObject, "City"));
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(mActivity, R.layout.spinner_item, mCityNameList);
                mCitySPN.setAdapter(cityAdapter);
                mCityList.clear();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    public static SubmitRequestFragment newInstance(SubmitOfferResponse response) {
        SubmitRequestFragment fragment = new SubmitRequestFragment();
        Bundle b = new Bundle();
        if (response != null)
            b.putParcelable("response", response);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            offerResponse = getArguments().getParcelable("response");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_submit_request, container, false);
        }
        return view;
    }

    private void showFields() {
        for (PropertyFields fields : AppConstants.mPropertyFields) {
            if (fields != null) {
                switch (fields.field) {
                    case "city":
                        if (fields.visible == 1) {
                            mCityRL.setVisibility(View.VISIBLE);
                            mCityVisible = true;
                            cityLabel = fields.label;
                        } else {
                            mCityRL.setVisibility(View.GONE);
                            mCityVisible = false;
                        }
                        break;
                    case "country":
                        if (fields.visible == 1) {
                            mCountryRL.setVisibility(View.VISIBLE);
                            isCountryVisible = true;
                            country = fields.label;
                        } else {
                            mCountryRL.setVisibility(View.GONE);
                            isCountryVisible = false;
                        }
                        break;
                }
            }
        }
        if (!isCountryVisible && mCityVisible) {
            getCityListAPI("4");
        }
        mDescriptionET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.post(new Runnable() {
                            public void run() {
                                scrollView.smoothScrollTo(0, mSubmitTV.getBottom()); // these are your x and y coordinates
                            }
                        });
                    }
                }, 1000);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (offerResponse != null)
            ((MainActivity) mActivity).setHeaderVisibility(HeaderHandler.EDIT_REQUEST);
        else
            ((MainActivity) mActivity).setHeaderVisibility(HeaderHandler.SUBMIT_REQUEST);
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    protected void initViews() {
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
//        mMaxET = (EditText) view.findViewById(R.id.et_max);
        mMinTV = (TextView) view.findViewById(R.id.tv_min);
        mMaxTV = (TextView) view.findViewById(R.id.tv_max);
        mDescriptionET = (EditText) view.findViewById(R.id.et_description);
        mZoneRV = (RecyclerView) view.findViewById(R.id.rv_zone);

        mCountrySPN = (Spinner) view.findViewById(R.id.spn_country);
        mCitySPN = (Spinner) view.findViewById(R.id.spn_city);
        mCountryRL = (RelativeLayout) view.findViewById(R.id.rl_country);
        mCityRL = (RelativeLayout) view.findViewById(R.id.rl_city);
        mSaleSPN = (Spinner) view.findViewById(R.id.spn_sale_type);
        mPriceRangeTV = (TextView) view.findViewById(R.id.tv_price_range);
        mPropertySPN = (Spinner) view.findViewById(R.id.spn_property_type);
        mSubmitTV = (TextView) view.findViewById(R.id.tv_submit);


        mRangeSeekBar = (RangeSeekBar<Long>) view.findViewById(R.id.range_seekbar);
        mRangeSeekBar.setNotifyWhileDragging(true);
        mRangeSeekBar.setRangeValues(0l, 10000000000l);
        mMinTV.setText("0");
        mMaxTV.setText("10000000000");
        mMinValue = 0;
        mMaxValue = 10000000000L;
        mRangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Long>() {

            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Long minValue, Long maxValue) {
                String min = String.valueOf(minValue);
                String max = String.valueOf(maxValue);
                if (TextUtils.isDigitsOnly(min) && TextUtils.isDigitsOnly(max)) {
                    mMinValue = Long.parseLong(min);
                    mMaxValue = Long.parseLong(max);
                }
                mMinTV.setText("" + min);
                mMaxTV.setText("" + max);
            }

        });


        mSubmitTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Zone Ids", mZoneIds.toString());
                AppValidationChecker.validateSubmitRequestApi(mActivity, mCountrySPN.getSelectedItem().toString(), mCitySPN.getSelectedItem().toString(), mSaleSPN.getSelectedItem().toString(), mPropertySPN.getSelectedItem().toString(), mMinValue, mMaxValue, mDescriptionET.getText().toString().trim(), SubmitRequestFragment.this);
            }
        });
    }


    @Override
    protected void initVariables() {
        if (!Validator.isEmptyString(mAppPreferences.getLabels())) {
            try {
                jsonObject = new JSONObject(mAppPreferences.getLabels());
                if (jsonObject != null && jsonObject.length() != 0) {
//                    mMaxTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Max:"));
//                    mMinTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Min:"));
                    mPriceRangeTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Price Range"));
                    mDescriptionET.setHint(AppUtilsMethod.getValueFromKey(jsonObject, "Describe what you need"));
                    mSubmitTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Submit"));
                    try {
                        if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Request max price"))) {
                            if (TextUtils.isDigitsOnly(AppUtilsMethod.getValueFromKey(jsonObject, "Request max price"))) {
                                long l = Long.parseLong(AppUtilsMethod.getValueFromKey(jsonObject, "Request max price"));
                                mRangeSeekBar.setRangeValues(0L, l);
                                mMinTV.setText("0");
                                mMaxTV.setText(String.valueOf(l));
                                mMinValue = 0;
                                mMaxValue = l;
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        mCountryList = new ArrayList<>();
        mCountyNameList = new ArrayList<>();

        mCityNameList = new ArrayList<>();
        mCityList = new ArrayList<>();


        mSaleTypeList = new ArrayList<>();
        mSaleTypeNameList = new ArrayList<>();

        mPropertyTypeList = new ArrayList<>();
        mPropertyTypeNameList = new ArrayList<>();

        mZoneTypeList = new ArrayList<>();

        mProgressDialog = new DialogManager(mActivity);
        final CustomGridLayoutManager layout = new CustomGridLayoutManager(mActivity.getApplicationContext());
        layout.setScrollEnabled(false);
        mZoneRV.setLayoutManager(layout);
        mZoneRV.setHasFixedSize(true);
        getSubmitRequestDataAPI();

        mCityNameList.clear();
        mCityNameList.add(AppUtilsMethod.getValueFromKey(jsonObject, "City"));
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(mActivity, R.layout.spinner_item, mCityNameList);
        mCitySPN.setAdapter(cityAdapter);
        mCitySPN.setEnabled(false);


        mCountyNameList.clear();
        if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Country")))
            mCountyNameList.add(AppUtilsMethod.getValueFromKey(jsonObject, "Country"));
        else
            mCountyNameList.add("Country");
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(mActivity, R.layout.spinner_item, mCountyNameList);
        mCountrySPN.setAdapter(countryAdapter);
        mCountrySPN.setEnabled(false);

        mSaleTypeNameList.clear();
        if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "For sale/rent")))
            mSaleTypeNameList.add(AppUtilsMethod.getValueFromKey(jsonObject, "For sale/rent"));
        else
            mSaleTypeNameList.add("For sale/rent");

        ArrayAdapter<String> saleAdapter = new ArrayAdapter<String>(mActivity, R.layout.spinner_item, mSaleTypeNameList);
        mSaleSPN.setAdapter(saleAdapter);
        mSaleSPN.setEnabled(false);

        mPropertyTypeNameList.clear();
        if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Property Type")))
            mPropertyTypeNameList.add(AppUtilsMethod.getValueFromKey(jsonObject, "Property Type"));
        else
            mPropertyTypeNameList.add("Property Type");
        ArrayAdapter<String> propertyAdapter = new ArrayAdapter<String>(mActivity, R.layout.spinner_item, mPropertyTypeNameList);
        mPropertySPN.setAdapter(propertyAdapter);
        mPropertySPN.setEnabled(false);

        if (offerResponse != null) {
            try {
                if (TextUtils.isDigitsOnly(offerResponse.minPrice)) {
                    mRangeSeekBar.setSelectedMinValue(Long.parseLong(offerResponse.minPrice));
                    mMinTV.setText(offerResponse.minPrice + "");
                    mMinValue = Long.parseLong(offerResponse.minPrice);
                }
                if (TextUtils.isDigitsOnly(offerResponse.maxPrice)) {
                    mRangeSeekBar.setSelectedMaxValue(Long.parseLong(offerResponse.maxPrice));
                    mMaxTV.setText(offerResponse.maxPrice + "");
                    mMaxValue = Long.parseLong(offerResponse.maxPrice);
                }
            } catch (Exception e) {

            }
            mDescriptionET.setText(offerResponse.description);
        }
        showFields();

        if (offerResponse != null)
            ((MainActivity) mActivity).setHeaderVisibility(HeaderHandler.EDIT_REQUEST);
        else
            ((MainActivity) mActivity).setHeaderVisibility(HeaderHandler.SUBMIT_REQUEST);
    }

    private void getSubmitRequestDataAPI() {
        if (Validator.isConnectedToInternet(mActivity.getApplicationContext())) {
            NetworkModel networkModel = new NetworkModel();
            API_SUBMIT_REQUEST_DATA = AppNetworkConstants.BASE_URL + "user/request.php?action=get-submit-request-data";
            NetworkService service = new NetworkService(API_SUBMIT_REQUEST_DATA, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(mActivity, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }

    private void updateRequestAPI(String countryId, String cityId, String saleTypeId, String propertyId, ArrayList<String> zoneTypeIds, long minPrice, long maxPrice, String description, String userId, String requestId) {
        API_SUBMIT_REQUEST = AppNetworkConstants.BASE_URL + "user/request.php";
        NetworkService serviceCall = new NetworkService(API_SUBMIT_REQUEST, AppConstants.METHOD_POST, this);
        MultipartBuilder multipartBuilder = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart(ParserKeys.action.toString(), AppNetworkConstants.ACTION_EDIT_REQUEST)
                .addFormDataPart(ParserKeys.country_id.toString(), countryId)
                .addFormDataPart(ParserKeys.city_id.toString(), cityId)
                .addFormDataPart(ParserKeys.sale_type_id.toString(), saleTypeId)
                .addFormDataPart(ParserKeys.property_type_id.toString(), propertyId)
                .addFormDataPart(ParserKeys.min_price.toString(), minPrice + "")
                .addFormDataPart(ParserKeys.max_price.toString(), maxPrice + "")
                .addFormDataPart(ParserKeys.description.toString(), description)

                .addFormDataPart(ParserKeys.user_id.toString(), userId)
                .addFormDataPart(ParserKeys.request_id.toString(), requestId);
        //city_id,country_id,max_price,min_price,description,
//        Property_type_id,sale_type_id,zone_type_id,user_id, request_id

        for (int i = 0; i < zoneTypeIds.size(); i++) {
            multipartBuilder.addFormDataPart(ParserKeys.zone_type_id.toString() + "[" + i + "]", zoneTypeIds.get(i));
        }
        RequestBody requestBody = (RequestBody) multipartBuilder.build();
        serviceCall.setRequestBody(requestBody);
        serviceCall.call(new NetworkModel());
    }


    private void submitRequestAPI(String countryId, String cityId, String saleTypeId, String propertyId, ArrayList<String> zoneTypeIds, long minPrice, long maxPrice, String description) {
        API_SUBMIT_REQUEST = AppNetworkConstants.BASE_URL + "user/request.php";
        NetworkService serviceCall = new NetworkService(API_SUBMIT_REQUEST, AppConstants.METHOD_POST, this);
        MultipartBuilder multipartBuilder = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart(ParserKeys.action.toString(), AppNetworkConstants.ACTION_SUBMIT_REQUEST)
                .addFormDataPart(ParserKeys.country_id.toString(), countryId)
                .addFormDataPart(ParserKeys.city_id.toString(), cityId)
                .addFormDataPart(ParserKeys.sale_type_id.toString(), saleTypeId)
                .addFormDataPart(ParserKeys.property_type_id.toString(), propertyId)
                .addFormDataPart(ParserKeys.min_price.toString(), minPrice + "")
                .addFormDataPart(ParserKeys.max_price.toString(), maxPrice + "")
                .addFormDataPart(ParserKeys.description.toString(), description);

        for (int i = 0; i < zoneTypeIds.size(); i++) {
            multipartBuilder.addFormDataPart(ParserKeys.zone_type_id.toString() + "[" + i + "]", zoneTypeIds.get(i));
        }
        RequestBody requestBody = (RequestBody) multipartBuilder.build();
        serviceCall.setRequestBody(requestBody);
        serviceCall.call(new NetworkModel());
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

    @Override
    public void onNetworkCallInitiated(String service) {
        if (mProgressDialog != null)
            mProgressDialog.show();

    }

    @Override
    public void onNetworkCallCompleted(String service, String response) {
        Log.e(TAG, " success " + response);
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        if (service.equalsIgnoreCase(API_SUBMIT_REQUEST_DATA)) {
            GetSubmitRequestData getSubmitRequestData = GetSubmitRequestData.fromJson(response);
            if (getSubmitRequestData != null && getSubmitRequestData.data != null) {
                if (getSubmitRequestData.data.countryList != null && getSubmitRequestData.data.countryList.size() > 0) {
                    mCountryList.clear();
                    mCountyNameList.clear();
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Country")))
                        mCountyNameList.add(AppUtilsMethod.getValueFromKey(jsonObject, "Country"));
                    else
                        mCountyNameList.add("Country");
                    mCountryList = getSubmitRequestData.data.countryList;
                    for (GetSubmitRequestData.Data.CountryList list : mCountryList) {
                        mCountyNameList.add(list.countryName);
                    }
                    CustomArrayAdapter adapter = new CustomArrayAdapter(mActivity, mCountyNameList);
                    mCountrySPN.setAdapter(adapter);
                    mCountrySPN.setEnabled(true);
                    if (mCountrySPN != null && onItemSelectedListener != null)
                        mCountrySPN.setOnItemSelectedListener(onItemSelectedListener);

                    if (offerResponse != null && offerResponse.countryId != null) {
                        for (int i = 0; i < mCountryList.size(); i++) {
                            if (offerResponse.countryId.equalsIgnoreCase(mCountryList.get(i).countryId)) {
                                mCountrySPN.setSelection(i + 1);
                                break;
                            }
                        }
                    }
                }
                if (getSubmitRequestData.data.saleTypeList != null && getSubmitRequestData.data.saleTypeList.size() > 0) {
                    mSaleTypeList.clear();
                    mSaleTypeNameList.clear();
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "For sale/rent")))
                        mSaleTypeNameList.add(AppUtilsMethod.getValueFromKey(jsonObject, "For sale/rent"));
                    else
                        mSaleTypeNameList.add("For sale/rent");

                    mSaleTypeList = getSubmitRequestData.data.saleTypeList;
                    for (GetSubmitRequestData.Data.SaleType list : mSaleTypeList) {
                        mSaleTypeNameList.add(list.saleTypeName);
                    }
                    CustomArrayAdapter adapter = new CustomArrayAdapter(mActivity, mSaleTypeNameList);
                    mSaleSPN.setAdapter(adapter);
                    mSaleSPN.setEnabled(true);
                    if (offerResponse != null && offerResponse.saleTypeId != null) {
                        for (int i = 0; i < mSaleTypeList.size(); i++) {
                            if (offerResponse.saleTypeId.equalsIgnoreCase(mSaleTypeList.get(i).saleTypeId)) {
                                mSaleSPN.setSelection(i + 1);
                                break;
                            }
                        }
                    }
                }
                if (getSubmitRequestData.data.propertyTypeList != null && getSubmitRequestData.data.propertyTypeList.size() > 0) {
                    mPropertyTypeList.clear();
                    mPropertyTypeNameList.clear();
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Property Type")))
                        mPropertyTypeNameList.add(AppUtilsMethod.getValueFromKey(jsonObject, "Property Type"));
                    else
                        mPropertyTypeNameList.add("Property Type");
                    mPropertyTypeList = getSubmitRequestData.data.propertyTypeList;
                    for (GetSubmitRequestData.Data.PropertyType list : mPropertyTypeList) {
                        mPropertyTypeNameList.add(list.propertyTypeName);
                    }
                    CustomArrayAdapter adapter = new CustomArrayAdapter(mActivity, mPropertyTypeNameList);
                    mPropertySPN.setAdapter(adapter);
                    mPropertySPN.setEnabled(true);

                    if (offerResponse != null && offerResponse.propertyTypeId != null) {
                        for (int i = 0; i < mPropertyTypeList.size(); i++) {
                            if (offerResponse.propertyTypeId.equalsIgnoreCase(mPropertyTypeList.get(i).propertyTypeId)) {
                                mPropertySPN.setSelection(i + 1);
                                break;
                            }
                        }
                    }
                }
                if (getSubmitRequestData.data.zoneTypeList != null && getSubmitRequestData.data.zoneTypeList.size() > 0) {
                    mZoneTypeList.clear();
                    mZoneTypeList = getSubmitRequestData.data.zoneTypeList;
                    ZoneTypeListAdapter zoneTypeListAdapter = new ZoneTypeListAdapter(mActivity, mZoneTypeList);
                    mZoneRV.setAdapter(zoneTypeListAdapter);
                    zoneTypeListAdapter.setOnItemClickListener(this);

                    if (offerResponse != null && offerResponse.zoneTypeId != null) {
                        for (int i = 0; i < mZoneTypeList.size(); i++) {
                            String ids[] = offerResponse.zoneTypeId.split(",");
                            for (int j = 0; j < ids.length; j++) {
                                if (ids[j].equalsIgnoreCase(mZoneTypeList.get(i).zoneTypeId)) {
                                    mZoneTypeList.get(i).isChecked = true;
                                }
                            }
                        }
                    }
                }
            } else
                CustomDialogFragment.getInstance(mActivity, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
        } else if (service.equalsIgnoreCase(API_CITY_LIST)) {
            GetCityListResponse getCityListResponse = GetCityListResponse.fromJson(response);
            if (getCityListResponse != null && getCityListResponse.data != null) {
                if (getCityListResponse.data.size() > 0) {
                    mCityList.clear();
                    mCityNameList.clear();
                    mCityNameList.add(AppUtilsMethod.getValueFromKey(jsonObject, "City"));
                    mCityList = getCityListResponse.data;
                    for (GetCityListResponse.CityList list : mCityList) {
                        mCityNameList.add(list.cityName);
                    }
                    CustomArrayAdapter adapter = new CustomArrayAdapter(mActivity, mCityNameList);
                    mCitySPN.setAdapter(adapter);
                    mCitySPN.setEnabled(true);

                    if (offerResponse != null && offerResponse.cityId != null) {
                        for (int i = 0; i < mCityList.size(); i++) {
                            if (offerResponse.cityId.equalsIgnoreCase(mCityList.get(i).cityId)) {
                                mCitySPN.setSelection(i + 1);
                                break;
                            }
                        }
                    }
                }
            } else
                CustomDialogFragment.getInstance(mActivity, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
        } else if (service.equalsIgnoreCase(API_SUBMIT_REQUEST)) {
//            {"success":true,"statuscode":"200","data":{"request_id":16}}
            BaseResponse baseResponse = BaseResponse.fromJson(response);
            if (baseResponse != null && baseResponse.success && baseResponse.statusCode == 200) {
                SubmitOfferFragment submitOfferFragment = new SubmitOfferFragment();
                FragmentUtils.replaceFragment(mActivity.getSupportFragmentManager(), submitOfferFragment.getClass().getSimpleName(), true, R.id.container, submitOfferFragment);
            } else if (baseResponse != null && baseResponse.success && !Validator.isEmptyString(baseResponse.successMessage)) {
                CustomDialogFragment.getInstance(mActivity, null, baseResponse.successMessage, null, AppConstants.OK, null, 1, null);
                SubmitOfferFragment submitOfferFragment = new SubmitOfferFragment();
                FragmentUtils.replaceFragment(mActivity.getSupportFragmentManager(), submitOfferFragment.getClass().getSimpleName(), true, R.id.container, submitOfferFragment);
            } else if (baseResponse.error && !Validator.isEmptyString(baseResponse.errorMessage)) {
                CustomDialogFragment.getInstance(mActivity, null, baseResponse.errorMessage, null, AppConstants.OK, null, 1, null);
            } else {
                CustomDialogFragment.getInstance(mActivity, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
            }
        }
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        CustomDialogFragment.getInstance(mActivity, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);

    }

    @Override
    public void onItemClick(int position) {
       /* if (mZoneIds != null && mZoneTypeList != null) {
            for (int i = 0; i < mZoneTypeList.size(); i++) {
                if (mZoneTypeList.get(i).isChecked)
                    mZoneIds.add(mZoneTypeList.get(i).zoneTypeId);
            }
            if (!mZoneIds.contains(mZoneTypeList.get(position).zoneTypeId))
                mZoneIds.add(mZoneTypeList.get(position).zoneTypeId);
            else
                mZoneIds.remove(mZoneTypeList.get(position).zoneTypeId);
        }*/
    }

    @Override
    public void onValidationError(int errorType, int errorResId) {
        switch (errorType) {
            case ErrorConstant.ERROR_TYPE_MIN_PRICE_EMPTY:
//                mMinET.requestFocus();
//                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Enter minimum price")))
//                    mMinET.setError(AppUtilsMethod.getValueFromKey(jsonObject, "Enter minimum price"));
//                else
//                    mMinET.setError(getString(errorResId));
                break;
            case ErrorConstant.ERROR_TYPE_MAX_PRICE_EMPTY:
//                mMaxET.requestFocus();
//                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Enter maximum price")))
//                    mMaxET.setError(AppUtilsMethod.getValueFromKey(jsonObject, "Enter maximum price"));
//                else
//                    mMaxET.setError(getString(errorResId));
                break;
            case ErrorConstant.ERROR_TYPE_DESCRIPTION_EMPTY:
                mDescriptionET.requestFocus();
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Enter description")))
                    mDescriptionET.setError(AppUtilsMethod.getValueFromKey(jsonObject, "Enter description"));
                else
                    mDescriptionET.setError(getString(errorResId));
                break;
        }
    }

    @Override
    public void onValidationSuccess() {
        String cityId = "";
        for (GetCityListResponse.CityList citiesList : mCityList) {
            if (citiesList != null && !Validator.isEmptyString(citiesList.cityName)) {
                if (citiesList.cityName.equalsIgnoreCase(mCitySPN.getSelectedItem().toString())) {
                    cityId = citiesList.cityId;
                    break;
                }
            }
        }
        String countryId = "";
        for (GetSubmitRequestData.Data.CountryList officeList : mCountryList) {
            if (officeList != null && !Validator.isEmptyString(officeList.countryName)) {
                if (officeList.countryName.equalsIgnoreCase(mCountrySPN.getSelectedItem().toString())) {
                    countryId = officeList.countryId;
                    break;
                }
            }
        }
        String saleId = "";
        for (GetSubmitRequestData.Data.SaleType region : mSaleTypeList) {
            if (region != null && !Validator.isEmptyString(region.saleTypeName)) {
                if (region.saleTypeName.equalsIgnoreCase(mSaleSPN.getSelectedItem().toString())) {
                    saleId = region.saleTypeId;
                    break;
                }
            }
        }
        String propertyId = "";
        for (GetSubmitRequestData.Data.PropertyType region : mPropertyTypeList) {
            if (region != null && !Validator.isEmptyString(region.propertyTypeName)) {
                if (region.propertyTypeName.equalsIgnoreCase(mPropertySPN.getSelectedItem().toString())) {
                    propertyId = region.propertyTypeId;
                    break;
                }
            }
        }
        for (int i = 0; i < mZoneTypeList.size(); i++) {
            if (mZoneTypeList.get(i).isChecked)
                mZoneIds.add(mZoneTypeList.get(i).zoneTypeId);
        }

        if (mCountryRL.getVisibility() == View.VISIBLE && Validator.isEmptyString(countryId)) {

            String error = "Please select " + country;
            if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, error)))
                error = AppUtilsMethod.getValueFromKey(jsonObject, error);
            else if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Please select")))
                error = AppUtilsMethod.getValueFromKey(jsonObject, "Please select") + " " + country;
            else
                error = getResources().getString(R.string.please_select) + " " + country;

            CustomDialogFragment.getInstance(mActivity, null, error, null, "OK", null, 1, null);

        } else if (mCityRL.getVisibility() == View.VISIBLE && Validator.isEmptyString(cityId)) {
            String error = "Please select " + cityLabel;
            if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, error)))
                error = AppUtilsMethod.getValueFromKey(jsonObject, error);

            else if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Please select")))
                error = AppUtilsMethod.getValueFromKey(jsonObject, "Please select") + " " + cityLabel;
            else
                error = getResources().getString(R.string.please_select) + " " + cityLabel;

            CustomDialogFragment.getInstance(mActivity, null, error, null, "OK", null, 1, null);
        } else if (mDescriptionET.getVisibility() == View.VISIBLE && Validator.isEmptyString(mDescriptionET.getText().toString().trim())) {

            mDescriptionET.requestFocus();
            if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Please enter Description")))
                mDescriptionET.setError(AppUtilsMethod.getValueFromKey(jsonObject, "Please enter Description"));
            else
                mDescriptionET.setError(getResources().getString(R.string.error_enter_description));

        } else if (mZoneIds != null && mZoneIds.size() > 0) {
            if (offerResponse != null)
                updateRequestAPI(countryId, cityId, saleId, propertyId, mZoneIds, mMinValue, mMaxValue, mDescriptionET.getText().toString(), offerResponse.userId, offerResponse.requestId);
            else
                submitRequestAPI(countryId, cityId, saleId, propertyId, mZoneIds, mMinValue, mMaxValue, mDescriptionET.getText().toString());
        } else {
            CustomDialogFragment.getInstance(mActivity, null, AppUtilsMethod.getValueFromKey(jsonObject, "Select a zone"), null, "OK", null, 1, null);
        }

    }

    public void changeLabels() {
        initVariables();
    }


    public class CustomGridLayoutManager extends GridLayoutManager {
        private boolean isScrollEnabled = true;

        public CustomGridLayoutManager(Context context) {
            super(context, 2);
        }

        public void setScrollEnabled(boolean flag) {
            this.isScrollEnabled = flag;
        }

        @Override
        public boolean canScrollVertically() {
            //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
            return isScrollEnabled && super.canScrollVertically();
        }
    }
}

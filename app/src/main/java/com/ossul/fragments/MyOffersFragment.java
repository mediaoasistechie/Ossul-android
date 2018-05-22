package com.ossul.fragments;

import android.app.Dialog;
import android.content.Intent;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ossul.R;
import com.ossul.activities.BaseActivity;
import com.ossul.activities.CommentsActivity;
import com.ossul.activities.FullImageActivity;
import com.ossul.activities.MainActivity;
import com.ossul.adapters.MyOffersAdapter;
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
import com.ossul.network.model.response.MyOffersResponse;
import com.ossul.network.model.response.PropertyFields;
import com.ossul.network.model.response.ViewOffersResponse;
import com.ossul.network.parser.ParserKeys;
import com.ossul.utility.AppUtilsMethod;
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
public class MyOffersFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, INetworkEvent, OnItemClickListener {
    private static final String TAG = MyOffersFragment.class.getSimpleName();
    private SwipeRefreshLayout swipeContainer;
    private View view;
    private String API_REMOVE_REQUEST = "";
    private DialogManager mProgressDialog;
    private MyOffersAdapter mSubmitOfferAdapter;
    private EditText mSearchET;
    private RecyclerView mOffersRV;
    private ArrayList<MyOffersResponse.Data> mOfferList;
    private TextView mNoDataTV;
    private String mFrom = "";
    private String API_GET_OFFERS = "";
    private String API_VIEW_OFFER = "";
    private Dialog dialog;
    private ImageView propertyIV;
    private TextView titleTV;
    private TextView descriptionTV, mMinPriceTV, mMaxPriceTV, mFromTV, mToTV;
    private TextView countryTV, cityTV;
    private TextView offerTypeTV, propertyTypeTV, zoneTV, priceTV, describeTV;
    private ImageView mAttachedUrl;
    private ProgressBar mProgressBar;
    private RelativeLayout imageRL;
    private JSONObject jsonObject = new JSONObject();
    private LinearLayout countryLL, offerTypeLL, cityLL, propertyTypeLL, mAttachementLL;

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
    private LinearLayout mCommentsLL;
    private TextView mCommentsCountTV, mCommentsTV;
    private String mOfferId = "";
    private String itemId = "";

    public static MyOffersFragment newInstance(String from) {
        MyOffersFragment fragment = new MyOffersFragment();
        Bundle b = new Bundle();
        b.putString("from", from);
        fragment.setArguments(b);
        return fragment;
    }


    public static MyOffersFragment newInstance(String from, String itemId) {
        MyOffersFragment fragment = new MyOffersFragment();
        Bundle b = new Bundle();
        b.putString("from", from);
        b.putString("itemId", itemId);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFrom = getArguments().getString("from");
            itemId = getArguments().getString("itemId");
        }
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
    public void onResume() {
        super.onResume();
        if (mFrom.equalsIgnoreCase("sent"))
            ((MainActivity) mActivity).setHeaderVisibility(HeaderHandler.SENT_REQUEST);
        else if (mFrom.equalsIgnoreCase("received"))
            ((MainActivity) mActivity).setHeaderVisibility(HeaderHandler.RECEIVED_REQUEST);

    }

    @Override
    protected void initViews() {
        mSearchET = (EditText) view.findViewById(R.id.et_search);
        mOffersRV = (RecyclerView) view.findViewById(R.id.rv_list);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayout);
        mNoDataTV = (TextView) view.findViewById(R.id.tv_no_data);

        mCountrySPN = (Spinner) view.findViewById(R.id.spn_country);
        mCitySPN = (Spinner) view.findViewById(R.id.spn_city);
        filterLL = (LinearLayout) view.findViewById(R.id.ll_filter);
        countryRL = (RelativeLayout) view.findViewById(R.id.rl_country);
        cityRL = (RelativeLayout) view.findViewById(R.id.rl_city);

    }


    @Override
    protected void initVariables() {
        if (!Validator.isEmptyString(mAppPreferences.getLabels())) {
            try {
                jsonObject = new JSONObject(mAppPreferences.getLabels());
                if (jsonObject != null && jsonObject.length() != 0) {
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "No offer found")))
                        mNoDataTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "No offer found"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        mOfferList = new ArrayList<>();
        mProgressDialog = new DialogManager(mActivity);
        mSearchET.setHint(AppUtilsMethod.getValueFromKey(jsonObject, "Search"));
        swipeContainer.setOnRefreshListener(this);
        mOffersRV.setHasFixedSize(true);
        final LinearLayoutManager layout = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        mOffersRV.setLayoutManager(layout);
        mSearchET.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mSubmitOfferAdapter != null)
                    mSubmitOfferAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (!Validator.isEmptyString(mFrom)) {
            if (mFrom.equalsIgnoreCase("sent")) {
                getCountryCitySettingsAPI("Sent Offers");
                callOffersAPI(AppNetworkConstants.ACTION_SENT_OFFERS, mCityId);
            } else if (mFrom.equalsIgnoreCase("received")) {
                getCountryCitySettingsAPI("Received Offers");
                callOffersAPI(AppNetworkConstants.ACTION_RECEIVED_OFFERS, mCityId);
            }
        }


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
                    SpinnerListAdapter cityAdapter = new SpinnerListAdapter(mActivity, mCityNameList);
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
                    if (mFrom.equalsIgnoreCase("sent"))
                        callOffersAPI(AppNetworkConstants.ACTION_SENT_OFFERS, mCityId);
                    else if (mFrom.equalsIgnoreCase("received"))
                        callOffersAPI(AppNetworkConstants.ACTION_RECEIVED_OFFERS, mCityId);
                } else {
                    mCityId = "";
                    if (mCitySPN.isEnabled()) {
                        if (mFrom.equalsIgnoreCase("sent"))
                            callOffersAPI(AppNetworkConstants.ACTION_SENT_OFFERS, mCityId);
                        else if (mFrom.equalsIgnoreCase("received"))
                            callOffersAPI(AppNetworkConstants.ACTION_RECEIVED_OFFERS, mCityId);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (!Validator.isEmptyString(itemId)) {
            mOfferId = itemId;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showPropertyDetailDialog(mActivity, mOfferId);
                }
            }, 1000);
        }
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

    private void callOffersAPI(String action, String cityId) {
        if (Validator.isConnectedToInternet(mActivity.getApplicationContext())) {
            NetworkModel networkModel = new NetworkModel();

            if (Validator.isEmptyString(cityId))
                API_GET_OFFERS = AppNetworkConstants.BASE_URL + "user/offer.php?action=" + action;
            else
                API_GET_OFFERS = AppNetworkConstants.BASE_URL + "user/offer.php?action=" + action + "&country_id=" + mCountryId + "&city_id=" + cityId;
            NetworkService service = new NetworkService(API_GET_OFFERS, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(mActivity, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }

    @Override
    public void onRefresh() {
        if (!Validator.isEmptyString(mFrom)) {
            if (mFrom.equalsIgnoreCase("sent"))
                callOffersAPI(AppNetworkConstants.ACTION_SENT_OFFERS, mCityId);
            else if (mFrom.equalsIgnoreCase("received"))
                callOffersAPI(AppNetworkConstants.ACTION_RECEIVED_OFFERS, mCityId);
        }
    }

    @Override
    public void onNetworkCallInitiated(String service) {
        if (mProgressDialog != null && !swipeContainer.isRefreshing())
            mProgressDialog.show();

    }

    @Override
    public void onNetworkCallCompleted(String service, String response) {
        Log.e(TAG, " success " + response);
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        swipeContainer.setRefreshing(false);
        if (service.equalsIgnoreCase(API_GET_OFFERS)) {
            MyOffersResponse submitOfferParentResponse = MyOffersResponse.fromJson(response);
            if (submitOfferParentResponse != null && submitOfferParentResponse.data != null) {
                mOfferList.clear();
                if (submitOfferParentResponse.data.size() > 0) {
                    mOfferList = submitOfferParentResponse.data;
                    mNoDataTV.setVisibility(View.GONE);
                    mOffersRV.setVisibility(View.VISIBLE);
                    mSubmitOfferAdapter = new MyOffersAdapter(mActivity, mOfferList, jsonObject, this);
                    mOffersRV.setAdapter(mSubmitOfferAdapter);
                    mSubmitOfferAdapter.setOnItemClickListener(this);
                } else {
                    mNoDataTV.setVisibility(View.VISIBLE);
                    mOffersRV.setVisibility(View.GONE);
                }
            } else
                CustomDialogFragment.getInstance(mActivity, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);

        } else if (service.equals(API_VIEW_OFFER)) {
            if (mProgressBar != null)
                mProgressBar.setVisibility(View.GONE);
            ViewOffersResponse submitOfferParentResponse = ViewOffersResponse.fromJson(response);
            if (submitOfferParentResponse != null && submitOfferParentResponse.success && submitOfferParentResponse.data != null) {
                setPopupData(submitOfferParentResponse.data);
            } else {
                CustomDialogFragment.getInstance(mActivity, null, mActivity.getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
            }
        } else if (service.equalsIgnoreCase(API_REMOVE_REQUEST)) {
            BaseResponse baseResponse = BaseResponse.fromJson(response);
            if (baseResponse != null && baseResponse.success) {
                if (!Validator.isEmptyString(baseResponse.successMessage))
                    CustomDialogFragment.getInstance(mActivity, null, baseResponse.successMessage, null, AppConstants.OK, null, 1, null);
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


    private void getCountryCitySettingsAPI(String params) {
        if (Validator.isConnectedToInternet(mActivity.getApplicationContext())) {
            NetworkModel networkModel = new NetworkModel();
            API_COUNTRY_CITY_SETTING = AppNetworkConstants.BASE_URL + "user/manage.php?action=get-country-city-setting&screen_name=" + params;
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

        if (mProgressBar != null)
            mProgressBar.setVisibility(View.GONE);
        swipeContainer.setRefreshing(false);
        CustomDialogFragment.getInstance(mActivity, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);

    }

    @Override
    public void onItemClick(int position) {
        if (mOfferList != null && mOfferList.size() > position) {
            if (!Validator.isEmptyString(mOfferList.get(position).offerId)) {
                mOfferId = mOfferList.get(position).offerId;
                showPropertyDetailDialog(mActivity, mOfferList.get(position).offerId);
            }
        }
    }


    private void showPropertyDetailDialog(final BaseActivity activity, final String offerId) {
        dialog = new Dialog(activity, R.style.PickerAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_view_offer);
        ImageView closeIV = (ImageView) dialog.findViewById(R.id.iv_close);
        propertyIV = (ImageView) dialog.findViewById(R.id.iv_property);
        mProgressBar = (ProgressBar) dialog.findViewById(R.id.progress);
        RelativeLayout mainRL = (RelativeLayout) dialog.findViewById(R.id.rl_main);
        imageRL = (RelativeLayout) dialog.findViewById(R.id.rl_image);

        mCommentsLL = (LinearLayout) dialog.findViewById(R.id.ll_comments);
        mCommentsCountTV = (TextView) dialog.findViewById(R.id.tv_comment_count);
        mCommentsTV = (TextView) dialog.findViewById(R.id.tv_comment);
        mCommentsLL.setVisibility(View.GONE);
        imageRL.setVisibility(View.GONE);
        mainRL.setBackground(mActivity.getResources().getDrawable(R.drawable.property_pupup_bg));
        mainRL.setPadding(AppUtilsMethod.dpToPx(mActivity, 15), AppUtilsMethod.dpToPx(mActivity, 10), AppUtilsMethod.dpToPx(mActivity, 15), AppUtilsMethod.dpToPx(mActivity, 10));
        titleTV = (TextView) dialog.findViewById(R.id.tv_property_title);
        descriptionTV = (TextView) dialog.findViewById(R.id.tv_description);
        mFromTV = (TextView) dialog.findViewById(R.id.tv_from);
        mMinPriceTV = (TextView) dialog.findViewById(R.id.tv_min_price);
        mMaxPriceTV = (TextView) dialog.findViewById(R.id.tv_max_price);
        mToTV = (TextView) dialog.findViewById(R.id.tv_to);
        countryTV = (TextView) dialog.findViewById(R.id.tv_country);
        cityTV = (TextView) dialog.findViewById(R.id.tv_city);
        offerTypeTV = (TextView) dialog.findViewById(R.id.tv_offer_type);
        propertyTypeTV = (TextView) dialog.findViewById(R.id.tv_property_type);
        zoneTV = (TextView) dialog.findViewById(R.id.tv_zone);
        priceTV = (TextView) dialog.findViewById(R.id.tv_price);

        describeTV = (TextView) dialog.findViewById(R.id.tv_offer_description);


        mAttachementLL = (LinearLayout) dialog.findViewById(R.id.ll_attachment);
        mAttachedUrl = (ImageView) dialog.findViewById(R.id.iv_attachment);

        countryLL = (LinearLayout) dialog.findViewById(R.id.ll_country);
        cityLL = (LinearLayout) dialog.findViewById(R.id.ll_city);
        offerTypeLL = (LinearLayout) dialog.findViewById(R.id.ll_offer_type);

        propertyTypeLL = (LinearLayout) dialog.findViewById(R.id.ll_property_type);

        TextView country = (TextView) dialog.findViewById(R.id.country);
        TextView city = (TextView) dialog.findViewById(R.id.city);
        TextView offerType = (TextView) dialog.findViewById(R.id.offer_type);
        TextView propertyType = (TextView) dialog.findViewById(R.id.property_type);
        TextView zone = (TextView) dialog.findViewById(R.id.zone);
        TextView price = (TextView) dialog.findViewById(R.id.price);
        TextView offer = (TextView) dialog.findViewById(R.id.offer);

        if (jsonObject != null && jsonObject.length() != 0) {
            country.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Country"));
            city.setText(AppUtilsMethod.getValueFromKey(jsonObject, "City"));
            offerType.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Offer Type"));
            propertyType.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Property Type"));
            zone.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Zone"));
            price.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Price"));
            offer.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Offer"));
        }


        getViewOfferAPI(offerId);

        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (!activity.isFinishing()) {
            dialog.show();
        }


    }

    private void setPopupData(final ViewOffersResponse.Data submitOfferResponse) {
        if (!Validator.isEmptyString(submitOfferResponse.propertyImage)) {
            ImageLoader.getInstance().displayImage(submitOfferResponse.propertyImage, propertyIV);
            imageRL.setVisibility(View.VISIBLE);
        } else {
            imageRL.setVisibility(View.GONE);
        }
        if (!Validator.isEmptyString(submitOfferResponse.title)) {
            titleTV.setVisibility(View.VISIBLE);
            titleTV.setText(submitOfferResponse.title);
        }
        descriptionTV.setText(submitOfferResponse.description);
        mFromTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "From") + " ");
        mMinPriceTV.setText(submitOfferResponse.minPrice + " " + mActivity.getResources().getString(R.string.sr) + " ");
        mToTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "To") + " ");
        mMaxPriceTV.setText(submitOfferResponse.maxPrice + " " + mActivity.getResources().getString(R.string.sr));

        if ((Validator.isEmptyString(submitOfferResponse.maxPrice) && Validator.isEmptyString(submitOfferResponse.minPrice)) || (submitOfferResponse.maxPrice.equalsIgnoreCase("0") && submitOfferResponse.minPrice.equalsIgnoreCase("0"))) {
            if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Price not mentioned")))
                mFromTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Price not mentioned"));
            else
                mFromTV.setText(mActivity.getResources().getString(R.string.price_not));
            mMaxPriceTV.setVisibility(View.GONE);
            mMinPriceTV.setVisibility(View.GONE);
            mToTV.setVisibility(View.GONE);
        } else {
            mFromTV.setVisibility(View.VISIBLE);
            mMaxPriceTV.setVisibility(View.VISIBLE);
            mMinPriceTV.setVisibility(View.VISIBLE);
            mToTV.setVisibility(View.VISIBLE);
        }


        countryTV.setText(submitOfferResponse.countryName);
        cityTV.setText(submitOfferResponse.cityName);
        offerTypeTV.setText(submitOfferResponse.saleTypeName);
        propertyTypeTV.setText(submitOfferResponse.propertyTypeName);
        ArrayList<String> zones = new ArrayList<>();
        if (submitOfferResponse.zoneTypeName != null) {
            for (ViewOffersResponse.Data.ZoneType zoneType : submitOfferResponse.zoneTypeName) {
                zones.add(zoneType.zoneTypeName);
            }
        }
        String zone = zones.toString().replace("[", "").replace("]", "");
        zoneTV.setText(zone);
        priceTV.setText(submitOfferResponse.minPrice + mActivity.getResources().getString(R.string.sr));
        describeTV.setText(submitOfferResponse.offerDescription);

        String comment = submitOfferResponse.comments > 1 ? " comments" : " comment";

        mCommentsCountTV.setText(submitOfferResponse.comments + comment);

        if (!Validator.isEmptyString(mOfferId) && mOfferList != null) {
            String url = "";
            for (MyOffersResponse.Data data : mOfferList) {
                if (mOfferId.equalsIgnoreCase(data.offerId) && !Validator.isEmptyString(data.offer_attachment)) {
                    mAttachementLL.setVisibility(View.VISIBLE);
                    url = data.offer_attachment;
                    ImageLoader.getInstance().displayImage(url, mAttachedUrl, new DisplayImageOptions.Builder()
                            .showImageForEmptyUri(R.drawable.color_grey)
                            .showImageOnFail(R.drawable.color_grey)
                            .showStubImage(R.drawable.color_grey)
                            .cacheOnDisc(true)
                            .cacheInMemory(true)
                            .build());

                    break;
                }
            }
            final String finalUrl = url;
            mAttachedUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent fullViewIntent = new Intent(mActivity, FullImageActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("data", null);
                        bundle.putString("image_url", finalUrl);
                        fullViewIntent.putExtra("bundle", bundle);
                        mActivity.startActivity(fullViewIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        mCommentsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mActivity, CommentsActivity.class);
                intent.putExtra("offer_id", submitOfferResponse.offerId);
                mActivity.startActivity(intent);
            }
        });

        mCommentsCountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mActivity, CommentsActivity.class);
                intent.putExtra("offer_id", submitOfferResponse.offerId);
                mActivity.startActivity(intent);
            }
        });
        if (AppConstants.mPropertyFields != null && AppConstants.mPropertyFields.size() > 0) {
            for (PropertyFields fields : AppConstants.mPropertyFields) {
                if (fields != null) {
                    switch (fields.field) {
                        case "country":
                            if (fields.visible == 1) {
                                countryLL.setVisibility(View.VISIBLE);
                            } else {
                                countryLL.setVisibility(View.GONE);
                            }
                            break;
                    }
                }
            }
        }

    }


    private void getViewOfferAPI(String offerId) {
        if (Validator.isConnectedToInternet(mActivity.getApplicationContext())) {
            if (mProgressBar != null)
                mProgressBar.setVisibility(View.VISIBLE);
            NetworkModel networkModel = new NetworkModel();
            API_VIEW_OFFER = AppNetworkConstants.BASE_URL + "user/offer.php?action=view-offer&offer_id=" + offerId;
            NetworkService service = new NetworkService(API_VIEW_OFFER, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(mActivity, null, mActivity.getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }

    public void callRemoveOfferAPI(String offerId, String userId) {
        API_REMOVE_REQUEST = AppNetworkConstants.BASE_URL + "user/offer.php";
        NetworkService serviceCall = new NetworkService(API_REMOVE_REQUEST, AppConstants.METHOD_POST, this);
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart(ParserKeys.action.toString(), AppNetworkConstants.ACTION_REMOVE_OFFER)
                .addFormDataPart(ParserKeys.user_id.toString(), userId)
                .addFormDataPart(ParserKeys.offer_id.toString(), offerId)
                .build();
        serviceCall.setRequestBody(requestBody);
        serviceCall.call(new NetworkModel());
    }

    public void changeLabels() {
        if (mFrom.equalsIgnoreCase("sent"))
            ((MainActivity) mActivity).setHeaderVisibility(HeaderHandler.SENT_REQUEST);
        else if (mFrom.equalsIgnoreCase("received"))
            ((MainActivity) mActivity).setHeaderVisibility(HeaderHandler.RECEIVED_REQUEST);
    }
}

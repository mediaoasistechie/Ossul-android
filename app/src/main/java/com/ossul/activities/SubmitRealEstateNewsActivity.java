package com.ossul.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.appcompat.BuildConfig;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ossul.R;
import com.ossul.adapters.CustomArrayAdapter;
import com.ossul.appconstant.AppConstants;
import com.ossul.dialog.CustomDialogFragment;
import com.ossul.dialog.DialogManager;
import com.ossul.dialog.TakeImageDialog;
import com.ossul.interfaces.INetworkEvent;
import com.ossul.interfaces.ImagePickerCallback;
import com.ossul.network.NetworkService;
import com.ossul.network.constants.AppNetworkConstants;
import com.ossul.network.model.NetworkModel;
import com.ossul.network.model.response.BaseResponse;
import com.ossul.network.model.response.CountriesList;
import com.ossul.network.model.response.GetCityListResponse;
import com.ossul.network.model.response.GetCountryListResponse;
import com.ossul.network.model.response.RealEstateNewsList;
import com.ossul.network.parser.ParserKeys;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.Validator;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public class SubmitRealEstateNewsActivity extends BaseActivity implements INetworkEvent, ImagePickerCallback {
    private static final String TAG = SubmitRealEstateNewsActivity.class.getSimpleName();
    private DialogManager mProgressDialog;
    private JSONObject jsonObject = new JSONObject();
    private TextView mHeaderTitleTV;
    private Spinner mCountrySPN;
    private Spinner mCitySPN;
    private String API_COUNTRY_LIST = "";
    private String API_CITY_LIST = "";
    private ArrayList<GetCityListResponse.CityList> mCityList = new ArrayList<>();
    private ArrayList<String> mCityNameList = new ArrayList<>();
    private String mCityId = "";
    private ArrayList<CountriesList> mCountryList = new ArrayList<>();

    private TextView mSubmitTV;
    private ArrayList<String> mCountryNameList;
    private String mCountryId = "";

    private File mFile1;
    private File mFile2;
    private File mFile3;
    private File mFile4;
    private File mFile5;

    private ImageView image1, image2, image3, image4, image5;
    private EditText mTitleET;
    private TakeImageDialog mTakeImageDialog;
    private EditText mDescriptionET;
    private HorizontalScrollView horizontalScrollView;
    private LinearLayout mPhotosLL;
    private String API_ADD_NEWS = "";
    private String city, country;
    private String allcity = "All City", allcountry = "All Country";
    private RealEstateNewsList mRealEstateNews;
    private String API_EDIT_NEWS = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_news);
        if (getIntent() != null && getIntent().getBundleExtra("bundle") != null) {
            mRealEstateNews = getIntent().getBundleExtra("bundle").getParcelable("data");
        }

        initData();
    }

    @Override
    protected void initViews() {
        mHeaderTitleTV = (TextView) findViewById(R.id.tv_header_title);
        mCountrySPN = (Spinner) findViewById(R.id.spn_country);
        mCitySPN = (Spinner) findViewById(R.id.spn_city);

        mTitleET = (EditText) findViewById(R.id.et_title);
        mDescriptionET = (EditText) findViewById(R.id.et_description);


        findViewById(R.id.iv_back).setOnClickListener(this);

        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        mPhotosLL = (LinearLayout) findViewById(R.id.ll_photo);

        image1 = (ImageView) findViewById(R.id.iv_image1);
        image2 = (ImageView) findViewById(R.id.iv_image2);
        image3 = (ImageView) findViewById(R.id.iv_image3);
        image4 = (ImageView) findViewById(R.id.iv_image4);
        image5 = (ImageView) findViewById(R.id.iv_image5);

        findViewById(R.id.iv_back).setOnClickListener(this);
        mSubmitTV = (TextView) findViewById(R.id.tv_submit);
        mSubmitTV.setOnClickListener(this);
        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);
        image4.setOnClickListener(this);
        image5.setOnClickListener(this);
        changeLabels();
    }

    private void changeLabels() {
        if (!Validator.isEmptyString(mAppPreferences.getLabels())) {
            try {
                jsonObject = new JSONObject(mAppPreferences.getLabels());
                if (jsonObject != null && jsonObject.length() != 0) {
                    mTitleET.setHint(AppUtilsMethod.getValueFromKey(jsonObject, "Add Title"));
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "City")))
                        city = AppUtilsMethod.getValueFromKey(jsonObject, "City");
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Country")))
                        country = AppUtilsMethod.getValueFromKey(jsonObject, "Country");

                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "All City")))
                        allcity = AppUtilsMethod.getValueFromKey(jsonObject, "All City");
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "All Country")))
                        allcountry = AppUtilsMethod.getValueFromKey(jsonObject, "All Country");

                    mDescriptionET.setHint(AppUtilsMethod.getValueFromKey(jsonObject, "Description"));
                    mSubmitTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Submit"));
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Submit News")))
                        mHeaderTitleTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Submit News"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void initVariables() {
        mProgressDialog = new DialogManager(this);

        mCityList = new ArrayList<>();
        mCountryList = new ArrayList<>();
        mCityNameList = new ArrayList<>();
        mCountryNameList = new ArrayList<>();
        mTakeImageDialog = new TakeImageDialog(this, this);

        image1.setTag(0);
        image2.setTag(0);
        image3.setTag(0);
        image4.setTag(0);
        image5.setTag(0);
        mCountrySPN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mCountryNameList != null && mCountryList != null && position > 0) {
                    String countryName = mCountryNameList.get(position);
                    for (CountriesList list : mCountryList) {
                        if (!Validator.isEmptyString(countryName) && list != null && countryName.equalsIgnoreCase(list.countryName)) {
                            mCountryId = list.countryId;
                        }
                    }
                    if (!Validator.isEmptyString(mCountryId)) {
                        getCityListAPI(mCountryId);
                    }
                } else
                    mCountryId = "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mCitySPN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mCityNameList != null && mCityList != null && position > 0) {
                    String countryName = mCityNameList.get(position);
                    for (GetCityListResponse.CityList list : mCityList) {
                        if (!Validator.isEmptyString(countryName) && list != null && countryName.equalsIgnoreCase(list.cityName)) {
                            mCityId = list.cityId;
                        }
                    }
                } else
                    mCityId = "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getCountryListAPI();
        mCountryNameList.clear();
        mCountryNameList.add(allcountry);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, mCountryNameList);
        mCountrySPN.setAdapter(countryAdapter);
        mCountrySPN.setEnabled(false);

        mCityNameList.clear();
        mCityNameList.add(allcity);
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, mCityNameList);
        mCitySPN.setAdapter(cityAdapter);
        mCitySPN.setEnabled(false);
        if (mRealEstateNews != null)
            setData();
    }

    private void setData() {
        if (mRealEstateNews == null)
            return;
        mTitleET.setText(mRealEstateNews.title);
        mDescriptionET.setText(mRealEstateNews.description);
        ArrayList<String> images = new ArrayList<>();
        JsonArray jsonElements = mRealEstateNews.images;
        if (jsonElements != null) {
            for (int i = 0; i < jsonElements.size(); i++) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonElements.get(i) + "");
                    images.add(AppUtilsMethod.getValueFromKey(jsonObject, "storage_path"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < images.size(); i++) {
                switch (i) {
                    case 0:
                        image1.setVisibility(View.VISIBLE);
                        ImageLoader.getInstance().displayImage(images.get(i), image1);
                        break;
                    case 1:
                        image2.setVisibility(View.VISIBLE);
                        ImageLoader.getInstance().displayImage(images.get(i), image2);
                        break;
                    case 2:
                        image3.setVisibility(View.VISIBLE);
                        ImageLoader.getInstance().displayImage(images.get(i), image3);
                        break;

                    case 3:
                        image4.setVisibility(View.VISIBLE);
                        ImageLoader.getInstance().displayImage(images.get(i), image4);
                        break;

                    case 4:
                        image5.setVisibility(View.VISIBLE);
                        ImageLoader.getInstance().displayImage(images.get(i), image5);
                        break;
                }
            }
        } else {
            if (mRealEstateNews.storagePath != null) {
                image1.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(mRealEstateNews.storagePath, image1);

            }
        }
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
    public void onNetworkCallInitiated(String service) {
        if (mProgressDialog != null)
            mProgressDialog.show();
    }

    @Override
    public void onNetworkCallCompleted(String service, String response) {
        Log.e(TAG, " success " + response);
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        if (service.equalsIgnoreCase(API_CITY_LIST)) {
            GetCityListResponse getCityListResponse = GetCityListResponse.fromJson(response);
            if (getCityListResponse != null && getCityListResponse.data != null) {
                if (getCityListResponse.data.size() > 0) {
                    mCityList.clear();
                    mCityNameList.clear();
                    mCityNameList.add(allcity);
                    mCityList = getCityListResponse.data;
                    for (GetCityListResponse.CityList list : mCityList) {
                        mCityNameList.add(list.cityName);
                    }

                    CustomArrayAdapter adapter = new CustomArrayAdapter(this, mCityNameList);
                    mCitySPN.setAdapter(adapter);
                    mCitySPN.setEnabled(true);

                    if (mRealEstateNews != null) {
                        for (int i = 0; i < mCityNameList.size(); i++) {
                            if (mCityNameList.get(i).equalsIgnoreCase(mRealEstateNews.cityName)) {
                                mCitySPN.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }
        } else if (service.equalsIgnoreCase(API_COUNTRY_LIST)) {
            GetCountryListResponse getCountryListResponse = GetCountryListResponse.fromJson(response);
            if (getCountryListResponse != null && getCountryListResponse.data != null) {
                if (getCountryListResponse.data.size() > 0) {
                    mCountryList.clear();
                    mCountryNameList.clear();
                    mCountryNameList.add(allcountry);
                    mCountryList = getCountryListResponse.data;
                    for (CountriesList list : mCountryList) {
                        mCountryNameList.add(list.countryName);
                    }

                    mCountryList.add(0, null);
                    CustomArrayAdapter adapter = new CustomArrayAdapter(this, mCountryNameList);
                    mCountrySPN.setAdapter(adapter);
                    mCountrySPN.setEnabled(true);
                    if (mRealEstateNews != null) {
                        for (int i = 0; i < mCountryNameList.size(); i++) {
                            if (mCountryNameList.get(i).equalsIgnoreCase(mRealEstateNews.countryName)) {
                                mCountrySPN.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }
        } else if (service.equalsIgnoreCase(API_ADD_NEWS) || service.equalsIgnoreCase(API_EDIT_NEWS)) {
            BaseResponse baseResponse = BaseResponse.fromJson(response);
            if (baseResponse != null && baseResponse.success) {
                setResult(RESULT_OK);
                finish();
            } else if (baseResponse != null && baseResponse.error && !Validator.isEmptyString(baseResponse.errorMessage)) {
                CustomDialogFragment.getInstance(this, null, baseResponse.errorMessage, null, AppConstants.OK, null, 1, null);
            }
        }
    }


    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        if (BuildConfig.DEBUG)
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onImageClicked(File file, String filePath, String imageName) {
        if (!Validator.isEmptyString(imageName)) {
            if (file != null) {
                if (imageName.equalsIgnoreCase("profile-pic-2")) {
                    mFile2 = file;
                    image2.invalidate();
                    image2.setImageURI(Uri.fromFile(file));
                    image2.setTag(2);
                    image3.setVisibility(View.VISIBLE);
                } else if (imageName.equalsIgnoreCase("profile-pic-3")) {
                    mFile3 = file;
                    image3.invalidate();
                    image3.setImageURI(Uri.fromFile(file));
                    image3.setTag(3);
                    image4.setVisibility(View.VISIBLE);
                } else if (imageName.equalsIgnoreCase("profile-pic-4")) {
                    mFile4 = file;
                    image4.invalidate();
                    image4.setImageURI(Uri.fromFile(file));
                    image4.setTag(4);
                    image5.setVisibility(View.VISIBLE);
                } else if (imageName.equalsIgnoreCase("profile-pic-5")) {
                    mFile5 = file;
                    image5.invalidate();
                    image5.setImageURI(Uri.fromFile(file));
                    image5.setTag(5);
                } else if (file != null) {
                    mFile1 = file;
                    image1.invalidate();
                    image1.setImageURI(Uri.fromFile(file));
                    image1.setTag(1);
                    image2.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == AppConstants.REQ_CODE_GALLERY_IMAGE_PICK || requestCode == AppConstants.REQ_CODE_CAMERA_IMAGE_PICK) {
            mTakeImageDialog.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_submit:

                if (Validator.isEmptyString(mTitleET.getText().toString().trim())) {
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Please Add Title")))
                        mTitleET.setError(AppUtilsMethod.getValueFromKey(jsonObject, "Please Add Title"));
                    else
                        mTitleET.setError("Please Add Title");
                    mTitleET.requestFocus();
                    return;
                }

                if (Validator.isEmptyString(mCountryId)) {
                    String error = "Please select " + country;
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, error)))
                        error = AppUtilsMethod.getValueFromKey(jsonObject, error);
                    else if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Please select")))
                        error = AppUtilsMethod.getValueFromKey(jsonObject, "Please select") + " " + country;
                    else
                        error = getResources().getString(R.string.please_select) + " " + country;
                    CustomDialogFragment.getInstance(this, null, error, null, AppConstants.OK, null, 1, null);
                    return;
                }

                if (Validator.isEmptyString(mCityId)) {
                    String error = "Please select " + city;
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, error)))
                        error = AppUtilsMethod.getValueFromKey(jsonObject, error);
                    else if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Please select")))
                        error = AppUtilsMethod.getValueFromKey(jsonObject, "Please select") + " " + city;
                    else
                        error = getResources().getString(R.string.please_select) + " " + city;
                    CustomDialogFragment.getInstance(this, null, error, null, AppConstants.OK, null, 1, null);
                    return;
                }

                if ((mFile1 == null && mFile2 == null && mFile3 == null && mFile4 == null && mFile5 == null) && Validator.isEmptyString(mDescriptionET.getText().toString().trim())) {
                    String msg = "Please upload image or enter description field";
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Please upload image or enter description filed")))
                        msg = AppUtilsMethod.getValueFromKey(jsonObject, "Please upload image or enter description filed");
                    CustomDialogFragment.getInstance(this, null, msg, null, AppConstants.OK, null, 1, null);
                    return;
                }

                if (mRealEstateNews != null) {
                    callEditNewsAPI(mCountryId, mTitleET.getText().toString(), mDescriptionET.getText().toString(), mRealEstateNews.realEstateNewsId);
                } else
                    callAddNewsAPI(mCountryId, mCityId, mTitleET.getText().toString(), mDescriptionET.getText().toString());
                break;
            case R.id.iv_image1:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 12);
                        return;
                    }
                }
                if ((int) image1.getTag() == 1) {
                    mTakeImageDialog.getImagePickerDialog(this, "profile-pic-1", "Choose another").show();
                } else
                    mTakeImageDialog.getImagePickerDialog(this, "profile-pic-1").show();
                break;
            case R.id.iv_image2:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 12);
                        return;
                    }
                }
                if ((int) image2.getTag() == 2) {
                    mTakeImageDialog.getImagePickerDialog(this, "profile-pic-2", "Choose another").show();
                } else
                    mTakeImageDialog.getImagePickerDialog(this, "profile-pic-2").show();
                break;
            case R.id.iv_image3:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 12);
                        return;
                    }
                }
                if ((int) image3.getTag() == 3) {
                    mTakeImageDialog.getImagePickerDialog(this, "profile-pic-3", "Choose another").show();
                } else
                    mTakeImageDialog.getImagePickerDialog(this, "profile-pic-3").show();
                break;
            case R.id.iv_image4:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 12);
                        return;
                    }
                }
                if ((int) image4.getTag() == 4) {
                    mTakeImageDialog.getImagePickerDialog(this, "profile-pic-4", "Choose another").show();
                } else
                    mTakeImageDialog.getImagePickerDialog(this, "profile-pic-4").show();
                break;
            case R.id.iv_image5:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 12);
                        return;
                    }
                }
                if ((int) image5.getTag() == 5) {
                    mTakeImageDialog.getImagePickerDialog(this, "profile-pic-5", "Choose another").show();
                } else
                    mTakeImageDialog.getImagePickerDialog(this, "profile-pic-5").show();
                break;

        }

    }

    private void callAddNewsAPI(String countryId, String cityId, String title, String description) {
        API_ADD_NEWS = AppNetworkConstants.BASE_URL + "user/news.php";
        NetworkService serviceCall = new NetworkService(API_ADD_NEWS, AppConstants.METHOD_POST, SubmitRealEstateNewsActivity.this);
        MultipartBuilder multipartBuilder = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart(ParserKeys.action.toString(), AppNetworkConstants.ACTION_SUBMIT_REAL_ESTATE_NEWS)
                .addFormDataPart(ParserKeys.title.toString(), title)
                .addFormDataPart(ParserKeys.country_id.toString(), countryId)
                .addFormDataPart(ParserKeys.city_id.toString(), cityId);

        if (!Validator.isEmptyString(description))
            multipartBuilder.addFormDataPart(ParserKeys.description.toString(), description);
        else
            multipartBuilder.addFormDataPart(ParserKeys.description.toString(), "");

        if (!Validator.isEmptyString(countryId))
            multipartBuilder.addFormDataPart(ParserKeys.country_id.toString(), countryId);
        if (mFile1 != null)
            multipartBuilder.addFormDataPart(ParserKeys.images.toString() + "[0]", mFile1.getName(), RequestBody.create(MediaType.parse("image/jpeg"), mFile1));
        if (mFile2 != null)
            multipartBuilder.addFormDataPart(ParserKeys.images.toString() + "[1]", mFile2.getName(), RequestBody.create(MediaType.parse("image/jpeg"), mFile2));
        if (mFile3 != null)
            multipartBuilder.addFormDataPart(ParserKeys.images.toString() + "[2]", mFile3.getName(), RequestBody.create(MediaType.parse("image/jpeg"), mFile3));
        if (mFile4 != null)
            multipartBuilder.addFormDataPart(ParserKeys.images.toString() + "[3]", mFile4.getName(), RequestBody.create(MediaType.parse("image/jpeg"), mFile4));
        if (mFile5 != null)
            multipartBuilder.addFormDataPart(ParserKeys.images.toString() + "[4]", mFile5.getName(), RequestBody.create(MediaType.parse("image/jpeg"), mFile5));
        RequestBody requestBody = (RequestBody) multipartBuilder.build();
        serviceCall.setRequestBody(requestBody);
        serviceCall.call(new NetworkModel(), mFile1);
    }


    private void callEditNewsAPI(String countryId, String title, String description, String newsId) {
        API_EDIT_NEWS = AppNetworkConstants.BASE_URL + "user/news.php";
        NetworkService serviceCall = new NetworkService(API_EDIT_NEWS, AppConstants.METHOD_POST, this);
        MultipartBuilder multipartBuilder = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart(ParserKeys.action.toString(), AppNetworkConstants.ACTION_EDIT_REAL_ESTATE_NEWS)
                .addFormDataPart(ParserKeys.title.toString(), title)
                .addFormDataPart(ParserKeys.country_id.toString(), countryId)
                .addFormDataPart(ParserKeys.realestate_news_id.toString(), newsId);
        if (!Validator.isEmptyString(description))
            multipartBuilder.addFormDataPart(ParserKeys.description.toString(), description);
        else
            multipartBuilder.addFormDataPart(ParserKeys.description.toString(), "");
        if (mFile1 != null)
            multipartBuilder.addFormDataPart(ParserKeys.images.toString() + "[0]", mFile1.getName(), RequestBody.create(MediaType.parse("image/jpeg"), mFile1));
        if (mFile2 != null)
            multipartBuilder.addFormDataPart(ParserKeys.images.toString() + "[1]", mFile2.getName(), RequestBody.create(MediaType.parse("image/jpeg"), mFile2));
        if (mFile3 != null)
            multipartBuilder.addFormDataPart(ParserKeys.images.toString() + "[2]", mFile3.getName(), RequestBody.create(MediaType.parse("image/jpeg"), mFile3));
        if (mFile4 != null)
            multipartBuilder.addFormDataPart(ParserKeys.images.toString() + "[3]", mFile4.getName(), RequestBody.create(MediaType.parse("image/jpeg"), mFile4));
        if (mFile5 != null)
            multipartBuilder.addFormDataPart(ParserKeys.images.toString() + "[4]", mFile5.getName(), RequestBody.create(MediaType.parse("image/jpeg"), mFile5));
        RequestBody requestBody = (RequestBody) multipartBuilder.build();
        serviceCall.setRequestBody(requestBody);
        serviceCall.call(new NetworkModel(), mFile1);
    }

}


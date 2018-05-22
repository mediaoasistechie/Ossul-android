package com.ossul.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ossul.R;
import com.ossul.activities.BaseActivity;
import com.ossul.activities.MainActivity;
import com.ossul.activities.MemberProfileActivity;
import com.ossul.adapters.SpinnerListAdapter;
import com.ossul.adapters.SubmitOfferAdapter;
import com.ossul.appconstant.AppConstants;
import com.ossul.apppreferences.AppPreferences;
import com.ossul.dialog.CustomDialogFragment;
import com.ossul.dialog.DialogManager;
import com.ossul.interfaces.INetworkEvent;
import com.ossul.interfaces.ImagePickerCallback;
import com.ossul.listener.EndlessRecyclerOnScrollListener;
import com.ossul.listener.OnItemClickListener;
import com.ossul.network.NetworkService;
import com.ossul.network.constants.AppNetworkConstants;
import com.ossul.network.model.NetworkModel;
import com.ossul.network.model.response.BaseResponse;
import com.ossul.network.model.response.CountriesList;
import com.ossul.network.model.response.CountryCitySettingResponse;
import com.ossul.network.model.response.GetCityListResponse;
import com.ossul.network.model.response.GetCountryListResponse;
import com.ossul.network.model.response.PropertyFields;
import com.ossul.network.model.response.SubmitOfferParentResponse;
import com.ossul.network.model.response.SubmitOfferResponse;
import com.ossul.network.parser.ParserKeys;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.Validator;
import com.ossul.view.HeaderHandler;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * * Fragment for offers list
 */
public class SubmitOfferFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, INetworkEvent, OnItemClickListener, ImagePickerCallback {
    private static final String TAG = SubmitOfferFragment.class.getSimpleName();
    private final int PERMISSION_READ_EXTERNAL_STORAGE = 10;
    private SwipeRefreshLayout swipeContainer;
    private View view;
    private DialogManager mProgressDialog;
    private SubmitOfferAdapter mSubmitOfferAdapter;
    private EditText mSearchET;
    private RecyclerView mSubmitOfferRV;
    private String API_SUBMIT_OFFER = "";
    private ArrayList<SubmitOfferResponse> mOfferList;
    private TextView mNoDataTV;
    private Dialog dialog;
    private String API_MAKE_OFFER_API = "";
    private JSONObject jsonObject = new JSONObject();
    private TextView attachmentTV;
    private File mFile;


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

    private boolean showLoader = false;
    private AdapterView.OnItemSelectedListener onCountrySelectedListener = new AdapterView.OnItemSelectedListener() {
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
    };


    private AdapterView.OnItemSelectedListener onCitySelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (mCityList != null && mCityList.size() > position && mCityList.get(position) != null) {
                mCityId = mCityList.get(position).cityId;
                callSubmitOfferAPI(1, mCityId);
            } else {
                mCityId = "";
                if (mCitySPN.isEnabled())
                    callSubmitOfferAPI(1, mCityId);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private String mRequesterId = "";
    private EndlessRecyclerOnScrollListener recyclerOnScrollListener;
    private RelativeLayout mLoaderRL;
    private Uri mCapturedImageURI;

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static Bitmap decodeSampledBitmapFromResource(File file, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getPath(), options);
    }

    private static File saveOutput(Context context, String filePath) {
        File file = new File(filePath);
        int WIDTH = 180;
        int HEIGHT = 180;
        Bitmap croppedImage = adjustOrientation(filePath, decodeSampledBitmapFromResource(file, WIDTH, HEIGHT));
        OutputStream outputStream;
        try {
            File tempFile = new File(context.getCacheDir(), System.currentTimeMillis() + ".jpg");
            tempFile.createNewFile();
            outputStream = new FileOutputStream(tempFile);
            if (croppedImage != null && outputStream != null) {
                croppedImage.compress(Bitmap.CompressFormat.PNG, 10, outputStream);
                return tempFile;
            }

            if (croppedImage != null) {
                croppedImage.recycle();
                return file;
            }


        } catch (IOException ignored) {
            return null;
        }
        return null;
    }

    private static Bitmap adjustOrientation(String filepath, Bitmap imageToBeCropped) {
        Matrix matrix = new Matrix();
        matrix.postRotate(getFileRotation(filepath));
        imageToBeCropped = Bitmap.createBitmap(imageToBeCropped, 0, 0, imageToBeCropped.getWidth(), imageToBeCropped.getHeight(), matrix, true);
        return imageToBeCropped;
    }

    private static int getFileRotation(String filepath) {
        int rotate = 0;
        try {
            ExifInterface exif = new ExifInterface(filepath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

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

    // get sd card path of image
    private String getPath(Intent intent) {
        Cursor cursor = null;
        String fileImagePath = "";
        try {
            Uri selectedImage;
            if (intent == null) {
                selectedImage = mCapturedImageURI;
            } else {
                if (intent.getData() == null) {
                    selectedImage = mCapturedImageURI;
                } else {
                    selectedImage = intent.getData();
                }
            }
            String[] filePathColumn = {MediaStore.MediaColumns.DATA};
            cursor = mActivity.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                fileImagePath = cursor.getString(columnIndex);
                cursor.close();
            }
        } catch (Exception ignored) {

        } finally {
            if (cursor != null)
                cursor.close();
        }
        return fileImagePath;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.REQ_CODE_GALLERY_IMAGE_PICK || requestCode == AppConstants.REQ_CODE_CAMERA_IMAGE_PICK) {
                String filePath = getPath(data);
                String fileFormat = null;
                if (filePath != null) {
                    String encoded;
                    try {
                        encoded = URLEncoder.encode(filePath, "UTF-8").replace("+", "%20");
                    } catch (UnsupportedEncodingException e) {
                        encoded = filePath;
                    }
                    fileFormat = MimeTypeMap.getFileExtensionFromUrl(encoded);
                }
                if (filePath != null && fileFormat != null && !fileFormat.isEmpty()) {

                    File file = saveOutput(mActivity, filePath);
                    if (file != null) {
                        mFile = file;
                        attachmentTV.setText(file.getName());
                    }
                }
            }
        }
    }

    private void openGallery() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(intent, AppConstants.REQ_CODE_GALLERY_IMAGE_PICK);
        }
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.TITLE, mActivity.getPackageName());
        mCapturedImageURI = mActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
        startActivityForResult(cameraIntent, AppConstants.REQ_CODE_CAMERA_IMAGE_PICK);
    }

    private AlertDialog.Builder getImagePickerDialog(final Activity activity) {
        String album = "Album", camera = "Camera";
        if (!Validator.isEmptyString(AppPreferences.get().getLabels())) {
            try {
                JSONObject jsonObject = new JSONObject(AppPreferences.get().getLabels());
                if (jsonObject != null && jsonObject.length() != 0) {
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Camera")))
                        camera = (AppUtilsMethod.getValueFromKey(jsonObject, "Camera"));
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Album")))
                        album = (AppUtilsMethod.getValueFromKey(jsonObject, "Album"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        CharSequence options[] = new CharSequence[]{album, camera};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        openGallery();
                        break;
                    case 1:
                        openCamera();
                }
            }
        });
        return builder;
    }

    @Override
    protected void initViews() {
        mSearchET = (EditText) view.findViewById(R.id.et_search);
        mSubmitOfferRV = (RecyclerView) view.findViewById(R.id.rv_list);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayout);
        mNoDataTV = (TextView) view.findViewById(R.id.tv_no_data);

        mCountrySPN = (Spinner) view.findViewById(R.id.spn_country);
        mCitySPN = (Spinner) view.findViewById(R.id.spn_city);
        filterLL = (LinearLayout) view.findViewById(R.id.ll_filter);
        countryRL = (RelativeLayout) view.findViewById(R.id.rl_country);
        cityRL = (RelativeLayout) view.findViewById(R.id.rl_city);
        mLoaderRL = (RelativeLayout) view.findViewById(R.id.rl_loader);
        mLoaderRL.setVisibility(View.GONE);
    }


    @Override
    protected void initVariables() {
        showLoader = false;
        if (!Validator.isEmptyString(mAppPreferences.getLabels())) {
            try {
                jsonObject = new JSONObject(mAppPreferences.getLabels());
                if (jsonObject != null && jsonObject.length() != 0) {
                    mSearchET.setHint(AppUtilsMethod.getValueFromKey(jsonObject, "Search"));
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "No data found")))
                        mNoDataTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "No data found"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        getCountryCitySettingsAPI();
        mOfferList = new ArrayList<>();
        mProgressDialog = new DialogManager(mActivity);
        swipeContainer.setOnRefreshListener(this);
        mSubmitOfferRV.setHasFixedSize(true);
        final LinearLayoutManager layout = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        mSubmitOfferRV.setLayoutManager(layout);
        callSubmitOfferAPI(1, mCityId);


        recyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layout) {
            @Override
            public void onLoadMore(int current_page) {
                callSubmitOfferAPI(current_page, mCityId);
                if (current_page >= 2)
                    mLoaderRL.setVisibility(View.VISIBLE);
            }
        };
        mSubmitOfferRV.addOnScrollListener(recyclerOnScrollListener);
        recyclerOnScrollListener.resetPage();

        mSearchET.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Validator.isEmptyString(mSearchET.getText().toString()) && mSubmitOfferAdapter != null)
                    mSubmitOfferAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
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

    private void callSubmitOfferAPI(int page, String cityId) {
        if (Validator.isConnectedToInternet(mActivity.getApplicationContext())) {
            if (page == 1)
                swipeContainer.setRefreshing(true);
            NetworkModel networkModel = new NetworkModel();
            if (Validator.isEmptyString(cityId))
                API_SUBMIT_OFFER = AppNetworkConstants.BASE_URL + "user/offer.php?action=submit-offer&page=" + page;
            else
                API_SUBMIT_OFFER = AppNetworkConstants.BASE_URL + "user/offer.php?action=submit-offer&page=" + page + "&country_id=" + mCountryId + "&city_id=" + cityId;
            NetworkService service = new NetworkService(API_SUBMIT_OFFER, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(mActivity, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }


    @Override
    public void onRefresh() {
        callSubmitOfferAPI(1, mCityId);
        recyclerOnScrollListener.resetPage();
    }

    @Override
    public void onNetworkCallInitiated(String service) {
        if (mProgressDialog != null && !swipeContainer.isRefreshing() && (service.equalsIgnoreCase(API_MAKE_OFFER_API) || service.equalsIgnoreCase(API_SUBMIT_OFFER) || showLoader)) {
            if (!service.equalsIgnoreCase(API_SUBMIT_OFFER))
                mProgressDialog.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) mActivity).setHeaderVisibility(HeaderHandler.SUBMIT_OFFER);
        recyclerOnScrollListener.resetPage();
    }

    public void changeLabels() {
        ((MainActivity) mActivity).setHeaderVisibility(HeaderHandler.SUBMIT_OFFER);
    }

    @Override
    public void onNetworkCallCompleted(String service, final String response) {
        Log.e(TAG, " success " + response);
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        swipeContainer.setRefreshing(false);
        if (service.equalsIgnoreCase(API_SUBMIT_OFFER)) {
            mLoaderRL.setVisibility(View.GONE);
            SubmitOfferParentResponse submitOfferParentResponse = SubmitOfferParentResponse.fromJson(response);
            if (submitOfferParentResponse != null && submitOfferParentResponse.data != null) {
                if (mOfferList != null && API_SUBMIT_OFFER.contains("page=1"))
                    mOfferList.clear();
                if (submitOfferParentResponse.data.size() > 0) {
                    mOfferList.addAll(submitOfferParentResponse.data);
                    mNoDataTV.setVisibility(View.GONE);
                    mSubmitOfferRV.setVisibility(View.VISIBLE);
                    if (mSubmitOfferAdapter == null) {
                        mSubmitOfferAdapter = new SubmitOfferAdapter(mActivity, mOfferList, jsonObject);
                        mSubmitOfferRV.setAdapter(mSubmitOfferAdapter);
                    } else {
                        mSubmitOfferAdapter.notifyDataSetChanged();
                    }
                    mSubmitOfferAdapter.setOnItemClickListener(this);
                } else {
                    mNoDataTV.setVisibility(View.VISIBLE);
                    mSubmitOfferRV.setVisibility(View.GONE);
                }
            } else
                CustomDialogFragment.getInstance(mActivity, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
        } else if (service.equalsIgnoreCase(API_MAKE_OFFER_API)) {
            final BaseResponse baseResponse = BaseResponse.fromJson(response);
            if (baseResponse != null && baseResponse.success) {
                if (dialog != null)
                    dialog.dismiss();
                if (!Validator.isEmptyString(baseResponse.success_message))
                    CustomDialogFragment.getInstance(mActivity, null, baseResponse.success_message, null, AppConstants.OK + "-finish", null, 1, new CustomDialogFragment.OnDialogClickListener() {
                        @Override
                        public void onClickOk() {
                            Intent profileIntent = new Intent(mActivity, MemberProfileActivity.class);
                            profileIntent.putExtra("userId", mRequesterId);
                            startActivityForResult(profileIntent, 1110);
                            mRequesterId = "";
                        }

                        @Override
                        public void onClickCancel() {

                        }
                    });
                else {
                    CustomDialogFragment.getInstance(mActivity, null, mActivity.getResources().getString(R.string.your_property_succes_placed), null, AppConstants.OK, null, 1, null);
                }
            } else if (baseResponse != null && baseResponse.error && !Validator.isEmptyString(baseResponse.errorMessage)) {
                CustomDialogFragment.getInstance(mActivity, null, baseResponse.errorMessage, null, AppConstants.OK, null, 1, null);
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
                            if (mCitySPN != null && onCitySelectedListener != null)
                                mCitySPN.setOnItemSelectedListener(onCitySelectedListener);
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

                    mCountrySPN.setOnItemSelectedListener(null);
                    mCountrySPN.setEnabled(true);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mCountrySPN != null && onCountrySelectedListener != null)
                                mCountrySPN.setOnItemSelectedListener(onCountrySelectedListener);
                        }
                    }, 2000);
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
            API_COUNTRY_CITY_SETTING = AppNetworkConstants.BASE_URL + "user/manage.php?action=get-country-city-setting&screen_name=Submit Offers";
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
        mLoaderRL.setVisibility(View.GONE);
        if (service.equalsIgnoreCase(API_MAKE_OFFER_API))
            mRequesterId = "";
        CustomDialogFragment.getInstance(mActivity, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);

    }

    @Override
    public void onItemClick(int position) {
        if (mOfferList != null && mOfferList.size() > position) {
            showPropertyDetailDialog(mActivity, mOfferList.get(position));
        }
    }

    public void showPropertyDetailDialog(final BaseActivity activity, final SubmitOfferResponse submitOfferResponse) {
        dialog = new Dialog(activity, R.style.PickerAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_request_detail);
        ImageView closeIV = (ImageView) dialog.findViewById(R.id.iv_close);
        RelativeLayout mainRL = (RelativeLayout) dialog.findViewById(R.id.rl_main);
        mainRL.setBackground(mActivity.getResources().getDrawable(R.drawable.property_pupup_bg));
        mainRL.setPadding(AppUtilsMethod.dpToPx(mActivity, 15), AppUtilsMethod.dpToPx(mActivity, 10), AppUtilsMethod.dpToPx(mActivity, 15), AppUtilsMethod.dpToPx(mActivity, 10));
        TextView titleTV = (TextView) dialog.findViewById(R.id.tv_property_title);
        final TextView descriptionTV = (TextView) dialog.findViewById(R.id.tv_description);

        TextView mFromTV = (TextView) dialog.findViewById(R.id.tv_from);
        TextView mMinPriceTV = (TextView) dialog.findViewById(R.id.tv_min_price);
        TextView mMaxPriceTV = (TextView) dialog.findViewById(R.id.tv_max_price);
        TextView mToTV = (TextView) dialog.findViewById(R.id.tv_to);

        LinearLayout countryLL = (LinearLayout) dialog.findViewById(R.id.ll_country);
        LinearLayout cityLL = (LinearLayout) dialog.findViewById(R.id.ll_city);
        LinearLayout offerTypeLL = (LinearLayout) dialog.findViewById(R.id.ll_offer_type);

        LinearLayout propertyTypeLL = (LinearLayout) dialog.findViewById(R.id.ll_property_type);


        TextView countryTV = (TextView) dialog.findViewById(R.id.tv_country);
        TextView cityTV = (TextView) dialog.findViewById(R.id.tv_city);
        TextView offerTypeTV = (TextView) dialog.findViewById(R.id.tv_offer_type);
        TextView propertyTypeTV = (TextView) dialog.findViewById(R.id.tv_property_type);
        TextView zoneTV = (TextView) dialog.findViewById(R.id.tv_zone);
        TextView priceTV = (TextView) dialog.findViewById(R.id.tv_price);

        final EditText describeET = (EditText) dialog.findViewById(R.id.et_description);

        TextView submitTV = (TextView) dialog.findViewById(R.id.tv_submit_offer);
        Button addAttachmentBTN = (Button) dialog.findViewById(R.id.btn_add_attachment);
        attachmentTV = (TextView) dialog.findViewById(R.id.tv_attachment);
        dialog.findViewById(R.id.ll_attachment).setVisibility(View.VISIBLE);


        if (submitOfferResponse == null)
            return;


        TextView country = (TextView) dialog.findViewById(R.id.country);
        TextView city = (TextView) dialog.findViewById(R.id.city);
        TextView offerType = (TextView) dialog.findViewById(R.id.offer_type);
        TextView propertyType = (TextView) dialog.findViewById(R.id.property_type);
        TextView zoneT = (TextView) dialog.findViewById(R.id.zone);
        TextView price = (TextView) dialog.findViewById(R.id.price);

        if (jsonObject != null && jsonObject.length() != 0) {
            country.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Country"));
            city.setText(AppUtilsMethod.getValueFromKey(jsonObject, "City"));
            offerType.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Offer Type"));
            propertyType.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Property Type"));
            zoneT.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Zone"));
            price.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Price"));
            describeET.setHint(AppUtilsMethod.getValueFromKey(jsonObject, "Describe your offer here..."));
            submitTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Submit Offer"));
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
        for (SubmitOfferResponse.ZoneType zoneType : submitOfferResponse.zoneTypeName) {
            zones.add(zoneType.zoneTypeName);
        }
        String zone = zones.toString().replace("[", "").replace("]", "");
        zoneTV.setText(zone);
        priceTV.setText(submitOfferResponse.minPrice + mActivity.getResources().getString(R.string.sr));

        if (AppConstants.mPropertyFields != null && AppConstants.mPropertyFields.size() > 0) {
            for (PropertyFields fields : AppConstants.mPropertyFields) {
                if (fields != null && fields.field.equalsIgnoreCase("country")) {
                    if (fields.visible == 1) {
                        countryLL.setVisibility(View.VISIBLE);
                    } else {
                        countryLL.setVisibility(View.GONE);
                    }
                }
            }
        }

        submitTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Validator.isEmptyString(describeET.getText().toString().trim())) {
                    callMakeOfferAPI(submitOfferResponse.requestId, describeET.getText().toString().trim(), submitOfferResponse.ownerId);
                } else {
                    CustomDialogFragment.getInstance(mActivity, null, AppUtilsMethod.getValueFromKey(jsonObject, "Describe what you need"), null, AppConstants.OK, null, 1, null);
                }
            }
        });
        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        addAttachmentBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (mActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE);
                        return;
                    }
                }

                getImagePickerDialog(mActivity).show();
            }
        });
        if (!activity.isFinishing()) {
            dialog.show();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ_EXTERNAL_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getImagePickerDialog(mActivity).show();
                }
                break;
            }
        }
    }

    private void callMakeOfferAPI(String requestId, String description, String ownerId) {
        mRequesterId = ownerId;
        API_MAKE_OFFER_API = AppNetworkConstants.BASE_URL + "user/offer.php";
        NetworkService serviceCall = new NetworkService(API_MAKE_OFFER_API, AppConstants.METHOD_POST, this);
        MultipartBuilder requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart(ParserKeys.action.toString(), AppNetworkConstants.ACTION_MAKE_OFFER)
                .addFormDataPart(ParserKeys.request_id.toString(), requestId)
                .addFormDataPart(ParserKeys.description.toString(), description);
        if (mFile != null)
            requestBody.addFormDataPart(ParserKeys.user_image.toString(), mFile.getName(), RequestBody.create(com.squareup.okhttp.MediaType.parse("image/jpeg"), mFile));
        RequestBody requestBody1 = requestBody.build();


        serviceCall.setRequestBody(requestBody1);
        serviceCall.call(new NetworkModel());
    }

    @Override
    public void onImageClicked(File file, String filePath, String imageName) {
        if (file != null) {
            this.mFile = file;
            attachmentTV.setText(filePath);
        }
    }
}


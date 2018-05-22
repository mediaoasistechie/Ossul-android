package com.ossul.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.DiscCacheUtil;
import com.nostra13.universalimageloader.core.assist.MemoryCacheUtil;
import com.ossul.R;
import com.ossul.appconstant.AppConstants;
import com.ossul.appconstant.ErrorConstant;
import com.ossul.apppreferences.AppPreferences;
import com.ossul.dialog.CustomDialogFragment;
import com.ossul.dialog.DialogManager;
import com.ossul.dialog.TakeImageDialog;
import com.ossul.interfaces.INetworkEvent;
import com.ossul.interfaces.IValidationResult;
import com.ossul.interfaces.ImagePickerCallback;
import com.ossul.network.NetworkService;
import com.ossul.network.constants.AppNetworkConstants;
import com.ossul.network.model.NetworkModel;
import com.ossul.network.model.response.GetPicResponse;
import com.ossul.network.model.response.GetProfileResponse;
import com.ossul.network.model.response.UpdateProfileResponse;
import com.ossul.network.parser.ParserKeys;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.AppValidationChecker;
import com.ossul.utility.Validator;
import com.ossul.view.CircularImageView;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class ProfileActivity extends BaseActivity implements IValidationResult, INetworkEvent, ImagePickerCallback {
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 12;
    private EditText mPasswordET, mRepeatPasswordET;
    private DialogManager mProgressDialog;
    private String API_GET_PROFILE = "";
    private CircularImageView mUserImageCIV;
    private TextView mUserNameTV;
    private TextView mLocationTV;
    private TextView mFollowingTV;
    private TextView mOfferTV;
    private TextView mRequestTV, mEditProfileTV;
    private EditText mNameET, mEmailET, mAreaCodeET, mMobileET, mAddressET;
    private TextView mHeaderTitleTV;
    private LinearLayout mMiddleLL;
    private ImageView mUploadProfileIV, mUploadWallIV;
    private String API_UPDATE_PROFILE = "";
    private String API_GET_PROFILE_PIC = "";
    private String API_GET_WALL_PIC = "";
    private String API_SET_PROFILE_PIC = "";
    private String API_SET_WALL_IMAGE = "";
    private TakeImageDialog mTakeImageDialog;
    private ImageView mWallIV;
    private String editMyProfile, ediProfile,
            myProfile, following, offers, requests;
    private JSONObject jsonObject = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initData();
    }

    @Override
    protected void initViews() {
        mHeaderTitleTV = (TextView) findViewById(R.id.tv_header_title);
        mUserImageCIV = (CircularImageView) findViewById(R.id.civ_user_image);
        mWallIV = (ImageView) findViewById(R.id.iv_wall_pic);
        mUserNameTV = (TextView) findViewById(R.id.tv_user_name);
        mLocationTV = (TextView) findViewById(R.id.tv_location);
        mMiddleLL = (LinearLayout) findViewById(R.id.ll_middle);
        mFollowingTV = (TextView) findViewById(R.id.tv_following);
        mOfferTV = (TextView) findViewById(R.id.tv_offers);
        mRequestTV = (TextView) findViewById(R.id.tv_requests);
        mNameET = (EditText) findViewById(R.id.et_name);
        mEmailET = (EditText) findViewById(R.id.et_email);
        mAreaCodeET = (EditText) findViewById(R.id.et_area_code);
        mMobileET = (EditText) findViewById(R.id.et_mobile);
        mAddressET = (EditText) findViewById(R.id.et_address);
        mEditProfileTV = (TextView) findViewById(R.id.tv_edit_profile);
        mUploadProfileIV = (ImageView) findViewById(R.id.iv_upload_profile_pic);
        mUploadWallIV = (ImageView) findViewById(R.id.iv_upload_wall_pic);

        mUploadProfileIV.setOnClickListener(this);
        mUploadWallIV.setOnClickListener(this);
        mEditProfileTV.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);

    }

    @Override
    protected void initVariables() {
        updateLabels();

        mProgressDialog = new DialogManager(this);
        mTakeImageDialog = new TakeImageDialog(this, this);
        getProfileAPI();
        setProfilePic();
        setWallPic();
        mEditProfileTV.setTag("edit");
        mEditProfileTV.setText(ediProfile);
        mEditProfileTV.setBackgroundColor(getResources().getColor(R.color.edit_btn_color));

        mFollowingTV.setText(following);
        mOfferTV.setText(offers);
        mRequestTV.setText(requests);
    }

    private void updateLabels() {
        if (!Validator.isEmptyString(mAppPreferences.getLabels())) {
            try {
                jsonObject = new JSONObject(mAppPreferences.getLabels());
                if (jsonObject != null && jsonObject.length() != 0) {
                    myProfile = AppUtilsMethod.getValueFromKey(jsonObject, "My Profile");
                    editMyProfile = AppUtilsMethod.getValueFromKey(jsonObject, "Edit My Profile");
                    ediProfile = AppUtilsMethod.getValueFromKey(jsonObject, "Edit Profile");
                    following = AppUtilsMethod.getValueFromKey(jsonObject, "Following");
                    offers = AppUtilsMethod.getValueFromKey(jsonObject, "Offers");
                    requests = AppUtilsMethod.getValueFromKey(jsonObject, "Requests");

                    offers = AppUtilsMethod.getValueFromKey(jsonObject, "Offers");
                    requests = AppUtilsMethod.getValueFromKey(jsonObject, "Requests");
//                    requests = AppUtilsMethod.getValueFromKey(jsonObject, "Address");


                    mUserNameTV.setHint(AppUtilsMethod.getValueFromKey(jsonObject, "Username"));
                    mEmailET.setHint(AppUtilsMethod.getValueFromKey(jsonObject, "Password"));
                    mMobileET.setHint(AppUtilsMethod.getValueFromKey(jsonObject, "Mobile Number"));
                    mAddressET.setHint(AppUtilsMethod.getValueFromKey(jsonObject, "Address"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setProfilePic() {
        if (!Validator.isEmptyString(mAppPreferences.getUserAvtar())) {
            ImageLoader.getInstance().displayImage(mAppPreferences.getUserAvtar(), mUserImageCIV, new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.mipmap.camera_placeholder)
                    .showImageOnFail(R.mipmap.camera_placeholder)
                    .showStubImage(R.mipmap.camera_placeholder)
                    .cacheOnDisc(true)
                    .cacheInMemory(true)
                    .build());
        } else {
            getProfilePicAPI();
        }
    }

    private void setWallPic() {
        if (!Validator.isEmptyString(mAppPreferences.getProfileWallPic())) {
            ImageLoader.getInstance().displayImage(mAppPreferences.getProfileWallPic(), mWallIV, new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.color_default)
                    .showImageOnFail(R.drawable.color_default)
                    .showStubImage(R.drawable.color_default)
                    .cacheOnDisc(true)
                    .cacheInMemory(true)
                    .build());
        } else {
            getWallPicAPI();
        }
    }

    private void getProfileAPI() {
        if (Validator.isConnectedToInternet(getApplicationContext())) {
            API_GET_PROFILE = AppNetworkConstants.BASE_URL + "user/manage.php?action=get-profile";
            NetworkService service = new NetworkService(API_GET_PROFILE, AppConstants.METHOD_GET, this);
            service.call(new NetworkModel());
        } else {
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null,AppConstants.OK, null, 1, null);
        }
    }

    private void getProfilePicAPI() {
        if (Validator.isConnectedToInternet(getApplicationContext())) {
            NetworkModel networkModel = new NetworkModel();
            API_GET_PROFILE_PIC = AppNetworkConstants.BASE_URL + "user/manage.php?action=get-profile-picture";
            NetworkService service = new NetworkService(API_GET_PROFILE_PIC, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }

    private void getWallPicAPI() {
        if (Validator.isConnectedToInternet(getApplicationContext())) {
            NetworkModel networkModel = new NetworkModel();
            API_GET_WALL_PIC = AppNetworkConstants.BASE_URL + "user/manage.php?action=get-wall-picture";
            NetworkService service = new NetworkService(API_GET_WALL_PIC, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }


    private void callPictureUploadApi(File file) {
        if (Validator.isConnectedToInternet(this)) {
            API_SET_PROFILE_PIC = AppNetworkConstants.BASE_URL + "user/manage.php,action=" + AppNetworkConstants.ACTION_SET_PROFILE_PIC;
            NetworkService serviceCall = new NetworkService(API_SET_PROFILE_PIC, AppConstants.METHOD_POST, this);
            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart(ParserKeys.action.toString(), AppNetworkConstants.ACTION_SET_PROFILE_PIC)
                    .addFormDataPart(ParserKeys.user_image.toString(), file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file))
                    .build();
            serviceCall.setRequestBody(requestBody);
            serviceCall.call(new NetworkModel(), file);
        } else {
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }

    }

    private void callWallPicUploadApi(File file) {
        if (Validator.isConnectedToInternet(this)) {
            API_SET_WALL_IMAGE = AppNetworkConstants.BASE_URL + "user/manage.php,action=" + AppNetworkConstants.ACTION_SET_WALL_PIC;
            NetworkService serviceCall = new NetworkService(API_SET_WALL_IMAGE, AppConstants.METHOD_POST, this);
            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart(ParserKeys.action.toString(), AppNetworkConstants.ACTION_SET_WALL_PIC)
                    .addFormDataPart(ParserKeys.user_image.toString(), file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file))
                    .build();
            serviceCall.setRequestBody(requestBody);
            serviceCall.call(new NetworkModel(), file);
        } else {
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_edit_profile:
                if (mEditProfileTV.getTag().equals("edit")) {
                    mEditProfileTV.setTag("save");
                    setEditable(true);
                } else {
                    if (Validator.isConnectedToInternet(getApplicationContext())) {
                        AppValidationChecker.validateUpdateProfileApi(mNameET.getText().toString().trim(), mEmailET.getText().toString().trim(), mMobileET.getText().toString().trim(), mAddressET.getText().toString().trim(),mAreaCodeET.getText().toString().trim(), this);
                    } else {
                        CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
                    }
                }

                break;

            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.iv_upload_profile_pic:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE);
                        return;
                    }
                }
                mTakeImageDialog.getImagePickerDialog(this, "profile-pic").show();
                break;

            case R.id.iv_upload_wall_pic:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE);
                        return;
                    }
                }
                mTakeImageDialog.getImagePickerDialog(this, "wall-pic").show();
                break;

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ_EXTERNAL_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                break;
            }
        }
    }

    @Override
    public void onValidationError(int errorType, int errorResId) {
        switch (errorType) {
            case ErrorConstant.ERROR_TYPE_USER_NAME_EMPTY:
                mNameET.requestFocus();
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Please Enter your Name")))
                    mNameET.setError(AppUtilsMethod.getValueFromKey(jsonObject, "Please Enter your Name"));
                else
                    mNameET.setError(getString(errorResId));
                break;
            case ErrorConstant.ERROR_TYPE_EMAIL_EMPTY:
                mEmailET.requestFocus();
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Please enter Email")))
                    mEmailET.setError(AppUtilsMethod.getValueFromKey(jsonObject, "Please enter Email"));
                else
                    mEmailET.setError(getString(errorResId));
                break;
            case ErrorConstant.ERROR_TYPE_EMAIL_INVALID:
                mEmailET.requestFocus();
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Please enter valid email id")))
                    mEmailET.setError(AppUtilsMethod.getValueFromKey(jsonObject, "Please enter valid email id"));
                else
                    mEmailET.setError(getString(errorResId));
                break;
            case ErrorConstant.ERROR_TYPE_MOBILE_EMPTY:
                mMobileET.requestFocus();
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Please Enter Mobile Number")))
                    mMobileET.setError(AppUtilsMethod.getValueFromKey(jsonObject, "Please Enter Mobile Number"));
                else
                    mMobileET.setError(getString(errorResId));
                break;

            case ErrorConstant.ERROR_TYPE_COUNTRY_CODE:
                mAreaCodeET.requestFocus();
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Please Enter Country Code")))
                    mAreaCodeET.setError(AppUtilsMethod.getValueFromKey(jsonObject, "Please Enter Country Code"));
                else
                    mAreaCodeET.setError(getString(errorResId));
                break;
            case ErrorConstant.ERROR_TYPE_ADDRESS_EMPTY:
                mAddressET.requestFocus();
                mAddressET.setError(getString(errorResId));
                break;

        }
    }

    @Override
    public void onValidationSuccess() {
        API_UPDATE_PROFILE = AppNetworkConstants.BASE_URL + "user/manage.php";
        NetworkService serviceCall = new NetworkService(API_UPDATE_PROFILE, AppConstants.METHOD_POST, ProfileActivity.this);
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart(ParserKeys.action.toString(), AppNetworkConstants.ACTION_UPDATE_PROFILE)
                .addFormDataPart(ParserKeys.email.toString(), mEmailET.getText().toString().trim())
                .addFormDataPart(ParserKeys.displayname.toString(), mNameET.getText().toString().trim())
                .addFormDataPart(ParserKeys.country_code.toString(),"+"+  mAreaCodeET.getText().toString().trim())
                .addFormDataPart(ParserKeys.phone_no.toString(), mMobileET.getText().toString().trim())
                .addFormDataPart(ParserKeys.address.toString(), mAddressET.getText().toString().trim())
                .build();
        serviceCall.setRequestBody(requestBody);
        serviceCall.call(new NetworkModel());

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
        if (service.equalsIgnoreCase(API_GET_PROFILE)) {
            GetProfileResponse getProfileResponse = GetProfileResponse.fromJson(response);
            if (getProfileResponse != null && getProfileResponse.success && getProfileResponse.statusCode == 200 && getProfileResponse.userData != null) {
                String s = getProfileResponse.toJson();
                if (!TextUtils.isEmpty(s)) {
                    mAppPreferences.setUserDetailPref(s);
                    setData(getProfileResponse);
                }

            } else if (getProfileResponse != null && getProfileResponse.error && !Validator.isEmptyString(getProfileResponse.errorMessage)) {
                CustomDialogFragment.getInstance(this, null, getProfileResponse.errorMessage, null, AppConstants.OK, null, 1, null);
            } else {
                CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
            }
        } else if (service.equalsIgnoreCase(API_UPDATE_PROFILE)) {
            UpdateProfileResponse baseResponse = UpdateProfileResponse.fromJson(response);
            if (baseResponse != null && baseResponse.success && !Validator.isEmptyString(baseResponse.successMessage)) {

                CustomDialogFragment.getInstance(this, null, baseResponse.successMessage, null, AppConstants.OK, null, 1, new CustomDialogFragment.OnDialogClickListener() {
                    @Override
                    public void onClickOk() {
                        setEditable(false);
                    }

                    @Override
                    public void onClickCancel() {

                    }
                });
            } else if (baseResponse != null && baseResponse.error && !Validator.isEmptyString(baseResponse.errorMessage)) {
                CustomDialogFragment.getInstance(this, null, baseResponse.errorMessage, null, AppConstants.OK, null, 1, null);
            } else {
                CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
            }
        } else if (service.equalsIgnoreCase(API_SET_PROFILE_PIC) || service.equalsIgnoreCase(API_GET_PROFILE_PIC)) {
            GetPicResponse getProfilePicResponse = GetPicResponse.fromJson(response);
            if (getProfilePicResponse != null && getProfilePicResponse.success && !Validator.isEmptyString(getProfilePicResponse.data.imagePath)) {
                mAppPreferences.setUserAvtar(getProfilePicResponse.data.imagePath);
                DiscCacheUtil.removeFromCache(getProfilePicResponse.data.imagePath, ImageLoader.getInstance().getDiscCache());
                MemoryCacheUtil.removeFromCache(getProfilePicResponse.data.imagePath, ImageLoader.getInstance().getMemoryCache());
                setProfilePic();
            } else if (getProfilePicResponse != null && getProfilePicResponse.error && !Validator.isEmptyString(getProfilePicResponse.errorMessage)) {
                CustomDialogFragment.getInstance(this, null, getProfilePicResponse.errorMessage, null, AppConstants.OK, null, 1, null);
            } else {
                CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
            }
        } else if (service.equalsIgnoreCase(API_SET_WALL_IMAGE) || service.equalsIgnoreCase(API_GET_WALL_PIC)) {
            GetPicResponse getProfilePicResponse = GetPicResponse.fromJson(response);
            if (getProfilePicResponse != null && getProfilePicResponse.success && !Validator.isEmptyString(getProfilePicResponse.data.imagePath)) {
                mAppPreferences.setProfileWallPic(getProfilePicResponse.data.imagePath);
                MemoryCacheUtil.removeFromCache(getProfilePicResponse.data.imagePath, ImageLoader.getInstance().getMemoryCache());
                DiscCacheUtil.removeFromCache(getProfilePicResponse.data.imagePath, ImageLoader.getInstance().getDiscCache());
                setWallPic();
            } else if (getProfilePicResponse != null && getProfilePicResponse.error && !Validator.isEmptyString(getProfilePicResponse.errorMessage)) {
                CustomDialogFragment.getInstance(this, null, getProfilePicResponse.errorMessage, null, AppConstants.OK, null, 1, null);
            } else {
                getWallPicAPI();
                CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
            }
        }

    }

    private void setData(GetProfileResponse getProfileResponse) {
        if (getProfileResponse != null && getProfileResponse.userData != null) {
            mUserNameTV.setText(getProfileResponse.userData.displayName);
            mNameET.setText(getProfileResponse.userData.displayName);
            mEmailET.setText(getProfileResponse.userData.email);
            mAreaCodeET.setText(getProfileResponse.userData.areaCode.replace("+",""));
            mMobileET.setText(getProfileResponse.userData.phone);
            mAddressET.setText(getProfileResponse.userData.address);


            mFollowingTV.setText(following + "\n" + getProfileResponse.userData.userFollowCount);
            mOfferTV.setText(offers + "\n" + getProfileResponse.userData.userOfferCount);
            mRequestTV.setText(requests + "\n" + getProfileResponse.userData.userRequestCount);

            AppUtilsMethod locationAddress = new AppUtilsMethod();
            if (!Validator.isEmptyString(AppPreferences.get().getLat()) && !Validator.isEmptyString(AppPreferences.get().getLongt()))
                locationAddress.getLocalityFromLocation(Double.parseDouble(AppPreferences.get().getLat()), Double.parseDouble(AppPreferences.get().getLongt()), getApplicationContext(), new GeocoderHandler());
        }
        setEditable(false);
    }

    private void setEditable(boolean isEditable) {
        if (isEditable)
            mNameET.setSelection(mNameET.getText().toString().length());
        mNameET.setEnabled(isEditable);
        mEmailET.setEnabled(false);
        mAreaCodeET.setEnabled(isEditable);
        mMobileET.setEnabled(isEditable);
        mAddressET.setEnabled(isEditable);

        if (isEditable) {
            mNameET.requestFocus();
            mNameET.setFocusableInTouchMode(isEditable);
            mEditProfileTV.setTag("save");
            mEditProfileTV.setText("");
            mHeaderTitleTV.setText(editMyProfile);
            mEditProfileTV.setBackground(getResources().getDrawable(R.drawable.save_btn_sgn));
            mLocationTV.setVisibility(View.GONE);
            mMiddleLL.setVisibility(View.GONE);
            mUploadProfileIV.setVisibility(View.VISIBLE);
            mUploadWallIV.setVisibility(View.VISIBLE);

        } else {
            mEditProfileTV.setTag("edit");
            mEditProfileTV.setText(ediProfile);
            mEditProfileTV.setBackgroundColor(getResources().getColor(R.color.edit_btn_color));
            mHeaderTitleTV.setText(myProfile);
            mLocationTV.setVisibility(View.VISIBLE);
            mMiddleLL.setVisibility(View.VISIBLE);
            mUploadProfileIV.setVisibility(View.GONE);
            mUploadWallIV.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == AppConstants.REQ_CODE_GALLERY_IMAGE_PICK || requestCode == AppConstants.REQ_CODE_CAMERA_IMAGE_PICK) {
            mTakeImageDialog.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onBackPressed() {
        if (mEditProfileTV.getTag().equals("save")) {
            getProfileAPI();
        } else
            super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onImageClicked(File file, String filePath, String imageName) {
        if (!Validator.isEmptyString(imageName)) {
            if (imageName.equalsIgnoreCase("profile-pic")) {
                if (file != null) {
                    callPictureUploadApi(file);
                    mUserImageCIV.setImageURI(Uri.fromFile(file));
                }

            } else if (imageName.equalsIgnoreCase("wall-pic")) {
                if (file != null) {
                    callWallPicUploadApi(file);
                    mWallIV.setImageURI(Uri.fromFile(file));
                }
            }
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            Log.i("address", locationAddress);
            mLocationTV.setText(locationAddress.toString());
        }
    }

}

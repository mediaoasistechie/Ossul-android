package com.ossul.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ossul.R;
import com.ossul.appconstant.AppConstants;
import com.ossul.dialog.CustomDialogFragment;
import com.ossul.dialog.DialogManager;
import com.ossul.interfaces.INetworkEvent;
import com.ossul.network.NetworkService;
import com.ossul.network.constants.AppNetworkConstants;
import com.ossul.network.model.NetworkModel;
import com.ossul.network.model.response.GetPicResponse;
import com.ossul.network.model.response.GetProfileResponse;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.Validator;
import com.ossul.view.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;


public class MemberProfileActivity extends BaseActivity implements INetworkEvent {
    private static final String TAG = MemberProfileActivity.class.getSimpleName();
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
    private String API_GET_WALL_PIC = "";
    private ImageView mWallIV;
    private String editMyProfile, ediProfile,
            myProfile = "Profile", following, offers, requests;
    private JSONObject jsonObject = new JSONObject();
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (getIntent() != null && getIntent().getStringExtra("userId") != null) {
            mUserId = getIntent().getStringExtra("userId");
        }
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
        getProfileAPI(mUserId);
//        getProfilePicAPI(mUserId);
//        getWallPicAPI(mUserId);
        mEditProfileTV.setText(ediProfile);
        mEditProfileTV.setTag("save");
        setEditable(true);
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
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Profile")))
                        myProfile = AppUtilsMethod.getValueFromKey(jsonObject, "Profile");
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

    private void getProfileAPI(String userId) {
        if (Validator.isConnectedToInternet(getApplicationContext())) {
            API_GET_PROFILE = AppNetworkConstants.BASE_URL + "user/manage.php?action=get-member-profile&member_id=" + userId;
            NetworkService service = new NetworkService(API_GET_PROFILE, AppConstants.METHOD_GET, this);
            service.call(new NetworkModel());
        } else {
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
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
        if (service.equalsIgnoreCase(API_GET_PROFILE)) {
            GetProfileResponse getProfileResponse = GetProfileResponse.fromJson(response);
            if (getProfileResponse != null && getProfileResponse.success && getProfileResponse.statusCode == 200 && getProfileResponse.userData != null) {
                String s = getProfileResponse.toJson();
                setData(getProfileResponse);
            } else if (getProfileResponse != null && getProfileResponse.error && !Validator.isEmptyString(getProfileResponse.errorMessage)) {
                CustomDialogFragment.getInstance(this, null, getProfileResponse.errorMessage, null, AppConstants.OK, null, 1, null);
            } else {
                CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
            }
        }

    }

    private void setData(GetProfileResponse getProfileResponse) {
        if (getProfileResponse != null && getProfileResponse.userData != null) {
            mUserNameTV.setText(getProfileResponse.userData.displayName);
            mNameET.setText(getProfileResponse.userData.displayName);
            mEmailET.setText(getProfileResponse.userData.email);
            mAreaCodeET.setText(getProfileResponse.userData.areaCode.replace("+", ""));
            mMobileET.setText(getProfileResponse.userData.phone);
            mAddressET.setText(getProfileResponse.userData.address);


            mFollowingTV.setText(following + "\n" + getProfileResponse.userData.userFollowCount);
            mOfferTV.setText(offers + "\n" + getProfileResponse.userData.userOfferCount);
            mRequestTV.setText(requests + "\n" + getProfileResponse.userData.userRequestCount);
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
            mLocationTV.setVisibility(View.GONE);
            mMiddleLL.setVisibility(View.VISIBLE);
            mUploadProfileIV.setVisibility(View.GONE);
            mUploadWallIV.setVisibility(View.GONE);
        }
        mEditProfileTV.setVisibility(View.GONE);
        mUploadWallIV.setVisibility(View.GONE);
        mUploadProfileIV.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

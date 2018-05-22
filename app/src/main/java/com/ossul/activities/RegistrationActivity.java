package com.ossul.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ossul.R;
import com.ossul.appconstant.AppConstants;
import com.ossul.appconstant.ErrorConstant;
import com.ossul.dialog.CustomDialogFragment;
import com.ossul.dialog.DialogManager;
import com.ossul.dialog.TakeImageDialog;
import com.ossul.interfaces.INetworkEvent;
import com.ossul.interfaces.IValidationResult;
import com.ossul.interfaces.ImagePickerCallback;
import com.ossul.network.NetworkService;
import com.ossul.network.constants.AppNetworkConstants;
import com.ossul.network.model.NetworkModel;
import com.ossul.network.model.response.GetLanguageResponse;
import com.ossul.network.model.response.LoginResponse;
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
import java.util.Locale;

/**
 * @author Rajan on 13-Dec-16.
 */

public class RegistrationActivity extends BaseActivity implements IValidationResult, INetworkEvent, ImagePickerCallback {
    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 12;
    private EditText mNameET;
    private EditText mEmailET;
    private EditText mPasswordET;
    private EditText mNumberET;
    private TextView mRegisterTV, mTitleTV;
    private DialogManager mProgressDialog;
    private EditText mAreaCodeET;
    private CircularImageView mImageCIV;
    private String API_SIGNUP = "";
    private TakeImageDialog mTakeImageDialog;
    private File mFile;
    private String API_GET_LABELS = "";
    private JSONObject jsonObject = new JSONObject();
    private String mFrom="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        if (getIntent() != null) {
            mFrom = getIntent().getStringExtra("from");
        }
        initData();
    }

    @Override
    protected void initViews() {
        mImageCIV = (CircularImageView) findViewById(R.id.civ_user_image);
        mNameET = (EditText) findViewById(R.id.et_name);
        mEmailET = (EditText) findViewById(R.id.et_email);
        mPasswordET = (EditText) findViewById(R.id.et_password);
        mAreaCodeET = (EditText) findViewById(R.id.et_area_code);
        mNumberET = (EditText) findViewById(R.id.et_number);
        mRegisterTV = (TextView) findViewById(R.id.tv_register);
        mTitleTV = (TextView) findViewById(R.id.tv_sign_up);

        findViewById(R.id.iv_back).setOnClickListener(this);
        mImageCIV.setOnClickListener(this);
        mRegisterTV.setOnClickListener(this);
        findViewById(R.id.tv_en).setOnClickListener(this);
        findViewById(R.id.tv_ar).setOnClickListener(this);
    }

    @Override
    protected void initVariables() {
        mProgressDialog = new DialogManager(this);
        mTakeImageDialog = new TakeImageDialog(this, this);

        Resources res = getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(mAppPreferences.getPrefLang().toLowerCase());
        res.updateConfiguration(conf, dm);
        if (!Validator.isEmptyString(mAppPreferences.getLabels())) {
            try {
                jsonObject = new JSONObject(mAppPreferences.getLabels());
                if (jsonObject != null && jsonObject.length() != 0) {
                    mNameET.setHint(AppUtilsMethod.getValueFromKey(jsonObject, "Name"));
                    mPasswordET.setHint(AppUtilsMethod.getValueFromKey(jsonObject, "Password"));
                    mEmailET.setHint(AppUtilsMethod.getValueFromKey(jsonObject, "Email"));
                    mAreaCodeET.setHint(AppUtilsMethod.getValueFromKey(jsonObject, "Area Code"));
                    mNumberET.setHint(AppUtilsMethod.getValueFromKey(jsonObject, "Mobile Number"));
                    mRegisterTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Register"));
                    mTitleTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Sign Up"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void updateLangAPI(String lang) {

        Resources res = getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(lang.toLowerCase());
        res.updateConfiguration(conf, dm);
        if (Validator.isConnectedToInternet(getApplicationContext())) {
            NetworkModel networkModel = new NetworkModel();
            mAppPreferences.setPrefLang(lang);
            API_GET_LABELS = AppNetworkConstants.BASE_URL + "user/auth.php?action=update-language&lang=" + lang;
            NetworkService service = new NetworkService(API_GET_LABELS, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ_EXTERNAL_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mTakeImageDialog.getImagePickerDialog(this, "profile-pic").show();
                }
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_register:
                if (Validator.isConnectedToInternet(this))
                    AppValidationChecker.validateSignUpApi(mNameET.getText().toString().trim(), mEmailET.getText().toString().trim(), mPasswordET.getText().toString().trim(), mAreaCodeET.getText().toString().trim(), mNumberET.getText().toString().trim(), this);
                else
                    CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
                break;
            case R.id.civ_user_image:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE);
                        return;
                    }
                }
                mTakeImageDialog.getImagePickerDialog(this, "profile-pic").show();
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_en:
                updateLangAPI("en");

                break;
            case R.id.tv_ar:
                updateLangAPI("ar");
                break;

        }
    }

    @Override
    public void onValidationError(int errorType, int errorResId) {
        switch (errorType) {
            case ErrorConstant.ERROR_TYPE_USER_NAME_EMPTY:
                mNameET.requestFocus();
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Please enter Email")))
                    mNameET.setError(AppUtilsMethod.getValueFromKey(jsonObject, "Please enter Email"));
                else
                    mNameET.setError(getString(errorResId));
                break;
            case ErrorConstant.ERROR_TYPE_EMAIL_EMPTY:
                mEmailET.requestFocus();
                String error = getString(errorResId);
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Please enter Email")))
                    error = AppUtilsMethod.getValueFromKey(jsonObject, "Please enter Email");
                mEmailET.setError(error);
                break;
            case ErrorConstant.ERROR_TYPE_EMAIL_INVALID:
                mEmailET.requestFocus();
                String error1 = getString(errorResId);
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Please enter valid email id")))
                    error1 = AppUtilsMethod.getValueFromKey(jsonObject, "Please enter valid email id");
                mEmailET.setError(error1);
                break;
            case ErrorConstant.ERROR_TYPE_PASSWORD_EMPTY:
                mPasswordET.requestFocus();
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Please enter Password")))
                    mPasswordET.setError(AppUtilsMethod.getValueFromKey(jsonObject, "Please enter Password"));
                else
                    mPasswordET.setError(getString(errorResId));
                break;
            case ErrorConstant.ERROR_TYPE_PASSWORD_LENGTH:
                mPasswordET.requestFocus();
                mPasswordET.setError(getString(errorResId));
                break;

            case ErrorConstant.ERROR_TYPE_COUNTRY_CODE:
                mAreaCodeET.requestFocus();
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Please Enter Country Code")))
                    mAreaCodeET.setError(AppUtilsMethod.getValueFromKey(jsonObject, "Please Enter Country Code"));
                else
                    mAreaCodeET.setError(getString(errorResId));
                break;
            case ErrorConstant.ERROR_TYPE_MOBILE_EMPTY:
                mNumberET.requestFocus();
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Please Enter Mobile Number")))
                    mNumberET.setError(AppUtilsMethod.getValueFromKey(jsonObject, "Please Enter Mobile Number"));
                else
                    mNumberET.setError(getString(errorResId));
                break;
            case ErrorConstant.ERROR_TYPE_VALID_MOBILE:
                mNumberET.requestFocus();
                mNumberET.setError(getString(errorResId));
                break;
        }
    }

    @Override
    public void onValidationSuccess() {
        API_SIGNUP = AppNetworkConstants.BASE_URL + "user/auth.php";
        NetworkService serviceCall = new NetworkService(API_SIGNUP, AppConstants.METHOD_POST, RegistrationActivity.this);
        MultipartBuilder multipartBuilder = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart(ParserKeys.action.toString(), AppNetworkConstants.ACTION_SIGN_UP)
                .addFormDataPart(ParserKeys.email.toString(), mEmailET.getText().toString().trim())
                .addFormDataPart(ParserKeys.displayname.toString(), mNameET.getText().toString().trim())
                .addFormDataPart(ParserKeys.password.toString(), mPasswordET.getText().toString().trim())
                .addFormDataPart(ParserKeys.country_code.toString(), "+" + mAreaCodeET.getText().toString().trim())
                .addFormDataPart(ParserKeys.phone_no.toString(), mNumberET.getText().toString().trim());
        if (mFile != null)
            multipartBuilder.addFormDataPart(ParserKeys.user_image.toString(), mFile.getName(), RequestBody.create(MediaType.parse("image/jpeg"), mFile));
        RequestBody requestBody = (RequestBody) multipartBuilder.build();
        serviceCall.setRequestBody(requestBody);
        serviceCall.call(new NetworkModel(), mFile);
    }

    @Override
    public void onNetworkCallInitiated(String service) {
        if (mProgressDialog != null)
            mProgressDialog.show();
    }

    @Override
    public void onNetworkCallCompleted(String service, String response) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        if (service.equalsIgnoreCase(API_SIGNUP)) {
            LoginResponse baseResponse = LoginResponse.fromJson(response);
            if (baseResponse != null && baseResponse.success && baseResponse.successCode == 200) {
                mAppPreferences.setLogin(true);
                if (!Validator.isEmptyString(baseResponse.token))
                    mAppPreferences.saveToken(baseResponse.token);
//                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));

                if (mFrom != null && mFrom.equalsIgnoreCase("logout")) {
                    startActivity(new Intent(this, MainActivity.class));
                } else {
                    setResult(RESULT_OK);
                }
                finish();
            } else if (baseResponse != null && !Validator.isEmptyString(baseResponse.alert)) {
                CustomDialogFragment.getInstance(this, null, baseResponse.alert, null, AppConstants.OK, null, 1, null);
            } else {
                CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
            }
        } else if (service.equalsIgnoreCase(API_GET_LABELS)) {
            GetLanguageResponse languageResponse = GetLanguageResponse.fromJson(response);
            if (languageResponse != null && languageResponse.data != null) {
                mAppPreferences.setLabels(languageResponse.data.toString());
                initVariables();
            }
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
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.REQ_CODE_GALLERY_IMAGE_PICK || requestCode == AppConstants.REQ_CODE_CAMERA_IMAGE_PICK) {
                mTakeImageDialog.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onImageClicked(File file, String filePath, String imageName) {
        if (file != null) {
            this.mFile = file;
            Uri uri = Uri.fromFile(file);
            mImageCIV.setImageURI(uri);
        }
    }
}

package com.ossul.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ossul.R;
import com.ossul.appconstant.AppConstants;
import com.ossul.appconstant.ErrorConstant;
import com.ossul.dialog.CustomDialogFragment;
import com.ossul.dialog.DialogManager;
import com.ossul.interfaces.INetworkEvent;
import com.ossul.interfaces.IValidationResult;
import com.ossul.network.NetworkService;
import com.ossul.network.constants.AppNetworkConstants;
import com.ossul.network.model.NetworkModel;
import com.ossul.network.model.response.GetLanguageResponse;
import com.ossul.network.model.response.LoginResponse;
import com.ossul.network.parser.ParserKeys;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.AppValidationChecker;
import com.ossul.utility.Validator;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * @author Rajan Tiwari on 10-Nov-16.
 */

public class LoginActivity extends BaseActivity implements IValidationResult, INetworkEvent {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static String API_LOGIN = "";
    private EditText mUserNameET, mPasswordET;
    private TextView mSignUpTV, mForgotPassTV, mLoginTV;
    private DialogManager mProgressDialog;
    private String API_GET_LABELS = "";
    private String mSignUpSTR = "Don't have an account? Sign Up";
    private String dontHave = "Don't have an account?";
    private String mForgotSTR = "Forgot Password? Click here";
    private String forgotPass = "Forgot Password?";
    private JSONObject jsonObject = new JSONObject();
    private String mFrom = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getIntent() != null) {
            mFrom = getIntent().getStringExtra("from");
        }
        initData();

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
    protected void initViews() {
        mUserNameET = (EditText) findViewById(R.id.et_user_name);
        mPasswordET = (EditText) findViewById(R.id.et_password);
        mLoginTV = (TextView) findViewById(R.id.tv_sign_in);
        mForgotPassTV = (TextView) findViewById(R.id.tv_forgot_password);
        mSignUpTV = (TextView) findViewById(R.id.tv_register_now);

        findViewById(R.id.tv_en).setOnClickListener(this);
        findViewById(R.id.tv_ar).setOnClickListener(this);
        mLoginTV.setOnClickListener(this);
    }

    @Override
    protected void initVariables() {

        Resources res = getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(mAppPreferences.getPrefLang().toLowerCase());
        res.updateConfiguration(conf, dm);
        mProgressDialog = new DialogManager(this);
        updateLabels();
        SpannableString ss = new SpannableString(mSignUpSTR);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivityForResult(intent, 11001);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.white));
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, dontHave.length(), mSignUpSTR.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mSignUpTV.setText(ss);
        mSignUpTV.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableString forgotPassSS = new SpannableString(mForgotSTR);
        ClickableSpan forgotSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivityForResult(intent, 102);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.white));
                ds.setUnderlineText(false);
            }
        };
        forgotPassSS.setSpan(forgotSpan, forgotPass.length(), mForgotSTR.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mForgotPassTV.setText(forgotPassSS);
        mForgotPassTV.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11001 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_sign_in:
                if (Validator.isConnectedToInternet(getApplicationContext())) {
                    AppValidationChecker.validateLoginApi(mUserNameET.getText().toString().trim(), mPasswordET.getText().toString().trim(), this);
                } else {
                    CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
                }
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
                mUserNameET.requestFocus();
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Please enter Email")))
                    mUserNameET.setError(AppUtilsMethod.getValueFromKey(jsonObject, "Please enter Email"));
                else
                    mUserNameET.setError(getString(errorResId));
                break;
            case ErrorConstant.ERROR_TYPE_EMAIL_INVALID:
                mUserNameET.requestFocus();
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Please enter valid email id")))
                    mUserNameET.setError(AppUtilsMethod.getValueFromKey(jsonObject, "Please enter valid email id"));
                else
                    mUserNameET.setError(getString(errorResId));
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
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "")))
                    mPasswordET.setError(AppUtilsMethod.getValueFromKey(jsonObject, ""));
                else
                    mPasswordET.setError(getString(errorResId));
                break;
        }
    }

    @Override
    public void onValidationSuccess() {
        API_LOGIN = AppNetworkConstants.BASE_URL + "user/auth.php";
        NetworkService serviceCall = new NetworkService(API_LOGIN, AppConstants.METHOD_POST, LoginActivity.this);
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart(ParserKeys.action.toString(), AppNetworkConstants.ACTION_LOGIN)
                .addFormDataPart(ParserKeys.email.toString(), mUserNameET.getText().toString().trim())
                .addFormDataPart(ParserKeys.password.toString(), mPasswordET.getText().toString().trim())
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
        if (service.equalsIgnoreCase(API_LOGIN)) {
            LoginResponse loginResponseBean = LoginResponse.fromJson(response);
            if (loginResponseBean != null && loginResponseBean.success && loginResponseBean.successCode == 200) {
                mAppPreferences.setLogin(true);
                if (!Validator.isEmptyString(loginResponseBean.token))
                    mAppPreferences.saveToken(loginResponseBean.token);
               if(mFrom!=null && mFrom.equalsIgnoreCase("logout")){
                startActivity(new Intent(this,MainActivity.class));
               }else {
                   setResult(RESULT_OK);
               }
                finish();
            } else if (loginResponseBean != null && loginResponseBean.error && !Validator.isEmptyString(loginResponseBean.errorMessage)) {
                CustomDialogFragment.getInstance(this, null, "" + loginResponseBean.errorMessage, null, AppConstants.OK, null, 1, null);
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

    private void updateLabels() {
        if (!Validator.isEmptyString(mAppPreferences.getLabels())) {
            try {
                jsonObject = new JSONObject(mAppPreferences.getLabels());
                if (jsonObject != null && jsonObject.length() != 0) {
                    mUserNameET.setHint(AppUtilsMethod.getValueFromKey(jsonObject, "Email"));
                    mPasswordET.setHint(AppUtilsMethod.getValueFromKey(jsonObject, "Password"));
                    mLoginTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Sign in"));
                    mSignUpSTR = AppUtilsMethod.getValueFromKey(jsonObject, "Don't have an account?")
                            + " " + AppUtilsMethod.getValueFromKey(jsonObject, "Sign Up");
                    dontHave = AppUtilsMethod.getValueFromKey(jsonObject, "Don't have an account?");
                    forgotPass = AppUtilsMethod.getValueFromKey(jsonObject, "Forgot Password?");
                    mForgotSTR = AppUtilsMethod.getValueFromKey(jsonObject, "Forgot Password?")
                            + " " + AppUtilsMethod.getValueFromKey(jsonObject, "Click here");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
    }

}

package com.ossul.activities;

import android.os.Bundle;
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
import com.ossul.network.model.response.BaseResponse;
import com.ossul.network.parser.ParserKeys;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.AppValidationChecker;
import com.ossul.utility.Validator;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;


public class ForgotPasswordActivity extends BaseActivity implements IValidationResult, INetworkEvent {
    private static final String TAG = ForgotPasswordActivity.class.getSimpleName();
    private EditText mEmailET;
    private DialogManager mProgressDialog;
    private String API_FORGOT_PASSWORD = "";
    private TextView mSubmitTV;
    private TextView mTitleTV;
    private JSONObject jsonObject = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initData();
    }

    @Override
    protected void initViews() {
        mEmailET = (EditText) findViewById(R.id.et_email);
        mTitleTV = (TextView) findViewById(R.id.tv_forgot_text);

        mSubmitTV = (TextView) findViewById(R.id.tv_submit);
        mSubmitTV.setOnClickListener(this);

    }

    @Override
    protected void initVariables() {
        mProgressDialog = new DialogManager(this);
        if (!Validator.isEmptyString(mAppPreferences.getLabels())) {
            try {
                jsonObject = new JSONObject(mAppPreferences.getLabels());
                if (jsonObject != null && jsonObject.length() != 0) {
                    mTitleTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Please enter your email then click submit"));
                    mEmailET.setHint(AppUtilsMethod.getValueFromKey(jsonObject, "Your email"));
                    mSubmitTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Submit"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_submit:
                if (Validator.isConnectedToInternet(getApplicationContext())) {
                    AppValidationChecker.validateResetPassApi(mEmailET.getText().toString().trim(), this);
                } else {
                    CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.something_went), null,AppConstants.OK, null, 1, null);
                }
                break;
        }
    }

    @Override
    public void onValidationError(int errorType, int errorResId) {
        switch (errorType) {
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
        }
    }

    @Override
    public void onValidationSuccess() {
        API_FORGOT_PASSWORD = AppNetworkConstants.BASE_URL + "user/auth.php";
        NetworkService serviceCall = new NetworkService(API_FORGOT_PASSWORD, AppConstants.METHOD_POST, ForgotPasswordActivity.this);
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart(ParserKeys.action.toString(), AppNetworkConstants.ACTION_FORGOT_PASSWORD)
                .addFormDataPart(ParserKeys.email.toString(), mEmailET.getText().toString().trim())
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
        if (service.equalsIgnoreCase(API_FORGOT_PASSWORD)) {
            BaseResponse forgotPasswordResponse = BaseResponse.fromJson(response);
            if (forgotPasswordResponse != null && !Validator.isEmptyString(forgotPasswordResponse.alert)) {
                CustomDialogFragment.getInstance(this, null, forgotPasswordResponse.alert, null, AppConstants.OK, null, 1, null);
            } else if (forgotPasswordResponse != null && !Validator.isEmptyString(forgotPasswordResponse.message)) {
                CustomDialogFragment.getInstance(this, null, forgotPasswordResponse.message, null,AppConstants.OK, null, 1, null);
            } else {
                CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.something_went), null, AppConstants.OK, null, 1, null);
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

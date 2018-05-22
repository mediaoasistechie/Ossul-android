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
import com.ossul.network.model.response.ChangePasswordResponse;
import com.ossul.network.parser.ParserKeys;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.AppValidationChecker;
import com.ossul.utility.Validator;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;


public class ChangePasswordActivity extends BaseActivity implements IValidationResult, INetworkEvent {
    private static final String TAG = ChangePasswordActivity.class.getSimpleName();
    private EditText mPasswordET, mRepeatPasswordET;
    private DialogManager mProgressDialog;
    private String API_CHANGE_PASSWORD = "";
    private JSONObject jsonObject = new JSONObject();
    private TextView mSavePassTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initData();
    }

    @Override
    protected void initViews() {
        mPasswordET = (EditText) findViewById(R.id.et_password);
        mRepeatPasswordET = (EditText) findViewById(R.id.et_repeat_password);

        mSavePassTV = (TextView) findViewById(R.id.tv_save_password);
        mSavePassTV.setOnClickListener(this);

    }

    @Override
    protected void initVariables() {
        mProgressDialog = new DialogManager(this);
        if (!Validator.isEmptyString(mAppPreferences.getLabels())) {
            try {
                jsonObject = new JSONObject(mAppPreferences.getLabels());
                if (jsonObject != null && jsonObject.length() != 0) {
                    mPasswordET.setHint(AppUtilsMethod.getValueFromKey(jsonObject, "Password"));
                    mRepeatPasswordET.setHint(AppUtilsMethod.getValueFromKey(jsonObject, "Repeat Password"));
                    mSavePassTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Save Password"));
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
            case R.id.tv_save_password:
                if (Validator.isConnectedToInternet(getApplicationContext())) {
                    AppValidationChecker.validateChangePassApi(mPasswordET.getText().toString().trim(), mRepeatPasswordET.getText().toString().trim(), this);
                } else {

                    CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, "OK", null, 1, null);
                }
                break;
        }
    }

    @Override
    public void onValidationError(int errorType, int errorResId) {
        switch (errorType) {
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
            case ErrorConstant.ERROR_TYPE_REPEAT_PASSWORD_EMPTY:
                mRepeatPasswordET.requestFocus();
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Please enter Confirm password")))
                    mRepeatPasswordET.setError(AppUtilsMethod.getValueFromKey(jsonObject, "Please enter Confirm password"));
                else
                    mRepeatPasswordET.setError(getString(errorResId));
                break;
            case ErrorConstant.ERROR_TYPE_PASSWORD_MISMATCH:
                mRepeatPasswordET.requestFocus();
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Password and Confirm password should be same")))
                    mRepeatPasswordET.setError(AppUtilsMethod.getValueFromKey(jsonObject, "Password and Confirm password should be same"));
                else
                    mRepeatPasswordET.setError(getString(errorResId));
                break;

        }
    }

    @Override
    public void onValidationSuccess() {
        API_CHANGE_PASSWORD = AppNetworkConstants.BASE_URL + "user/manage.php";
        NetworkService serviceCall = new NetworkService(API_CHANGE_PASSWORD, AppConstants.METHOD_POST, ChangePasswordActivity.this);
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart(ParserKeys.action.toString(), AppNetworkConstants.ACTION_UPDATE_PASSWORD)
                .addFormDataPart(ParserKeys.npassword.toString(), mPasswordET.getText().toString().trim())
                .addFormDataPart(ParserKeys.cfpassword.toString(), mRepeatPasswordET.getText().toString().trim())
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
        if (service.equalsIgnoreCase(API_CHANGE_PASSWORD)) {
            ChangePasswordResponse forgotPasswordResponse = ChangePasswordResponse.fromJson(response);
            if (forgotPasswordResponse != null && forgotPasswordResponse.success && !Validator.isEmptyString(forgotPasswordResponse.validateMessage)) {
                CustomDialogFragment.getInstance(this, null, forgotPasswordResponse.validateMessage, null, "OK", null, 1, new CustomDialogFragment.OnDialogClickListener() {
                    @Override
                    public void onClickOk() {
                        ChangePasswordActivity.this.finish();
                    }

                    @Override
                    public void onClickCancel() {

                    }
                });
            } else if (forgotPasswordResponse != null && forgotPasswordResponse.error && !Validator.isEmptyString(forgotPasswordResponse.errorMessage)) {
                CustomDialogFragment.getInstance(this, null, forgotPasswordResponse.errorMessage, null, "OK", null, 1, null);
            } else {
                CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.something_went), null, "OK", null, 1, null);
            }
        }

    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.something_went), null, "OK", null, 1, null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

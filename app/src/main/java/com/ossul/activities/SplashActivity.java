package com.ossul.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.ossul.R;
import com.ossul.appconstant.AppConstants;
import com.ossul.apppreferences.AppPreferences;
import com.ossul.interfaces.INetworkEvent;
import com.ossul.network.NetworkService;
import com.ossul.network.constants.AppNetworkConstants;
import com.ossul.network.model.NetworkModel;
import com.ossul.network.parser.ParserKeys;
import com.ossul.utility.Validator;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;


/**
 * @author Rajan Tiwari
 *         This class is used as a splash screen. it is an entry screen for
 *         the app.
 */
public class SplashActivity extends BaseActivity implements INetworkEvent {

    private static final int PERMISSION_ALL = 10;
    private String API_SAVE_TOKEN = "";
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE
            };
            if (!hasPermissions(this, PERMISSIONS)) {
                requestPermissions(PERMISSIONS, PERMISSION_ALL);
                return;
            }

        }
        initData();
    }


    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_ALL:
                if (grantResults.length > 0) {
                    initData();
                }

                break;

        }
    }

    @Override
    protected void initViews() {
        //TODO: hit an api to save device token if available
        if (!Validator.isEmptyString(AppPreferences.get().getGcmId()))
            saveTokenAPI();

    }

    private void saveTokenAPI() {
        API_SAVE_TOKEN = AppNetworkConstants.BASE_URL + "user/auth.php";
        NetworkService serviceCall = new NetworkService(API_SAVE_TOKEN, AppConstants.METHOD_POST, this);
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart(ParserKeys.action.toString(), AppNetworkConstants.ACTION_SAVE_DEVICE_TOKEN)
                .addFormDataPart(ParserKeys.device_id.toString(), AppPreferences.get().getGcmId())
                .build();
        serviceCall.setRequestBody(requestBody);
        serviceCall.call(new NetworkModel());
    }

    @Override
    protected void initVariables() {
        intent = getIntent();
        if (null != intent && intent.getData() != null) {
            AppConstants.intentPath = intent;
        } else {
            AppConstants.intentPath = null;
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
//                if (AppPreferences.get().isLogin()) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                } else {
//                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                }
                finish();
            }
        }, 3000);
    }


    @Override
    public void onNetworkCallInitiated(String service) {

    }

    @Override
    public void onNetworkCallCompleted(String service, String response) {

        Log.d("onNetworkCallCompleted", "success");
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        Log.d("onNetworkCallError", "error");

    }
}

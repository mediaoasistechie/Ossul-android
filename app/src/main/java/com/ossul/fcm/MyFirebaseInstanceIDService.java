package com.ossul.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
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
 * Created by Rajan on 11-Feb-17.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("MyFirebaseInstanceID", "Refreshed token: " + refreshedToken);
        if (!Validator.isEmptyString(refreshedToken)) {
            AppPreferences.get().setGcmId(refreshedToken);
            //TODO: call api to save device token
            String API_SAVE_TOKEN = AppNetworkConstants.BASE_URL + "user/auth.php";
            NetworkService serviceCall = new NetworkService(API_SAVE_TOKEN, AppConstants.METHOD_POST, new INetworkEvent() {
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
            });
            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart(ParserKeys.action.toString(), AppNetworkConstants.ACTION_SAVE_DEVICE_TOKEN)
                    .addFormDataPart(ParserKeys.device_id.toString(), AppPreferences.get().getGcmId())
                    .build();
            serviceCall.setRequestBody(requestBody);
            serviceCall.call(new NetworkModel());

        }
        super.onTokenRefresh();

    }
}

package com.ossul.network.model.response;

import android.util.Log;

import com.google.gson.Gson;

public class LoginResponse extends BaseResponse {

    public LoginResponse() {

    }

    public static LoginResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, LoginResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "GSON==>" + e.getMessage());
            return null;
        }
    }

}

package com.ossul.network.model.response;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateProfileResponse {

    @Expose
    @SerializedName("success")
    public boolean success;

    @Expose
    @SerializedName("successmsg")
    public String successMessage;

    @Expose
    @SerializedName("error")
    public boolean error;

    @Expose
    @SerializedName("errromessage")
    public String errorMessage;

    public UpdateProfileResponse() {

    }

    public static UpdateProfileResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, UpdateProfileResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "GSON==>" + e.getMessage());
            return null;
        }
    }

}

package com.ossul.network.model.response;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseResponse {
    @Expose
    @SerializedName("success")
    public boolean success;

    @Expose
    @SerializedName("successcode")
    public int successCode;

    @Expose
    @SerializedName("statuscode")
    public int statusCode;


    @Expose
    @SerializedName("token")
    public String token;

    @Expose
    @SerializedName("msg")
    public String msg;

    @Expose
    @SerializedName("message")
    public String message;


    @Expose
    @SerializedName("successmsg")
    public String successMessage;

    @Expose
    @SerializedName("sucess_message")
    public String success_message;

    @Expose
    @SerializedName("alert")
    public String alert;


    @Expose
    @SerializedName("error")
    public boolean error;

    @Expose
    @SerializedName("error_message")
    public String errorMessage;

    public BaseResponse() {

    }

    public static BaseResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, BaseResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "GSON==>" + e.getMessage());
            return null;
        }
    }

    public String emptyJson() {
        return "{}";
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

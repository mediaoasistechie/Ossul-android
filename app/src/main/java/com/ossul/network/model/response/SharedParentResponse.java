package com.ossul.network.model.response;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SharedParentResponse extends BaseResponse {
    @Expose
    @SerializedName("data")
    public ArrayList<SharedChildResponse> data = new ArrayList<>();

    public SharedParentResponse() {

    }

    public static SharedParentResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, SharedParentResponse.class);
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

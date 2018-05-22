package com.ossul.network.model.response;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetOfficeParentResponse extends BaseResponse {
    @Expose
    @SerializedName("data")
    public JsonArray data;

    public GetOfficeParentResponse() {

    }

    public static GetOfficeParentResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, GetOfficeParentResponse.class);
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

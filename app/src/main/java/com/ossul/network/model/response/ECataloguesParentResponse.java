package com.ossul.network.model.response;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ECataloguesParentResponse extends BaseResponse {
    @Expose
    @SerializedName("data")
    public Data data = new Data();

    public ECataloguesParentResponse() {

    }

    public class Data {
        @Expose
        @SerializedName("default")
        public ArrayList<DefaultECataloguesResponse> defaultECataloguesResponses = new ArrayList();

        @Expose
        @SerializedName("custom")
        public ArrayList<UserECataloguesResponse> userECataloguesResponses = new ArrayList();
    }

    public static ECataloguesParentResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, ECataloguesParentResponse.class);
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

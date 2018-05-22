package com.ossul.network.model.response;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetPropertiesFieldsResponse extends BaseResponse {

    @Expose
    @SerializedName("data")
    public Data data = new Data();


    public GetPropertiesFieldsResponse() {

    }

    public static GetPropertiesFieldsResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, GetPropertiesFieldsResponse.class);
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

    public class Data {
        @Expose
        @SerializedName("propertyFields")
        public JsonArray propertyFields;
    }
}

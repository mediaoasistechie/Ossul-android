package com.ossul.network.model.response;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserECataloguesResponse extends BaseResponse {
    @SerializedName("list_name")
    public String listName;
    @Expose
    @SerializedName("list_id")
    public String listId;
    @Expose
    @SerializedName("list_type")
    public String listType;
    @Expose
    @SerializedName("total_property")
    public String totalProperty;
    @Expose
    @SerializedName("list_img_path")
    public String listImagePath;

    public UserECataloguesResponse() {

    }

    public static UserECataloguesResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, UserECataloguesResponse.class);
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

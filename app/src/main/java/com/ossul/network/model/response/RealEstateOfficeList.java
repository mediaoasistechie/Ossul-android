package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RealEstateOfficeList extends BaseResponse {
    @Expose
    @SerializedName("store_id")
    public String storeId;

    @Expose
    @SerializedName("store_title")
    public String storeName;

    public RealEstateOfficeList() {

    }


    public static RealEstateOfficeList fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, RealEstateOfficeList.class);
        } catch (Exception e) {
            e.printStackTrace();
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

package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegionList extends BaseResponse {
    @Expose
    @SerializedName("region_id")
    public String regionId;

    @Expose
    @SerializedName("region_name")
    public String regionName;


    public RegionList() {

    }


    public static RegionList fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, RegionList.class);
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

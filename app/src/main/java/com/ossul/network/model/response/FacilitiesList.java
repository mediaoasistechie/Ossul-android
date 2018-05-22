package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FacilitiesList extends BaseResponse {
    @Expose
    @SerializedName("property_facility_id")
    public String propertyFacilityId;

    @Expose
    @SerializedName("property_facility_name")
    public String propertyFacilityName;


    public FacilitiesList() {

    }


    public static FacilitiesList fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, FacilitiesList.class);
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

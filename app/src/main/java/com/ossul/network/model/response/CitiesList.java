package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CitiesList extends BaseResponse {
    @Expose
    @SerializedName("city_id")
    public String cityId;

    @Expose
    @SerializedName("city_name")
    public String cityName;

    @Expose
    @SerializedName("country_id")
    public String countryId;


    public CitiesList() {

    }


    public static CitiesList fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, CitiesList.class);
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

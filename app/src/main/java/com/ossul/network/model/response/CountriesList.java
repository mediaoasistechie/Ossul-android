package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountriesList extends BaseResponse {
    @Expose
    @SerializedName("country_name")
    public String countryName;

    @Expose
    @SerializedName("country_id")
    public String countryId;

    @Expose
    @SerializedName("status")
    public String status;

    @Expose
    @SerializedName("creation_date")
    public String creation_date;

    @Expose
    @SerializedName("updation_date")
    public String updation_date;

    public CountriesList() {

    }


    public static CountriesList fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, CountriesList.class);
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

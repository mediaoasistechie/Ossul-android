package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetCityListResponse extends BaseResponse {
    @Expose
    @SerializedName("data")
    public ArrayList<CityList> data = new ArrayList<>();


    public GetCityListResponse() {

    }


    public static GetCityListResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, GetCityListResponse.class);
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

    public class CityList {

        @Expose
        @SerializedName("city_id")
        public String cityId;

        @Expose
        @SerializedName("status")
        public String status;

        @Expose
        @SerializedName("city_name")
        public String cityName;
        @Expose
        @SerializedName("country_id")
        public String countryId;
    }
}

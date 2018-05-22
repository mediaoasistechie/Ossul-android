package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetCountryListResponse extends BaseResponse {
    @Expose
    @SerializedName("data")
    public ArrayList<CountriesList> data = new ArrayList<>();


    public GetCountryListResponse() {

    }


    public static GetCountryListResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, GetCountryListResponse.class);
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

package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountryCitySettingResponse extends BaseResponse {
    @Expose
    @SerializedName("data")
    public Data data;

    public CountryCitySettingResponse() {

    }

    public static CountryCitySettingResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, CountryCitySettingResponse.class);
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

    public class Data {

        @SerializedName("country_city_id")
        public String countryCityId;
        @Expose
        @SerializedName("screen_name")
        public String screen_name;

        @Expose
        @SerializedName("country")
        public int country;

        @Expose
        @SerializedName("city")
        public int city;
    }
}

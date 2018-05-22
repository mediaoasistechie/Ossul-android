package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetPropertyCreateInfo extends BaseResponse {
    @Expose
    @SerializedName("data")
    public Data data = new Data();


    public GetPropertyCreateInfo() {

    }

    public static GetPropertyCreateInfo fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, GetPropertyCreateInfo.class);
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

        @Expose
        @SerializedName("countries")
        public JsonArray countries;
        @Expose
        @SerializedName("cities")
        public JsonArray cities;

        @Expose
        @SerializedName("neighbourhood")
        public JsonArray neighbourhood;

        @Expose
        @SerializedName("propertyFields")
        public JsonArray propertyFields;

        @Expose
        @SerializedName("stores")
        public JsonArray stores;

        @Expose
        @SerializedName("regions")
        public JsonArray regions;

        @Expose
        @SerializedName("propertyTypes")
        public JsonArray propertyTypes;

        @Expose
        @SerializedName("facilities")
        public JsonArray facilities;

        @Expose
        @SerializedName("categories")
        public JsonArray categories;

        @Expose
        @SerializedName("offerType")
        public JsonArray offerType;
    }

}

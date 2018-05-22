package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Rajan on 31-Jan-17.
 */
public class MyOffersResponse {

    @Expose
    @SerializedName("data")
    public ArrayList<Data> data = new ArrayList<>();

    public MyOffersResponse() {

    }

    public static MyOffersResponse fromJson(String json) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, MyOffersResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public class Data {
        @Expose
        @SerializedName("offer_id")
        public String offerId;
        @Expose
        @SerializedName("request_id")
        public String requestId;
        @Expose
        @SerializedName("user_id")
        public String userId;
        @Expose
        @SerializedName("title")
        public String title;

        @Expose
        @SerializedName("email")
        public String email;
        @Expose
        @SerializedName("phone")
        public String phone;

        @Expose
        @SerializedName("offer_attachment")
        public String offer_attachment;

        @Expose
        @SerializedName("sale_type_id")
        public String saleTypeId;
        @Expose
        @SerializedName("property_type_id")
        public String propertyTypeId;
        @Expose
        @SerializedName("zone_type_id")
        public String zoneTypeId;
        @Expose
        @SerializedName("country_id")
        public String countryId;
        @Expose
        @SerializedName("city_id")
        public String cityId;
        @Expose
        @SerializedName("description")
        public String description;
        @Expose
        @SerializedName("min_price")
        public String minPrice;
        @Expose
        @SerializedName("max_price")
        public String maxPrice;
        @Expose
        @SerializedName("status")
        public String status;
        @Expose
        @SerializedName("creation_date")
        public String creationDate;
        @Expose
        @SerializedName("updation_date")
        public String updationDate;
        @Expose
        @SerializedName("city_name")
        public String cityName;

        @Expose
        @SerializedName("offered_by")
        public String offeredBy;

        @Expose
        @SerializedName("offer_comment_count")
        public int comments;

        @Expose
        @SerializedName("country_name")
        public String countryName;
        @Expose
        @SerializedName("sale_type_name")
        public String saleTypeName;
        @Expose
        @SerializedName("offered_to")
        public String offeredTo;
        @Expose
        @SerializedName("property_type_name")
        public String propertyTypeName;
        @Expose
        @SerializedName("offered_user_id")
        public String offeredUserId;
        @Expose
        @SerializedName("owner_id")
        public String ownerId;
        @Expose
        @SerializedName("property_image")
        public String propertyImage;
        @Expose
        @SerializedName("zone_type_name")
        public ArrayList<ZoneType> zoneTypeName = new ArrayList<>();

        public class ZoneType {
            @Expose
            @SerializedName("zone_type_id")
            public String zoneTypeId;

            @Expose
            @SerializedName("zone_type_name")
            public String zoneTypeName;

        }
    }
}
package com.ossul.network.model.response;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OfficeDetailResponse extends BaseResponse {
    @Expose
    @SerializedName("data")
    public Data data = new Data();

    public OfficeDetailResponse() {

    }

    public static OfficeDetailResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, OfficeDetailResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "GSON==>" + e.getMessage());
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
        @SerializedName("store_property_count")
        public String storePropertyCount;
        @Expose
        @SerializedName("latitude")
        public double latitude;
        @Expose
        @SerializedName("store_branch_id")
        public double storeBranchId;
        @Expose
        @SerializedName("longitude")
        public String longitude;
        @Expose
        @SerializedName("location")
        public String location;
        @Expose
        @SerializedName("store_id")
        public String storeId;
        @Expose
        @SerializedName("store_title")
        public String storeTitle;
        @Expose
        @SerializedName("photo_id")
        public String photoId;
        @Expose
        @SerializedName("wall_photo_id")
        public String wallPhotoId;
        @Expose
        @SerializedName("email")
        public String email;
        @Expose
        @SerializedName("website")
        public String website;
        @Expose
        @SerializedName("phone")
        public String phone;

        @Expose
        @SerializedName("follow_count")
        public String followCount;
        @Expose
        @SerializedName("status")
        public String status;
        @Expose
        @SerializedName("creation_date")
        public String creationDate;
        @SerializedName("modified_date")
        public String modifiedDate;
        @Expose
        @SerializedName("image_path")
        public String imagePath;
        @Expose
        @SerializedName("city_name")
        public String cityName;
        @Expose
        @SerializedName("wall_image_path")
        public String wallImagePath;
    }
}

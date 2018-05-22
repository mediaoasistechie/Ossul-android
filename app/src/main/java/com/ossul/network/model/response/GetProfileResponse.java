package com.ossul.network.model.response;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetProfileResponse extends BaseResponse{

    @Expose
    @SerializedName("data")
    public UserData userData = new UserData();

    public GetProfileResponse() {

    }

    public class UserData {
        @Expose
        @SerializedName("user_id")
        public String userId;
        @Expose
        @SerializedName("email")
        public String email;
        @Expose
        @SerializedName("password")
        public String password;
        @Expose
        @SerializedName("displayname")
        public String displayName;
        @Expose
        @SerializedName("phone")
        public String phone;
        @Expose
        @SerializedName("phone_no")
        public String phoneNo;
        @Expose
        @SerializedName("photo_id")
        public String photoId;
        @Expose
        @SerializedName("level_id")
        public String levelId;
        @Expose
        @SerializedName("usual_store_id")
        public String usualStoreId;
        @Expose
        @SerializedName("country_id")
        public String countryId;
        @Expose
        @SerializedName("region_id")
        public String regionId;
        @Expose
        @SerializedName("city_id")
        public String cityId;
        @Expose
        @SerializedName("device_id")
        public String deviceId;
        @Expose
        @SerializedName("wall_photo_id")
        public String wallPhotoId;
        @Expose
        @SerializedName("address")
        public String address;
        @Expose
        @SerializedName("status")
        public String status;
        @Expose
        @SerializedName("creation_date")
        public String creationDate;
        @Expose
        @SerializedName("modified_date")
        public String modifiedDate;
        @Expose
        @SerializedName("area_code")
        public String areaCode;

        @Expose
        @SerializedName("user_offer_count")
        public String userOfferCount;
        @Expose
        @SerializedName("user_follow_count")
        public String userFollowCount;
        @Expose
        @SerializedName("user_request_count")
        public String userRequestCount;


    }

    public static GetProfileResponse fromJson(String json) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, GetProfileResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "GSON==>" + e.getMessage());
            return null;
        }
    }


}

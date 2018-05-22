package com.ossul.network.model.response;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RealEstateNewsList extends BaseResponse implements Parcelable {

    @Expose
    @SerializedName("realestate_news_id")
    public String realEstateNewsId;

    @Expose
    @SerializedName("user_id")
    public String userId;
    @Expose
    @SerializedName("country_id")
    public String countryId;

    @Expose
    @SerializedName("city_id")
    public String cityId;

    @Expose
    @SerializedName("title")
    public String title;

    @Expose
    @SerializedName("description")
    public String description;

    @Expose
    @SerializedName("photo_id")
    public String photoId;


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
    @SerializedName("displayname")
    public String displayName;

    @Expose
    @SerializedName("country_name")
    public String countryName;

    @Expose
    @SerializedName("city_name")
    public String cityName;

    @SerializedName("storage_path")
    public String storagePath;

    @Expose
    @SerializedName("images")
    public JsonArray images;

    @Expose
    @SerializedName("share_link")
    public String shareLink;


    public RealEstateNewsList() {

    }

    protected RealEstateNewsList(Parcel in) {
        realEstateNewsId = in.readString();
        userId = in.readString();
        countryId = in.readString();
        cityId = in.readString();
        title = in.readString();
        description = in.readString();
        photoId = in.readString();
        status = in.readString();
        creationDate = in.readString();
        updationDate = in.readString();
        displayName = in.readString();
        countryName = in.readString();
        cityName = in.readString();
        storagePath = in.readString();
    }

    public static final Creator<RealEstateNewsList> CREATOR = new Creator<RealEstateNewsList>() {
        @Override
        public RealEstateNewsList createFromParcel(Parcel in) {
            return new RealEstateNewsList(in);
        }

        @Override
        public RealEstateNewsList[] newArray(int size) {
            return new RealEstateNewsList[size];
        }
    };

    public static RealEstateNewsList fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, RealEstateNewsList.class);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(realEstateNewsId);
        dest.writeString(userId);
        dest.writeString(countryId);
        dest.writeString(cityId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(photoId);
        dest.writeString(status);
        dest.writeString(creationDate);
        dest.writeString(updationDate);
        dest.writeString(displayName);
        dest.writeString(countryName);
        dest.writeString(cityName);
        dest.writeString(storagePath);
    }
}

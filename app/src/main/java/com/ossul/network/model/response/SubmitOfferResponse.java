package com.ossul.network.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Rajan on 31-Jan-17.
 */
public class SubmitOfferResponse implements Parcelable {


    public static final Creator<SubmitOfferResponse> CREATOR = new Creator<SubmitOfferResponse>() {
        @Override
        public SubmitOfferResponse createFromParcel(Parcel in) {
            return new SubmitOfferResponse(in);
        }

        @Override
        public SubmitOfferResponse[] newArray(int size) {
            return new SubmitOfferResponse[size];
        }
    };
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
    @SerializedName("requested_by")
    public String requestedBy;
    @Expose
    @SerializedName("country_name")
    public String countryName;
    @Expose
    @SerializedName("sale_type_name")
    public String saleTypeName;
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

    public SubmitOfferResponse() {

    }

    protected SubmitOfferResponse(Parcel in) {
        requestId = in.readString();
        userId = in.readString();
        title = in.readString();
        saleTypeId = in.readString();
        propertyTypeId = in.readString();
        zoneTypeId = in.readString();
        countryId = in.readString();
        cityId = in.readString();
        description = in.readString();
        minPrice = in.readString();
        maxPrice = in.readString();
        status = in.readString();
        creationDate = in.readString();
        updationDate = in.readString();
        cityName = in.readString();
        requestedBy= in.readString();
        countryName = in.readString();
        saleTypeName = in.readString();
        propertyTypeName = in.readString();
        offeredUserId = in.readString();
        ownerId = in.readString();
        propertyImage = in.readString();
    }

    public static SubmitOfferResponse fromJson(String json) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, SubmitOfferResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(requestId);
        dest.writeString(userId);
        dest.writeString(title);
        dest.writeString(saleTypeId);
        dest.writeString(propertyTypeId);
        dest.writeString(zoneTypeId);
        dest.writeString(countryId);
        dest.writeString(cityId);
        dest.writeString(description);
        dest.writeString(minPrice);
        dest.writeString(maxPrice);
        dest.writeString(status);
        dest.writeString(creationDate);
        dest.writeString(updationDate);
        dest.writeString(cityName);
        dest.writeString(countryName);
        dest.writeString(countryName);
        dest.writeString(saleTypeName);
        dest.writeString(propertyTypeName);
        dest.writeString(offeredUserId);
        dest.writeString(ownerId);
        dest.writeString(propertyImage);
    }

    public class ZoneType {

        @Expose
        @SerializedName("zone_type_id")
        public String zoneTypeId;

        @Expose
        @SerializedName("zone_type_name")
        public String zoneTypeName;

    }
}
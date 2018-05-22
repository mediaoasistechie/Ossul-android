package com.ossul.network.model.response;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Rajan on 31-Jan-17.
 */
public class SpecialOfferProperty extends BaseResponse implements Parcelable {
    @Expose
    @SerializedName("property_id")
    public String propertyId;

    @Expose
    @SerializedName("property_title")
    public String propertyTitle;

    @Expose
    @SerializedName("description")
    public String description;

    @Expose
    @SerializedName("weight")
    public String weight;

    @Expose
    @SerializedName("offer_type_name")
    public String offerTypeName;

    @Expose
    @SerializedName("city_name")
    public String cityName;

    @Expose
    @SerializedName("categoryId")
    public String categoryId;


    @Expose
    @SerializedName("category_name")
    public String categoryName;

    @Expose
    @SerializedName("price")
    public String price;

    @Expose
    @SerializedName("store_title")
    public String storeTitle;

    @Expose
    @SerializedName("store_id")
    public String storeId;


    @Expose
    @SerializedName("storage_path")
    public String imageStoragePath;

    @Expose
    @SerializedName("attachments")
    public ArrayList<String> attachments;

    @Expose
    @SerializedName("property_price")
    public String propertyPrice;

    @Expose
    @SerializedName("offer_price")
    public String propertyOfferPrice;


    @Expose
    @SerializedName("product_offer_from")
    public String productOfferFrom;

    @Expose
    @SerializedName("product_offer_to")
    public String productOfferTo;

    @Expose
    @SerializedName("share_Link")
    public String shareLink;

    @Expose
    @SerializedName("creation_date")
    public String creationDate;


    public SpecialOfferProperty() {

    }

    protected SpecialOfferProperty(Parcel in) {
        propertyId = in.readString();
        propertyTitle = in.readString();
        description = in.readString();
        weight = in.readString();
        offerTypeName = in.readString();
        cityName = in.readString();
        categoryId = in.readString();
        categoryName = in.readString();
        price = in.readString();
        storeTitle = in.readString();
        storeId = in.readString();
        imageStoragePath = in.readString();
        attachments = in.createStringArrayList();
        propertyPrice = in.readString();
        propertyOfferPrice = in.readString();
        productOfferFrom = in.readString();
        productOfferTo = in.readString();
        shareLink = in.readString();
        creationDate = in.readString();
    }

    public static final Creator<SpecialOfferProperty> CREATOR = new Creator<SpecialOfferProperty>() {
        @Override
        public SpecialOfferProperty createFromParcel(Parcel in) {
            return new SpecialOfferProperty(in);
        }

        @Override
        public SpecialOfferProperty[] newArray(int size) {
            return new SpecialOfferProperty[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(propertyId);
        dest.writeString(propertyTitle);
        dest.writeString(description);
        dest.writeString(weight);
        dest.writeString(offerTypeName);
        dest.writeString(cityName);
        dest.writeString(categoryId);
        dest.writeString(categoryName);
        dest.writeString(price);
        dest.writeString(storeTitle);
        dest.writeString(storeId);
        dest.writeString(imageStoragePath);
        dest.writeStringList(attachments);
        dest.writeString(propertyPrice);
        dest.writeString(propertyOfferPrice);
        dest.writeString(productOfferFrom);
        dest.writeString(productOfferTo);
        dest.writeString(shareLink);
        dest.writeString(creationDate);
    }

    public static SpecialOfferProperty fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, SpecialOfferProperty.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "GSON==>" + e.getMessage());
            return null;
        }
    }
}
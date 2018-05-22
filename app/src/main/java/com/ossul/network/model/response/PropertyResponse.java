package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PropertyResponse extends BaseResponse {
    @Expose
    @SerializedName("list_id")
    public String listId;
    @Expose
    @SerializedName("property_id")
    public String propertyId;
    @Expose
    @SerializedName("property_title")
    public String propertyTitle;
    @Expose
    @SerializedName("price")
    public String price;

    @Expose
    @SerializedName("address")
    public String address;
    @Expose
    @SerializedName("weight")
    public String weight;

    @Expose
    @SerializedName("property_quantity")
    public String propertyQuantity;

    @Expose
    @SerializedName("image_path")
    public String imagePath;

    public PropertyResponse() {

    }

    public static PropertyResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, PropertyResponse.class);
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
}

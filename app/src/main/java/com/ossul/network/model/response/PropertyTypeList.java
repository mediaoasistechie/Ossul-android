package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PropertyTypeList extends BaseResponse {
    @Expose
    @SerializedName("property_type_id")
    public String propertyTypeId;

    @Expose
    @SerializedName("property_type_name")
    public String propertyTypeName;


    public PropertyTypeList() {

    }


    public static PropertyTypeList fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, PropertyTypeList.class);
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

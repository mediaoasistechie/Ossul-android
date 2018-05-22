package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PropertyFields extends BaseResponse {
    @Expose
    @SerializedName("id")
    public String id;

    @Expose
    @SerializedName("field")
    public String field;

    @Expose
    @SerializedName("label")
    public String label;

    @Expose
    @SerializedName("visible")
    public int visible;


    public PropertyFields() {

    }


    public static PropertyFields fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, PropertyFields.class);
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

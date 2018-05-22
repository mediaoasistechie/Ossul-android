package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryList extends BaseResponse {
    @Expose
    @SerializedName("category_id")
    public String categoryId;

    @Expose
    @SerializedName("category_name")
    public String categoryName;

    public boolean isChecked;


    public CategoryList() {

    }


    public static CategoryList fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, CategoryList.class);
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

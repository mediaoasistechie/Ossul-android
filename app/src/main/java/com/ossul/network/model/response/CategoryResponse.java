package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryResponse extends BaseResponse {
    @Expose
    @SerializedName("list_id")
    public String listId;
    @Expose
    @SerializedName("category_id")
    public String categoryId;
    @Expose
    @SerializedName("category_name")
    public String categoryName;
    @Expose
    @SerializedName("image_path")
    public String imagePath;


    public CategoryResponse() {

    }

    public static CategoryResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, CategoryResponse.class);
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

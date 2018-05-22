package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetCategoryListResponse extends BaseResponse {
    @Expose
    @SerializedName("data")
    public ArrayList<CategoryList> data = new ArrayList<>();


    public GetCategoryListResponse() {

    }


    public static GetCategoryListResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, GetCategoryListResponse.class);
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

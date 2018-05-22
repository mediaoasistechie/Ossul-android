package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InsertPropertyResponse extends BaseResponse {
    @Expose
    @SerializedName("data")
    public String data;

    public InsertPropertyResponse() {

    }


    public static InsertPropertyResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, InsertPropertyResponse.class);
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

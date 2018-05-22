package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SubmitOfferParentResponse extends BaseResponse {
    @Expose
    @SerializedName("data")
    public ArrayList<SubmitOfferResponse> data = new ArrayList<>();


    public SubmitOfferParentResponse() {

    }


    public static SubmitOfferParentResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, SubmitOfferParentResponse.class);
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

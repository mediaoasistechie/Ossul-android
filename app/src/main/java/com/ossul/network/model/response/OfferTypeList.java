package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OfferTypeList extends BaseResponse {
    @Expose
    @SerializedName("offer_type_id")
    public String offerTypeId;

    @Expose
    @SerializedName("offer_type_name")
    public String offerTypeName;


    public OfferTypeList() {

    }


    public static OfferTypeList fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, OfferTypeList.class);
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

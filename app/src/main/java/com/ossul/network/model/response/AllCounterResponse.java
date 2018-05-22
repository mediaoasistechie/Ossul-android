package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllCounterResponse extends BaseResponse {

    @Expose
    @SerializedName("data")
    public Data data = new Data();

    public AllCounterResponse() {

    }

    public static AllCounterResponse fromJson(String json) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, AllCounterResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public class Data {

        @Expose
        @SerializedName("userReceivedListCount")
        public String userReceivedListCount;


        @Expose
        @SerializedName("receivedOfferCount")
        public String receivedOfferCount;

        @Expose
        @SerializedName("sentOfferCount")
        public String sentOfferCount;

        @Expose
        @SerializedName("userOwnListCount")
        public String userOwnListCount;
    }
}

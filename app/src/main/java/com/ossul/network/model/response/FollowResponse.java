package com.ossul.network.model.response;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FollowResponse extends BaseResponse {

    @Expose
    @SerializedName("data")
    public Data data = new Data();

    public FollowResponse() {

    }

    public static FollowResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, FollowResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "GSON==>" + e.getMessage());
            return null;
        }
    }

    public class Data {
        @Expose
        @SerializedName("isFollowing")
        public boolean isFollowing;
    }
}

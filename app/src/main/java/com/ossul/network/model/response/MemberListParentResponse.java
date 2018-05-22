package com.ossul.network.model.response;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MemberListParentResponse extends BaseResponse {
    @Expose
    @SerializedName("data")
    public ArrayList<MemberListResponse> data = new ArrayList<>();

    public MemberListParentResponse() {

    }

    public static MemberListParentResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, MemberListParentResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "GSON==>" + e.getMessage());
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

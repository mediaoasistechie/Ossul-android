package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MemberListResponse extends BaseResponse {
    @Expose
    @SerializedName("user_id")
    public String userId;
    @Expose
    @SerializedName("email")
    public String email;
    @Expose
    @SerializedName("displayname")
    public String displayName;
    @Expose
    @SerializedName("phone")
    public String phone;
    @Expose
    @SerializedName("user_image")
    public String userImage;

    public boolean isSelected=false;

    public MemberListResponse() {

    }

    public static MemberListResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, MemberListResponse.class);
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

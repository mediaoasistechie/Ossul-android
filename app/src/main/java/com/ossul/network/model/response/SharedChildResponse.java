package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SharedChildResponse extends BaseResponse {
    @Expose
    @SerializedName("share_id")
    public String shareId;
    @Expose
    @SerializedName("referrer_id")
    public String referrerId;
    @Expose
    @SerializedName("referred_id")
    public String referredId;
    @Expose
    @SerializedName("list_id")
    public String listId;
    @Expose
    @SerializedName("list_name")
    public String listName;
    @Expose
    @SerializedName("list_img_path")
    public String listImgPath;
    @Expose
    @SerializedName("list_property_count")
    public String listPropertyCount;
    @Expose
    @SerializedName("referred_by")
    public String referredBy;

    public SharedChildResponse() {

    }

    public static SharedChildResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, SharedChildResponse.class);
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

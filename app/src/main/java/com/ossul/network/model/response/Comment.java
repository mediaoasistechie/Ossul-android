package com.ossul.network.model.response;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * * on 17/11/15.
 */
public class Comment {
    @SerializedName("comment_id")
    @Expose
    public long commentId;
    @SerializedName("user_id")
    @Expose
    public long userId;
    @SerializedName("offer_id")
    @Expose
    public long offerId;
    @SerializedName("comment")
    @Expose
    public String commentText;
    @SerializedName("creation_date")
    @Expose
    public String creationDate;
    @SerializedName("modified_date")
    @Expose
    public String modifiedDate;
    @SerializedName("commented_by")
    @Expose
    public String commentedBy;


    public static Comment fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, Comment.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "GSON==>" + e.getMessage());
            return null;
        }
    }
}

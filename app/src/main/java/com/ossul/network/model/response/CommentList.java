package com.ossul.network.model.response;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * * on 17/11/15.
 */
public class CommentList extends BaseResponse {

    @SerializedName("data")
    @Expose
    public ArrayList<Comment> list;

    public static CommentList fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, CommentList.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "GSON==>" + e.getMessage());
            return null;
        }
    }
}

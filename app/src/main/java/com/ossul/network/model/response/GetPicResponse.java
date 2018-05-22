package com.ossul.network.model.response;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetPicResponse extends BaseResponse {

    @Expose
    @SerializedName("data")
    public Data data = new Data();

    public GetPicResponse() {

    }

    public class Data {
        @Expose
        @SerializedName("imagePath")
        public String imagePath;
    }

    public static GetPicResponse fromJson(String json) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, GetPicResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "GSON==>" + e.getMessage());
            return null;
        }
    }


}

package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateECatalougeResponse extends BaseResponse {
    @Expose
    @SerializedName("list_id")
    public String listId;

    public CreateECatalougeResponse() {

    }

    public static CreateECatalougeResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, CreateECatalougeResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

package com.ossul.network.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Rajan Tiwari
 */
public class NetworkModel {

    public String getJsonBody() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);

    }
}

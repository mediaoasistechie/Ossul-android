package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetSubmitRequestData extends BaseResponse {
    @Expose
    @SerializedName("data")
    public Data data = new Data();


    public GetSubmitRequestData() {

    }

    public static GetSubmitRequestData fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, GetSubmitRequestData.class);
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

    public class Data {

        @Expose
        @SerializedName("country_list")
        public ArrayList<CountryList> countryList = new ArrayList<>();

        @Expose
        @SerializedName("zonetype_list")
        public ArrayList<ZoneType> zoneTypeList = new ArrayList<>();

        @Expose
        @SerializedName("saletype_list")
        public ArrayList<SaleType> saleTypeList = new ArrayList<>();
        @Expose
        @SerializedName("propertytype_list")
        public ArrayList<PropertyType> propertyTypeList = new ArrayList<>();

        public class CountryList {

            @Expose
            @SerializedName("country_id")
            public String countryId;

            @Expose
            @SerializedName("country_name")
            public String countryName;
            @Expose
            @SerializedName("status")
            public String status;


        }

        public class ZoneType {

            @Expose
            @SerializedName("zone_type_id")
            public String zoneTypeId;

            @Expose
            @SerializedName("zone_type_name")
            public String zoneTypeName;
            @Expose
            @SerializedName("status")
            public String status;


            public boolean isChecked;

        }

        public class SaleType {
            @Expose
            @SerializedName("sale_type_id")
            public String saleTypeId;

            @Expose
            @SerializedName("sale_type_name")
            public String saleTypeName;
            @Expose
            @SerializedName("status")
            public String status;

        }

        public class PropertyType {

            @Expose
            @SerializedName("property_type_id")
            public String propertyTypeId;

            @Expose
            @SerializedName("property_type_name")
            public String propertyTypeName;
            @Expose
            @SerializedName("status")
            public String status;
        }
    }

    /*creation_date,updation_date*/
}

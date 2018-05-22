package com.ossul.network.model.response;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PropertyDetailsResponse extends BaseResponse {
    @Expose
    @SerializedName("data")
    public Data data = new Data();


    public PropertyDetailsResponse() {

    }

    public static PropertyDetailsResponse fromJson(String json) {
        try {
            Gson gson = new Gson();

            return gson.fromJson(json, PropertyDetailsResponse.class);
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
        @SerializedName("property_id")
        public String propertyId;
        @Expose
        @SerializedName("store_id")
        public String storeId;
        @Expose
        @SerializedName("property_title")
        public String propertyTitle;
        @Expose
        @SerializedName("price")
        public String price;
        @Expose
        @SerializedName("owner_id")
        public String ownerId;
        @Expose
        @SerializedName("category_id")
        public String categoryId;
        @Expose
        @SerializedName("area")
        public String area;
        @Expose
        @SerializedName("built_area")
        public String builtArea;
        @Expose
        @SerializedName("description")
        public String description;
        @Expose
        @SerializedName("neighborhood")
        public String neighborhood;
        @Expose
        @SerializedName("phone_no")
        public String phoneNo;
        @Expose
        @SerializedName("address")
        public String address;
        @Expose
        @SerializedName("category_name")
        public String categoryName;
        @Expose
        @SerializedName("city_name")
        public String cityName;

        @Expose
        @SerializedName("office_title")
        public String officeTitle;
        @Expose
        @SerializedName("country_name")
        public String countryName;
        @Expose
        @SerializedName("neighborhood_name")
        public String neighborhoodName;
        @Expose
        @SerializedName("region_name")
        public String regionName;
        @Expose
        @SerializedName("offer_type_name")
        public String offerTypeName;
        @Expose
        @SerializedName("storage_path")
        public String storagePath;
        @Expose
        @SerializedName("property_type_name")
        public String propertyTypeName;
        @Expose
        @SerializedName("images")
        public JsonArray images;
    }
}

/*
{
  "success": true,
  "data": {
    "property_id": "1",
    "store_id": "1",
    "barcode": "",
    "property_title": "Commercial Building for rent",
    "weight": null,
    "price": "95000",
    "photo_id": "215",
    "owner_id": "79",
    "category_id": "10",
    "default_dailylist": "1",
    "default_weeklylist": "0",
    "default_monthlylist": "1",
    "status": "1",
    "creation_date": "0000-00-00 00:00:00",
    "modified_date": "2017-01-03 07:06:16",
    "city_id": "10",
    "region_id": "5",
    "facility_ids": "",
    "property_type_id": "11",
    "offer_type_id": "4",
    "property_area_id": null,
    "location_id": "175",
    "area": "",
    "built_area": "",
    "no_of_rooms": "0",
    "no_of_bedrooms": "0",
    "no_of_bathrooms": "0",
    "description": "Commercial Building for rent",
    "neighborhood": "Olaya",
    "mobile_show": "0",
    "is_verified": "1",
    "is_special_offer": "0",
    "product_offer_from": null,
    "product_offer_to": null,
    "offer_price": "",
    "phone_no": "",
    "address": "OLAYA 11393",
    "category_name": "Commercial",
    "city_name": "Riyadh",
    "office_title": "Al Saedan",
    "country_name": "Saudi Arabia",
    "neighborhood_name": null,
    "neighborhood_id": null,
    "region_name": "North",
    "offer_type_name": "For Rent",
    "property_type_name": "Building",
    "property_area_name": null,
    "storage_path": "http://www.usoool.com/usoool/uploads/product_1483445176.jpg",
    "facilities": [],
    "images": [
      {
        "storage_path": "http://www.usoool.com/usoool/uploads/product_1483445176.jpg"
      },
      {
        "storage_path": "http://www.usoool.com/usoool/uploads/store_wall_1.jpg"
      },
      {
        "storage_path": "http://www.usoool.com/usoool/uploads/store_1.jpg"
      },
      {
        "storage_path": "http://www.usoool.com/usoool/uploads/product_1483433442.jpg"
      }
    ]
  }
}

*/
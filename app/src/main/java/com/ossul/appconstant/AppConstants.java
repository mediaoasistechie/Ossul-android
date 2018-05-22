package com.ossul.appconstant;

import android.content.Intent;

import com.ossul.network.model.response.PropertyFields;

import java.util.ArrayList;

/**
 * @author Rajan Tiwari
 */
public class AppConstants {
    public static final String METHOD_POST = "post";
    public static final String METHOD_GET = "get";
    public static final int REQ_CODE_CAMERA_IMAGE_PICK = 1;
    public static final int REQ_CODE_GALLERY_IMAGE_PICK = 0;
    public static final String DEFAULT = "default";
    public static final String USER = "user";
    public static final int CREATE_ECATALOGUE = 3;
    public static final String RESIDENTIAL = "residentail";
    public static final String COMMERCIAL = "commercial";
    public static final String LAND = "land";
    public static final int SHARED_SUCCESS = 4;
    public static final int INTENT_PROPERTY_CREATED = 5;
    public static final int LOCATION = 6;
    public static final String NOTIFICATION_ID = "notificationId";
    public static final String ID = "uniqueId";
    public static final String ITEM_ID = "itemId";
    public static final String ITEM_TYPE = "itemType";
    public static ArrayList<String> contactList = new ArrayList<>();
    public static ArrayList<PropertyFields> mPropertyFields = new ArrayList<>();
    public static Intent intentPath = new Intent();
    public static String OK="OK";
    public static int APP_UPDATE=10;
}

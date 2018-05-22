package com.ossul.apppreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author Rajan enum for SharedPreference of application because it will
 *         use through out the app.
 */
public class AppPreferences {
    private static final String SHARED_PREFERENCE_NAME = "OssulAppPreference";
    private static final String DEVICE_TOKEN = "device_token";
    private static final String TOKEN = "token";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String USER_IMAGE_URL = "user_image_path";
    private static final String WALL_PIC_URL = "wall_image_path";
    private static AppPreferences mAppPreferences;
    private SharedPreferences mPreferences;
    private Editor mEditor;
    private String profileWallPic;

    private AppPreferences(Context context) {
        mPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static AppPreferences get() {
        return mAppPreferences;
    }

    /**
     * private constructor for singleton class
     *
     * @param context
     */
    public static void initAppPreferences(Context context) {
        mAppPreferences = new AppPreferences(context);
    }

    public boolean isLogin() {
        return mPreferences.getBoolean("is_login", false);
    }

    public void setLogin(boolean value) {
        mEditor.putBoolean("is_login", value);
        mEditor.commit();
    }

    /**
     * Used to clear all the values stored in preferences
     *
     * @return void
     */
    public void clearPreferences() {
        mEditor.clear();
        mEditor.commit();
    }

    public String getToken() {
        return mPreferences.getString(TOKEN, "");
    }

    public void saveToken(String token) {
        mEditor.putString(TOKEN, token).commit();
    }

    public String getUserDetailPref() {
        return mPreferences.getString("USER_DETAIL", "");

    }

    public void setUserDetailPref(String s) {
        mEditor.putString("USER_DETAIL", s).commit();
    }

    public String getLabels() {
        return mPreferences.getString("LABELS", "{}");

    }

    public void setLabels(String s) {
        mEditor.putString("LABELS", s).commit();
    }


    public String getUserAvtar() {
        return mPreferences.getString(USER_IMAGE_URL, "");
    }

    public void setUserAvtar(String imageUrl) {
        mEditor.putString(USER_IMAGE_URL, imageUrl).commit();
    }

    public String getProfileWallPic() {
        return mPreferences.getString(WALL_PIC_URL, "");
    }

    public void setProfileWallPic(String profileWallPic) {
        mEditor.putString(WALL_PIC_URL, profileWallPic).commit();
    }

    public String getLat() {
        return mPreferences.getString(LATITUDE, "0.0");
    }

    public void setLat(String lat) {
        mEditor.putString(LATITUDE, lat);
        mEditor.commit();
    }

    public String getLongt() {
        return mPreferences.getString(LONGITUDE, "");
    }

    public void setLongt(String longt) {
        mEditor.putString(LONGITUDE, longt);
        mEditor.commit();
    }

    public String getGcmId() {
        return mPreferences.getString(DEVICE_TOKEN, "");
    }

    public void setGcmId(String refreshedToken) {
        mEditor.putString(DEVICE_TOKEN, refreshedToken);
        mEditor.commit();
    }

    public String getPrefLang() {
        return mPreferences.getString("PREF_LANG", "en");
    }

    public void setPrefLang(String lang) {
        mEditor.putString("PREF_LANG", lang);
        mEditor.commit();
    }

    public String getUserId() {
        return mPreferences.getString("user_id", "");
    }

    public void setUserId(String userId) {
        mEditor.putString("user_id", userId);
        mEditor.commit();
    }
}

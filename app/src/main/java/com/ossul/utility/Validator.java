package com.ossul.utility;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * *Validator class to check some validation.
 */
public class Validator {
    //check if enter email is valid or not
    public static boolean validEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    //checks if string is empty
    public static boolean isEmptyString(String text) {
        return text == null || text.trim().length() == 0;
    }
    public static boolean isProbablyArabic(String s) {
        for (int i = 0; i < s.length();) {
            int c = s.codePointAt(i);
            if (c >= 0x0600 && c <= 0x06E0)
                return true;
            i += Character.charCount(c);
        }
        return false;
    }
    //checks if the internet connection is working
    public static boolean isConnectedToInternet(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION};
            if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                ConnectivityManager conectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo[] ntwrkInfoList = conectivityManager.getAllNetworkInfo();

                for (NetworkInfo ntwrkInfo : ntwrkInfoList) {
                    if ((ntwrkInfo.getType() == ConnectivityManager.TYPE_WIFI || ntwrkInfo
                            .getType() == ConnectivityManager.TYPE_MOBILE)
                            && ntwrkInfo.isConnected()) {
                        return true;
                    }
                }
            } else
                return true;
        } else {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            for (NetworkInfo info : networkInfo) {
                if ((info.getType() == ConnectivityManager.TYPE_WIFI || info
                        .getType() == ConnectivityManager.TYPE_MOBILE)
                        && info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
}

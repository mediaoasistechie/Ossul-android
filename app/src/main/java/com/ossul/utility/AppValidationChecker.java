package com.ossul.utility;

import android.text.TextUtils;

import com.ossul.R;
import com.ossul.activities.BaseActivity;
import com.ossul.appconstant.ErrorConstant;
import com.ossul.interfaces.IValidationResult;


public class AppValidationChecker {

    /**
     * use to validate email and password for sign in
     *
     * @param email
     * @param password
     * @param iValidationResult
     */
    public static void validateLoginApi(String email, String password, IValidationResult iValidationResult) {
        if (TextUtils.isEmpty(email)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_USER_NAME_EMPTY, R.string.error_enter_email);
        } else if (!Validator.validEmail(email)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_EMAIL_INVALID, R.string.error_enter_valid_email);
        } else if (TextUtils.isEmpty(password)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_PASSWORD_EMPTY, R.string.error_enter_password);
        }/* else if (password.length() < 3 || password.length() > 12) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_PASSWORD_LENGTH, R.string.error_password_length);
        }*/ else {
            iValidationResult.onValidationSuccess();
        }
    }

    public static void validateSignUpApi(String name, String email, String password, String countryCode, String mobile, IValidationResult iValidationResult) {
        if (TextUtils.isEmpty(name)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_USER_NAME_EMPTY, R.string.error_enter_name);
        } else if (TextUtils.isEmpty(email)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_EMAIL_EMPTY, R.string.error_enter_email);
        } else if (!AppUtilsMethod.isEmailValid(email)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_EMAIL_INVALID, R.string.error_enter_valid_email);
        } else if (TextUtils.isEmpty(password)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_PASSWORD_EMPTY, R.string.error_enter_password);
        } /*else if (password.length() < 3 || password.length() > 12) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_PASSWORD_LENGTH, R.string.error_password_length);
        } else if (TextUtils.isEmpty(countryCode) || countryCode.equalsIgnoreCase("+")) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_COUNTRY_CODE, R.string.error_enter_country_code);
        } */ else if (TextUtils.isEmpty(mobile)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_MOBILE_EMPTY, R.string.error_enter_mobile);
        }/* else if (mobile.trim().length() > 15 || mobile.trim().length() < 10) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_MOBILE_EMPTY, R.string.error_enter_mobile);
        }*/
        /* else if () {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_VALID_MOBILE, R.string.error_enter_valid_mobile);
        }*/
        else {
            iValidationResult.onValidationSuccess();
        }
    }

    public static void validateUpdateProfileApi(String name, String email, String phone, String address, String countryCode, IValidationResult iValidationResult) {
        if (TextUtils.isEmpty(name)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_USER_NAME_EMPTY, R.string.error_enter_name);
        } else if (TextUtils.isEmpty(email)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_EMAIL_EMPTY, R.string.error_enter_email);
        } else if (!AppUtilsMethod.isEmailValid(email)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_EMAIL_INVALID, R.string.error_enter_valid_email);
        } else if (TextUtils.isEmpty(phone)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_MOBILE_EMPTY, R.string.error_enter_valid_mobile);
        }/* else if (TextUtils.isEmpty(countryCode)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_COUNTRY_CODE, R.string.error_enter_country_code);
        } */else if (TextUtils.isEmpty(address)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_ADDRESS_EMPTY, R.string.error_enter_address);
        } else {
            iValidationResult.onValidationSuccess();
        }
    }

    public static void validateResetPassApi(String email, IValidationResult iValidationResult) {
        if (TextUtils.isEmpty(email)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_EMAIL_EMPTY, R.string.error_enter_email);
        } else if (!AppUtilsMethod.isEmailValid(email)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_EMAIL_INVALID, R.string.error_enter_valid_email);
        } else {
            iValidationResult.onValidationSuccess();
        }
    }

    public static void validateChangePassApi(String password, String repeatPassword, IValidationResult iValidationResult) {
        if (TextUtils.isEmpty(password)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_PASSWORD_EMPTY, R.string.error_enter_password);
        }/* else if (password.length() < 3 || password.length() > 12) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_PASSWORD_LENGTH, R.string.error_password_length);
        } */ else if (TextUtils.isEmpty(repeatPassword)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_REPEAT_PASSWORD_EMPTY, R.string.error_enter_repeat_password);
        } else if (!password.equals(repeatPassword)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_PASSWORD_MISMATCH, R.string.error_password_mismatch);
        } else {
            iValidationResult.onValidationSuccess();
        }
    }

    public static void validateCreatePropertyApi(BaseActivity activity, String title, String address, String neighbourhood, String city, String realEstateOffice, String region, String propertyType, String category, String offerType, String price, String description, IValidationResult iValidationResult) {
       /* if (Validator.isEmptyString(title)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_TITLE_EMPTY, R.string.error_enter_title);
        } else if (Validator.isEmptyString(address)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_ADDRESS_EMPTY, R.string.error_enter_location);
        } else if (Validator.isEmptyString(neighbourhood)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_NEIGHBOURHOOD_EMPTY, R.string.error_enter_neighbourhood);
        } else if (Validator.isEmptyString(city) || city.equalsIgnoreCase("city")) {
            CustomDialogFragment.getInstance(activity, null, activity.getResources().getString(R.string.error_select_city), null, "OK", null, 1, null);
        } else if (Validator.isEmptyString(realEstateOffice) || realEstateOffice.equalsIgnoreCase("Real Estate office")) {
            CustomDialogFragment.getInstance(activity, null, activity.getResources().getString(R.string.error_select_office), null, "OK", null, 1, null);
        } else if (Validator.isEmptyString(region) || region.equalsIgnoreCase("Region")) {
            CustomDialogFragment.getInstance(activity, null, activity.getResources().getString(R.string.error_select_region), null, "OK", null, 1, null);
        } else if (Validator.isEmptyString(propertyType) || propertyType.equalsIgnoreCase("Property Type")) {
            CustomDialogFragment.getInstance(activity, null, activity.getResources().getString(R.string.error_select_property_type), null, "OK", null, 1, null);
        } else if (Validator.isEmptyString(category) || category.equalsIgnoreCase("Category")) {
            CustomDialogFragment.getInstance(activity, null, activity.getResources().getString(R.string.error_select_category), null, "OK", null, 1, null);
        } else if (Validator.isEmptyString(offerType) || offerType.equalsIgnoreCase("Offer Type")) {
            CustomDialogFragment.getInstance(activity, null, activity.getResources().getString(R.string.error_select_offer_type), null, "OK", null, 1, null);
        } else if (Validator.isEmptyString(price)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_PRICE_EMPTY, R.string.error_enter_price);
        } else if (Validator.isEmptyString(description)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_DESCRIPTION_EMPTY, R.string.error_enter_description);
        } else*/
        {
            iValidationResult.onValidationSuccess();
        }
    }

    public static void validateSubmitRequestApi(BaseActivity activity, String country, String city, String saleType, String propertyType, long minPrice, long maxPrice, String description, IValidationResult iValidationResult) {
        /*if (Validator.isEmptyString(country) || country.equalsIgnoreCase("Country")) {
            CustomDialogFragment.getInstance(activity, null, activity.getResources().getString(R.string.error_select_country), null, "OK", null, 1, null);
        } else if (Validator.isEmptyString(city) || city.equalsIgnoreCase("city")) {
            CustomDialogFragment.getInstance(activity, null, activity.getResources().getString(R.string.error_select_city), null, "OK", null, 1, null);
        } else if (Validator.isEmptyString(saleType) || saleType.equalsIgnoreCase("for sale/rent")) {
            CustomDialogFragment.getInstance(activity, null, activity.getResources().getString(R.string.error_select_sale_type), null, "OK", null, 1, null);
        } else if (Validator.isEmptyString(propertyType) || propertyType.equalsIgnoreCase("Property Type")) {
            CustomDialogFragment.getInstance(activity, null, activity.getResources().getString(R.string.error_select_property_type), null, "OK", null, 1, null);
        }*//* else if (Validator.isEmptyString(minPrice)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_MIN_PRICE_EMPTY, R.string.error_enter_min_price);
        } else if (Validator.isEmptyString(maxPrice)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_MAX_PRICE_EMPTY, R.string.error_enter_max_price);
        }*//*  if (Validator.isEmptyString(description)) {
            iValidationResult.onValidationError(ErrorConstant.ERROR_TYPE_DESCRIPTION_EMPTY, R.string.error_enter_description);
        } else*/
        {
            iValidationResult.onValidationSuccess();
        }
    }
}

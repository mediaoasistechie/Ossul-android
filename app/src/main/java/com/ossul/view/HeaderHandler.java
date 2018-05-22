package com.ossul.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ossul.R;
import com.ossul.apppreferences.AppPreferences;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.Validator;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * * 28/11/15.
 */
public class HeaderHandler {

    public static final String KEY_EMPTY = "";
    public static final String MY_REQUEST = "My Request";
    public static final String SUBMIT_REQUEST = "Submit Request";
    public static final String SUBMIT_OFFER = "Submit Offer";
    public static final String SENT_REQUEST = "Sent Offer";
    public static final String RECEIVED_REQUEST = "Received Offer";
    public static final String EDIT_REQUEST = "Edit Request";
    ImageView mMenuIV;
    ImageView mBackIV;
    ImageView mAppLogoIV;
    TextView mHeaderTV;
    private JSONObject jsonObject = new JSONObject();
    private View view;

    public HeaderHandler(View view) {
        this.view = view;
        mMenuIV = (ImageView) view.findViewById(R.id.action_menu);
        mBackIV = (ImageView) view.findViewById(R.id.iv_back_header);
        mHeaderTV = (TextView) view.findViewById(R.id.tv_header);
        mAppLogoIV = (ImageView) view.findViewById(R.id.iv_app_icon);
        if (!Validator.isEmptyString(AppPreferences.get().getLabels())) {
            try {
                jsonObject = new JSONObject(AppPreferences.get().getLabels());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void setHeaderVisibility(String headerTitle) {
        try {
            jsonObject = new JSONObject(AppPreferences.get().getLabels());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch ("" + headerTitle) {
            case SUBMIT_REQUEST:
                view.setVisibility(View.VISIBLE);

                mMenuIV.setVisibility(View.VISIBLE);
                mBackIV.setVisibility(View.GONE);
                mAppLogoIV.setVisibility(View.GONE);
                mHeaderTV.setVisibility(View.VISIBLE);
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, SUBMIT_REQUEST)))
                    mHeaderTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, SUBMIT_REQUEST));
                else
                    mHeaderTV.setText(headerTitle);
                break;
            case SUBMIT_OFFER:
                view.setVisibility(View.VISIBLE);

                mMenuIV.setVisibility(View.VISIBLE);
                mBackIV.setVisibility(View.GONE);
                mAppLogoIV.setVisibility(View.GONE);
                mHeaderTV.setVisibility(View.VISIBLE);
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Submit Offer")))
                    mHeaderTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Submit Offer"));
                else
                    mHeaderTV.setText(headerTitle);
                break;

            case MY_REQUEST:
                view.setVisibility(View.VISIBLE);
                mMenuIV.setVisibility(View.VISIBLE);
                mBackIV.setVisibility(View.GONE);
                mAppLogoIV.setVisibility(View.GONE);
                mHeaderTV.setVisibility(View.VISIBLE);
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "My request")))
                    mHeaderTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "My request"));
                else
                    mHeaderTV.setText(headerTitle);
                break;

            case SENT_REQUEST:
                view.setVisibility(View.VISIBLE);

                mMenuIV.setVisibility(View.VISIBLE);
                mBackIV.setVisibility(View.GONE);
                mAppLogoIV.setVisibility(View.GONE);
                mHeaderTV.setVisibility(View.VISIBLE);
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Sent Offer")))
                    mHeaderTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Sent Offer"));
                else
                    mHeaderTV.setText(headerTitle);
                break;

            case RECEIVED_REQUEST:
                mMenuIV.setVisibility(View.VISIBLE);
                mBackIV.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
                mAppLogoIV.setVisibility(View.GONE);
                mHeaderTV.setVisibility(View.VISIBLE);
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Received Offers")))
                    mHeaderTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Received Offers"));
                else
                    mHeaderTV.setText(headerTitle);
                break;
            case EDIT_REQUEST:
                mMenuIV.setVisibility(View.VISIBLE);
                mBackIV.setVisibility(View.GONE);
                mAppLogoIV.setVisibility(View.GONE);
                mHeaderTV.setVisibility(View.VISIBLE);
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, EDIT_REQUEST)))
                    mHeaderTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, EDIT_REQUEST));
                else
                    mHeaderTV.setText(headerTitle);
                view.setVisibility(View.VISIBLE);
                break;
            default:
                if (view != null)
                    view.setVisibility(View.VISIBLE);
                mMenuIV.setVisibility(View.VISIBLE);
                mBackIV.setVisibility(View.GONE);
                mAppLogoIV.setVisibility(View.VISIBLE);
                mHeaderTV.setVisibility(View.GONE);
                mHeaderTV.setText("");
                break;
        }
    }


}

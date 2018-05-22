package com.ossul.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ossul.R;
import com.ossul.adapters.ImagesAdapter;
import com.ossul.adapters.ViewPageAdapter;
import com.ossul.appconstant.AppConstants;
import com.ossul.apppreferences.AppPreferences;
import com.ossul.dialog.CustomDialogFragment;
import com.ossul.dialog.DialogManager;
import com.ossul.interfaces.INetworkEvent;
import com.ossul.listener.OnItemClickListener;
import com.ossul.network.NetworkService;
import com.ossul.network.constants.AppNetworkConstants;
import com.ossul.network.model.NetworkModel;
import com.ossul.network.model.response.GetSpecialPropertyParentRes;
import com.ossul.network.model.response.SpecialOfferProperty;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.Validator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PropertyDetailsActivity extends BaseActivity implements View.OnClickListener, INetworkEvent {
    private ViewPager viewPager;
    private TextView mTitleTV, mDescriptionTV, mPublishedByTV, mPublishedDateTV;
    private JSONObject jsonObject = new JSONObject();
    private TextView mDateTV;
    private RecyclerView mImagesRV;
    private TextView mByTV;
    private TextView mHeaderTitleTV;
    private String TAG = PropertyDetailsActivity.class.getSimpleName();
    private DialogManager mProgressDialog;
    private String API_GET_REAL_ESTATE_NEWS_BY_ID = "";
    private TextView tvOfferedPrice, tvOffered;
    private String API_VIEW_OFFER;

    public void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup) child.getParent();
        if (null != parent) {
            parent.removeView(child);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    parent.addView(child);
                }
            }, 500);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        initData();

    }

    protected void initData() {
        initViews();
        initVariables();
    }

    @Override
    protected void initViews() {
        mHeaderTitleTV = (TextView) findViewById(R.id.tv_header_title);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        mImagesRV = (RecyclerView) findViewById(R.id.rv_images);

        mTitleTV = (TextView) findViewById(R.id.tv_title);
        mDescriptionTV = (TextView) findViewById(R.id.tv_description);
        mDescriptionTV = (TextView) findViewById(R.id.tv_description);
        mPublishedByTV = (TextView) findViewById(R.id.tv_published);
        mByTV = (TextView) findViewById(R.id.tv_by);
        mPublishedDateTV = (TextView) findViewById(R.id.tv_published_date);
        mDateTV = (TextView) findViewById(R.id.tv_date);
        findViewById(R.id.iv_close).setOnClickListener(this);
        tvOffered = (TextView) findViewById(R.id.tv_offered);
        tvOfferedPrice = (TextView) findViewById(R.id.tv_offered_price);
        findViewById(R.id.rl_offer).setVisibility(View.VISIBLE);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                sendViewToBack(mImagesRV);
                sendViewToBack(mDescriptionTV);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initVariables() {
        mProgressDialog = new DialogManager(this);
        final LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mImagesRV.setLayoutManager(layout);
        mImagesRV.setHasFixedSize(true);

        if (!Validator.isEmptyString(AppPreferences.get().getLabels())) {
            try {
                jsonObject = new JSONObject(AppPreferences.get().getLabels());
                if (jsonObject != null && jsonObject.length() != 0) {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (getIntent() != null && getIntent().getBundleExtra("bundle") != null && getIntent().getBundleExtra("bundle").getParcelable("data") != null) {
            if (getIntent().getStringExtra("for").equalsIgnoreCase("property")) {
                final ArrayList<String> images = new ArrayList<>();
                if (getIntent().getStringArrayListExtra("images") != null && getIntent().getStringArrayListExtra("images").size() > 0) {
                    images.addAll(getIntent().getStringArrayListExtra("images"));
                }
                final SpecialOfferProperty realEstateNewsList = (SpecialOfferProperty) getIntent().getBundleExtra("bundle").getParcelable("data");
                setData(realEstateNewsList, images);
            }
        }
        if (getIntent() != null && !Validator.isEmptyString(getIntent().getStringExtra("id"))) {
            if (!Validator.isEmptyString(getIntent().getStringExtra("for")) && getIntent().getStringExtra("for").equalsIgnoreCase("property")) {
                getPropertyById(getIntent().getStringExtra("id"));
            }
        }

    }

    private void setData(SpecialOfferProperty realEstateNewsList, final ArrayList<String> images) {
        if (realEstateNewsList == null)
            return;

        mHeaderTitleTV.setText(realEstateNewsList.propertyTitle);
        mTitleTV.setText(realEstateNewsList.propertyTitle + "");
        if (!Validator.isEmptyString(realEstateNewsList.description)) {
            mDescriptionTV.setVisibility(View.VISIBLE);
            mDescriptionTV.setText(Html.fromHtml(realEstateNewsList.description));
        } else {
            mDescriptionTV.setVisibility(View.GONE);
        }
        String offerPrice = "Offer Price";
        if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Offer Price")))
            offerPrice = (AppUtilsMethod.getValueFromKey(jsonObject, "Offer Price"));
        tvOffered.setText(offerPrice + ": ");

        if (!Validator.isEmptyString(realEstateNewsList.propertyOfferPrice))
            tvOfferedPrice.setText(realEstateNewsList.propertyOfferPrice);


        String publishBy;
        if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Price")))
            publishBy = (AppUtilsMethod.getValueFromKey(jsonObject, "Price"));
        else publishBy = "Price";
        mPublishedByTV.setText(publishBy);

        String publishDate;
        if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Offer till")))
            publishDate = (AppUtilsMethod.getValueFromKey(jsonObject, "Offer till"));
        else
            publishDate = "Offer till";

        mPublishedDateTV.setText(publishDate + ": ");
        mByTV.setText(" :" + realEstateNewsList.price);
        if (!Validator.isEmptyString(realEstateNewsList.creationDate))
            mDateTV.setText(AppUtilsMethod.formattedDate(realEstateNewsList.productOfferTo));

        if (images != null && images.size() > 0) {
            mImagesRV.setVisibility(View.VISIBLE);
            viewPager.setAdapter(new ViewPageAdapter(this, images));
            final ImagesAdapter imagesAdapter = new ImagesAdapter(this, images);
            mImagesRV.setAdapter(imagesAdapter);
            imagesAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if (images.size() > position && !Validator.isEmptyString(images.get(position))) {
                        viewPager.setCurrentItem(position);
                    }
                }
            });
        }
    }

    private void getPropertyById(String offerId) {
        if (Validator.isConnectedToInternet(getApplicationContext())) {
            NetworkModel networkModel = new NetworkModel();
            API_VIEW_OFFER = AppNetworkConstants.BASE_URL + "store/SpecialOffers.php?action=view-offer&offer_id=" + offerId;
            NetworkService service = new NetworkService(API_VIEW_OFFER, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                onBackPressed();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_out_up, R.anim.slide_out_down);
    }

    @Override
    public void onNetworkCallInitiated(String service) {
        if (mProgressDialog != null) {
            mProgressDialog.show();
        }
    }

    @Override
    public void onNetworkCallCompleted(String service, String response) {
        Log.e(TAG, " success " + response);
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        if (service.equals(API_VIEW_OFFER)) {
            GetSpecialPropertyParentRes getSpecialPropertyParentRes = GetSpecialPropertyParentRes.fromJson(response);
            if (getSpecialPropertyParentRes != null && getSpecialPropertyParentRes.data != null && getSpecialPropertyParentRes.data.size() > 0) {
                setData(getSpecialPropertyParentRes.data.get(0), getSpecialPropertyParentRes.data.get(0).attachments);
            }
        }
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();

    }
}

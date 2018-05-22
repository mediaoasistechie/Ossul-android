package com.ossul.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ossul.R;
import com.ossul.activities.BaseActivity;
import com.ossul.activities.PropertyDetailsActivity;
import com.ossul.network.model.response.SpecialOfferProperty;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.Validator;

import org.json.JSONObject;

import java.util.ArrayList;

public class SpecialOfferAdapter extends RecyclerView.Adapter<SpecialOfferAdapter.ViewHolder> {
    private static JSONObject jsonObject;
    private final LayoutInflater inflater;
    private ArrayList<SpecialOfferProperty> mPropertyArrayList;
    private BaseActivity mActivity;

    public SpecialOfferAdapter(BaseActivity activity, ArrayList<SpecialOfferProperty> properties, JSONObject jsonObject) {
        this.mActivity = activity;
        this.mPropertyArrayList = properties;
        this.jsonObject = jsonObject;
        inflater = (LayoutInflater) this.mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public SpecialOfferAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_real_esate_news, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mPropertyArrayList == null)
            return;
        holder.setData(mPropertyArrayList.get(position), mActivity);
    }

    @Override
    public int getItemCount() {
        return mPropertyArrayList != null ? mPropertyArrayList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ArrayList<String> images = new ArrayList<>();
        private TextView mPublishedDateTV;
        private TextView mPublishedByTV;
        private TextView mDescriptionTV;
        private CardView mItemCV;
        private ImageView mImageIV;
        private TextView mTitleTV;
        private TextView tvOffered;
        private TextView tvOfferedPrice;
        private TextView mByTV;
        private TextView mDateTV;
        private TextView mShareTV;


        public ViewHolder(View itemView) {
            super(itemView);

            mItemCV = (CardView) itemView.findViewById(R.id.card_view);
            mImageIV = (ImageView) itemView.findViewById(R.id.iv_real_estate_news);
            mTitleTV = (TextView) itemView.findViewById(R.id.tv_title);
            mDescriptionTV = (TextView) itemView.findViewById(R.id.tv_description);
            mPublishedByTV = (TextView) itemView.findViewById(R.id.tv_published);
            mByTV = (TextView) itemView.findViewById(R.id.tv_by);
            itemView.findViewById(R.id.rl_offer).setVisibility(View.VISIBLE);

            tvOffered = (TextView) itemView.findViewById(R.id.tv_offered);
            tvOfferedPrice = (TextView) itemView.findViewById(R.id.tv_offered_price);

            mPublishedDateTV = (TextView) itemView.findViewById(R.id.tv_published_date);
            mDateTV = (TextView) itemView.findViewById(R.id.tv_date);
            mShareTV = (TextView) itemView.findViewById(R.id.tv_share);
        }


        void setData(final SpecialOfferProperty response, final BaseActivity activity) {
            if (response != null) {
                mTitleTV.setText(response.propertyTitle + "");
                if (!Validator.isEmptyString(response.description)) {
                    mDescriptionTV.setVisibility(View.VISIBLE);
                    mDescriptionTV.setText(Html.fromHtml(response.description));
                } else {
                    mDescriptionTV.setVisibility(View.GONE);
                }

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
                if (!Validator.isEmptyString(response.productOfferTo))
                    mDateTV.setText(AppUtilsMethod.formattedDate(response.productOfferTo));
                mByTV.setText(" :" + response.price);


                String offerPrice = "Offer Price";
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Offer Price")))
                    offerPrice = (AppUtilsMethod.getValueFromKey(jsonObject, "Offer Price"));
                tvOffered.setText(offerPrice + ": ");

                if (!Validator.isEmptyString(response.propertyOfferPrice))
                    tvOfferedPrice.setText(response.propertyOfferPrice);

                if (response.attachments != null && response.attachments.size() > 0) {
                    images.addAll(response.attachments);
                } else if (response.imageStoragePath != null) {

                    images.add(response.imageStoragePath);
                }
                if (images != null && images.size() > 0) {
                    ImageLoader.getInstance().displayImage(images.get(0) + "", mImageIV, new DisplayImageOptions.Builder()
                            .showImageForEmptyUri(R.drawable.color_grey)
                            .showImageOnFail(R.drawable.color_grey)
                            .showStubImage(R.drawable.color_grey)
                            .cacheOnDisc(true)
                            .cacheInMemory(true)
                            .build());
                } else {
                    mImageIV.setVisibility(View.GONE);
                }
                mShareTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            final Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            String shareText = "";
                            String link = "http://www.ossul.com/special-offers/";
                            if (!Validator.isEmptyString(response.propertyTitle)) {
                               /* if (Validator.isProbablyArabic(realEstateNewsList.title)) {
                                    link = "/http://www.ossul.com/economic-news";
                                    link = realEstateNewsList.realEstateNewsId + "/" + realEstateNewsList.title + link;
                                    shareT
                                    ext = link + " :" + realEstateNewsList.title;
                                } else*/
                                {
                                    String strTitle = response.propertyTitle.replaceAll(" ", "-");
                                    if (strTitle != null && strTitle.length() > 20) {
                                        strTitle = strTitle.substring(0, 18);
                                    }
                                    link = link + strTitle + "/" + response.propertyId;
                                    shareText = response.propertyTitle + "\n" + link;
                                }
                            }
                            if (!Validator.isEmptyString(response.shareLink)) {
                                shareText = response.shareLink;
                            }
                            intent.putExtra(Intent.EXTRA_TEXT, shareText);
                            activity.startActivity(Intent.createChooser(intent, "Share news"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                mItemCV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, PropertyDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("data", response);
                        intent.putExtra("for", "property");
                        intent.putExtra("bundle", bundle);
                        intent.putStringArrayListExtra("images", images);
                        activity.startActivityForResult(intent, 10);
                        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

                    }
                });


               /* final RelativeLayout back_dim_layout = (RelativeLayout) activity.findViewById(R.id.back_dim_layout);
                popupMenu = new PopupMenu(activity, itemView, 3);
                if (realEstateNewsList.userId.equalsIgnoreCase(AppPreferences.get().getUserId())) {
                    popupMenu.inflate(R.menu.popup_menu);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                Intent intent = new Intent(activity, SubmitRealEstateNewsActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("data", realEstateNewsList);
                                intent.putExtra("bundle", bundle);
                                activity.startActivityForResult(intent, 101);
                                return true;
                            case R.id.delete:

                                try {
                                    ((RealEstateNewsActivity) activity).deleteNews(realEstateNewsList.realEstateNewsId,getAdapterPosition());
                                } catch (Exception e) {

                                }
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu menu) {
                        back_dim_layout.setVisibility(View.GONE);
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (realEstateNewsList.userId.equalsIgnoreCase(AppPreferences.get().getUserId())) {
                            back_dim_layout.setVisibility(View.VISIBLE);
                            popupMenu.show();
                        }

                        return true;
                    }
                });*/
            }

        }

    }
}

package com.ossul.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ossul.R;
import com.ossul.activities.BaseActivity;
import com.ossul.activities.EconomicNewsActivity;
import com.ossul.activities.NewsDetailsActivity;
import com.ossul.activities.SubmitEconomicNewsActivity;
import com.ossul.apppreferences.AppPreferences;
import com.ossul.network.model.response.EconomicNewsList;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.Validator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;


public class EconomicNewsAdapter extends RecyclerView.Adapter<EconomicNewsAdapter.ViewHolder> {
    private static JSONObject jsonObject;
    private static ArrayList<EconomicNewsList> newsList;
    private BaseActivity mActivity;

    public EconomicNewsAdapter(BaseActivity activity, ArrayList<EconomicNewsList> realEstateNewsList, JSONObject jsonObject) {
        this.mActivity = activity;
        this.jsonObject = jsonObject;
        this.newsList = realEstateNewsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_real_esate_news, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (newsList == null)
            return;
        holder.setData(newsList.get(position), mActivity);
    }

    @Override
    public int getItemCount() {
        return newsList != null ? newsList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final ArrayList<String> images = new ArrayList<>();
        public PopupMenu popupMenu;
        private TextView mPublishedDateTV;
        private TextView mPublishedByTV;
        private TextView mDescriptionTV;
        private CardView mItemCV;
        private ImageView mImageIV;
        private TextView mTitleTV;
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
            mPublishedDateTV = (TextView) itemView.findViewById(R.id.tv_published_date);
            mDateTV = (TextView) itemView.findViewById(R.id.tv_date);
            mShareTV = (TextView) itemView.findViewById(R.id.tv_share);
        }

        void setData(final EconomicNewsList realEstateNewsList, final BaseActivity activity) {
            if (images != null)
                images.clear();
            if (realEstateNewsList != null) {
                mTitleTV.setText(realEstateNewsList.title + "");
                if (!Validator.isEmptyString(realEstateNewsList.description)) {
                    mDescriptionTV.setVisibility(View.VISIBLE);
                    mDescriptionTV.setText(Html.fromHtml(realEstateNewsList.description));
                } else {
                    mDescriptionTV.setVisibility(View.GONE);
                }


                String publishBy;
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Published By")))
                    publishBy = (AppUtilsMethod.getValueFromKey(jsonObject, "Published By"));
                else publishBy = "Published By";
                mPublishedByTV.setText(publishBy);
                String publishDate;
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Date")))
                    publishDate = (AppUtilsMethod.getValueFromKey(jsonObject, "Date"));
                else
                    publishDate = "Date";

                mPublishedDateTV.setText(publishDate + ": ");
                mByTV.setText(" :" + realEstateNewsList.displayName);
                mDateTV.setText(AppUtilsMethod.formattedDateTimeToDisplay(realEstateNewsList.creationDate));
                if (realEstateNewsList.images != null && realEstateNewsList.images.size() > 0) {
                    JsonArray jsonElements = realEstateNewsList.images;
                    for (int i = 0; i < jsonElements.size(); i++) {
                        try {
                            JSONObject jsonObject = new JSONObject(jsonElements.get(i) + "");
                            if (jsonObject != null) {
                                images.add(AppUtilsMethod.getValueFromKey(jsonObject, "storage_path"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    mImageIV.setVisibility(View.VISIBLE);

                    Collections.reverse(images);
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
                            String link = "http://www.ossul.com/economic-news/";
                            if (!Validator.isEmptyString(realEstateNewsList.title)) {
//                                if (Validator.isProbablyArabic(realEstateNewsList.title)) {
//                                    link = "/http://www.ossul.com/economic-news";
//                                    link = realEstateNewsList.economicNewsId + "/" + realEstateNewsList.title + link;
//
//                                    shareText = link + " :" + realEstateNewsList.title;
                                /*} else*/
                                {
                                    String strTitle = realEstateNewsList.title.replaceAll(" ", "-");
                                    if (strTitle != null && strTitle.length() > 20) {
                                        strTitle = strTitle.substring(0, 18);
                                    }
                                    link = link + strTitle + "/" + realEstateNewsList.economicNewsId;
                                    shareText = realEstateNewsList.title + "\n" + link;
                                }
                            }

                            if (!Validator.isEmptyString(realEstateNewsList.shareLink)) {
                                shareText = realEstateNewsList.shareLink;
                            }
                            intent.putExtra(Intent.EXTRA_TEXT, shareText);
                            activity.startActivity(Intent.createChooser(intent, "Share news"));
                        } catch (Exception e) {

                        }
                    }
                });
                mItemCV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, NewsDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("data", realEstateNewsList);
                        intent.putExtra("for", "economic");
                        intent.putExtra("bundle", bundle);
                        intent.putStringArrayListExtra("images", images);
                        activity.startActivityForResult(intent, 11);
                        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

                    }
                });

                final RelativeLayout back_dim_layout = (RelativeLayout) activity.findViewById(R.id.back_dim_layout);

                popupMenu = new PopupMenu(activity, itemView, 3);
                if (realEstateNewsList.userId.equalsIgnoreCase(AppPreferences.get().getUserId())) {
                    popupMenu.inflate(R.menu.popup_menu);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                Intent intent = new Intent(activity, SubmitEconomicNewsActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("data", realEstateNewsList);
                                intent.putExtra("bundle", bundle);
                                activity.startActivityForResult(intent, 102);
                                return true;
                            case R.id.delete:
                                try {
                                    ((EconomicNewsActivity) activity).deleteNews(realEstateNewsList.economicNewsId,getAdapterPosition());
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
                });

            }
        }
    }
}

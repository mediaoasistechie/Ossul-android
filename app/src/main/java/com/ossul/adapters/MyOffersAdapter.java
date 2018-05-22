package com.ossul.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ossul.R;
import com.ossul.activities.BaseActivity;
import com.ossul.activities.CommentsActivity;
import com.ossul.fragments.MyOffersFragment;
import com.ossul.listener.OnItemClickListener;
import com.ossul.network.model.response.MyOffersResponse;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.Validator;

import org.json.JSONObject;

import java.util.ArrayList;


public class MyOffersAdapter extends RecyclerView.Adapter<MyOffersAdapter.ViewHolder> implements Filterable {
    private JSONObject jsonObject;
    private ArrayList<MyOffersResponse.Data> mLists;
    private ArrayList<MyOffersResponse.Data> filteredData;
    private BaseActivity mActivity;
    private ItemFilter mFilter = new ItemFilter();
    private OnItemClickListener onItemClickListener;
    private MyOffersFragment mFragment;

    public MyOffersAdapter(BaseActivity activity, ArrayList<MyOffersResponse.Data> lists, JSONObject jsonObject, MyOffersFragment myOffersFragment) {
        this.mActivity = activity;
        this.mLists = lists;
        this.jsonObject = jsonObject;
        this.filteredData = lists;
        this.mFragment = myOffersFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.adapter_my_request, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(filteredData.get(position), position);

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return filteredData != null ? filteredData.size() : 0;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvPhone;
        private TextView tvEmail;
        private TextView mCityTV;
        private TextView mByTV;
        private TextView mEditTV;
        private TextView mTitleTV;
        private TextView mFromTV;
        private TextView mMinPriceTV;
        private TextView mMaxPriceTV;
        private TextView mToTV, mCommentsCountTV, mCommentsTV;
        private ImageView mRemoveIV;
        private LinearLayout mCommentsLL;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleTV = (TextView) itemView.findViewById(R.id.tv_title);
            mCityTV = (TextView) itemView.findViewById(R.id.tv_city);
            mEditTV = (TextView) itemView.findViewById(R.id.tv_make_offer);
            mByTV = (TextView) itemView.findViewById(R.id.tv_requested_by);
            tvEmail = (TextView) itemView.findViewById(R.id.tv_email);
            tvPhone = (TextView) itemView.findViewById(R.id.tv_phone);
            mFromTV = (TextView) itemView.findViewById(R.id.tv_from);
            mMinPriceTV = (TextView) itemView.findViewById(R.id.tv_min_price);
            mMaxPriceTV = (TextView) itemView.findViewById(R.id.tv_max_price);
            mToTV = (TextView) itemView.findViewById(R.id.tv_to);
            mRemoveIV = (ImageView) itemView.findViewById(R.id.iv_close);
            mCommentsLL = (LinearLayout) itemView.findViewById(R.id.ll_comments);
            mCommentsCountTV = (TextView) itemView.findViewById(R.id.tv_comment_count);
            mCommentsTV = (TextView) itemView.findViewById(R.id.tv_comment);
            mByTV.setVisibility(View.VISIBLE);
            mCommentsLL.setVisibility(View.VISIBLE);
            mRemoveIV.setVisibility(View.GONE);

        }

        void setData(final MyOffersResponse.Data response, final int position) {
            mTitleTV.setText(response.description);
            mCityTV.setText(response.cityName);
            mFromTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "From") + " ");
            mMinPriceTV.setText(response.minPrice + " " + mActivity.getResources().getString(R.string.sr) + " ");
            mToTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "To") + " ");
            mMaxPriceTV.setText(response.maxPrice + " " + mActivity.getResources().getString(R.string.sr));


            if ((Validator.isEmptyString(response.maxPrice) && Validator.isEmptyString(response.minPrice)) || (response.maxPrice.equalsIgnoreCase("0") && response.minPrice.equalsIgnoreCase("0"))) {
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Price not mentioned")))
                    mFromTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Price not mentioned"));
                else
                    mFromTV.setText(mActivity.getResources().getString(R.string.price_not));
                mMaxPriceTV.setVisibility(View.GONE);
                mMinPriceTV.setVisibility(View.GONE);
                mToTV.setVisibility(View.GONE);
            } else {
                mFromTV.setVisibility(View.VISIBLE);
                mMaxPriceTV.setVisibility(View.VISIBLE);
                mMinPriceTV.setVisibility(View.VISIBLE);
                mToTV.setVisibility(View.VISIBLE);
            }


            if (!Validator.isEmptyString(response.offeredTo))
                mByTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "To") + " " + response.offeredTo);
            else if (!Validator.isEmptyString(response.offeredBy))
                mByTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "By") + " " + response.offeredBy);
            String email = "Email", phone = "Phone";
            if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Email")))
                email = AppUtilsMethod.getValueFromKey(jsonObject, "Email");
            if (!Validator.isEmptyString(response.email)) {
                tvEmail.setText(email + ": " + response.email);
                tvEmail.setVisibility(View.VISIBLE);
            }
            if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Phone")))
                phone = AppUtilsMethod.getValueFromKey(jsonObject, "Phone");
            if (!Validator.isEmptyString(response.phone)) {
                tvPhone.setText(phone + ": " + response.phone);
                tvPhone.setVisibility(View.VISIBLE);
            }


            mEditTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "View Offer"));
            mEditTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position);
                    }
                }
            });

            String comment = response.comments > 1 ? " comments" : " comment";

            mCommentsCountTV.setText(response.comments + comment);
            mRemoveIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFragment != null) {
                        mFragment.callRemoveOfferAPI(response.requestId, response.userId);
                        mLists.remove(position);
                        notifyDataSetChanged();
                    }
                }
            });
            mCommentsCountTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (response != null && response.offerId != null) {
                        Intent intent = new Intent(mActivity, CommentsActivity.class);
                        intent.putExtra("offer_id", response.offerId);
                        mActivity.startActivity(intent);
                    }
                }
            });

            mCommentsTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (response != null && response.offerId != null) {
                        Intent intent = new Intent(mActivity, CommentsActivity.class);
                        intent.putExtra("offer_id", response.offerId);
                        mActivity.startActivity(intent);
                    }
                }
            });
        }
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<MyOffersResponse.Data> list = mLists;

            int count = list.size();
            final ArrayList<MyOffersResponse.Data> nlist = new ArrayList<MyOffersResponse.Data>(count);

            String filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).title;
                if (filterableString.toLowerCase().contains(filterString.toLowerCase())) {
                    nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<MyOffersResponse.Data>) results.values;
            notifyDataSetChanged();
        }
    }

}

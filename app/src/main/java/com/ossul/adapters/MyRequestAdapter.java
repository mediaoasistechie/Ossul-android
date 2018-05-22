package com.ossul.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.ossul.R;
import com.ossul.activities.BaseActivity;
import com.ossul.fragments.MyRequestFragment;
import com.ossul.listener.OnItemClickListener;
import com.ossul.network.model.response.SubmitOfferResponse;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.Validator;

import org.json.JSONObject;

import java.util.ArrayList;


public class MyRequestAdapter extends RecyclerView.Adapter<MyRequestAdapter.ViewHolder> implements Filterable {
    private JSONObject jsonObject;
    private ArrayList<SubmitOfferResponse> mLists;
    private ArrayList<SubmitOfferResponse> filteredData;
    private BaseActivity mActivity;
    private ItemFilter mFilter = new ItemFilter();
    private MyRequestFragment myRequestFragment;
    private OnItemClickListener onItemClickListener;

    public MyRequestAdapter(BaseActivity activity, ArrayList<SubmitOfferResponse> lists, JSONObject jsonObject, MyRequestFragment myRequestFragment) {
        this.mActivity = activity;
        this.mLists = lists;
        this.myRequestFragment = myRequestFragment;
        this.jsonObject = jsonObject;
        this.filteredData = lists;
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

        private TextView mCityTV;
        private TextView mEditTV;
        private TextView mTitleTV;
        private TextView mFromTV;
        private TextView mMinPriceTV;
        private TextView mMaxPriceTV;
        private TextView mToTV;
        private ImageView mRemoveIV;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleTV = (TextView) itemView.findViewById(R.id.tv_title);
            mFromTV = (TextView) itemView.findViewById(R.id.tv_from);
            mMinPriceTV = (TextView) itemView.findViewById(R.id.tv_min_price);
            mMaxPriceTV = (TextView) itemView.findViewById(R.id.tv_max_price);
            mToTV = (TextView) itemView.findViewById(R.id.tv_to);
            mCityTV = (TextView) itemView.findViewById(R.id.tv_city);
            mEditTV = (TextView) itemView.findViewById(R.id.tv_make_offer);
            mRemoveIV = (ImageView) itemView.findViewById(R.id.iv_close);


        }

        void setData(final SubmitOfferResponse response, final int position) {
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

            mEditTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Edit"));
            mEditTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position);
                    }
                }
            });
            mRemoveIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myRequestFragment != null) {
                        ((MyRequestFragment) myRequestFragment).callRemoveRequestAPI(response.requestId, response.userId);
                        mLists.remove(position);
                        notifyDataSetChanged();
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

            final ArrayList<SubmitOfferResponse> list = mLists;
            int count = list.size();
            final ArrayList<SubmitOfferResponse> nlist = new ArrayList<SubmitOfferResponse>(count);

            String filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).description;
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
            filteredData = (ArrayList<SubmitOfferResponse>) results.values;
            notifyDataSetChanged();
        }
    }

}

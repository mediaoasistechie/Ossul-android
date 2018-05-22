package com.ossul.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.ossul.R;
import com.ossul.activities.BaseActivity;
import com.ossul.listener.OnItemClickListener;
import com.ossul.network.model.response.GetSubmitRequestData;

import java.util.ArrayList;


public class ZoneTypeListAdapter extends RecyclerView.Adapter<ZoneTypeListAdapter.ViewHolder> {
    private ArrayList<GetSubmitRequestData.Data.ZoneType> mList;
    private BaseActivity mActivity;
    private OnItemClickListener listener;
    private int mSelectedPosition = -1;

    public ZoneTypeListAdapter(BaseActivity activity, ArrayList<GetSubmitRequestData.Data.ZoneType> list) {
        this.mActivity = activity;
        this.mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_zone_type, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CheckBox mZoneCB;
        public RelativeLayout mItemRL;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemRL = (RelativeLayout) itemView.findViewById(R.id.rl_main);
            mZoneCB = (CheckBox) itemView.findViewById(R.id.cb_zone);
        }

        void setData(final GetSubmitRequestData.Data.ZoneType response, final int position) {
            mZoneCB.setText(response.zoneTypeName);
            if (response.isChecked) {
                mZoneCB.setChecked(true);
            } else
                mZoneCB.setChecked(false);
            mZoneCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        response.isChecked = true;
                    else
                        response.isChecked = false;
                    if (listener != null) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}

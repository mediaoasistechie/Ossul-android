package com.ossul.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ossul.R;
import com.ossul.activities.BaseActivity;
import com.ossul.network.model.response.BaseResponse;

import java.util.ArrayList;

/***
 * Adapter to set data for age list
 ***/
public class CustomArrayAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private ArrayList<String> mNameList;
    private ArrayList<? extends BaseResponse> mList;
    private BaseActivity mContext;

    public CustomArrayAdapter(BaseActivity context, ArrayList<? extends BaseResponse> list, ArrayList<String> nameList) {
        this.mContext = context;
        this.mList = list;
        this.mNameList = nameList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public CustomArrayAdapter(BaseActivity context, ArrayList<String> nameList) {

        this.mContext = context;
        this.mNameList = nameList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return mNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return mNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_custom_array,
                    parent, false);
        }
        TextView adapterText = (TextView) convertView
                .findViewById(R.id.tv_name);
        adapterText.setText(mNameList.get(position));

        View bottomLine = (View) convertView
                .findViewById(R.id.tv_bottom_line);
        if (null != mNameList && position != mNameList.size() - 1) {
            bottomLine.setVisibility(View.VISIBLE);
        } else {
            bottomLine.setVisibility(View.GONE);
        }
        return convertView;
    }

    // to set drop down view of age list
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_spinner_item, parent, false);
        }

        TextView adapterText = (TextView) convertView.findViewById(R.id.tv_name);
        adapterText.setText(mNameList.get(position));

        View bottomLine = (View) convertView.findViewById(R.id.tv_bottom_line);
        if (null != mNameList && position != mNameList.size() - 1) {
            bottomLine.setVisibility(View.VISIBLE);
        } else {
            bottomLine.setVisibility(View.GONE);
        }
        return convertView;
    }
}

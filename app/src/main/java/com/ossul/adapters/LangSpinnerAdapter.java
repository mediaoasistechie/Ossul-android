package com.ossul.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.ossul.R;


/***
 * Adapter to set data of countries list
 ***/
public class LangSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private final Context context;
    private final LayoutInflater inflater;

    public LangSpinnerAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.adapter_lang,
                        parent, false);
            }
            ImageView adapterImage = (ImageView) convertView
                    .findViewById(R.id.iv_countryflag);
            TextView adapterText= (TextView) convertView
                    .findViewById(R.id.tv_country);

            if (position == 1) {
                adapterImage.setImageResource(R.mipmap.ar_icon);
                adapterText.setText("عربى");
            } else if (position == 0) {
                adapterImage.setImageResource(R.mipmap.en_icon);
                adapterText.setText("English");
            }
            return convertView;
        }
    }

    //to set the drop down view when spinner is expanded
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.adapter_lang,
                        parent, false);
            }
            ImageView adapterImage = (ImageView) convertView
                    .findViewById(R.id.iv_countryflag);

            TextView adapterText= (TextView) convertView
                    .findViewById(R.id.tv_country);

            if (position == 1) {
                adapterImage.setImageResource(R.mipmap.ar_icon);
                adapterText.setText("عربى");
            } else if (position == 0) {
                adapterImage.setImageResource(R.mipmap.en_icon);
                adapterText.setText("English");
            }
            return convertView;
        }
    }
}

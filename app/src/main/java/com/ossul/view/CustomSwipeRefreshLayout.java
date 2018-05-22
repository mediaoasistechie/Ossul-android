package com.ossul.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.ossul.R;

public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {

    public CustomSwipeRefreshLayout(Context context) {
        super(context);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setColorSchemeColors(context.getResources().getColor(R.color.colorAccent), context.getResources().getColor(R.color.colorPrimary),context.getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalStateException e) {
            return true;
        }
    }
}

package com.carecloud.carepaylibray.customcomponents;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {

    private boolean enablePaging = false;

    public void setEnablePaging(boolean enablePaging) {
        this.enablePaging = enablePaging;
    }

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.enablePaging && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.enablePaging && super.onInterceptTouchEvent(event);
    }

}
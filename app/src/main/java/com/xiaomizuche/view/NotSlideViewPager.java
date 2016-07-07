package com.xiaomizuche.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NotSlideViewPager extends ViewPager {
    private boolean scrollble = false;

    public NotSlideViewPager(Context context) {
        super(context);
    }

    public NotSlideViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (scrollble == false) {
            return false;
        } else {
            return super.onTouchEvent(ev);
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (scrollble == false) {
            return false;
        } else {
            return super.onInterceptTouchEvent(ev);
        }

    }

    public void setScrollble(boolean scrollble) {
        this.scrollble = scrollble;
    }
}

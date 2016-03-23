package com.chenyp.collaboration.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class HackyViewPager extends ViewPager {

    private boolean isCanScroll;

    public HackyViewPager(Context context) {
        super(context);
        isCanScroll = true;
    }

    public HackyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        isCanScroll = true;
    }

    /*@Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return isCanScroll && super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return false;
    }*/

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (isCanScroll) {
            if (v == this && v instanceof HackyViewPager) {
                return false;
            }
        }
        return super.canScroll(v, checkV, dx, x, y);
    }

    public boolean isCanScroll() {
        return isCanScroll;
    }

    public void setCanScroll(boolean canScroll) {
        isCanScroll = canScroll;
    }
}

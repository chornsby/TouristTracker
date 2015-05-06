package com.chornsby.touristtracker.actionbar.tabs;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NonDraggableViewPager extends ViewPager {

    public NonDraggableViewPager(Context context) {
        super(context);
    }

    public NonDraggableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Ignore touch events
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    /**
     * Ignore touch events
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}

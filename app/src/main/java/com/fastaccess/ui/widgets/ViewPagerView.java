package com.fastaccess.ui.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import static android.R.attr.enabled;


/**
 * Created by kosh20111 on 10/8/2015.
 * <p/>
 * Viewpager that has scrolling animation by default
 */
public class ViewPagerView extends ViewPager {

    private boolean isEnabled;

    public ViewPagerView(Context context) {
        super(context);
    }

    public ViewPagerView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        int[] attrsArray = {enabled};
        TypedArray array = context.obtainStyledAttributes(attrs, attrsArray);
        isEnabled = array.getBoolean(0, true);
        array.recycle();
    }

    @Override public boolean isEnabled() {
        return isEnabled;
    }

    @Override public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        requestLayout();
    }

    @Override public void setAdapter(@Nullable PagerAdapter adapter) {
        super.setAdapter(adapter);
        if (isInEditMode()) return;
        if (adapter != null) {
            setOffscreenPageLimit(adapter.getCount());
        }
    }

    @SuppressLint("ClickableViewAccessibility") @Override public boolean onTouchEvent(MotionEvent event) {
        try {
            return !isEnabled() || super.onTouchEvent(event);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent event) {
        try {
            return isEnabled() && super.onInterceptTouchEvent(event);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}

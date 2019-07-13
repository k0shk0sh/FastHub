package com.fastaccess.github.ui.widget

import android.R.attr.enabled
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager


/**
 * Created by kosh20111 on 10/8/2015.
 *
 *
 * Viewpager that has scrolling animation by default
 */
class ViewPagerView : ViewPager {

    private var isEnabled: Boolean = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val attrsArray = intArrayOf(enabled)
        val array = context.obtainStyledAttributes(attrs, attrsArray)
        isEnabled = array.getBoolean(0, true)
        array.recycle()
    }

    override fun isEnabled(): Boolean {
        return isEnabled
    }

    override fun setEnabled(enabled: Boolean) {
        this.isEnabled = enabled
        requestLayout()
    }

    override fun setAdapter(adapter: PagerAdapter?) {
        super.setAdapter(adapter)
        if (isInEditMode) return
        if (adapter != null) {
            offscreenPageLimit = adapter.count
        }
    }

    @SuppressLint("ClickableViewAccessibility") override fun onTouchEvent(event: MotionEvent): Boolean {
        try {
            return !isEnabled() || super.onTouchEvent(event)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }

        return false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        try {
            return isEnabled() && super.onInterceptTouchEvent(event)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }

        return false
    }
}
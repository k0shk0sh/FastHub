package com.fastaccess.github.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fastaccess.github.R
import com.fastaccess.github.extensions.getColorCompat
import com.google.android.material.appbar.AppBarLayout

class ParentSwipeRefreshLayout : SwipeRefreshLayout, AppBarLayout.OnOffsetChangedListener {
    var appBarLayout: AppBarLayout? = null
        set(value) {
            field = value
            value?.addOnOffsetChangedListener(this)
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setColorSchemeColors(context.getColorCompat(R.color.material_indigo_600), context.getColorCompat(R.color.material_indigo_700),
                context.getColorCompat(R.color.material_indigo_800), context.getColorCompat(R.color.material_indigo_900))
    }

    override fun onDetachedFromWindow() {
        appBarLayout?.removeOnOffsetChangedListener(this)
        appBarLayout = null
        super.onDetachedFromWindow()
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
        this.isEnabled = i == 0
    }
}
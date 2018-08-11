package com.fastaccess.github.ui.widget.recyclerview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.fastaccess.github.R
import com.fastaccess.github.ui.widget.recyclerview.decoration.InsetDividerDecoration
import com.fastaccess.github.utils.extensions.getColorAttr

/**
 * Created by Kosh on 23.06.18.
 */
class BaseRecyclerView constructor(context: Context,
                                   attrs: AttributeSet? = null,
                                   defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    fun addKeyLineDivider() {
        if (canAddDivider()) {
            val resources = resources
            addItemDecoration(InsetDividerDecoration(resources.getDimensionPixelSize(R.dimen.divider_height),
                    resources.getDimensionPixelSize(R.dimen.keyline_2), context.getColorAttr(R.attr.dividerColor)))
        }
    }

    fun addDivider() {
        if (canAddDivider()) {
            val resources = resources
            addItemDecoration(InsetDividerDecoration(resources.getDimensionPixelSize(R.dimen.divider_height), 0, context.getColorAttr(R.attr.dividerColor)))
        }
    }

    private fun canAddDivider(): Boolean {
        if (layoutManager != null) {
            val layoutManager = this.layoutManager
            when (layoutManager) {
                is GridLayoutManager -> return layoutManager.spanCount == 1
                is LinearLayoutManager -> return true
                is StaggeredGridLayoutManager -> return layoutManager.spanCount == 1
            }
        }
        return false
    }
}
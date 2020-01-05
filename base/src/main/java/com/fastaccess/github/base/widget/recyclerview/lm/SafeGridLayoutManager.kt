package com.fastaccess.github.base.widget.recyclerview.lm

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Kosh on 31.10.18.
 */
class SafeGridLayoutManager : GridLayoutManager {

    private var iconSize: Int = 0

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    constructor(context: Context?, spanCount: Int) : super(context, spanCount)
    constructor(context: Context?, spanCount: Int, orientation: Int, reverseLayout: Boolean) : super(context, spanCount, orientation, reverseLayout)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
            updateCount()
        } catch (ignored: Exception) {
        }
    }

    override fun onMeasure(recycler: RecyclerView.Recycler, state: RecyclerView.State, widthSpec: Int, heightSpec: Int) {
        try {
            super.onMeasure(recycler, state, widthSpec, heightSpec)
        } catch (ignored: Exception) {
        }
    }

    private fun updateCount() {
        if (iconSize > 1) {
            var spanCount = Math.max(1, width / iconSize)
            if (spanCount < 1) {
                spanCount = 1
            }
            this.spanCount = spanCount
        }
    }

    fun getIconSize(): Int {
        return iconSize
    }

    fun setIconSize(iconSize: Int) {
        this.iconSize = iconSize
        updateCount()
    }
}
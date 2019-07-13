package com.fastaccess.github.ui.widget.recyclerview.lm

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * Created by Kosh on 31.10.18.
 */
class SafeStaggeredLayoutManager : StaggeredGridLayoutManager {
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    constructor(spanCount: Int, orientation: Int) : super(spanCount, orientation)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (ignored: Exception) {
        }
    }

    override fun onMeasure(recycler: RecyclerView.Recycler, state: RecyclerView.State, widthSpec: Int, heightSpec: Int) {
        try {
            super.onMeasure(recycler, state, widthSpec, heightSpec)
        } catch (ignored: Exception) {
        }
    }
}
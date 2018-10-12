package com.fastaccess.github.ui.widget.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Kosh on 23.06.18.
 */
class BaseRecyclerView constructor(context: Context,
                                   attrs: AttributeSet? = null,
                                   defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private var emptyView: View? = null
    private var parentView: View? = null


    private val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            showEmptyView()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            showEmptyView()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            showEmptyView()
        }
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        super.setAdapter(adapter)
        if (isInEditMode) return
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer)
            observer.onChanged()
        }
    }

    private fun showEmptyView() {
        val adapter = adapter
        if (adapter != null) {
            if (emptyView != null) {
                if (adapter.itemCount == 0) {
                    showParentOrSelf(false)
                } else {
                    showParentOrSelf(true)
                }
            }
        } else {
            if (emptyView != null) {
                showParentOrSelf(false)
            }
        }
    }

    private fun showParentOrSelf(showRecyclerView: Boolean) {
        parentView?.isVisible = parentView != null
        isVisible = true
        emptyView?.isVisible = !showRecyclerView
    }

    fun setEmptyView(emptyView: View, parentView: View? = null) {
        this.emptyView = emptyView
        this.parentView = parentView
        showEmptyView()
    }
}
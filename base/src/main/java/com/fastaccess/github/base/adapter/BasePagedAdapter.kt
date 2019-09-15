package com.fastaccess.github.base.adapter

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
/**
 * Created by Kosh on 12.10.18.
 */
abstract class BasePagedAdapter<M>(
    diffCallback: DiffUtil.ItemCallback<M?>
) : PagedListAdapter<M, BaseViewHolder<M>>(diffCallback) {

    var currentState = CurrentState.LOADING
        set(value) {
            field = value
            if (itemCount < 10) return // do not add progress if we are in first page!
            notifyItemChanged(itemCount)
        }

    abstract fun contentViewHolder(parent: ViewGroup): BaseViewHolder<M>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<M> {
        return if (viewType == CONTENT_VIEW_TYPE) contentViewHolder(parent) else LoadingViewHolder(parent)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<M>, position: Int) {
        if (getItemViewType(position) == CONTENT_VIEW_TYPE) {
            getItem(position)?.let { holder.bind(it) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < super.getItemCount()) CONTENT_VIEW_TYPE else FOOTER_VIEW_TYPE
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasFooter()) 1 else 0
    }

    fun getValue(position: Int) = getItem(position)

    private fun hasFooter(): Boolean {
        return super.getItemCount() != 0 && (currentState == CurrentState.LOADING || currentState == CurrentState.ERROR)
    }

    companion object {
        const val CONTENT_VIEW_TYPE = 1
        const val FOOTER_VIEW_TYPE = 2
    }
}

enum class CurrentState {
    DONE, LOADING, ERROR
}
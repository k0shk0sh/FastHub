package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fastaccess.data.model.TimelineModel
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.github.ui.adapter.viewholder.CommentViewHolder
import com.fastaccess.github.ui.adapter.viewholder.IssueContentViewHolder
import com.fastaccess.github.ui.adapter.viewholder.LoadingViewHolder
import net.nightwhistler.htmlspanner.HtmlSpanner

/**
 * Created by Kosh on 20.01.19.
 */
class IssueTimelineAdapter(
    private val htmlSpanner: HtmlSpanner,
    private val theme: Int
) : ListAdapter<TimelineModel, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    private val notifyCallback by lazy {
        { position: Int ->
            notifyItemChanged(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.let {
            when {
                it.issue != null -> HEADER
                it.comment != null -> COMMENT
                else -> CONTENT
            }
        } ?: super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            COMMENT -> CommentViewHolder(parent, htmlSpanner, theme, notifyCallback)
            CONTENT -> IssueContentViewHolder(parent)
            else -> LoadingViewHolder<Any>(parent).apply { itemView.isVisible = false }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CommentViewHolder -> holder.bind(getItem(position).comment)
            is IssueContentViewHolder -> holder.bind(getItem(position))
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is BaseViewHolder<*>) {
            holder.onDetached()
        }
        super.onViewRecycled(holder)
    }

    fun getItemByPosition(position: Int): TimelineModel? {
        return kotlin.runCatching { getItem(position) }.getOrNull()
    }

    companion object {
        private const val HEADER = 1
        private const val COMMENT = 2
        private const val CONTENT = 3

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TimelineModel?>() {
            override fun areItemsTheSame(oldItem: TimelineModel, newItem: TimelineModel): Boolean = oldItem.hashCode() == newItem.hashCode()
            override fun areContentsTheSame(oldItem: TimelineModel, newItem: TimelineModel): Boolean = oldItem == newItem
        }
    }
}

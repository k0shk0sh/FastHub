package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fastaccess.data.model.TimelineModel
import com.fastaccess.github.ui.adapter.viewholder.CommentViewHolder
import com.fastaccess.github.ui.adapter.viewholder.IssueTimelineHeaderViewHolder
import com.fastaccess.github.ui.adapter.viewholder.LoadingViewHolder
import net.nightwhistler.htmlspanner.HtmlSpanner

/**
 * Created by Kosh on 20.01.19.
 */
class IssueTimelineAdapter(private val htmlSpanner: HtmlSpanner) : ListAdapter<TimelineModel, RecyclerView.ViewHolder>(DIFF_CALLBACK) {
    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.let {
            when {
                it.issue != null -> HEADER
                it.comment != null -> COMMENT
                else -> COMMIT
            }
        } ?: super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER -> IssueTimelineHeaderViewHolder(parent, htmlSpanner)
            COMMENT -> CommentViewHolder(parent, htmlSpanner)
            else -> LoadingViewHolder<Any>(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is IssueTimelineHeaderViewHolder -> holder.bind(getItem(position).issue)
            is CommentViewHolder -> holder.bind(getItem(position).comment)
            else -> holder.itemView.isVisible = false
        }
    }

    companion object {

        private const val HEADER = 1
        private const val COMMIT = 2
        private const val COMMENT = 3

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TimelineModel?>() {
            override fun areItemsTheSame(oldItem: TimelineModel, newItem: TimelineModel): Boolean = oldItem.hashCode() == newItem.hashCode()
            override fun areContentsTheSame(oldItem: TimelineModel, newItem: TimelineModel): Boolean = oldItem == newItem
        }
    }
}

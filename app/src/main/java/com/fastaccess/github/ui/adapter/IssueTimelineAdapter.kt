package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.fastaccess.data.persistence.models.IssueModel
import com.fastaccess.github.ui.adapter.viewholder.IssueTimelineHeaderViewHolder

/**
 * Created by Kosh on 20.01.19.
 */
class IssueTimelineAdapter : ListAdapter<IssueModel, IssueTimelineHeaderViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueTimelineHeaderViewHolder = IssueTimelineHeaderViewHolder(parent)
    override fun onBindViewHolder(holder: IssueTimelineHeaderViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<IssueModel?>() {
            override fun areItemsTheSame(oldItem: IssueModel, newItem: IssueModel): Boolean = oldItem.title == newItem.title
            override fun areContentsTheSame(oldItem: IssueModel, newItem: IssueModel): Boolean = oldItem == newItem
        }
    }
}

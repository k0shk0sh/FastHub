package com.fastaccess.fasthub.commit.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.fastaccess.data.model.FullCommitModel

class CommitsAdapter(
    private val callback: (FullCommitModel) -> Unit
) : ListAdapter<FullCommitModel, CommitsViewHolder>(object : DiffUtil.ItemCallback<FullCommitModel?>() {
    override fun areItemsTheSame(oldItem: FullCommitModel, newItem: FullCommitModel): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: FullCommitModel, newItem: FullCommitModel): Boolean = oldItem == newItem
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommitsViewHolder = CommitsViewHolder(parent).apply {
        itemView.setOnClickListener {
            getItem(adapterPosition)?.let(callback)
        }
    }

    override fun onBindViewHolder(holder: CommitsViewHolder, position: Int) = holder.bind(getItem(position))
}
package com.fastaccess.fasthub.commit.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fastaccess.data.model.CommitFilesModel

class CommitFilesAdapter(
    private val callback: (Int, CommitFilesModel) -> Unit
) : ListAdapter<CommitFilesModel, CommitFileViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommitFileViewHolder = CommitFileViewHolder(parent).apply {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position == RecyclerView.NO_POSITION) return@setOnClickListener
            getItem(position)?.let { callback.invoke(position, it) }
        }
    }

    override fun onBindViewHolder(holder: CommitFileViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CommitFilesModel?>() {
            override fun areItemsTheSame(oldItem: CommitFilesModel, newItem: CommitFilesModel) = oldItem.rawUrl == newItem.rawUrl
            override fun areContentsTheSame(oldItem: CommitFilesModel, newItem: CommitFilesModel) = oldItem == newItem
        }
    }
}
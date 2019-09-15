package com.fastaccess.fasthub.commit.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fastaccess.data.model.CommentModel
import io.noties.markwon.Markwon

class CommitCommentsAdapter(
    private val markwon: Markwon,
    private val theme: Int,
    private val commentClickListener: (position: Int, model: CommentModel) -> Unit,
    private val deleteCommentListener: (position: Int, model: CommentModel) -> Unit,
    private val editCommentListener: (position: Int, model: CommentModel) -> Unit
) : ListAdapter<CommentModel, CommitCommentViewHolder>(DIFF_CALLBACK) {

    private val notifyCallback by lazy {
        { position: Int ->
            notifyItemChanged(position)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommitCommentViewHolder = CommitCommentViewHolder(
        parent, markwon, theme, notifyCallback, { position ->
            getItemByPosition(position)?.let { deleteCommentListener.invoke(position, it) }
        }, { position ->
            getItemByPosition(position)?.let { editCommentListener.invoke(position, it) }
        }).apply {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position == RecyclerView.NO_POSITION) return@setOnClickListener
            getItemByPosition(position)?.let { commentClickListener.invoke(position, it) }
        }
    }

    override fun onBindViewHolder(holder: CommitCommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getItemByPosition(position: Int): CommentModel? = runCatching { getItem(position) }.getOrNull()


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CommentModel?>() {
            override fun areItemsTheSame(oldItem: CommentModel, newItem: CommentModel) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: CommentModel, newItem: CommentModel) = oldItem == newItem
        }
    }
}
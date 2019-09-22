package com.fastaccess.fasthub.reviews.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fastaccess.data.model.CommentModel
import com.fastaccess.data.model.TimelineModel
import io.noties.markwon.Markwon

class ReviewsAdapter(
    private val markwon: Markwon,
    private val theme: Int,
    private val commentClickListener: (position: Int, model: CommentModel) -> Unit,
    private val deleteCommentListener: (position: Int, model: CommentModel) -> Unit,
    private val editCommentListener: (position: Int, model: TimelineModel) -> Unit
) : ListAdapter<TimelineModel, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    private val notifyCallback by lazy {
        { position: Int ->
            notifyItemChanged(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        REVIEW -> ReviewViewHolder(parent, markwon, theme, notifyCallback, { position ->
            getItem(position)?.let {
                it.review?.isReviewBody = true
                editCommentListener.invoke(position, it)
            }
        })

        COMMENT -> ReviewCommentViewHolder(parent, markwon, theme, notifyCallback, { position ->
            getItem(position)?.comment?.let { deleteCommentListener.invoke(position, it) }
        }, { position ->
            getItem(position)?.let { editCommentListener.invoke(position, it) }
        }).apply {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                getItem(position)?.comment?.let { commentClickListener.invoke(position, it) }
            }
        }
        else -> throw IllegalArgumentException("not supported")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ReviewViewHolder -> getItem(position)?.review?.let { holder.bind(it) }
            is ReviewCommentViewHolder -> getItem(position)?.comment?.let { holder.bind(it) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.let {
            when {
                it.review != null -> REVIEW
                it.comment != null -> COMMENT
                else -> -1
            }
        } ?: super.getItemViewType(position)
    }

    companion object {
        private const val REVIEW = 1
        private const val COMMENT = 2

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TimelineModel?>() {
            override fun areItemsTheSame(
                oldItem: TimelineModel,
                newItem: TimelineModel
            ): Boolean = oldItem.hashCode() == newItem.hashCode()

            override fun areContentsTheSame(
                oldItem: TimelineModel,
                newItem: TimelineModel
            ): Boolean = oldItem == newItem
        }
    }
}
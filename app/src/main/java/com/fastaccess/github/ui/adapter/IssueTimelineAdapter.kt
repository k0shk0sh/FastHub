package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fastaccess.data.model.CommentModel
import com.fastaccess.data.model.TimelineModel
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.github.ui.adapter.viewholder.*
import io.noties.markwon.Markwon

/**
 * Created by Kosh on 20.01.19.
 */
class IssueTimelineAdapter(
    private val markwon: Markwon,
    private val theme: Int,
    private val commentClickListener: (position: Int, model: CommentModel) -> Unit,
    private val deleteCommentListener: (position: Int, model: TimelineModel) -> Unit,
    private val editCommentListener: (position: Int, model: TimelineModel) -> Unit
) : ListAdapter<TimelineModel, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    private val notifyCallback by lazy {
        { position: Int ->
            notifyItemChanged(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.let {
            when {
                it.comment != null -> COMMENT
                !it.review?.body.isNullOrEmpty() || it.review?.comment != null -> REVIEW_THREAD
                it.commitThread != null -> COMMIT_THREAD
                else -> CONTENT
            }
        } ?: super.getItemViewType(position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            COMMENT -> CommentViewHolder(parent, markwon, theme, notifyCallback, { position ->
                getItemByPosition(position)?.let { deleteCommentListener.invoke(position, it) }
            }, { position ->
                getItemByPosition(position)?.let { editCommentListener.invoke(position, it) }
            }).apply {
                itemView.setOnClickListener {
                    val position = adapterPosition
                    if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                    getItemByPosition(position)?.comment?.let { commentClickListener.invoke(position, it) }
                }
            }
            REVIEW_THREAD -> ReviewViewHolder(parent, markwon, theme, notifyCallback, { position, isReviewBody ->
                getItemByPosition(position)?.let {
                    it.review?.isReviewBody = isReviewBody
                    deleteCommentListener.invoke(position, it)
                }
            }, { position, isReviewBody ->
                getItemByPosition(position)?.let {
                    it.review?.isReviewBody = isReviewBody
                    editCommentListener.invoke(position, it)
                }
            }).apply {
                itemView.setOnClickListener {
                    val position = adapterPosition
                    if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                    getItemByPosition(position)?.comment?.let { commentClickListener.invoke(position, it) }
                }
            }
            COMMIT_THREAD -> CommitThreadViewHolder(parent, markwon, theme, notifyCallback, { position ->
                getItemByPosition(position)?.let { deleteCommentListener.invoke(position, it) }
            }, { position ->
                getItemByPosition(position)?.let { editCommentListener.invoke(position, it) }
            }).apply {
                itemView.setOnClickListener {
                    val position = adapterPosition
                    if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                    getItemByPosition(position)?.comment?.let { commentClickListener.invoke(position, it) }
                }
            }
            CONTENT -> IssueContentViewHolder(parent)
            else -> LoadingViewHolder<Any>(parent).apply { itemView.isVisible = false }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder) {
            is CommentViewHolder -> holder.bind(getItem(position).comment)
            is ReviewViewHolder -> holder.bind(getItem(position).review)
            is CommitThreadViewHolder -> holder.bind(getItem(position).commitThread)
            is IssueContentViewHolder -> holder.bind(getItem(position))
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is BaseViewHolder<*>) {
            holder.onDetached()
        }
        super.onViewRecycled(holder)
    }

    fun getItemByPosition(position: Int): TimelineModel? = runCatching { getItem(position) }.getOrNull()

    companion object {
        private const val HEADER = 1
        private const val COMMENT = 2
        private const val CONTENT = 3
        private const val REVIEW_THREAD = 4
        private const val COMMIT_THREAD = 5

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

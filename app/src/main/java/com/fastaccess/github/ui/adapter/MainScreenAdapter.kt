package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fastaccess.data.model.MainScreenModel
import com.fastaccess.data.model.MainScreenModelRowType
import com.fastaccess.github.R
import com.fastaccess.github.extensions.getDrawableCompat
import com.fastaccess.github.ui.adapter.viewholder.FeedsViewHolder
import com.fastaccess.github.ui.adapter.viewholder.MyIssuesPrsViewHolder
import com.fastaccess.github.ui.adapter.viewholder.NotificationsViewHolder
import com.fastaccess.github.ui.adapter.viewholder.TitleSectionViewHolder
import com.fastaccess.markdown.widget.SpannableBuilder

/**
 * Created by Kosh on 04.11.18.
 */
class MainScreenAdapter(
    private val clickListener: (model: MainScreenModel) -> Unit
) : ListAdapter<MainScreenModel, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            MainScreenModelRowType.FEED.rowType -> FeedsViewHolder(parent).apply { setOnClick(this) }
            MainScreenModelRowType.NOTIFICATION.rowType -> NotificationsViewHolder(parent).apply { setOnClick(this) }
            MainScreenModelRowType.ISSUES.rowType -> MyIssuesPrsViewHolder(parent).apply { setOnClick(this) }
            MainScreenModelRowType.PRS.rowType -> MyIssuesPrsViewHolder(parent).apply { setOnClick(this) }
            else -> TitleSectionViewHolder(parent).apply { setOnClick(this) }
        }
    }

    private fun setOnClick(viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            if (position > RecyclerView.NO_POSITION) {
                clickListener.invoke(getItem(position))
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val item = getItem(position) ?: return
        when (holder) {
            is FeedsViewHolder -> item.feed?.let { holder.bind(it) }
            is NotificationsViewHolder -> item.notificationModel?.let { holder.bind(it) }
            is MyIssuesPrsViewHolder -> item.issuesPullsModel?.let { holder.bind(it) }
            is TitleSectionViewHolder -> {
                when (item.mainScreenModelRowType) {
                    MainScreenModelRowType.FEED_TITLE -> holder.bind(holder.itemView.context.getString(R.string.feeds))
                    MainScreenModelRowType.NOTIFICATION_TITLE -> holder.bind(
                        if (item.hasBubble) {
                            SpannableBuilder.builder()
                                .append(holder.itemView.context.getString(R.string.notifications))
                                .space().space()
                                .append(holder.itemView.context.getDrawableCompat(R.drawable.circle_shape_red_small))
                        } else {
                            holder.itemView.context.getString(R.string.notifications)
                        }
                    )
                    MainScreenModelRowType.ISSUES_TITLE -> holder.bind(holder.itemView.context.getString(R.string.issues))
                    MainScreenModelRowType.PRS_TITLE -> holder.bind(holder.itemView.context.getString(R.string.pull_requests))
                    else -> holder.itemView.isVisible = false
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).mainScreenModelRowType?.rowType ?: super.getItemViewType(position)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MainScreenModel?>() {
            override fun areItemsTheSame(
                oldItem: MainScreenModel,
                newItem: MainScreenModel
            ):
                Boolean = oldItem.mainScreenModelRowType == newItem.mainScreenModelRowType

            override fun areContentsTheSame(
                oldItem: MainScreenModel,
                newItem: MainScreenModel
            ):
                Boolean = oldItem == newItem
        }
    }
}
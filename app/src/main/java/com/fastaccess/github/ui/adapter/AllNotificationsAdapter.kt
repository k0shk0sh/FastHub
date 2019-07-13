package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fastaccess.data.model.GroupedNotificationsModel
import com.fastaccess.github.ui.adapter.viewholder.NotificationsViewHolder
import com.fastaccess.github.ui.adapter.viewholder.TitleSectionViewHolder

/**
 * Created by Kosh on 04.11.18.
 */
class AllNotificationsAdapter : ListAdapter<GroupedNotificationsModel, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            GroupedNotificationsModel.HEADER -> TitleSectionViewHolder(parent)
            else -> NotificationsViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        when (holder) {
            is NotificationsViewHolder -> {
                val notification = item.notification
                if (notification != null) {
                    holder.bind(notification)
                } else {
                    holder.itemView.isVisible = false
                }
            }
            is TitleSectionViewHolder -> holder.bind(item.repo?.fullName ?: item.repo?.name ?: "N/A")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).rowType
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GroupedNotificationsModel?>() {
            override fun areItemsTheSame(oldItem: GroupedNotificationsModel, newItem: GroupedNotificationsModel):
                Boolean = oldItem.rowType == newItem.rowType

            override fun areContentsTheSame(oldItem: GroupedNotificationsModel, newItem: GroupedNotificationsModel):
                Boolean = oldItem == newItem
        }
    }
}
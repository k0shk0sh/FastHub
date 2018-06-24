package com.fastaccess.github.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.fastaccess.data.persistence.models.NotificationModel
import com.fastaccess.github.R
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.github.utils.extensions.timeAgo
import kotlinx.android.synthetic.main.notification_main_screen_row_item.view.*

/**
 * Created by Kosh on 17.06.18.
 */
class NotificationsAdapter : ListAdapter<NotificationModel, NotificationsAdapter.ViewHolder>(object : DiffUtil.ItemCallback<NotificationModel?>() {
            override fun areItemsTheSame(oldItem: NotificationModel, newItem: NotificationModel) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: NotificationModel, newItem: NotificationModel) = oldItem == newItem
        }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class ViewHolder(parent: ViewGroup) : BaseViewHolder<NotificationModel>(LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_main_screen_row_item, parent, false)) {
        override fun bind(item: NotificationModel) {
            itemView.let {
                item.subject?.let { subject ->
                    it.notificationTitle.text = subject.title ?: ""
                    subject.type?.let { type ->
                        when (type) {
                            "PullRequest" -> it.dateWithIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_pull_requests_uncolored_small, 0)
                            "Issue" -> it.dateWithIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_issues_small, 0)
                            "Commit" -> it.dateWithIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_branch, 0)
                            else -> it.dateWithIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_info_outline_small, 0)
                        }
                    }
                }
                it.repoName.text = item.repository?.fullName ?: ""
                it.dateWithIcon.text = item.updatedAt?.timeAgo()
            }
        }
    }
}
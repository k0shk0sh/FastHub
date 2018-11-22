package com.fastaccess.github.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fastaccess.data.persistence.models.NotificationModel
import com.fastaccess.github.R
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.github.extensions.timeAgo
import kotlinx.android.synthetic.main.notification_main_screen_row_item.view.*

/**
 * Created by Kosh on 12.10.18.
 */

class NotificationsViewHolder(parent: ViewGroup) : BaseViewHolder<NotificationModel>(LayoutInflater.from(parent.context)
        .inflate(R.layout.notification_main_screen_row_item, parent, false)) {
    override fun bind(item: NotificationModel) {
        itemView.apply {
            item.subject?.let { subject ->
                notificationTitle.text = subject.title ?: ""
                subject.type?.let { type ->
                    when (type) {
                        "PullRequest" -> dateWithIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_pull_requests_uncolored_small, 0)
                        "Issue" -> dateWithIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_issues_small, 0)
                        "Commit" -> dateWithIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_branch, 0)
                        else -> dateWithIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_info_outline_small, 0)
                    }
                }
            }
            repoName.text = item.repository?.fullName ?: ""
            dateWithIcon.text = item.updatedAt?.timeAgo()
        }
    }
}
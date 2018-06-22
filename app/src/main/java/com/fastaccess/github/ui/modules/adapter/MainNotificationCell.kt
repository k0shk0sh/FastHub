package com.fastaccess.github.ui.modules.adapter

import android.content.Context
import com.fastaccess.data.persistence.models.NotificationModel
import com.fastaccess.github.R
import com.fastaccess.github.utils.extensions.timeAgo
import com.jaychang.srv.kae.SimpleCell
import kotlinx.android.synthetic.main.notification_main_screen_row_item.view.*

/**
 * Created by Kosh on 17.06.18.
 */
class MainNotificationCell(private val node: NotificationModel) : SimpleCell<NotificationModel>(node) {

    override fun getLayoutRes(): Int = R.layout.notification_main_screen_row_item

    override fun onBindViewHolder(holder: com.jaychang.srv.kae.SimpleViewHolder, position: Int, context: Context, payload: Any?) {
        holder.itemView.let {
            node.subject?.let { subject ->
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
            it.repoName.text = node.repository?.fullName ?: ""
            it.dateWithIcon.text = node.updatedAt?.timeAgo()
        }
    }

}
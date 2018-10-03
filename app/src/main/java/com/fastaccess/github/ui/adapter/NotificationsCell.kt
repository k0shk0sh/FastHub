package com.fastaccess.github.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import com.fastaccess.data.persistence.models.NotificationModel
import com.fastaccess.github.R
import com.fastaccess.github.utils.extensions.timeAgo
import com.jaychang.srv.Updatable
import com.jaychang.srv.kae.SimpleCell
import com.jaychang.srv.kae.SimpleViewHolder
import kotlinx.android.synthetic.main.notification_main_screen_row_item.view.*

/**
 * Created by Kosh on 17.06.18.
 */
class NotificationsCell(private val node: NotificationModel) : SimpleCell<NotificationModel>(node), Updatable<NotificationModel> {
    override fun getLayoutRes(): Int = R.layout.notification_main_screen_row_item

    override fun areContentsTheSame(newItem: NotificationModel): Boolean = node == newItem

    override fun getChangePayload(newItem: NotificationModel): Any = newItem

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int, context: Context, payload: Any?) {
        holder.itemView.let {
            val item = payload as? NotificationModel ?: item
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
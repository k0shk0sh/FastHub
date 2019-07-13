package com.fastaccess.github.ui.adapter.viewholder

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import com.fastaccess.data.persistence.models.NotificationModel
import com.fastaccess.github.R
import com.fastaccess.github.extensions.getColorCompat
import com.fastaccess.github.extensions.getDrawableCompat
import com.fastaccess.github.extensions.timeAgo
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.github.ui.widget.recyclerview.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.notification_main_screen_row_item.view.*

/**
 * Created by Kosh on 12.10.18.
 */

class NotificationsViewHolder(parent: ViewGroup) : BaseViewHolder<NotificationModel>(LayoutInflater.from(parent.context)
    .inflate(R.layout.notification_main_screen_row_item, parent, false)), SwipeToDeleteCallback.AllowSwipeToDeleteDelegate {

    private var item: NotificationModel? = null

    override val drawableStart: Drawable? = null
    override val drawableStartBackground: Int = 0

    override val drawableEnd: Drawable?
        get() {
            if (item?.unread == true) {
                return itemView.context.getDrawableCompat(R.drawable.ic_done)?.apply {
                    setTint(Color.WHITE)
                }
            }
            return null
        }

    override val drawableEndBackground: Int = itemView.context.getColorCompat(R.color.material_green_700)

    override fun bind(item: NotificationModel) {
        this.item = item
        itemView.apply {
            item.subject?.let { subject ->
                notificationTitle.text = subject.title ?: ""
                subject.type?.let { type ->
                    when (type) {
                        "PullRequest" -> typeIcon.setImageResource(R.drawable.ic_pull_requests)
                        "Issue" -> typeIcon.setImageResource(R.drawable.ic_issues)
                        "Commit" -> typeIcon.setImageResource(R.drawable.ic_branch)
                        else -> typeIcon.setImageResource(R.drawable.ic_issues)
                    }
                }
            }
            repoName.text = item.repository?.fullName ?: ""
            dateWithIcon.text = item.updatedAt?.timeAgo()
        }
    }
}
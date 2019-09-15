package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.fastaccess.data.persistence.models.NotificationModel
import com.fastaccess.github.base.adapter.BasePagedAdapter
import com.fastaccess.github.base.adapter.BaseViewHolder
import com.fastaccess.github.ui.adapter.viewholder.NotificationsViewHolder

/**
 * Created by Kosh on 12.10.18.
 */
class UnreadNotificationsAdapter : BasePagedAdapter<NotificationModel>(DIFF_CALLBACK) {

    override fun contentViewHolder(parent: ViewGroup): BaseViewHolder<NotificationModel> = NotificationsViewHolder(parent)

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NotificationModel?>() {
            override fun areItemsTheSame(oldItem: NotificationModel, newItem: NotificationModel): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: NotificationModel, newItem: NotificationModel): Boolean = oldItem == newItem
        }
    }
}
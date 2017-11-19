package com.fastaccess.ui.adapter

import android.view.ViewGroup
import com.fastaccess.R
import com.fastaccess.data.dao.model.FastHubNotification
import com.fastaccess.ui.adapter.viewholder.FastHubNotificationViewHolder
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by Kosh on 02 Jun 2017, 1:36 PM
 */

class FastHubNotificationsAdapter(data: List<FastHubNotification>) : BaseRecyclerAdapter<FastHubNotification,
        FastHubNotificationViewHolder, BaseViewHolder.OnItemClickListener<FastHubNotification>>(data) {

    override fun viewHolder(parent: ViewGroup?, viewType: Int): FastHubNotificationViewHolder {
        return FastHubNotificationViewHolder(BaseViewHolder.getView(parent!!, R.layout.fasthub_notification_row_item), this)
    }

    override fun onBindView(holder: FastHubNotificationViewHolder?, position: Int) {
        holder?.bind(getItem(position))
    }

}
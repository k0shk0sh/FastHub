package com.fastaccess.ui.modules.notification.fasthub

import com.fastaccess.data.dao.model.FastHubNotification
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by Kosh on 19.11.17.
 */
interface FastHubNotificationsMvp {

    interface View : BaseMvp.FAView, BaseViewHolder.OnItemClickListener<FastHubNotification> {
        fun notifyAdapter(items: List<FastHubNotification>?)
    }

    interface Presenter {
        fun getData(): List<FastHubNotification>
        fun load()
    }
}
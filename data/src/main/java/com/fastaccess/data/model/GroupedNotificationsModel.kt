package com.fastaccess.data.model

import com.fastaccess.data.persistence.models.NotificationModel
import com.fastaccess.data.persistence.models.NotificationRepository

/**
 * Created by Kosh on 04.11.18.
 */
data class GroupedNotificationsModel(
    var rowType: Int = HEADER,
    var repo: NotificationRepository? = null,
    var notification: NotificationModel? = null
) {
    companion object {
        const val HEADER = 1
        const val CONTENT = 2
    }
}
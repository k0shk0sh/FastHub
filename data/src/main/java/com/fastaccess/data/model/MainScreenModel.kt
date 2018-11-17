package com.fastaccess.data.model

import com.fastaccess.data.persistence.models.FeedModel
import com.fastaccess.data.persistence.models.MainIssuesPullsModel
import com.fastaccess.data.persistence.models.NotificationModel

data class MainScreenModel(
    var mainScreenModelRowType: MainScreenModelRowType? = null,
    var feed: FeedModel? = null,
    var notificationModel: NotificationModel? = null,
    var issuesPullsModel: MainIssuesPullsModel? = null
)

enum class MainScreenModelRowType(val rowType: Int) {
    FEED_TITLE(0), FEED(1),
    NOTIFICATION_TITLE(2), NOTIFICATION(3),
    ISSUES_TITLE(4), ISSUES(5),
    PRS_TITLE(6), PRS(7)
}
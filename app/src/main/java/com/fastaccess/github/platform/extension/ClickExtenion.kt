package com.fastaccess.github.platform.extension

import androidx.fragment.app.Fragment
import com.fastaccess.data.model.MainScreenModel
import com.fastaccess.data.model.MainScreenModelRowType
import com.fastaccess.data.persistence.models.FeedModel
import com.fastaccess.domain.response.enums.EventsType
import com.fastaccess.github.utils.*
import com.fastaccess.github.utils.extensions.route
import timber.log.Timber

/**
 * Created by Kosh on 2019-01-29.
 */


fun MainScreenModel.onClick(fragment: Fragment) {
    val model = this
    when (model.mainScreenModelRowType) {
        MainScreenModelRowType.FEED_TITLE -> fragment.route(FEEDS_LINK)
        MainScreenModelRowType.FEED -> model.feed?.onClick(fragment)
        MainScreenModelRowType.NOTIFICATION_TITLE -> fragment.route(NOTIFICATION_LINK)
        MainScreenModelRowType.NOTIFICATION -> Timber.e("${model.notificationModel}")
        MainScreenModelRowType.ISSUES_TITLE -> fragment.route(FILTER_ISSUE_LINK)
        MainScreenModelRowType.ISSUES -> fragment.route("$GITHUB_LINK${model.issuesPullsModel?.repoName}/issues/${model.issuesPullsModel?.number}")
        MainScreenModelRowType.PRS_TITLE -> fragment.route(FILTER_PR_LINK)
        MainScreenModelRowType.PRS -> Timber.e("${model.issuesPullsModel}")
    }
}

fun FeedModel.onClick(fragment: Fragment) {
    this.let { feed ->
        when (feed.type) {
            EventsType.IssueCommentEvent -> fragment.route("$GITHUB_LINK${feed.payload?.issue?.repo?.name}" +
                "/issues/${feed.payload?.issue?.number}")
            EventsType.IssuesEvent -> fragment.route("$GITHUB_LINK${feed.payload?.issue?.repo?.fullName}/issues/${feed.payload?.issue?.number}")
            else -> fragment.route(feed.actor?.url)
        }
    }
}
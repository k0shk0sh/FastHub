package com.fastaccess.github.platform.extension

import androidx.fragment.app.Fragment
import com.fastaccess.data.model.MainScreenModel
import com.fastaccess.data.model.MainScreenModelRowType
import com.fastaccess.data.persistence.models.FeedModel
import com.fastaccess.domain.response.enums.EventsType
import com.fastaccess.fasthub.commit.dialog.CommitListBottomSheetDialog
import com.fastaccess.github.base.utils.FEEDS_LINK
import com.fastaccess.github.base.utils.FILTER_ISSUE_LINK
import com.fastaccess.github.base.utils.FILTER_PR_LINK
import com.fastaccess.github.base.utils.NOTIFICATION_LINK
import com.fastaccess.github.extensions.route
import com.fastaccess.github.ui.modules.profile.ProfileActivity
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
        MainScreenModelRowType.ISSUES -> fragment.route("${model.issuesPullsModel?.url}")
        MainScreenModelRowType.PRS_TITLE -> fragment.route(FILTER_PR_LINK)
        MainScreenModelRowType.PRS -> fragment.route(model.issuesPullsModel?.url)
    }
}

fun FeedModel.onClick(fragment: Fragment) {
    Timber.e("$type")
    when (type) {
        EventsType.IssueCommentEvent -> fragment.route("${payload?.issue?.htmlUrl}")
        EventsType.IssuesEvent -> fragment.route("${payload?.issue?.htmlUrl}")
        EventsType.PullRequestEvent -> fragment.route("${payload?.pullRequest?.htmlUrl}")
        EventsType.PullRequestReviewCommentEvent -> fragment.route("${payload?.pullRequest?.htmlUrl}")
        EventsType.PullRequestReviewEvent -> fragment.route("${payload?.pullRequest?.htmlUrl}")
        EventsType.PushEvent -> {
            payload?.commits?.let { list ->
                if (list.size > 1) {
                    val m = payload?.commits?.associateBy({ it.sha?.take(7) ?: "" }, { it.url })
                    CommitListBottomSheetDialog.show(fragment.childFragmentManager, m as HashMap<String, String>)
                } else {
                    fragment.route(list[0].url)
                }
            }

        }
        else -> {

            if(fragment.activity != null && ( ( fragment.activity as Any ) !is ProfileActivity ) ) {
                fragment.route(actor?.url)
            }

        }// TODO(handle click)
    }
}
package com.fastaccess.github.ui.modules.multipurpose

import android.os.Bundle
import com.evernote.android.state.State
import com.fastaccess.data.model.ActivityType
import com.fastaccess.github.R
import com.fastaccess.github.base.deeplink.AppDeepLink
import com.fastaccess.github.ui.modules.feed.fragment.FeedsFragment
import com.fastaccess.github.ui.modules.issuesprs.fragment.FilterIssuePullRequestsFragment
import com.fastaccess.github.ui.modules.notifications.NotificationPagerFragment
import com.fastaccess.github.ui.modules.search.fragment.SearchFragment
import com.fastaccess.github.extensions.fromDeepLink
import com.fastaccess.github.extensions.replace

/**
 * Created by Kosh on 20.10.18.
 */
@AppDeepLink("/me/{what}")
class MultiPurposeActivity : com.fastaccess.github.base.BaseActivity() {

    @State var activityType: ActivityType? = null

    override fun layoutRes(): Int = R.layout.fragment_activity_layout

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            if (!fromDeepLink()) {
                finish()
                return
            }
            activityType = ActivityType.getTypeSafely(intent?.getStringExtra("what"))
            activityType?.let { activityType ->
                when (activityType) {
                    ActivityType.FEEDS -> replace(R.id.container, FeedsFragment.newInstance())
                    ActivityType.NOTIFICATION -> replace(R.id.container, NotificationPagerFragment.newInstance())
                    ActivityType.FILTER_ISSUE, ActivityType.FILTER_PR -> replace(R.id.container,
                        FilterIssuePullRequestsFragment.newInstance(activityType))
                    ActivityType.SEARCH -> replace(R.id.container, SearchFragment.newInstance())
                }
            } ?: run {
                finish()
                return@run
            }
        }
        val activityType = activityType
        if (activityType == null) {
            finish()
            return
        }
    }
}
package com.fastaccess.github.ui.multipurpose

import android.os.Bundle
import com.evernote.android.state.State
import com.fastaccess.data.model.ActivityType
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.platform.deeplink.AppDeepLink
import com.fastaccess.github.ui.modules.feed.fragment.FeedsFragment
import com.fastaccess.github.ui.modules.notifications.fragment.unread.UnreadNotificationsFragment
import com.fastaccess.github.utils.extensions.fromDeepLink
import com.fastaccess.github.utils.extensions.replace
import kotlinx.android.synthetic.main.appbar_center_title_layout.*
import timber.log.Timber

/**
 * Created by Kosh on 20.10.18.
 */
@AppDeepLink("/me/{what}")
class MultiPurposeActivity : BaseActivity() {

    @State var activityType: ActivityType? = null

    override fun layoutRes(): Int = R.layout.toolbar_activity_layout

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
                    ActivityType.NOTIFICATION -> replace(R.id.container, UnreadNotificationsFragment.newInstance())
                }
            } ?: kotlin.run {
                finish()
                return@run
            }
        }
        val activityType = activityType
        if (activityType == null) {
            finish()
            return
        }
        toolbarTitle.text = getString(activityType.activityTitle)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }
}
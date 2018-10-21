package com.fastaccess.github.ui.modules.notifications

import android.os.Bundle
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.platform.deeplink.AppDeepLink
import com.fastaccess.github.ui.modules.feed.fragment.FeedsFragment
import com.fastaccess.github.utils.extensions.replace
import kotlinx.android.synthetic.main.appbar_center_title_layout.*

/**
 * Created by Kosh on 20.10.18.
 */
@AppDeepLink("/me/notifications")
class NotificationsActivity : BaseActivity() {
    override fun layoutRes(): Int = R.layout.toolbar_activity_layout

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {
        toolbarTitle.text = title
        toolbar.setNavigationOnClickListener { onBackPressed() }
        if (savedInstanceState == null) {
            replace(R.id.container, FeedsFragment.newInstance())
        }
    }
}
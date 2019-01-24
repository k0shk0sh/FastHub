package com.fastaccess.github.ui.modules.trending

import android.os.Bundle
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.platform.deeplink.WebDeepLink
import com.fastaccess.github.ui.modules.trending.fragment.TrendingFragment
import com.fastaccess.github.utils.extensions.replace

/**
 * Created by Kosh on 18.08.18.
 */
@WebDeepLink("/trending", "/trending/{lang}")
class TrendingActivity : BaseActivity() {
    override fun layoutRes(): Int = R.layout.activity_main

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {
        if (savedInstanceState == null && intent != null) {
            val lan = intent?.extras?.getString("lang")
            val since = intent?.extras?.getString("since")

            replace(R.id.container, TrendingFragment.newInstance(lan, since))
        }
    }
}
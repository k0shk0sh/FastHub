package com.fastaccess.github.ui.modules.profile

import android.os.Bundle
import com.fastaccess.github.R
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.replace
import com.fastaccess.github.base.deeplink.WebDeepLink
import com.fastaccess.github.ui.modules.profile.fragment.ProfileFragment

/**
 * Created by Kosh on 18.08.18.
 */
@WebDeepLink("/{login}", "/users/{login}")
class ProfileActivity : com.fastaccess.github.base.BaseActivity() {
    override fun layoutRes(): Int = R.layout.activity_main

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {
        if (savedInstanceState == null && intent != null) {
            val login = intent?.extras?.getString("login")
            val tab = intent?.extras?.getString("tab")
            if (login == null) {
                finish()
                return
            }
            replace(R.id.container, ProfileFragment.newInstance(login, tab), ProfileFragment.TAG)
        }
    }

    override fun onBackPressed() {
        (supportFragmentManager.findFragmentByTag(ProfileFragment.TAG) as? ProfileFragment)?.onBackPressed()?.isTrue {
            super.onBackPressed()
        }
    }
}
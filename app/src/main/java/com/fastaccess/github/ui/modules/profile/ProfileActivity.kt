package com.fastaccess.github.ui.modules.profile

import android.os.Bundle
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.platform.deeplink.AppDeepLink
import com.fastaccess.github.platform.deeplink.WebDeepLink
import com.fastaccess.github.ui.modules.profile.fragment.ProfileFragment
import com.fastaccess.github.utils.extensions.replace

/**
 * Created by Kosh on 18.08.18.
 */
@WebDeepLink("/{login}", "/{login}/{page}", "/users/{login}", "/users/{login}/{page}")
@AppDeepLink("/{login}", "/{login}/{page}", "/users/{login}", "/users/{login}/{page}")
class ProfileActivity : BaseActivity() {
    override fun layoutRes(): Int = R.layout.activity_main

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {
        if (savedInstanceState == null && intent != null) {
            val login = intent?.extras?.getString("login")
            val tab = intent?.extras?.getString("tab") ?: intent?.extras?.getString("page")
            if (login == null) {
                finish()
                return
            }
            replace(R.id.container, ProfileFragment.newInstance(login, tab))
        }
    }
}
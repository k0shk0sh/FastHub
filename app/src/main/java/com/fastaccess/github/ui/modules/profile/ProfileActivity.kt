package com.fastaccess.github.ui.modules.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.airbnb.deeplinkdispatch.DeepLink
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.platform.deeplink.AppDeepLink
import com.fastaccess.github.platform.deeplink.WebDeepLink
import com.fastaccess.github.ui.modules.profile.fragment.ProfileFragment
import com.fastaccess.github.utils.BundleConstant
import com.fastaccess.github.utils.extensions.replace
import timber.log.Timber

/**
 * Created by Kosh on 18.08.18.
 */
@WebDeepLink("/{login}")
@AppDeepLink("/{login}")
class ProfileActivity : BaseActivity() {
    override fun layoutRes(): Int = R.layout.activity_main

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {
        if (savedInstanceState == null && intent != null) {
            val isDeepLink = intent?.getBooleanExtra(DeepLink.IS_DEEP_LINK, false)
            val login = intent?.extras?.getString("login")
            Timber.e("${isDeepLink}, ${intent?.extras?.getString("login")}")
            replace(R.id.container, ProfileFragment.newInstance(login ?: intent.getStringExtra(BundleConstant.EXTRA)))
        }
    }

    companion object {
        fun start(context: Context, login: String) {
            context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://github.com/$login")
            })
        }
    }
}
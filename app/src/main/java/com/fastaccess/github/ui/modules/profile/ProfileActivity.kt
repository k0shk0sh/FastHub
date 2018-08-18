package com.fastaccess.github.ui.modules.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.ui.modules.profile.fragment.ProfileFragment
import com.fastaccess.github.utils.BundleConstant
import com.fastaccess.github.utils.extensions.replace

/**
 * Created by Kosh on 18.08.18.
 */
class ProfileActivity : BaseActivity() {
    override fun layoutRes(): Int = R.layout.activity_main

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {
        if (savedInstanceState == null && intent != null) {
            replace(R.id.container, ProfileFragment.newInstance(intent.getStringExtra(BundleConstant.EXTRA)))
        }
    }

    companion object {
        fun start(context: Context, login: String) {
            context.startActivity(Intent(context, ProfileActivity::class.java)
                    .apply {
                        putExtra(BundleConstant.EXTRA, login)
                    })
        }
    }
}
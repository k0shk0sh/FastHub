package com.fastaccess.github.ui.modules.main

import android.os.Bundle
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.replace
import com.fastaccess.github.ui.modules.main.fragment.MainFragment

class MainActivity : BaseActivity() {

    override fun layoutRes(): Int = R.layout.activity_main

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            replace(R.id.container, MainFragment.newInstance(), MainFragment.TAG)
        }
    }

    override fun onBackPressed() {
        (supportFragmentManager.findFragmentByTag(MainFragment.TAG) as? MainFragment)?.onBackPressed()?.isTrue {
            super.onBackPressed()
        }
    }
}
package com.fastaccess.github.ui.modules.main

import android.os.Bundle
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.ui.modules.main.fragment.MainFragment
import com.fastaccess.github.utils.extensions.replace

class MainActivity : BaseActivity() {

    override fun layoutRes(): Int = R.layout.activity_main

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            replace(R.id.container, MainFragment.newInstance(), MainFragment.TAG)
        }
    }
}

package com.fastaccess.github.ui.modules.profile.repos

import android.os.Bundle
import android.view.View
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment

/**
 * Created by Kosh on 06.10.18.
 */
class ProfileReposFragment : BaseFragment() {
    override fun layoutRes(): Int = R.layout.simple_refresh_list_layout
    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {}
}
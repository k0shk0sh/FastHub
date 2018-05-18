package com.fastaccess.github.ui.modules.login

import android.os.Bundle
import android.view.View
import com.fastaccess.github.base.BaseFragment

/**
 * Created by Kosh on 18.05.18.
 */
class BaseAuthLoginFragment : BaseFragment() {
    override fun layoutRes(): Int = 0

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {}

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {}
}
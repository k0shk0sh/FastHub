package com.fastaccess.github.ui.modules.auth.chooser

import android.content.Context
import android.os.Bundle
import android.view.View
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.ui.modules.auth.callback.LoginChooserCallback
import kotlinx.android.synthetic.main.login_chooser_fragment_layout.*

/**
 * Created by Kosh on 19.05.18.
 */
class LoginChooserFragment : BaseFragment() {

    private var callback: LoginChooserCallback? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        callback = context as LoginChooserCallback
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    override fun viewModel(): BaseViewModel? = null
    override fun layoutRes() = R.layout.login_chooser_fragment_layout
    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {}
    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        basicAuth.setOnClickListener { callback?.navToBasicAuth(loginCard) }
        accessToken.setOnClickListener { callback?.navToAccessToken(loginCard) }
        enterprise.setOnClickListener { callback?.navToEnterprise(loginCard) }
    }

    companion object {
        const val TAG = "LoginChooserFragment"
    }
}
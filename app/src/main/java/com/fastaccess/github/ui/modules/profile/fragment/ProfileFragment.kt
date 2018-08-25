package com.fastaccess.github.ui.modules.profile.fragment

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.view.isVisible
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.utils.BundleConstant
import com.fastaccess.github.utils.extensions.clearDarkStatusBarIcons
import com.fastaccess.github.utils.extensions.getColorAttr
import com.fastaccess.github.utils.extensions.me
import com.fastaccess.github.utils.extensions.showHideFabAnimation
import kotlinx.android.synthetic.main.profile_main_fragment_layout.*

/**
 * Created by Kosh on 18.08.18.
 */
class ProfileFragment : BaseFragment() {

    private val loginBundle: String by lazy { arguments?.getString(BundleConstant.EXTRA) ?: "" }

    override fun layoutRes(): Int = R.layout.profile_main_fragment_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        username.text = loginBundle
        actionsHolder.isVisible = loginBundle != me()
        toolbar.navigationIcon?.setTint(requireContext().getColorAttr(android.R.attr.textColorPrimaryInverse))
        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        activity?.let { activity ->
            activity.clearDarkStatusBarIcons()
            activity.window?.statusBarColor = activity.getColorAttr(R.attr.colorAccent)
        }
        Handler().postDelayed({
            swipeRefresh.isRefreshing = false
            userImageView.showHideFabAnimation(true)
        }, 5000)
        swipeRefresh.setOnRefreshListener {
            userImageView.showHideFabAnimation(false)
        }
    }


    companion object {
        fun newInstance(login: String) = ProfileFragment().apply {
            arguments = Bundle().apply {
                putString(BundleConstant.EXTRA, login)
            }
        }
    }
}
package com.fastaccess.github.ui.modules.profile.fragment

import android.os.Bundle
import android.view.View
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.utils.BundleConstant
import kotlinx.android.synthetic.main.profile_main_fragment_layout.*

/**
 * Created by Kosh on 18.08.18.
 */
class ProfileFragment : BaseFragment() {

    override fun layoutRes(): Int = R.layout.profile_main_fragment_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        username.text = arguments?.getString(BundleConstant.EXTRA) ?: ""
    }


    companion object {
        fun newInstance(loign: String) = ProfileFragment().apply {
            arguments = Bundle().apply {
                putString(BundleConstant.EXTRA, loign)
            }
        }
    }
}
package com.fastaccess.github.ui.modules.main.fragment

import android.os.Bundle
import android.view.View
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import kotlinx.android.synthetic.main.appbar_center_title_layout.*

/**
 * Created by Kosh on 12.06.18.
 */
class MainFragment : BaseFragment() {
    override fun layoutRes(): Int = R.layout.main_fragment_layout
    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        toolbarTitle.setText(R.string.app_name)
    }

    companion object {
        const val TAG = "MainFragment"
        fun newInstance(): MainFragment = MainFragment()
    }
}
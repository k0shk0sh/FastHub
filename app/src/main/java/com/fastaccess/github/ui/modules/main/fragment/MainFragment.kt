package com.fastaccess.github.ui.modules.main.fragment

import android.os.Bundle
import android.view.View
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomappbar.BottomAppBar
import kotlinx.android.synthetic.main.appbar_center_title_layout.*
import kotlinx.android.synthetic.main.main_fragment_layout.*

/**
 * Created by Kosh on 12.06.18.
 */
class MainFragment : BaseFragment() {
    override fun layoutRes(): Int = R.layout.main_fragment_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        toolbarTitle.setText(R.string.app_name)
        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val offset = Math.abs(verticalOffset)
            if (offset == appBarLayout.totalScrollRange) {
                bottomBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
            } else {
                bottomBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
            }
        })

        bottomBar.inflateMenu(R.menu.main_menu)
        bottomBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.search -> true
                else -> false
            }
        }
    }

    companion object {
        const val TAG = "MainFragment"
        fun newInstance(): MainFragment = MainFragment()
    }
}
package com.fastaccess.github.base

import android.os.Bundle
import android.view.View
import com.fastaccess.github.base.callback.UpdateTabCount
import com.google.android.material.tabs.TabLayout

/**
 * Created by Kosh on 07.10.18.
 */

abstract class BasePagerFragment : BaseFragment(), UpdateTabCount {
    abstract fun onPageSelected(page: Int)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tab = view.findViewById(R.id.tabs) as? TabLayout
        tab?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) = this@BasePagerFragment.onPageSelected(p0?.position ?: 0)
            override fun onTabUnselected(p0: TabLayout.Tab?) = Unit
            override fun onTabSelected(p0: TabLayout.Tab?) = Unit
        })
    }
}
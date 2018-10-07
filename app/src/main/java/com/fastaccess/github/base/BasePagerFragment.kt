package com.fastaccess.github.base

import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.fastaccess.github.R
import com.readystatesoftware.chuck.internal.support.SimpleOnPageChangedListener

/**
 * Created by Kosh on 07.10.18.
 */

abstract class BasePagerFragment : BaseFragment() {
    abstract fun onPageSelected(page: Int)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager: ViewPager? = view.findViewById(R.id.viewpager)
        viewPager?.addOnPageChangeListener(object : SimpleOnPageChangedListener() {
            override fun onPageSelected(position: Int) {
                this@BasePagerFragment.onPageSelected(position)
            }
        })
    }
}
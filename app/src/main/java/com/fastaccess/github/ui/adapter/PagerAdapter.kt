package com.fastaccess.github.ui.adapter

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.fastaccess.data.model.ViewPagerModel

/**
 * Created by Kosh on 05.10.18.
 */
class PagerAdapter(
        fragmentManager: FragmentManager,
        private val list: ArrayList<ViewPagerModel>
) : FragmentStatePagerAdapter(fragmentManager) {
    override fun getItem(position: Int) = list[position].fragment
    override fun getCount() = list.size
    override fun getPageTitle(position: Int) = list[position].text
    fun getModel(position: Int) = list.getOrNull(position)
    fun getIndex(name: String) = list.indexOfFirst { name == it.text.toString() }
}
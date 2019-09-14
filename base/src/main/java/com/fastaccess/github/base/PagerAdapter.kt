package com.fastaccess.github.base

import android.annotation.SuppressLint
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.fastaccess.data.model.FragmentType
import com.fastaccess.data.model.ViewPagerModel

/**
 * Created by Kosh on 05.10.18.
 */
@SuppressLint("WrongConstant") class PagerAdapter(
    fragmentManager: FragmentManager,
    private val list: ArrayList<ViewPagerModel>
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int) = list[position].fragment
    override fun getCount() = list.size
    override fun getPageTitle(position: Int) = list[position].text
    fun getModel(position: Int) = list.getOrNull(position)
    fun getIndex(type: FragmentType) = list.indexOfFirst { it.fragmentType == type }
}
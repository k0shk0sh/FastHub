package com.fastaccess.ui.base

import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.view.View
import android.widget.TextView
import com.fastaccess.R
import com.fastaccess.data.dao.FragmentPagerAdapterModel
import com.fastaccess.data.dao.model.Login
import com.fastaccess.helper.PrefGetter
import com.fastaccess.ui.adapter.FragmentsPagerAdapter
import com.fastaccess.ui.widgets.AvatarLayout
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.ViewPagerView

/**
 * Created by Kosh on 09 Jul 2017, 3:50 PM
 */
class MainNavDrawer(val view: BaseActivity<*, *>, private val extraNav: NavigationView?) {


    init {
        setupView()
        val viewpager = view.findViewById<ViewPagerView>(R.id.drawerViewPager)
        viewpager?.let {
            it.adapter = FragmentsPagerAdapter(view.getSupportFragmentManager(), FragmentPagerAdapterModel.buildForDrawer(view))
            view.findViewById<TabLayout>(R.id.drawerTabLayout)?.setupWithViewPager(it)
        }
    }

    fun setupView() {
        val view = extraNav?.getHeaderView(0) ?: return
        val userModel: Login? = Login.getUser()
        userModel?.let {
            (view.findViewById<View>(R.id.navAvatarLayout) as AvatarLayout).setUrl(it.avatarUrl, null, false,
                    PrefGetter.isEnterprise())
            (view.findViewById<View>(R.id.navUsername) as TextView).text = it.login
            val navFullName = view.findViewById<FontTextView>(R.id.navFullName)
            when (it.name.isNullOrBlank()) {
                true -> navFullName.visibility = View.GONE
                else -> {
                    navFullName.visibility = View.VISIBLE
                    navFullName.text = it.name
                }
            }
            view.findViewById<View>(R.id.donatedIcon).visibility = if (PrefGetter.hasSupported()) View.VISIBLE else View.GONE
            view.findViewById<View>(R.id.proTextView).visibility = if (PrefGetter.isProEnabled()) View.VISIBLE else View.GONE
        }
    }
}
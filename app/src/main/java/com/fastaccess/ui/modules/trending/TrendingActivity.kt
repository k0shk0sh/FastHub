package com.fastaccess.ui.modules.trending

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.helper.Logger
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.modules.trending.fragment.TrendingFragment


/**
 * Created by Kosh on 30 May 2017, 10:57 PM
 */

class TrendingActivity : BaseActivity<TrendingMvp.View, TrendingPresenter>(), TrendingMvp.View {

    private var trendingFragment: TrendingFragment? = null
    val navMenu by lazy { findViewById(R.id.navMenu) as NavigationView }
    val daily by lazy { findViewById(R.id.daily) as TextView }
    val weekly by lazy { findViewById(R.id.weekly) as TextView }
    val monthly by lazy { findViewById(R.id.monthly) as TextView }
    val drawerLayout by lazy { findViewById(R.id.drawer) as DrawerLayout }

    @State var selectedTitle: String = ""

    fun onDailyClicked() {
        Logger.e()
        daily.isSelected = true
        weekly.isSelected = false
        monthly.isSelected = false
        setValues()
    }

    fun onWeeklyClicked() {
        weekly.isSelected = true
        daily.isSelected = false
        monthly.isSelected = false
        setValues()
    }

    fun onMonthlyClicked() {
        monthly.isSelected = true
        weekly.isSelected = false
        daily.isSelected = false
        setValues()
    }

    override fun layout(): Int {
        return R.layout.trending_activity_layout
    }

    override fun isTransparent(): Boolean {
        return true
    }

    override fun canBack(): Boolean {
        return true
    }

    override fun isSecured(): Boolean {
        return false
    }

    override fun providePresenter(): TrendingPresenter {
        return TrendingPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.e(selectedTitle)
        trendingFragment = supportFragmentManager.findFragmentById(R.id.trendingFragment) as TrendingFragment?
        daily.setOnClickListener { onDailyClicked() }
        weekly.setOnClickListener { onWeeklyClicked() }
        monthly.setOnClickListener { onMonthlyClicked() }
        presenter.onLoadLanguage()
        navMenu.setNavigationItemSelectedListener(this)
        if (savedInstanceState == null) {
            daily.isSelected = true
            setValues()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.trending_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu) {
            drawerLayout.openDrawer(Gravity.END)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAppend(title: String) {
        navMenu.menu.add(R.id.languageGroup, title.hashCode(), Menu.NONE, title)
                .setCheckable(true)
                .isChecked = title == selectedTitle
    }

    private fun onItemClicked(item: MenuItem?): Boolean {
        selectedTitle = item?.title.toString()
        setValues()
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        closeDrawerLayout()
        return onItemClicked(item)
    }

    private fun closeDrawerLayout() {
        drawerLayout.closeDrawer(Gravity.END)
    }

    private fun setValues() {
        closeDrawerLayout()
        trendingFragment?.onSetQuery(selectedTitle, getSince())
    }

    private fun getSince(): String {
        when {
            daily.isSelected -> return "daily"
            weekly.isSelected -> return "weekly"
            monthly.isSelected -> return "monthly"
            else -> return "daily"
        }
    }
}
package com.fastaccess.ui.modules.trending

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.Logger
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.modules.main.MainActivity
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

    companion object {
        fun getTrendingIntent(context: Context, lang: String?, query: String?): Intent {
            val intent = Intent(context, TrendingActivity::class.java)
            intent.putExtras(Bundler.start()
                    .put(BundleConstant.EXTRA, lang)
                    .put(BundleConstant.EXTRA_TWO, query)
                    .end())
            return intent
        }
    }

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
        trendingFragment = supportFragmentManager.findFragmentById(R.id.trendingFragment) as TrendingFragment?
        daily.setOnClickListener { onDailyClicked() }
        weekly.setOnClickListener { onWeeklyClicked() }
        monthly.setOnClickListener { onMonthlyClicked() }
        navMenu.setNavigationItemSelectedListener(this)
        setupIntent(savedInstanceState)
        presenter.onLoadLanguage()
        onSelectTrending()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.trending_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu -> {
                drawerLayout.openDrawer(Gravity.END)
                return true
            }
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onAppend(title: String) {
        navMenu.menu.add(R.id.languageGroup, title.hashCode(), Menu.NONE, title)
                .setCheckable(true)
                .isChecked = title.toLowerCase() == selectedTitle.toLowerCase()
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
        Logger.e(selectedTitle, getSince())
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

    private fun setupIntent(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            if (intent != null && intent.extras != null) {
                val bundle = intent.extras
                if (bundle != null) {
                    val lang: String = bundle.getString(BundleConstant.EXTRA)
                    val query: String = bundle.getString(BundleConstant.EXTRA_TWO)
                    if (!lang.isNullOrEmpty()) {
                        selectedTitle = lang
                    }
                    if (!query.isNullOrEmpty()) {
                        when (query.toLowerCase()) {
                            "daily" -> daily.isSelected = true
                            "weekly" -> weekly.isSelected = true
                            "monthly" -> monthly.isSelected = true
                        }
                    } else {
                        daily.isSelected = true
                    }
                } else {
                    daily.isSelected = true
                }
            } else {
                daily.isSelected = true
            }
            setValues()
        }
    }
}
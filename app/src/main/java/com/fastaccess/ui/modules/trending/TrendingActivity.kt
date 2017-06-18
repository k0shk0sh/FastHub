package com.fastaccess.ui.modules.trending

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.support.annotation.ColorInt
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.text.Editable
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import butterknife.OnClick
import butterknife.OnEditorAction
import butterknife.OnTextChanged
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.helper.*
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.modules.main.MainActivity
import com.fastaccess.ui.modules.trending.fragment.TrendingFragment
import com.fastaccess.ui.widgets.FontEditText
import com.fastaccess.ui.widgets.bindView


/**
 * Created by Kosh on 30 May 2017, 10:57 PM
 */

class TrendingActivity : BaseActivity<TrendingMvp.View, TrendingPresenter>(), TrendingMvp.View {
    private var trendingFragment: TrendingFragment? = null

    val navMenu: NavigationView by bindView(R.id.navMenu)
    val daily: TextView by bindView(R.id.daily)
    val weekly: TextView by bindView(R.id.weekly)
    val monthly: TextView by bindView(R.id.monthly)
    val drawerLayout: DrawerLayout by bindView(R.id.drawer)
    val clear: View by bindView(R.id.clear)
    val searchEditText: FontEditText by bindView(R.id.searchEditText)


    @State var selectedTitle: String = "All Language"

    @OnTextChanged(value = R.id.searchEditText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED) fun onTextChange(s: Editable) {
        val text = s.toString()
        if (text.isEmpty()) {
            AnimHelper.animateVisibility(clear, false)
        } else {
            AnimHelper.animateVisibility(clear, true)
        }
    }

    @OnEditorAction(R.id.searchEditText) fun onSearch(): Boolean {
        presenter.onFilterLanguage(InputHelper.toString(searchEditText))
        ViewHelper.hideKeyboard(searchEditText)
        return true
    }

    @OnClick(R.id.daily) fun onDailyClicked() {
        Logger.e()
        daily.isSelected = true
        weekly.isSelected = false
        monthly.isSelected = false
        setValues()
    }

    @OnClick(R.id.weekly) fun onWeeklyClicked() {
        weekly.isSelected = true
        daily.isSelected = false
        monthly.isSelected = false
        setValues()
    }

    @OnClick(R.id.monthly) fun onMonthlyClicked() {
        monthly.isSelected = true
        weekly.isSelected = false
        daily.isSelected = false
        setValues()
    }

    @OnClick(R.id.clear) fun onClearSearch() {
        ViewHelper.hideKeyboard(searchEditText)
        searchEditText.setText("")
        onClearMenu()
        presenter.onLoadLanguage()
    }

    override fun layout(): Int = R.layout.trending_activity_layout

    override fun isTransparent(): Boolean = true

    override fun canBack(): Boolean = true

    override fun isSecured(): Boolean = false

    override fun providePresenter(): TrendingPresenter = TrendingPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navMenu.itemIconTintList = null
        trendingFragment = supportFragmentManager.findFragmentById(R.id.trendingFragment) as TrendingFragment?
        navMenu.setNavigationItemSelectedListener({ item ->
            closeDrawerLayout()
            onItemClicked(item)
        })
        setupIntent(savedInstanceState)
        if (savedInstanceState == null) {
            presenter.onLoadLanguage()
        } else {
             Handler().postDelayed({
                 Logger.e(searchEditText.text)
                 if (InputHelper.isEmpty(searchEditText)) { //searchEditText.text is always empty even tho there is a text in it !!!!!!!
                     presenter.onLoadLanguage()
                 } else {
                     presenter.onFilterLanguage(InputHelper.toString(searchEditText))
                 }
             }, 300)
        }
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

    override fun onAppend(title: String, color: Int) {
        navMenu.menu.add(R.id.languageGroup, title.hashCode(), Menu.NONE, title)
                .setCheckable(true)
                .setIcon(createOvalShape(color))
                .isChecked = title.toLowerCase() == selectedTitle.toLowerCase()
    }

    override fun onClearMenu() {
        navMenu.menu.clear()
    }

    private fun onItemClicked(item: MenuItem?): Boolean {
        when (item?.title.toString()) {
            "All Language" -> selectedTitle = ""
            else -> selectedTitle = item?.title.toString()
        }
        Logger.e(selectedTitle)
        setValues()
        return true
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

    private fun createOvalShape(@ColorInt color: Int): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        drawable.setSize(24, 24)
        drawable.setColor(if (color == 0) Color.LTGRAY else color)
        return drawable
    }

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
}
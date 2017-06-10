package com.fastaccess.ui.modules.theme

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.view.View
import butterknife.bindView
import com.fastaccess.R
import com.fastaccess.data.dao.FragmentPagerAdapterModel
import com.fastaccess.ui.adapter.FragmentsPagerAdapter
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.theme.fragment.ThemeFragmentMvp
import com.fastaccess.ui.widgets.ViewPagerView

/**
 * Created by Kosh on 08 Jun 2017, 10:34 PM
 */

class ThemeActivity : BaseActivity<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>(), ThemeFragmentMvp.ThemeListener {

    val tabs: TabLayout by bindView(R.id.tabs)
    val pager: ViewPagerView by bindView(R.id.pager)

    override fun layout(): Int {
        return R.layout.tabbed_viewpager
    }

    override fun isTransparent(): Boolean {
        return false
    }

    override fun canBack(): Boolean {
        return true
    }

    override fun isSecured(): Boolean {
        return false
    }

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> {
        return BasePresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            onChangePrimaryDarkColor(ContextCompat.getColor(this, R.color.light_primary), true)
        }
        tabs.visibility = View.GONE
        pager.adapter = FragmentsPagerAdapter(supportFragmentManager, FragmentPagerAdapterModel.buildForTheme())
    }

    override fun onChangePrimaryDarkColor(color: Int, darkIcons: Boolean) {
        window.statusBarColor = color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pager.systemUiVisibility = if (darkIcons) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else 0
        }
    }

    override fun onThemeApplied() {
        setResult(Activity.RESULT_OK)
        showMessage(R.string.success, R.string.change_theme_warning)
        finish()
    }
}
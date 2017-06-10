package com.fastaccess.ui.modules.theme

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewAnimationUtils
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

    val pager: ViewPagerView by bindView(R.id.pager)
    val parentLayout: View by bindView(R.id.parentLayout)

    override fun layout(): Int {
        return R.layout.theme_viewpager
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
        pager.adapter = FragmentsPagerAdapter(supportFragmentManager, FragmentPagerAdapterModel.buildForTheme())
        pager.clipToPadding = false
        val partialWidth = resources.getDimensionPixelSize(R.dimen.spacing_s_large)
        val pageMargin = resources.getDimensionPixelSize(R.dimen.spacing_normal)
        val pagerPadding = partialWidth + pageMargin
        pager.pageMargin = pageMargin
        pager.setPageTransformer(true, CardsPagerTransformerBasic(4, 10))
        pager.setPadding(pagerPadding, pagerPadding, pagerPadding, pagerPadding)

    }

    override fun onChangePrimaryDarkColor(color: Int, darkIcons: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val view = window.decorView
            view.systemUiVisibility = if (darkIcons) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else view.systemUiVisibility and View
                    .SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        val cx = parentLayout.width / 2
        val cy = parentLayout.height / 2
        if (parentLayout.isAttachedToWindow) {
            val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
            val anim = ViewAnimationUtils.createCircularReveal(parentLayout, cx, cy, 0f, finalRadius)
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    window?.statusBarColor = color
                }
            })
            parentLayout.setBackgroundColor(color)
            anim.start()
        } else {
            parentLayout.setBackgroundColor(color)
            window.statusBarColor = color
        }
    }

    override fun onThemeApplied() {
        setResult(Activity.RESULT_OK)
        showMessage(R.string.success, R.string.change_theme_warning)
        finish()
    }

    inner class CardsPagerTransformerBasic(private val baseElevation: Int, private val raisingElevation: Int) : ViewPager.PageTransformer {
        override fun transformPage(page: View?, position: Float) {
            val absPosition = Math.abs(position)
            if (absPosition >= 1) {
                page?.elevation = baseElevation.toFloat()
            } else {
                page?.elevation = (1 - absPosition) * raisingElevation + baseElevation
            }
        }


    }
}
package com.fastaccess.ui.modules.theme.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.fastaccess.R
import com.fastaccess.helper.*
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.modules.main.donation.DonateActivity
import com.fastaccess.ui.modules.main.premium.PremiumActivity
import com.fastaccess.ui.widgets.SpannableBuilder

/**
 * Created by Kosh on 08 Jun 2017, 10:53 PM
 */

class ThemeFragment : BaseFragment<ThemeFragmentMvp.View, ThemeFragmentPresenter>(), ThemeFragmentMvp.View {

    @BindView(R.id.apply) lateinit var apply: FloatingActionButton
    @BindView(R.id.toolbar) lateinit var toolbar: Toolbar

    private var unbinder: Unbinder? = null

    private val THEME = "appTheme"
    private var primaryDarkColor: Int = 0
    private var theme: Int = 0
    private var themeListener: ThemeFragmentMvp.ThemeListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        themeListener = context as ThemeFragmentMvp.ThemeListener
    }

    override fun onDetach() {
        themeListener = null
        super.onDetach()
    }

    override fun fragmentLayout(): Int = 0

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        apply.setOnClickListener {
            setTheme()
        }
        if (isPremiumTheme()) {
            toolbar.title = SpannableBuilder.builder().foreground(getString(R.string.premium_theme), Color.RED)
        }
        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        theme = arguments!!.getInt(BundleConstant.ITEM)
        val contextThemeWrapper = ContextThemeWrapper(activity, theme)
        primaryDarkColor = ViewHelper.getPrimaryDarkColor(contextThemeWrapper)
        val localInflater = inflater.cloneInContext(contextThemeWrapper)
        val view = localInflater.inflate(R.layout.theme_layout, container, false)!!
        unbinder = ButterKnife.bind(this, view)
        return view
    }

    override fun providePresenter(): ThemeFragmentPresenter {
        return ThemeFragmentPresenter()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (themeListener != null) {
                themeListener!!.onChangePrimaryDarkColor(primaryDarkColor, theme == R.style.ThemeLight)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val productKey = data?.getStringExtra(BundleConstant.ITEM)
            productKey?.let {
                when (it) {
                    getString(R.string.amlod_theme_purchase) -> setTheme(getString(R.string.amlod_theme_mode))
                    getString(R.string.midnight_blue_theme_purchase) -> setTheme(getString(R.string.mid_night_blue_theme_mode))
                    getString(R.string.theme_bluish_purchase) -> setTheme(getString(R.string.bluish_theme))
                }
            }
        }
    }

    companion object {
        fun newInstance(style: Int): ThemeFragment {
            val fragment = ThemeFragment()
            fragment.arguments = Bundler.start()
                    .put(BundleConstant.ITEM, style)
                    .end()
            return fragment
        }
    }

    private fun setTheme() {
        when (theme) {
            R.style.ThemeLight -> setTheme(getString(R.string.light_theme_mode))
            R.style.ThemeDark -> setTheme(getString(R.string.dark_theme_mode))
            R.style.ThemeAmlod -> applyAmlodTheme()
            R.style.ThemeBluish -> applyBluishTheme()
            R.style.ThemeMidnight -> applyMidnightTheme()
        }
    }

    private fun applyBluishTheme() {
        if (!isGoogleSupported()) return
        if (PrefGetter.isBluishEnabled() || PrefGetter.isProEnabled()) {
            setTheme(getString(R.string.bluish_theme))
        } else {
            DonateActivity.start(this, getString(R.string.theme_bluish_purchase))
        }
    }

    private fun applyAmlodTheme() {
        if (!isGoogleSupported()) return
        if (PrefGetter.isAmlodEnabled() || PrefGetter.isProEnabled()) {
            setTheme(getString(R.string.amlod_theme_mode))
        } else {
            DonateActivity.start(this, getString(R.string.amlod_theme_purchase))
        }
    }

    private fun applyMidnightTheme() {
        if (!isGoogleSupported()) return
        if (PrefGetter.isProEnabled() || PrefGetter.isAllFeaturesUnlocked()) {
            setTheme(getString(R.string.mid_night_blue_theme_mode))
        } else {
            PremiumActivity.startActivity(context!!)
        }
    }

    private fun setTheme(theme: String) {
        PrefHelper.set(THEME, theme)
        themeListener?.onThemeApplied()
    }

    private fun isPremiumTheme(): Boolean = theme != R.style.ThemeLight && theme != R.style.ThemeDark

    private fun isGoogleSupported(): Boolean {
        if (AppHelper.isGoogleAvailable(context!!)) {
            return true
        }
        showErrorMessage(getString(R.string.common_google_play_services_unsupported_text))
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder?.unbind()
    }
}

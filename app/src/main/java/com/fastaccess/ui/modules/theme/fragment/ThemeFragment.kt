package com.fastaccess.ui.modules.theme.fragment

import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fastaccess.R
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.PrefHelper
import com.fastaccess.helper.ViewHelper
import com.fastaccess.ui.base.BaseFragment

/**
 * Created by Kosh on 08 Jun 2017, 10:53 PM
 */

class ThemeFragment : BaseFragment<ThemeFragmentMvp.View, ThemeFragmentPresenter>(), ThemeFragmentMvp.View {

    val apply: FloatingActionButton by lazy { view!!.findViewById<FloatingActionButton>(R.id.apply) }

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

    override fun fragmentLayout(): Int {
        return 0
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        apply.setOnClickListener { PrefHelper.set("appTheme", getString(R.string.amlod_theme_mode)) }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        theme = arguments.getInt(BundleConstant.ITEM)
        val contextThemeWrapper = ContextThemeWrapper(activity, theme)
        primaryDarkColor = ViewHelper.getPrimaryDarkColor(contextThemeWrapper)
        val localInflater = inflater?.cloneInContext(contextThemeWrapper)
        val view = localInflater?.inflate(R.layout.theme_layout, container, false)
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

    companion object {
        fun newInstance(style: Int): ThemeFragment {
            val fragment = ThemeFragment()
            fragment.arguments = Bundler.start()
                    .put(BundleConstant.ITEM, style)
                    .end()
            return fragment
        }
    }
}

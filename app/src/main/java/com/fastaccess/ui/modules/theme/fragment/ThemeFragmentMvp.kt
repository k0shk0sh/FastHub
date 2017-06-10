package com.fastaccess.ui.modules.theme.fragment

import android.support.annotation.ColorInt

import com.fastaccess.ui.base.mvp.BaseMvp

/**
 * Created by Kosh on 08 Jun 2017, 10:52 PM
 */

interface ThemeFragmentMvp {

    interface ThemeListener {
        fun onChangePrimaryDarkColor(@ColorInt color: Int, darkIcons: Boolean)

        fun onThemeApplied()
    }

    interface View : BaseMvp.FAView

    interface Presenter
}

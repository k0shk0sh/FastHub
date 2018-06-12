package com.fastaccess.github.base.engine

import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.ui.modules.auth.LoginChooserActivity
import com.fastaccess.resources.R

/**
 * Created by Kosh on 18.05.18.
 */
object ThemeEngine {

    fun setTheme(activity: BaseActivity, theme: Int) {
        if (!hasTheme(activity)) {
            activity.setTheme(when (theme) {
                1 -> R.style.ThemeLight
                2 -> R.style.ThemeDark
                3 -> R.style.ThemeAmlod
                4 -> R.style.ThemeBluish
                else -> R.style.ThemeLight
            })
        }
    }

    private fun hasTheme(activity: BaseActivity) = activity is LoginChooserActivity
}
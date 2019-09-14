package com.fastaccess.github.base.engine

import android.graphics.Color
import androidx.annotation.ColorInt
import com.fastaccess.github.base.BaseActivity
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


    fun isLightTheme(theme: Int) = theme == R.style.ThemeLight

    @ColorInt fun getCodeBackground(theme: Int): Int {
        return when (theme) {
            2 -> Color.parseColor("#22252A")
            3 -> Color.parseColor("#0B162A")
            4 -> Color.parseColor("#111C2C")
            else -> Color.parseColor("#EEEEEE")
        }
    }

    private fun hasTheme(activity: BaseActivity) = activity.hasTheme()
}
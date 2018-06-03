package com.fastaccess.github.base.engine

import androidx.annotation.StyleRes
import com.fastaccess.data.storage.FastHubSharedPreference
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.ui.modules.auth.LoginChooserActivity
import com.fastaccess.resources.R
import javax.inject.Inject

/**
 * Created by Kosh on 18.05.18.
 */
class ThemeEngine @Inject constructor(private val preference: FastHubSharedPreference) {

    @StyleRes fun getTheme(activity: BaseActivity): Int {
        if (!hasTheme(activity)) {
            val themeName = preference.get(THEME_KEY, null) as String?
            themeName?.let {
                val resource = activity.resources
                when {
                    it.equals(resource.getString(R.string.light_theme_mode), true) -> R.style.ThemeLight
                    it.equals(resource.getString(R.string.dark_theme_mode), true) -> R.style.ThemeDark
                    it.equals(resource.getString(R.string.amlod_theme_mode), true) -> R.style.ThemeAmlod
                    it.equals(resource.getString(R.string.blue_theme_mode), true) -> R.style.ThemeBluish
                    else -> R.style.ThemeLight
                }
            } ?: R.style.ThemeLight
        }
        return 0
    }

    private fun hasTheme(activity: BaseActivity) = activity is LoginChooserActivity

    companion object {
        private const val THEME_KEY = "appTheme"
    }
}
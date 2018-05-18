package com.fastaccess.github.base.engine

import android.content.res.Resources
import androidx.annotation.StyleRes
import com.fastaccess.data.storage.FastHubSharedPreference
import com.fastaccess.resources.R
import javax.inject.Inject

/**
 * Created by Kosh on 18.05.18.
 */
class ThemeEngine @Inject constructor(private val resource: Resources,
                                      private val preference: FastHubSharedPreference) {

    @StyleRes fun getTheme(): Int {
        if (!hasTheme()) {
            val themeName = preference.get(THEME_KEY, null) as String?
            themeName?.let {
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

    private fun hasTheme() = false //TODO

    /* (activity is LoginChooserActivity || activity is LoginActivity ||
            activity is DonateActivity)*/
    companion object {
        private const val THEME_KEY = "appTheme"
    }
}
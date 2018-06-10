package com.fastaccess.github.base.engine

import androidx.annotation.StyleRes
import com.fastaccess.data.storage.FastHubSharedPreference
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.ui.modules.auth.LoginChooserActivity
import com.fastaccess.github.utils.extensions.theme
import com.fastaccess.resources.R
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kosh on 18.05.18.
 */
class ThemeEngine @Inject constructor(private val preference: FastHubSharedPreference) {

    @StyleRes fun getTheme(activity: BaseActivity): Int {
        if (!hasTheme(activity)) {
            val theme = preference.theme
            Timber.e("$theme")
            when (theme) {
                1 -> R.style.ThemeLight
                2 -> R.style.ThemeDark
                3 -> R.style.ThemeAmlod
                4 -> R.style.ThemeBluish
                else -> R.style.ThemeLight
            }
        }
        return 0
    }

    private fun hasTheme(activity: BaseActivity) = activity is LoginChooserActivity
}
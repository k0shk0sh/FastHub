package com.fastaccess.provider.theme

import android.app.Activity
import android.app.ActivityManager
import android.graphics.BitmapFactory
import android.support.annotation.StyleRes
import com.danielstone.materialaboutlibrary.MaterialAboutActivity
import com.fastaccess.R
import com.fastaccess.helper.Logger
import com.fastaccess.helper.PrefGetter
import com.fastaccess.helper.ViewHelper
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.modules.login.LoginActivity
import com.fastaccess.ui.modules.login.chooser.LoginChooserActivity
import com.fastaccess.ui.modules.main.donation.DonateActivity

/**
 * Created by Kosh on 07 Jun 2017, 6:52 PM
 */

object ThemeEngine {

    fun apply(activity: BaseActivity<*, *>) {
        if (hasTheme(activity)) {
            return
        }
        val themeMode = PrefGetter.getThemeType(activity)
        val themeColor = PrefGetter.getThemeColor(activity)
        activity.setTheme(getTheme(themeMode, themeColor))
        setTaskDescription(activity)
        applyNavBarColor(activity)
    }

    private fun applyNavBarColor(activity: Activity) {
        if (!PrefGetter.isNavBarTintingDisabled() && PrefGetter.getThemeType() != PrefGetter.LIGHT) {
            activity.window.navigationBarColor = ViewHelper.getPrimaryColor(activity)
        }
    }

    fun applyForAbout(activity: MaterialAboutActivity) {
        val themeMode = PrefGetter.getThemeType(activity)
        when (themeMode) {
            PrefGetter.LIGHT -> activity.setTheme(R.style.AppTheme_AboutActivity_Light)
            PrefGetter.DARK -> activity.setTheme(R.style.AppTheme_AboutActivity_Dark)
            PrefGetter.AMLOD -> activity.setTheme(R.style.AppTheme_AboutActivity_Amlod)
            PrefGetter.MID_NIGHT_BLUE -> activity.setTheme(R.style.AppTheme_AboutActivity_Midnight)
            PrefGetter.BLUISH -> activity.setTheme(R.style.AppTheme_AboutActivity_Bluish)
        }
        setTaskDescription(activity)
    }

    fun applyDialogTheme(activity: BaseActivity<*, *>) {
        val themeMode = PrefGetter.getThemeType(activity)
        val themeColor = PrefGetter.getThemeColor(activity)
        activity.setTheme(getDialogTheme(themeMode, themeColor))
        setTaskDescription(activity)
    }

    @StyleRes private fun getTheme(themeMode: Int, themeColor: Int): Int {
        Logger.e(themeMode, themeColor)
        // I wish if I could simplify this :'( too many cases for the love of god.
        when (themeMode) {
            PrefGetter.LIGHT -> when (themeColor) {
                PrefGetter.RED -> return R.style.ThemeLight_Red
                PrefGetter.PINK -> return R.style.ThemeLight_Pink
                PrefGetter.PURPLE -> return R.style.ThemeLight_Purple
                PrefGetter.DEEP_PURPLE -> return R.style.ThemeLight_DeepPurple
                PrefGetter.INDIGO -> return R.style.ThemeLight_Indigo
                PrefGetter.BLUE -> return R.style.ThemeLight
                PrefGetter.LIGHT_BLUE -> return R.style.ThemeLight_LightBlue
                PrefGetter.CYAN -> return R.style.ThemeLight_Cyan
                PrefGetter.TEAL -> return R.style.ThemeLight_Teal
                PrefGetter.GREEN -> return R.style.ThemeLight_Green
                PrefGetter.LIGHT_GREEN -> return R.style.ThemeLight_LightGreen
                PrefGetter.LIME -> return R.style.ThemeLight_Lime
                PrefGetter.YELLOW -> return R.style.ThemeLight_Yellow
                PrefGetter.AMBER -> return R.style.ThemeLight_Amber
                PrefGetter.ORANGE -> return R.style.ThemeLight_Orange
                PrefGetter.DEEP_ORANGE -> return R.style.ThemeLight_DeepOrange
                else -> return R.style.ThemeLight
            }
            PrefGetter.DARK -> when (themeColor) {
                PrefGetter.RED -> return R.style.ThemeDark_Red
                PrefGetter.PINK -> return R.style.ThemeDark_Pink
                PrefGetter.PURPLE -> return R.style.ThemeDark_Purple
                PrefGetter.DEEP_PURPLE -> return R.style.ThemeDark_DeepPurple
                PrefGetter.INDIGO -> return R.style.ThemeDark_Indigo
                PrefGetter.BLUE -> return R.style.ThemeDark
                PrefGetter.LIGHT_BLUE -> return R.style.ThemeDark_LightBlue
                PrefGetter.CYAN -> return R.style.ThemeDark_Cyan
                PrefGetter.GREEN -> return R.style.ThemeDark_Green
                PrefGetter.TEAL -> return R.style.ThemeDark_Teal
                PrefGetter.LIGHT_GREEN -> return R.style.ThemeDark_LightGreen
                PrefGetter.LIME -> return R.style.ThemeDark_Lime
                PrefGetter.YELLOW -> return R.style.ThemeDark_Yellow
                PrefGetter.AMBER -> return R.style.ThemeDark_Amber
                PrefGetter.ORANGE -> return R.style.ThemeDark_Orange
                PrefGetter.DEEP_ORANGE -> return R.style.ThemeDark_DeepOrange
                else -> return R.style.ThemeDark
            }
            PrefGetter.AMLOD -> when (themeColor) {
                PrefGetter.RED -> return R.style.ThemeAmlod_Red
                PrefGetter.PINK -> return R.style.ThemeAmlod_Pink
                PrefGetter.PURPLE -> return R.style.ThemeAmlod_Purple
                PrefGetter.DEEP_PURPLE -> return R.style.ThemeAmlod_DeepPurple
                PrefGetter.INDIGO -> return R.style.ThemeAmlod_Indigo
                PrefGetter.BLUE -> return R.style.ThemeAmlod
                PrefGetter.LIGHT_BLUE -> return R.style.ThemeAmlod_LightBlue
                PrefGetter.CYAN -> return R.style.ThemeAmlod_Cyan
                PrefGetter.TEAL -> return R.style.ThemeAmlod_Teal
                PrefGetter.GREEN -> return R.style.ThemeAmlod_Green
                PrefGetter.LIGHT_GREEN -> return R.style.ThemeAmlod_LightGreen
                PrefGetter.LIME -> return R.style.ThemeAmlod_Lime
                PrefGetter.YELLOW -> return R.style.ThemeAmlod_Yellow
                PrefGetter.AMBER -> return R.style.ThemeAmlod_Amber
                PrefGetter.ORANGE -> return R.style.ThemeAmlod_Orange
                PrefGetter.DEEP_ORANGE -> return R.style.ThemeAmlod_DeepOrange
                else -> return R.style.ThemeAmlod
            }
            PrefGetter.MID_NIGHT_BLUE -> when (themeColor) {
                PrefGetter.RED -> return R.style.ThemeMidnight_Red
                PrefGetter.PINK -> return R.style.ThemeMidnight_Pink
                PrefGetter.PURPLE -> return R.style.ThemeMidnight_Purple
                PrefGetter.DEEP_PURPLE -> return R.style.ThemeMidnight_DeepPurple
                PrefGetter.INDIGO -> return R.style.ThemeMidnight_Indigo
                PrefGetter.BLUE -> return R.style.ThemeMidnight
                PrefGetter.LIGHT_BLUE -> return R.style.ThemeMidnight_LightBlue
                PrefGetter.CYAN -> return R.style.ThemeMidnight_Cyan
                PrefGetter.TEAL -> return R.style.ThemeMidnight_Teal
                PrefGetter.GREEN -> return R.style.ThemeMidnight_Green
                PrefGetter.LIGHT_GREEN -> return R.style.ThemeMidnight_LightGreen
                PrefGetter.LIME -> return R.style.ThemeMidnight_Lime
                PrefGetter.YELLOW -> return R.style.ThemeMidnight_Yellow
                PrefGetter.AMBER -> return R.style.ThemeMidnight_Amber
                PrefGetter.ORANGE -> return R.style.ThemeMidnight_Orange
                PrefGetter.DEEP_ORANGE -> return R.style.ThemeMidnight_DeepOrange
                else -> return R.style.ThemeMidnight
            }
            PrefGetter.BLUISH -> when (themeColor) {
                PrefGetter.RED -> return R.style.ThemeBluish_Red
                PrefGetter.PINK -> return R.style.ThemeBluish_Pink
                PrefGetter.PURPLE -> return R.style.ThemeBluish_Purple
                PrefGetter.DEEP_PURPLE -> return R.style.ThemeBluish_DeepPurple
                PrefGetter.INDIGO -> return R.style.ThemeBluish_Indigo
                PrefGetter.BLUE -> return R.style.ThemeBluish
                PrefGetter.LIGHT_BLUE -> return R.style.ThemeBluish_LightBlue
                PrefGetter.CYAN -> return R.style.ThemeBluish_Cyan
                PrefGetter.TEAL -> return R.style.ThemeBluish_Teal
                PrefGetter.GREEN -> return R.style.ThemeBluish_Green
                PrefGetter.LIGHT_GREEN -> return R.style.ThemeBluish_LightGreen
                PrefGetter.LIME -> return R.style.ThemeBluish_Lime
                PrefGetter.YELLOW -> return R.style.ThemeBluish_Yellow
                PrefGetter.AMBER -> return R.style.ThemeBluish_Amber
                PrefGetter.ORANGE -> return R.style.ThemeBluish_Orange
                PrefGetter.DEEP_ORANGE -> return R.style.ThemeBluish_DeepOrange
                else -> return R.style.ThemeBluish
            }
        }
        return R.style.ThemeLight
    }

    @StyleRes private fun getDialogTheme(themeMode: Int, themeColor: Int): Int {
        when (themeMode) {
            PrefGetter.LIGHT -> when (themeColor) {
                PrefGetter.RED -> return R.style.DialogThemeLight_Red
                PrefGetter.PINK -> return R.style.DialogThemeLight_Pink
                PrefGetter.PURPLE -> return R.style.DialogThemeLight_Purple
                PrefGetter.DEEP_PURPLE -> return R.style.DialogThemeLight_DeepPurple
                PrefGetter.INDIGO -> return R.style.DialogThemeLight_Indigo
                PrefGetter.BLUE -> return R.style.DialogThemeLight
                PrefGetter.LIGHT_BLUE -> return R.style.DialogThemeLight_LightBlue
                PrefGetter.CYAN -> return R.style.DialogThemeLight_Cyan
                PrefGetter.TEAL -> return R.style.DialogThemeLight_Teal
                PrefGetter.GREEN -> return R.style.DialogThemeLight_Green
                PrefGetter.LIGHT_GREEN -> return R.style.DialogThemeLight_LightGreen
                PrefGetter.LIME -> return R.style.DialogThemeLight_Lime
                PrefGetter.YELLOW -> return R.style.DialogThemeLight_Yellow
                PrefGetter.AMBER -> return R.style.DialogThemeLight_Amber
                PrefGetter.ORANGE -> return R.style.DialogThemeLight_Orange
                PrefGetter.DEEP_ORANGE -> return R.style.DialogThemeLight_DeepOrange
                else -> return R.style.DialogThemeLight
            }
            PrefGetter.DARK -> when (themeColor) {
                PrefGetter.RED -> return R.style.DialogThemeDark_Red
                PrefGetter.PINK -> return R.style.DialogThemeDark_Pink
                PrefGetter.PURPLE -> return R.style.DialogThemeDark_Purple
                PrefGetter.DEEP_PURPLE -> return R.style.DialogThemeDark_DeepPurple
                PrefGetter.INDIGO -> return R.style.DialogThemeDark_Indigo
                PrefGetter.BLUE -> return R.style.DialogThemeDark
                PrefGetter.LIGHT_BLUE -> return R.style.DialogThemeDark_LightBlue
                PrefGetter.CYAN -> return R.style.DialogThemeDark_Cyan
                PrefGetter.TEAL -> return R.style.DialogThemeDark_Teal
                PrefGetter.GREEN -> return R.style.DialogThemeDark_Green
                PrefGetter.LIGHT_GREEN -> return R.style.DialogThemeDark_LightGreen
                PrefGetter.LIME -> return R.style.DialogThemeDark_Lime
                PrefGetter.YELLOW -> return R.style.DialogThemeDark_Yellow
                PrefGetter.AMBER -> return R.style.DialogThemeDark_Amber
                PrefGetter.ORANGE -> return R.style.DialogThemeDark_Orange
                PrefGetter.DEEP_ORANGE -> return R.style.DialogThemeDark_DeepOrange
                else -> return R.style.DialogThemeDark
            }
            PrefGetter.AMLOD -> when (themeColor) {
                PrefGetter.RED -> return R.style.DialogThemeAmlod_Red
                PrefGetter.PINK -> return R.style.DialogThemeAmlod_Pink
                PrefGetter.PURPLE -> return R.style.DialogThemeAmlod_Purple
                PrefGetter.DEEP_PURPLE -> return R.style.DialogThemeAmlod_DeepPurple
                PrefGetter.INDIGO -> return R.style.DialogThemeAmlod_Indigo
                PrefGetter.BLUE -> return R.style.DialogThemeAmlod
                PrefGetter.LIGHT_BLUE -> return R.style.DialogThemeAmlod_LightBlue
                PrefGetter.CYAN -> return R.style.DialogThemeAmlod_Cyan
                PrefGetter.TEAL -> return R.style.DialogThemeAmlod_Teal
                PrefGetter.GREEN -> return R.style.DialogThemeAmlod_Green
                PrefGetter.LIGHT_GREEN -> return R.style.DialogThemeAmlod_LightGreen
                PrefGetter.LIME -> return R.style.DialogThemeAmlod_Lime
                PrefGetter.YELLOW -> return R.style.DialogThemeAmlod_Yellow
                PrefGetter.AMBER -> return R.style.DialogThemeAmlod_Amber
                PrefGetter.ORANGE -> return R.style.DialogThemeAmlod_Orange
                PrefGetter.DEEP_ORANGE -> return R.style.DialogThemeAmlod_DeepOrange
                else -> return R.style.DialogThemeAmlod
            }
            PrefGetter.MID_NIGHT_BLUE -> when (themeColor) {
                PrefGetter.RED -> return R.style.DialogThemeMidnight_Red
                PrefGetter.PINK -> return R.style.DialogThemeMidnight_Pink
                PrefGetter.PURPLE -> return R.style.DialogThemeMidnight_Purple
                PrefGetter.DEEP_PURPLE -> return R.style.DialogThemeMidnight_DeepPurple
                PrefGetter.INDIGO -> return R.style.DialogThemeMidnight_Indigo
                PrefGetter.BLUE -> return R.style.DialogThemeMidnight
                PrefGetter.LIGHT_BLUE -> return R.style.DialogThemeMidnight_LightBlue
                PrefGetter.CYAN -> return R.style.DialogThemeMidnight_Cyan
                PrefGetter.TEAL -> return R.style.DialogThemeMidnight_Teal
                PrefGetter.GREEN -> return R.style.DialogThemeMidnight_Green
                PrefGetter.LIGHT_GREEN -> return R.style.DialogThemeMidnight_LightGreen
                PrefGetter.LIME -> return R.style.DialogThemeMidnight_Lime
                PrefGetter.YELLOW -> return R.style.DialogThemeMidnight_Yellow
                PrefGetter.AMBER -> return R.style.DialogThemeMidnight_Amber
                PrefGetter.ORANGE -> return R.style.DialogThemeMidnight_Orange
                PrefGetter.DEEP_ORANGE -> return R.style.DialogThemeMidnight_DeepOrange
                else -> return R.style.DialogThemeLight
            }
            PrefGetter.BLUISH -> when (themeColor) {
                PrefGetter.RED -> return R.style.DialogThemeBluish_Red
                PrefGetter.PINK -> return R.style.DialogThemeBluish_Pink
                PrefGetter.PURPLE -> return R.style.DialogThemeBluish_Purple
                PrefGetter.DEEP_PURPLE -> return R.style.DialogThemeBluish_DeepPurple
                PrefGetter.INDIGO -> return R.style.DialogThemeBluish_Indigo
                PrefGetter.BLUE -> return R.style.DialogThemeBluish
                PrefGetter.LIGHT_BLUE -> return R.style.DialogThemeBluish_LightBlue
                PrefGetter.CYAN -> return R.style.DialogThemeBluish_Cyan
                PrefGetter.TEAL -> return R.style.DialogThemeBluish_Teal
                PrefGetter.GREEN -> return R.style.DialogThemeBluish_Green
                PrefGetter.LIGHT_GREEN -> return R.style.DialogThemeBluish_LightGreen
                PrefGetter.LIME -> return R.style.DialogThemeBluish_Lime
                PrefGetter.YELLOW -> return R.style.DialogThemeBluish_Yellow
                PrefGetter.AMBER -> return R.style.DialogThemeBluish_Amber
                PrefGetter.ORANGE -> return R.style.DialogThemeBluish_Orange
                PrefGetter.DEEP_ORANGE -> return R.style.DialogThemeBluish_DeepOrange
                else -> return R.style.DialogThemeBluish
            }
        }
        return R.style.DialogThemeLight
    }

    private fun setTaskDescription(activity: Activity) {
        activity.setTaskDescription(ActivityManager.TaskDescription(activity.getString(R.string.app_name),
                BitmapFactory.decodeResource(activity.resources, R.mipmap.ic_launcher), ViewHelper.getPrimaryColor(activity)))
    }

    private fun hasTheme(activity: BaseActivity<*, *>) = (activity is LoginChooserActivity || activity is LoginActivity ||
            activity is DonateActivity)
}
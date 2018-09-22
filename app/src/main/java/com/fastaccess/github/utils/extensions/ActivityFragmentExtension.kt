package com.fastaccess.github.utils.extensions

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseActivity

/**
 * Created by Kosh on 12.06.18.
 */

fun BaseActivity.replaceIfNotExisting(containerId: Int,
                                      fragment: Fragment,
                                      tag: String? = null,
                                      callback: ((Fragment) -> Unit)? = null) {

    val existingFragment = supportFragmentManager.findFragmentByTag(tag)
    if (existingFragment == null) {
        supportFragmentManager.beginTransaction()
                .replace(containerId, fragment, tag)
                .runOnCommit {
                    callback?.invoke(fragment)
                }
                .commitNow()
    } else {
        callback?.invoke(fragment)
    }
}

fun BaseActivity.replace(containerId: Int,
                         fragment: Fragment,
                         tag: String? = null,
                         callback: ((Fragment) -> Unit)? = null) {
    supportFragmentManager.beginTransaction()
            .replace(containerId, fragment, tag ?: fragment.getSimpleName())
            .runOnCommit {
                callback?.invoke(fragment)
            }
            .commitNow()
}


fun Context.getColorAttr(attr: Int): Int {
    val theme = theme
    val typedArray = theme.obtainStyledAttributes(intArrayOf(attr))
    val color = typedArray.getColor(0, Color.LTGRAY)
    typedArray.recycle()
    return color
}

fun FragmentActivity.clearDarkStatusBarIcons() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.window?.decorView?.let { decoder ->
            decoder.systemUiVisibility = decoder.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }
}

fun FragmentActivity.setStatusBarColor(colorAttr: Int = R.attr.colorAccent) {
    window?.statusBarColor = getColorAttr(colorAttr)
}

fun Fragment.getDrawable(@DrawableRes drawableRes: Int): Drawable? = ContextCompat.getDrawable(requireContext(), drawableRes)
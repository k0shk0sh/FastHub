package com.fastaccess.github.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.airbnb.deeplinkdispatch.DeepLink

/**
 * Created by Kosh on 12.06.18.
 */

fun AppCompatActivity.replaceIfNotExisting(
    containerId: Int,
    fragment: Fragment,
    tag: String? = null,
    callback: ((Fragment) -> Unit)? = null
) {

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

fun AppCompatActivity.replace(
    containerId: Int,
    fragment: Fragment,
    tag: String? = null,
    callback: ((Fragment) -> Unit)? = null
) {
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

fun Context.getColorCompat(id: Int): Int = ContextCompat.getColor(this, id)

fun Context.getDrawableCompat(id: Int): Drawable? = ContextCompat.getDrawable(this, id)

fun FragmentActivity.clearDarkStatusBarIcons() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.window?.decorView?.let { decoder ->
            decoder.systemUiVisibility = decoder.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }
}

fun FragmentActivity.setStatusBarColor(colorAttr: Int = com.fastaccess.github.extensions.R.attr.colorAccent) {
    window?.statusBarColor = getColorAttr(colorAttr)
}

fun Fragment.getDrawable(@DrawableRes drawableRes: Int): Drawable? = ContextCompat.getDrawable(requireContext(), drawableRes)


fun Context.isConnected(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
    val isConnected = activeNetwork?.isConnected == true
    return isConnected
}

fun AppCompatActivity.fromDeepLink() = intent?.getBooleanExtra(DeepLink.IS_DEEP_LINK, false) ?: false

fun Activity.showKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
}
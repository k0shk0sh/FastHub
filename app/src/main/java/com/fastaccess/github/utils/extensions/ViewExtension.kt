package com.fastaccess.github.utils.extensions

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.fastaccess.github.R
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Kosh on 03.06.18.
 */


private val FAST_OUT_LINEAR_IN_INTERPOLATOR = FastOutLinearInInterpolator()
private val LINEAR_OUT_SLOW_IN_INTERPOLATOR = LinearOutSlowInInterpolator()

fun TextView.asString(): String = this.text?.toString() ?: ""

fun Snackbar.materialize(): Snackbar {
    val params = this.view.layoutParams as ViewGroup.MarginLayoutParams
    val margin = context.resources.getDimensionPixelSize(R.dimen.spacing_xs_large)
    params.setMargins(margin, margin, margin, 12)
    this.view.layoutParams = params
    this.view.background = context.getDrawable(R.drawable.snackbar_background)
    ViewCompat.setElevation(this.view, 6f)
    return this
}

fun View.showHideFabAnimation(show: Boolean, listener: ((show: Boolean) -> Unit)? = null) {
    val view = this
    if (show) {
        view.animate().cancel()
        if (ViewCompat.isLaidOut(view)) {
            if (view.visibility != View.VISIBLE) {
                view.alpha = 0f
                view.scaleY = 0f
                view.scaleX = 0f
            }
            view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(200)
                    .setInterpolator(LINEAR_OUT_SLOW_IN_INTERPOLATOR)
                    .withStartAction {
                        view.visibility = View.VISIBLE
                        listener?.invoke(true)
                    }
        } else {
            view.visibility = View.VISIBLE
            view.alpha = 1f
            view.scaleY = 1f
            view.scaleX = 1f
            listener?.invoke(true)
        }
    } else {
        view.animate()
                .scaleX(0f)
                .scaleY(0f)
                .alpha(0f)
                .setDuration(150)
                .withEndAction {
                    view.visibility = View.GONE
                    listener?.invoke(false)
                }
                .interpolator = FAST_OUT_LINEAR_IN_INTERPOLATOR
    }
}
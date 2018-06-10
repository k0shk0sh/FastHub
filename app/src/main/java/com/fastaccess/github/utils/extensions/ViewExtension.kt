package com.fastaccess.github.utils.extensions

import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.fastaccess.github.R
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Kosh on 03.06.18.
 */


fun TextView.asString(): String? = this.text?.toString()

fun Snackbar.materialize(): Snackbar {
    val params = this.view.layoutParams as ViewGroup.MarginLayoutParams
    val margin = context.resources.getDimensionPixelSize(R.dimen.spacing_xs_large)
    params.setMargins(margin, margin, margin, 12)
    this.view.layoutParams = params
    this.view.background = context.getDrawable(R.drawable.snackbar_background)
    ViewCompat.setElevation(this.view, 6f)
    return this
}
package com.fastaccess.github.base.extensions

import android.content.Context
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.MenuRes
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.transition.TransitionManager
import com.fastaccess.github.base.R
import com.fastaccess.github.base.widget.recyclerview.decoration.InsetDividerDecoration
import com.fastaccess.github.extensions.getColorAttr
import com.fastaccess.github.extensions.getDrawableCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Kosh on 03.06.18.
 */

private val FAST_OUT_LINEAR_IN_INTERPOLATOR = FastOutLinearInInterpolator()
private val LINEAR_OUT_SLOW_IN_INTERPOLATOR = LinearOutSlowInInterpolator()

fun TextView.asString(): String = this.text?.toString() ?: ""

fun Snackbar.materialize(drawable: Int? = null): Snackbar {
    val params = this.view.layoutParams as ViewGroup.MarginLayoutParams
    val margin = context.resources.getDimensionPixelSize(R.dimen.spacing_xs_large)
    params.setMargins(margin, margin, margin, 12)
    this.view.layoutParams = params
    this.view.background = context.getDrawableCompat(drawable ?: R.drawable.snackbar_background)
    ViewCompat.setElevation(this.view, 6f)
    return this
}

fun View.showHideFabAnimation(
    show: Boolean,
    listener: ((show: Boolean) -> Unit)? = null
) {
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
            .setInterpolator(FAST_OUT_LINEAR_IN_INTERPOLATOR)
            .withEndAction {
                view.visibility = View.GONE
                listener?.invoke(false)
            }
    }
}

fun RecyclerView.addKeyLineDivider() {
    if (canAddDivider()) {
        val resources = resources
        addItemDecoration(
            InsetDividerDecoration(
                resources.getDimensionPixelSize(R.dimen.divider_height),
                resources.getDimensionPixelSize(R.dimen.keyline_2), context.getColorAttr(R.attr.dividerColor)
            )
        )
    }
}

fun RecyclerView.addDivider() {
    if (canAddDivider()) {
        val resources = resources
        addItemDecoration(
            InsetDividerDecoration(resources.getDimensionPixelSize(R.dimen.divider_height), 0, context.getColorAttr(R.attr.dividerColor))
        )
    }
}

fun RecyclerView.canAddDivider(): Boolean {
    if (layoutManager != null) {
        val layoutManager = this.layoutManager
        when (layoutManager) {
            is GridLayoutManager -> return layoutManager.spanCount == 1
            is LinearLayoutManager -> return true
            is StaggeredGridLayoutManager -> return layoutManager.spanCount == 1
        }
    }
    return false
}

fun <V : View?> BottomSheetBehavior<V>.setBottomSheetCallback(
    onStateChanged: ((newState: Int) -> Unit)? = null,
    onSlide: ((slideOffset: Float) -> Unit)? = null
) {
    this.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(
            bottomSheet: View,
            slideOffset: Float
        ) {
            onSlide?.invoke(slideOffset)
        }

        override fun onStateChanged(
            bottomSheet: View,
            newState: Int
        ) {
            onStateChanged?.invoke(newState)
        }
    })
}

fun View.beginDelayedTransition() = TransitionManager.beginDelayedTransition(this as ViewGroup)

fun View.focusAndshowKeyboard() {
    requestFocusFromTouch()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.popMenu(
    @MenuRes menuId: Int,
    menuCallback: ((menu: Menu) -> Unit)?,
    callback: ((itemId: Int) -> Unit)?
) {
    setOnClickListener {
        val menu = PopupMenu(context, this)
        menu.inflate(menuId)
        menuCallback?.invoke(menu.menu)
        menu.setOnMenuItemClickListener {
            callback?.invoke(it.itemId)
            return@setOnMenuItemClickListener true
        }
        menu.show()
    }
}
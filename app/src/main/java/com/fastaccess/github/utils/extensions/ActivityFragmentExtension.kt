package com.fastaccess.github.utils.extensions

import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.extensions.isConnected
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Kosh on 12.06.18.
 */

fun BaseFragment.isConnected(): Boolean {
    val isConnected = context?.isConnected() ?: false
    if (!isConnected) {
        view?.let {
            Snackbar.make(it.findViewById(R.id.coordinatorLayout) ?: it, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG)
                .materialize(R.drawable.snackbar_background_error)
                .show()
        }
    }
    return isConnected
}
package com.fastaccess.github.utils.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.extensions.isConnected
import com.fastaccess.github.ui.modules.routing.RoutingActivity
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

/**
 * Created by Kosh on 12.06.18.
 */

fun Fragment.route(url: String?) {
    context?.route(url)
}

fun Context.route(url: String?) {
    url?.let {
        Timber.e("routing to: $url")
        val intent = Intent(this, RoutingActivity::class.java)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}

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
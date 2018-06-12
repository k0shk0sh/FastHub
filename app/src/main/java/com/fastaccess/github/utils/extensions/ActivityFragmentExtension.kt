package com.fastaccess.github.utils.extensions

import androidx.fragment.app.Fragment
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
            .replace(containerId, fragment, tag)
            .runOnCommit {
                callback?.invoke(fragment)
            }
            .commitNow()
}
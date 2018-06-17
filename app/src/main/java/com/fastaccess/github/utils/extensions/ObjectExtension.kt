package com.fastaccess.github.utils.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * Created by Kosh on 19.05.18.
 */


fun Any.getSimpleName() = this::class.java.simpleName

fun Boolean.isTrue(body: (() -> Unit)?): Boolean {
    if (this) body?.invoke()
    return this
}

fun Boolean.isFalse(body: (() -> Unit)?): Boolean {
    if (!this) body?.invoke()
    return this
}

fun <T> LiveData<T>.observeNotNull(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(owner, Observer { it?.let(observer) })
}
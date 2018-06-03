package com.fastaccess.github.utils.extensions

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
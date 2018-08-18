package com.fastaccess.github.utils.extensions

import android.text.format.DateUtils
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.fastaccess.data.persistence.models.RepositoryModel
import com.fastaccess.data.persistence.models.UserModel
import java.util.*

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

fun Date.timeAgo(): CharSequence {
    val now = System.currentTimeMillis()
    return DateUtils.getRelativeTimeSpanString(this.time, now, DateUtils.SECOND_IN_MILLIS)
}

fun String.replaceAllNewLines(prefix: String = " "): String {
    return this.replace("\\r?\\n|\\r".toRegex(), prefix)
}

fun UserModel.itsMe() = "k0shk0sh".equals(login, true)

fun RepositoryModel.itsFastHub() = "k0shk0sh/FastHub".equals(name, true) || "k0shk0sh/FastHub".equals(fullName, true)

fun Any.me() = "k0shk0sh"
fun Any.fastHub() = "k0shk0sh/FastHub"
package com.fastaccess.github.extensions

import android.text.format.DateUtils
import java.text.SimpleDateFormat
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

fun Date.timeAgo(): CharSequence {
    val now = System.currentTimeMillis()
    return DateUtils.getRelativeTimeSpanString(this.time, now, DateUtils.SECOND_IN_MILLIS)
}

fun String.replaceAllNewLines(prefix: String = " "): String {
    return this.replace("\\r?\\n|\\r".toRegex(), prefix)
}

fun me() = "k0shk0sh"
fun myProfile() = "app://fasthub/k0shk0sh"
fun fastHub() = "k0shk0sh/FastHub"

fun Long.formatNumber(): String {
    if (this < 999) return this.toString()
    val count = this.toDouble()
    val exp = (Math.log(count) / Math.log(1000.0)).toInt()
    return String.format("%.1f%c", count / Math.pow(1000.0, exp.toDouble()), "kMGTPE"[exp - 1])
}

inline fun runSafely(execute: () -> Unit, noinline onErrorCallback: (() -> Unit)? = null) {
    try {
        execute.invoke()
    } catch (e: Exception) {
        e.printStackTrace()
        onErrorCallback?.invoke()
    }
}

fun getDateByDays(days: Int): String {
    val cal = Calendar.getInstance()
    val s = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
    cal.add(Calendar.DAY_OF_YEAR, days)
    return s.format(Date(cal.timeInMillis))
}

fun getLastWeekDate(): String {
    return getDateByDays(-7)
}
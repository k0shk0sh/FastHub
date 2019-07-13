package com.fastaccess.github.extensions

import android.graphics.Color
import androidx.annotation.ColorInt
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

fun Date.timeAgo(): CharSequence = DatePrettifier.getTimeAgo(this)

fun String.replaceAllNewLines(prefix: String = " "): String {
    return this.replace("\\r?\\n|\\r".toRegex(), prefix)
}

fun Long.formatNumber(): String {
    if (this < 999) return this.toString()
    val count = this.toDouble()
    val exp = (Math.log(count) / Math.log(1000.0)).toInt()
    return String.format("%.1f%c", count / Math.pow(1000.0, exp.toDouble()), "kMGTPE"[exp - 1])
}

fun Int.formatNumber(): String {
    if (this < 999) return this.toString()
    val count = this.toDouble()
    val exp = (Math.log(count) / Math.log(1000.0)).toInt()
    return String.format("%.1f%c", count / Math.pow(1000.0, exp.toDouble()), "kMGTPE"[exp - 1])
}

fun getDateByDays(days: Int): String = DatePrettifier.getDateByDays(days)

fun getLastWeekDate(): String = DatePrettifier.lastWeekDate

@ColorInt fun Int.generateTextColor(): Int {
    val a = 1 - (0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.blue(this)) / 255
    return if (a < 0.5) Color.BLACK else Color.WHITE
}

fun <T> ArrayList<T>.addIfNotNull(t: T?) {
    t?.let(::add)
}
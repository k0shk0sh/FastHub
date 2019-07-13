package com.fastaccess.github.extensions

import android.text.format.DateUtils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DatePrettifier private constructor() {

    private val lock = Any()

    private val dateFormat: DateFormat
    private val prettifier: DateFormat

    init {
        dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getDefault()
        prettifier = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        prettifier.timeZone = TimeZone.getDefault()
    }

    fun format(date: Date): String {
        synchronized(lock) {
            return dateFormat.format(date)
        }
    }

    companion object {
        private val instance = DatePrettifier()
        private val DAY_MILLIS = TimeUnit.DAYS.toMillis(1)
        private val HOUR_MILLIS = TimeUnit.HOURS.toMillis(1)
        private val MINUTE_MILLIS = TimeUnit.MINUTES.toMillis(1)

        fun getTimeAgo(parsedDate: Date?): CharSequence {
            var _time = parsedDate?.time ?: return "N/A"
            if (_time < 1000000000000L) {
                _time *= 1000
            }

            val now = System.currentTimeMillis()
            if (_time > now || _time <= 0) {
                return "N/A"
            }


            val diff = now - _time
            return when {
                diff < MINUTE_MILLIS -> "just now"
                diff < 50 * MINUTE_MILLIS -> {
                    val mns = (diff / MINUTE_MILLIS)
                    "$mns${if (mns > 1) "ms" else "m"} ago"
                }
                diff < 24 * HOUR_MILLIS -> {
                    val hours = (diff / HOUR_MILLIS)
                    "$hours${if (hours > 1) "hs" else "h"}  ago"
                }
                else -> {
                    val days = (diff / DAY_MILLIS)
                    "$days${if (days > 1) "ds" else "d"} ago"
                }
            }
        }

        fun getTimeAgo(toParse: String?): CharSequence {
            try {
                val parsedDate = instance.dateFormat.parse(toParse)
                val now = System.currentTimeMillis()
                return DateUtils.getRelativeTimeSpanString(parsedDate.time, now, DateUtils.SECOND_IN_MILLIS)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "N/A"
        }


        fun toGithubDate(date: Date): String = instance.format(date)

        fun prettifyDate(timestamp: Long): String = instance.prettifier.format(timestamp)

        fun getDateFromString(date: String): Date? = kotlin.runCatching { instance.prettifier.parse(date) }.getOrNull()

        fun getDateByDays(days: Int): String {
            val cal = Calendar.getInstance()
            val s = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
            cal.add(Calendar.DAY_OF_YEAR, days)
            return s.format(Date(cal.timeInMillis))
        }

        val lastWeekDate: String
            get() = getDateByDays(-7)
    }

}
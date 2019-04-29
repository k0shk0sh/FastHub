package com.fastaccess.github.extensions

import android.text.format.DateUtils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

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

        fun getTimeAgo(parsedDate: Date?): CharSequence {
            if (parsedDate != null) {
                val now = System.currentTimeMillis()
                return DateUtils.getRelativeTimeSpanString(parsedDate.time, now, DateUtils.SECOND_IN_MILLIS)
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
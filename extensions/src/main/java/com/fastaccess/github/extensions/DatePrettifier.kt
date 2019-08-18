package com.fastaccess.github.extensions

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
        private val times = listOf(
            TimeUnit.DAYS.toMillis(365), TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(1), TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1), TimeUnit.SECONDS.toMillis(1)
        )
        private val timesString = listOf("y", "m", "d", "h", "m", "s")

        fun getTimeAgo(parsedDate: Date?): CharSequence {
            val date = parsedDate?.time ?: return "N/A"
            val duration = System.currentTimeMillis() - date
            val sb = StringBuilder()
            for (i in 0 until times.size) {
                val temp = duration / times[i]
                if (temp > 0) {
                    sb.append(temp)
                        .append(timesString[i])
                        .append(if (temp > 1) "s" else "")
                        .append(" ago")
                    break
                }
            }
            return if (sb.toString().isEmpty()) "just now" else sb.toString()
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
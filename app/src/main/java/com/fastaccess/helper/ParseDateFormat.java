package com.fastaccess.helper;

import android.support.annotation.Nullable;
import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ParseDateFormat {

    private static final ParseDateFormat INSTANCE = new ParseDateFormat();

    private static ParseDateFormat getInstance() {
        return INSTANCE;
    }

    private final Object lock = new Object();
    private final DateFormat dateFormat;
    private final TimeZone timeZone;

    private ParseDateFormat() {
        dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        dateFormat.setTimeZone(TimeZone.getDefault());
        timeZone = TimeZone.getDefault();
    }

    public String format(Date date) {
        synchronized (lock) {
            return dateFormat.format(date);
        }
    }

    public static CharSequence getTimeAgo(@Nullable Date parsedDate) {
        if (parsedDate != null) {
            long toLocalTime = parsedDate.getTime() + getInstance().timeZone.getRawOffset() + getInstance().timeZone.getDSTSavings();
            if (INSTANCE.timeZone.getID().equalsIgnoreCase("UTC")) {
                toLocalTime = parsedDate.getTime();
            }
            return DateUtils.getRelativeTimeSpanString(toLocalTime, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        }
        return "N/A";
    }
}
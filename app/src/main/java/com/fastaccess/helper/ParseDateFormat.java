package com.fastaccess.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ParseDateFormat {

    private static final ParseDateFormat INSTANCE = new ParseDateFormat();

    private final Object lock = new Object();

    private final DateFormat dateFormat;

    private ParseDateFormat() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getDefault());
    }

    @NonNull public String format(Date date) {
        synchronized (lock) {
            return dateFormat.format(date);
        }
    }


    @NonNull public static CharSequence getTimeAgo(@Nullable String toParse) {
        try {
            Date parsedDate = getInstance().dateFormat.parse(toParse);
            long now = System.currentTimeMillis();
            return DateUtils.getRelativeTimeSpanString(parsedDate.getTime(), now, DateUtils.SECOND_IN_MILLIS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    @NonNull public static CharSequence getTimeAgo(@Nullable Date parsedDate) {
        if (parsedDate != null) {
            long now = System.currentTimeMillis();
            return DateUtils.getRelativeTimeSpanString(parsedDate.getTime(), now, DateUtils.SECOND_IN_MILLIS);
        }
        return "N/A";
    }

    @NonNull public static String toGithubDate(@NonNull Date date) {
        return getInstance().format(date);
    }

    @NonNull public static String prettifyDate(long timestamp) {
        return new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(new Date(timestamp));
    }

    @Nullable public static Date getDateFromString(@NonNull String date) {
        try {
            return new SimpleDateFormat("dd-MM-yyyy", Locale.US).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull private static ParseDateFormat getInstance() {
        return INSTANCE;
    }

    private static String getDateByDays(int days) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return s.format(new Date(cal.getTimeInMillis()));
    }

    public static String getLastWeekDate() {
        return getDateByDays(-7);
    }

}
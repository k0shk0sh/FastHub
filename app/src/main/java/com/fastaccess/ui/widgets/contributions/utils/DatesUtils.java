package com.fastaccess.ui.widgets.contributions.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Copyright 2016 Javier González
 * All right reserved.
 */
public class DatesUtils {

    /**
     * Get the day of week from a date.
     * 0 for SUN.
     * 1 for MON.
     * .
     * .
     * .
     * 6 for SAT.
     *
     * @param year
     *         The year of the date.
     * @param month
     *         The month of the date.
     * @param day
     *         The day of month of the date.
     * @return Integer to determine the day of week.
     */
    @SuppressLint("WrongConstant") public static int getWeekDayFromDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        calendar.add(Calendar.SECOND, 0);
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * Get the short month name for a certain date.
     *
     * @param year
     *         The year of the date.
     * @param month
     *         The month of the date.
     * @param day
     *         The day of the date.
     * @return The short name of the month.
     */
    @SuppressLint("WrongConstant") public static String getShortMonthName(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        calendar.add(Calendar.SECOND, 0);
        SimpleDateFormat month_date = new SimpleDateFormat("MMM", Locale.US);
        return month_date.format(calendar.getTime());
    }

    /**
     * Return if the date given is a first week of mount
     *
     * @param year
     *         The year of the date.
     * @param month
     *         The month of the date.
     * @param day
     *         The day of the date.
     * @return true or false
     */
    @SuppressLint("WrongConstant") public static boolean isFirstWeekOfMount(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        calendar.add(Calendar.SECOND, 0);
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);

        return calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH) == 1;
    }

    /**
     * Return if the date given is a first day of week
     *
     * @param year
     *         The year of the date.
     * @param month
     *         The month of the date.
     * @param day
     *         The day of the date.
     * @return true or false
     */
    @SuppressLint("WrongConstant") public static boolean isFirstDayOfWeek(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        calendar.add(Calendar.SECOND, 0);
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);

        return calendar.get(Calendar.DAY_OF_WEEK) == 1;
    }

}

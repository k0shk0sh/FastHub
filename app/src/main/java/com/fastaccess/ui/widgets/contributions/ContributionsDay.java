package com.fastaccess.ui.widgets.contributions;

/**
 * Copyright 2016 Javier González
 * All right reserved.
 */
public class ContributionsDay {

    public int year = Integer.MIN_VALUE;
    public int month = Integer.MIN_VALUE;
    public int day = Integer.MIN_VALUE;

    // Level is used to record the color of the block
    public int level = Integer.MIN_VALUE;
    // Data is used to calculated the height of the pillar
    private int data = Integer.MIN_VALUE;

    public ContributionsDay(int year, int month, int day, int level, int data) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.level = level;
        this.data = data;
    }
}

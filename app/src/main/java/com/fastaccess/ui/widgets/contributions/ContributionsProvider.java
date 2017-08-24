package com.fastaccess.ui.widgets.contributions;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.helper.InputHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2016 Javier González
 * All right reserved.
 */
public class ContributionsProvider {

    private final static String FILL_STRING = "fill=\"";
    private final static String DATA_STRING = "data-count=\"";
    private final static String DATE_STRING = "data-date=\"";

    @NonNull public List<ContributionsDay> getContributions(@Nullable String string) {
        ArrayList<ContributionsDay> contributions = new ArrayList<>();
        if (InputHelper.isEmpty(string)) return contributions;
        int fillPos = -1;
        int dataPos = -1;
        int datePos = -1;
        while (true) {
            fillPos = string.indexOf(FILL_STRING, fillPos + 1);
            dataPos = string.indexOf(DATA_STRING, dataPos + 1);
            datePos = string.indexOf(DATE_STRING, datePos + 1);
            if (fillPos == -1) break;
            int level = 0;
            String levelString = string.substring(fillPos + FILL_STRING.length(), fillPos + FILL_STRING.length() + 7);
            switch (levelString) {
                case "#eeeeee":
                    level = 0;
                    break;
                case "#d6e685":
                    level = 1;
                    break;
                case "#8cc665":
                    level = 2;
                    break;
                case "#44a340":
                    level = 3;
                    break;
                case "#1e6823":
                    level = 4;
                    break;
                case "#ebedf0":
                    level = 0;
                    break;
                case "#239a3b":
                    level = 1;
                    break;
                case "#c6e48b":
                    level = 2;
                    break;
                case "#7bc96f":
                    level = 3;
                    break;
                case "#196127":
                    level = 4;
                    break;
            }

            int dataEndPos = string.indexOf("\"", dataPos + DATA_STRING.length());
            String dataString = string.substring(dataPos + DATA_STRING.length(), dataEndPos);
            int data = Integer.valueOf(dataString);
            String dateString = string.substring(datePos + DATE_STRING.length(), datePos + DATE_STRING.length() + 11);
            contributions.add(new ContributionsDay(
                    Integer.valueOf(dateString.substring(0, 4)),
                    Integer.valueOf(dateString.substring(5, 7)),
                    Integer.valueOf(dateString.substring(8, 10)),
                    level,
                    data
            ));
        }

        return contributions;
    }
}

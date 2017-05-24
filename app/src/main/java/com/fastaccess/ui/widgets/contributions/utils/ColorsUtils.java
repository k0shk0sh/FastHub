package com.fastaccess.ui.widgets.contributions.utils;

import android.graphics.Color;

/**
 * Copyright 2016 Javier González
 * All right reserved.
 */
public class ColorsUtils {

    /**
     * Calculate the value for different color.
     *
     * @param baseColor
     *         Value of base color.
     * @param emptyColor
     *         Value of empty color
     * @param level
     *         Level.
     * @return The value for the level of the base color.
     */
    public static int calculateLevelColor(int baseColor, int emptyColor, int level) {
        if (level == 0) {
            return emptyColor;
        }
        return Color.rgb(
                calculateR(Color.red(baseColor), level),
                calculateG(Color.green(baseColor), level),
                calculateB(Color.blue(baseColor), level));
    }

    /**
     * Calculate the red value for different level.
     *
     * @param baseR
     *         Red value of base color.
     * @param level
     *         Level.
     * @return The red value for the level of the base color.
     */
    private static int calculateR(int baseR, int level) {
        switch (level) {
            case 0:
                return 238;
            case 1:
                return baseR;
            case 2:
                return (int) (baseR * (9 + 46 + 15) / (37f + 9 + 46 + 15));
            case 3:
                return (int) (baseR * (46 + 15) / (37f + 9 + 46 + 15));
            case 4:
                return (int) (baseR * (15) / (37f + 9 + 46 + 15));
            default:
                return 238;
        }
    }

    /**
     * Calculate the green value for different level.
     *
     * @param baseG
     *         Green value of base color.
     * @param level
     *         Level.
     * @return The green value for the level of the base color.
     */
    private static int calculateG(int baseG, int level) {
        switch (level) {
            case 0:
                return 238;
            case 1:
                return baseG;
            case 2:
                return (int) (baseG * (35 + 59 + 104) / (32f + 35 + 59 + 104));
            case 3:
                return (int) (baseG * (59 + 104) / (32f + 35 + 59 + 104));
            case 4:
                return (int) (baseG * (104) / (32f + 35 + 59 + 104));
            default:
                return 238;
        }
    }

    /**
     * Calculate the blue value for different level.
     *
     * @param baseB
     *         Blue value of base color.
     * @param level
     *         Level.
     * @return The blue value for the level of the base color.
     */
    private static int calculateB(int baseB, int level) {
        switch (level) {
            case 0:
                return 238;
            case 1:
                return baseB;
            case 2:
                return (int) (baseB * (37 + 29 + 35) / (32f + 37 + 29 + 35));
            case 3:
                return (int) (baseB * (29 + 35) / (32f + 37 + 29 + 35));
            case 4:
                return (int) (baseB * (35) / (32f + 37 + 29 + 35));
            default:
                return 238;
        }
    }

}

package com.fastaccess.ui.widgets.color;

import android.support.annotation.Nullable;

import com.annimon.stream.Objects;

import java.util.Arrays;
import java.util.List;

public class ColorGenerator {

    public static ColorGenerator MATERIAL;

    static {
        MATERIAL = create(Arrays.asList(
                0xff1976d2,
                0xff00838f,
                0xff512da8,
                0xff2e7d32,
                0xff283593,
                0xff01579b,
                0xffc51162,
                0xff6a1b9a,
                0xffd50000,
                0xff00695c
        ));
    }

    private final List<Integer> colors;

    private static ColorGenerator create(List<Integer> colorList) {
        return new ColorGenerator(colorList);
    }

    private ColorGenerator(List<Integer> colorList) {
        colors = colorList;
    }

    public int getColor(@Nullable Object key) {
        key = Objects.toString(key, "default");
        return colors.get(Math.abs(key.hashCode()) % colors.size());
    }
}
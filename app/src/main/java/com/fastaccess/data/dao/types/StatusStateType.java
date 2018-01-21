package com.fastaccess.data.dao.types;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.Stream;
import com.fastaccess.R;

/**
 * Created by Kosh on 10 Apr 2017, 3:41 AM
 */

public enum StatusStateType {
    failure(R.drawable.ic_issues_small),
    pending(R.drawable.ic_time_small),
    success(R.drawable.ic_check_small),
    error(R.drawable.ic_issues_small);

    @DrawableRes private int drawableRes;

    StatusStateType(@DrawableRes int drawableRes) {
        this.drawableRes = drawableRes;
    }

    @DrawableRes public int getDrawableRes() {
        return drawableRes;
    }

    @NonNull public static StatusStateType getState(@Nullable String status) {
        return Stream.of(values())
                .filter(value -> value.name().toLowerCase().equalsIgnoreCase(status))
                .findFirst()
                .orElse(pending);
    }
}

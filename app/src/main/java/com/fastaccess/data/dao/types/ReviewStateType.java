package com.fastaccess.data.dao.types;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.annimon.stream.Stream;
import com.fastaccess.R;

/**
 * Created by Kosh on 10 Apr 2017, 4:27 PM
 */

public enum ReviewStateType {
    COMMENTED(R.string.reviewed, R.drawable.ic_eye),
    CHANGES_REQUESTED(R.string.request_changes, R.drawable.ic_clear),
    REQUEST_CHANGES(R.string.reviewed, R.drawable.ic_eye),
    DISMISSED(R.string.dismissed_review, R.drawable.ic_clear),
    APPROVED(R.string.approved_these_changes, R.drawable.ic_done),
    APPROVE(R.string.approved_these_changes, R.drawable.ic_done);

    private int stringRes;
    private int drawableRes;

    ReviewStateType(@StringRes int stringRes, @DrawableRes int drawableRes) {
        this.stringRes = stringRes;
        this.drawableRes = drawableRes;
    }

    @StringRes public int getStringRes() {
        return stringRes > 0 ? stringRes : R.string.reviewed;
    }

    @DrawableRes public int getDrawableRes() {
        return drawableRes > 0 ? drawableRes : R.drawable.ic_eye;
    }

    @Nullable public static ReviewStateType getType(@NonNull String state) {
        return Stream.of(values())
                .filter(value -> value.name().toLowerCase().equalsIgnoreCase(state.toLowerCase()))
                .findFirst()
                .orElse(null);
    }
}

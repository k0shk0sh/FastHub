package com.fastaccess.data.dao.types;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.fastaccess.R;

/**
 * Created by Kosh on 10 Apr 2017, 4:27 PM
 */

public enum ReviewStateType {
    COMMENTED(R.string.reviewed, R.drawable.ic_eye),
    CHANGES_REQUESTED(R.string.reviewed, R.drawable.ic_eye),
    DISMISSED(R.string.dismissed_review, R.drawable.ic_clear),
    APPROVED(R.string.reviewed, R.drawable.ic_done);

    private int stringRes;
    private int drawableRes;

    ReviewStateType(@StringRes int stringRes, @DrawableRes int drawableRes) {
        this.stringRes = stringRes;
    }

    @StringRes public int getStringRes() {
        return stringRes > 0 ? stringRes : R.string.reviewed;
    }

    @DrawableRes public int getDrawableRes() {
        return drawableRes > 0 ? drawableRes : R.drawable.ic_eye;
    }
}

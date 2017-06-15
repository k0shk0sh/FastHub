package com.fastaccess.data.dao.types;

import android.support.annotation.DrawableRes;

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
}

package com.fastaccess.data.dao.types;

import android.support.annotation.StringRes;

import com.fastaccess.R;

public enum IssueState {
    open(R.string.opened),
    closed(R.string.closed),
    all(R.string.all);

    int status;

    IssueState(@StringRes int status) {
        this.status = status;
    }

    @StringRes public int getStatus() {
        return status;
    }
}
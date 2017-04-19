package com.fastaccess.data.dao.types;

import com.fastaccess.R;

/**
 * Created by Kosh on 19 Apr 2017, 7:57 PM
 */

public enum NotificationType {
    PullRequest(R.drawable.ic_pull_requests),
    Issue(R.drawable.ic_issues),
    Commit(R.drawable.ic_push);

    int drawableRes;

    NotificationType(int drawableRes) {
        this.drawableRes = drawableRes;
    }

    public int getDrawableRes() {
        return drawableRes > 0 ? drawableRes : R.drawable.ic_issues;
    }
}

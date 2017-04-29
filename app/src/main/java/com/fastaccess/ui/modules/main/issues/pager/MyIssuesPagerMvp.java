package com.fastaccess.ui.modules.main.issues.pager;

import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;

/**
 * Created by Kosh on 26 Mar 2017, 12:15 AM
 */

public interface MyIssuesPagerMvp {

    interface View extends BaseMvp.FAView, RepoPagerMvp.TabsBadgeListener {}

    interface Presenter {}
}

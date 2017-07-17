package com.fastaccess.ui.modules.repos.pull_requests.pull_request.merge;

import android.support.annotation.NonNull;

import com.fastaccess.ui.base.mvp.BaseMvp;

/**
 * Created by Kosh on 18 Mar 2017, 12:11 PM
 */

public interface MergePullReqeustMvp {

    interface MergeCallback {
        void onMerge(@NonNull String msg, @NonNull String mergeMethod);
    }

    interface View extends BaseMvp.FAView {

    }

    interface Presenter {}
}

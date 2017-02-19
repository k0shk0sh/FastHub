package com.fastaccess.ui.modules.repos.issues.issue;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.IssueModel;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 03 Dec 2016, 3:45 PM
 */

interface RepoIssuesMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener, android.view.View.OnClickListener {
        void onNotifyAdapter();

        @NonNull OnLoadMore<IssueState> getLoadMore();

        void onAddIssue();
    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseViewHolder.OnItemClickListener<IssueModel>,
            BaseMvp.PaginationListener<IssueState> {

        void onFragmentCreated(@NonNull Bundle bundle);

        void onWorkOffline();

        @NonNull ArrayList<IssueModel> getIssues();

        @NonNull String repoId();

        @NonNull String login();
    }
}

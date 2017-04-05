package com.fastaccess.ui.modules.repos.issues.issue;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.PullsIssuesParser;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 03 Dec 2016, 3:45 PM
 */

interface RepoIssuesMvp {

    int ISSUE_REQUEST_CODE = 1002;

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener, android.view.View.OnClickListener {
        void onNotifyAdapter();

        @NonNull OnLoadMore<IssueState> getLoadMore();

        void onAddIssue();

        void onUpdateCount(int totalCount);

        void onOpenIssue(@NonNull PullsIssuesParser parser);
    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseViewHolder.OnItemClickListener<Issue>,
            BaseMvp.PaginationListener<IssueState> {

        void onFragmentCreated(@NonNull Bundle bundle, @NonNull IssueState issueState);

        void onWorkOffline();

        @NonNull ArrayList<Issue> getIssues();

        @NonNull String repoId();

        @NonNull String login();
    }
}

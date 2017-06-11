package com.fastaccess.ui.modules.repos.pull_requests.pull_request;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.PullsIssuesParser;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 03 Dec 2016, 3:45 PM
 */

interface RepoPullRequestMvp {

    int PULL_REQUEST_REQUEST_CODE = 1003;

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener, android.view.View.OnClickListener {
        void onNotifyAdapter(@Nullable List<PullRequest> items, int page);

        @NonNull OnLoadMore<IssueState> getLoadMore();

        void onUpdateCount(int totalCount);

        void onOpenPullRequest(@NonNull PullsIssuesParser parser);

        void onShowPullRequestPopup(@NonNull PullRequest item);
    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseViewHolder.OnItemClickListener<PullRequest>,
            BaseMvp.PaginationListener<IssueState> {
        void onFragmentCreated(@NonNull Bundle bundle);

        void onWorkOffline();

        @NonNull ArrayList<PullRequest> getPullRequests();

        @NonNull IssueState getIssueState();
    }
}

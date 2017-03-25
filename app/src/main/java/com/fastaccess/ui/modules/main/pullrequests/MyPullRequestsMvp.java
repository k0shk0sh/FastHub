package com.fastaccess.ui.modules.main.pullrequests;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 25 Mar 2017, 11:39 PM
 */

public interface MyPullRequestsMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener, android.view.View.OnClickListener {
        void onNotifyAdapter();

        @NonNull OnLoadMore<IssueState> getLoadMore();

        void onSetCount(int totalCount);
    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseViewHolder.OnItemClickListener<PullRequest>,
            BaseMvp.PaginationListener<IssueState> {

        @NonNull ArrayList<PullRequest> getPullRequests();
    }
}

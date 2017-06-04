package com.fastaccess.ui.modules.main.issues;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.data.dao.types.MyIssuesType;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 25 Mar 2017, 11:39 PM
 */

public interface MyIssuesMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener, android.view.View.OnClickListener {
        void onNotifyAdapter(@Nullable List<Issue> items, int page);

        @NonNull OnLoadMore<IssueState> getLoadMore();

        void onSetCount(int totalCount);

        void onFilterIssue(@NonNull IssueState issueState);

        void onShowPopupDetails(@NonNull Issue item);
    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseViewHolder.OnItemClickListener<Issue>,
            BaseMvp.PaginationListener<IssueState> {

        @NonNull ArrayList<Issue> getIssues();

        void onSetIssueType(@NonNull MyIssuesType issuesType);
    }
}

package com.fastaccess.ui.modules.filter.issues.fragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 09 Apr 2017, 7:06 PM
 */

public interface FilterIssuesMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener, android.view.View.OnClickListener {

        void onClear();

        void onSearch(@NonNull String query, boolean isOpen, boolean isIssue, boolean isEnterprise);

        void onNotifyAdapter(@Nullable List<Issue> items, int page);

        @NonNull OnLoadMore<String> getLoadMore();

        void onSetCount(int totalCount);

        void onItemClicked(@NonNull Issue item);

        void onShowPopupDetails(@NonNull Issue item);
    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseViewHolder.OnItemClickListener<Issue>,
            BaseMvp.PaginationListener<String> {

        @NonNull ArrayList<Issue> getIssues();

    }
}

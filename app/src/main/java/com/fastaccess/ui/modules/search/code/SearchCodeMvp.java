package com.fastaccess.ui.modules.search.code;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.SearchCodeModel;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 03 Dec 2016, 3:45 PM
 */

interface SearchCodeMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener, android.view.View.OnClickListener {
        void onNotifyAdapter(@Nullable List<SearchCodeModel> items, int page);

        void onSetTabCount(int count);

        void onSetSearchQuery(@NonNull String query, boolean showRepoName);

        void onQueueSearch(@NonNull String query);

        void onQueueSearch(@NonNull String query, boolean showRepoName);

        @NonNull OnLoadMore<String> getLoadMore();

        void onItemClicked(@NonNull SearchCodeModel item);
    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseViewHolder.OnItemClickListener<SearchCodeModel>,
            BaseMvp.PaginationListener<String> {

        @NonNull ArrayList<SearchCodeModel> getCodes();

    }
}

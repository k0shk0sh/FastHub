package com.fastaccess.ui.modules.profile.starred;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 03 Dec 2016, 3:45 PM
 */

interface ProfileStarredMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener, android.view.View.OnClickListener {
        void onNotifyAdapter(@Nullable List<Repo> items, int page);

        @NonNull OnLoadMore<String> getLoadMore();

        void onUpdateCount(int starredCount);
    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseViewHolder.OnItemClickListener<Repo>,
            BaseMvp.PaginationListener<String> {

        @NonNull ArrayList<Repo> getRepos();

        void onWorkOffline(@NonNull String login);
    }
}

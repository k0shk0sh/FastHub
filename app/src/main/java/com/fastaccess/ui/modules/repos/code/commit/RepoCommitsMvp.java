package com.fastaccess.ui.modules.repos.code.commit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.CommitModel;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 03 Dec 2016, 3:45 PM
 */

interface RepoCommitsMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener, android.view.View.OnClickListener {
        void onNotifyAdapter();

        @NonNull OnLoadMore getLoadMore();
    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseViewHolder.OnItemClickListener<CommitModel>,
            BaseMvp.PaginationListener {
        void onFragmentCreated(@NonNull Bundle bundle);

        @NonNull ArrayList<CommitModel> getCommits();

        void onWorkOffline();
    }
}

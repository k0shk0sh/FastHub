package com.fastaccess.ui.modules.repos.code.commit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.BranchesModel;
import com.fastaccess.data.dao.model.Commit;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.repos.extras.branches.BranchesMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 03 Dec 2016, 3:45 PM
 */

interface RepoCommitsMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener,
            android.view.View.OnClickListener, BranchesMvp.BranchSelectionListener {
        void onNotifyAdapter(@Nullable List<Commit> items, int page);

        @NonNull OnLoadMore getLoadMore();

        void setBranchesData(@NonNull List<BranchesModel> branches, boolean firstTime);

        void onShowCommitCount(long sum);
    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseViewHolder.OnItemClickListener<Commit>,
            BaseMvp.PaginationListener {
        void onFragmentCreated(@NonNull Bundle bundle);

        @NonNull ArrayList<Commit> getCommits();

        void onWorkOffline();

        void onBranchChanged(@NonNull String branch);

        String getDefaultBranch();

    }
}

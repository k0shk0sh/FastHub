package com.fastaccess.ui.modules.repos.code.files;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.RepoFilesModel;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 20 Nov 2016, 11:10 AM
 */

interface RepoFilesMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener {
        void onNotifyAdapter();

        void onItemClicked(@NonNull RepoFilesModel model);

        void onMenuClicked(@NonNull RepoFilesModel model, android.view.View view);

        void onSetData(@NonNull String login, @NonNull String repoId, @Nullable String path);

        boolean isRefreshing();
    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseViewHolder.OnItemClickListener<RepoFilesModel> {

        @NonNull ArrayList<RepoFilesModel> getFiles();

        void onWorkOffline();

        void onCallApi();

        void onInitDataAndRequest(@NonNull String login, @NonNull String repoId, @Nullable String path);

        @Nullable ArrayList<RepoFilesModel> getCachedFiles(@NonNull String url);

        void cacheFiles(@NonNull String url, @NonNull ArrayList<RepoFilesModel> files);
    }


}

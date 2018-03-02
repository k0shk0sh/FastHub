package com.fastaccess.ui.modules.repos.code.files;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.model.RepoFile;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.repos.git.delete.DeleteContentFileCallback;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 20 Nov 2016, 11:10 AM
 */

interface RepoFilesMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener, DeleteContentFileCallback {
        void onNotifyAdapter();

        void onItemClicked(@NonNull RepoFile model);

        void onMenuClicked(int position, @NonNull RepoFile model, android.view.View view);

        void onSetData(@NonNull String login, @NonNull String repoId, @NonNull String path, @NonNull String ref,
                       boolean clear, @Nullable RepoFile toAppend);

        boolean isRefreshing();

        void onUpdateTab(@Nullable RepoFile toAppend);
    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseViewHolder.OnItemClickListener<RepoFile> {

        @NonNull ArrayList<RepoFile> getFiles();

        void onWorkOffline();

        void onCallApi(@Nullable RepoFile toAppend);

        void onInitDataAndRequest(@NonNull String login, @NonNull String repoId, @NonNull String path,
                                  @NonNull String ref, boolean clear, @Nullable RepoFile toAppend);

        @Nullable List<RepoFile> getCachedFiles(@NonNull String url, @NonNull String ref);

        void onDeleteFile(@NonNull String message, @NonNull RepoFile item, @NonNull String branch);
    }


}

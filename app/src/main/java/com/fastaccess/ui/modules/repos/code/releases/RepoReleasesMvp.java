package com.fastaccess.ui.modules.repos.code.releases;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.SimpleUrlsModel;
import com.fastaccess.data.dao.model.Release;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.dialog.ListDialogView;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 03 Dec 2016, 3:45 PM
 */

interface RepoReleasesMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener,
            android.view.View.OnClickListener, ListDialogView.onSimpleItemSelection<SimpleUrlsModel> {
        void onNotifyAdapter(@Nullable List<Release> items, int page);

        @NonNull OnLoadMore getLoadMore();

        void onDownload(@NonNull Release item);

        void onShowDetails(@NonNull Release item);
    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseViewHolder.OnItemClickListener<Release>,
            BaseMvp.PaginationListener {

        void onFragmentCreated(@NonNull Bundle bundle);

        void onWorkOffline();

        @NonNull ArrayList<Release> getReleases();
    }
}

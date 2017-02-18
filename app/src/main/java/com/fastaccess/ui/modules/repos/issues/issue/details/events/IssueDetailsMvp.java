package com.fastaccess.ui.modules.repos.issues.issue.details.events;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.IssueEventAdapterModel;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 13 Dec 2016, 12:36 AM
 */

interface IssueDetailsMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener, android.view.View.OnClickListener {

        void onNotifyAdapter();

        @NonNull OnLoadMore getLoadMore();
    }

    interface Presenter extends BaseMvp.FAPresenter, BaseViewHolder.OnItemClickListener<IssueEventAdapterModel>,
            BaseMvp.PaginationListener {

        @NonNull ArrayList<IssueEventAdapterModel> getEvents();

        void onFragmentCreated(@Nullable Bundle bundle);

        void onWorkOffline();
    }
}

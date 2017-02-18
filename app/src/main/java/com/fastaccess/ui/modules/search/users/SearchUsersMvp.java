package com.fastaccess.ui.modules.search.users;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.UserModel;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 03 Dec 2016, 3:45 PM
 */

interface SearchUsersMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener, android.view.View.OnClickListener {
        void onNotifyAdapter();

        void onSetSearchQuery(@NonNull String query);

        @NonNull OnLoadMore<String> getLoadMore();
    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseViewHolder.OnItemClickListener<UserModel>,
            BaseMvp.PaginationListener<String> {

        @NonNull ArrayList<UserModel> getUsers();

    }
}

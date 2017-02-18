
package com.fastaccess.ui.modules.profile.followers;

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

interface ProfileFollowersMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener, android.view.View.OnClickListener {
        void onNotifyAdapter();

        @NonNull OnLoadMore<String> getLoadMore();
    }

    interface Presenter extends BaseMvp.FAPresenter,
            BaseViewHolder.OnItemClickListener<UserModel>,
            BaseMvp.PaginationListener<String> {

        @NonNull ArrayList<UserModel> getFollowers();

        void onWorkOffline(@NonNull String login);
    }
}

package com.fastaccess.ui.modules.repos.extras.misc;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.model.User;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 04 May 2017, 8:30 PM
 */

public interface RepoMiscMVp {

    int WATCHERS = 0;
    int FORKS = 1;
    int STARS = 2;

    @IntDef({
            WATCHERS,
            FORKS,
            STARS
    })
    @Retention(RetentionPolicy.SOURCE) @interface MiscType {}


    interface View extends BaseMvp.FAView {
        void onNotifyAdapter(@Nullable List<User> items, int page);

        @NonNull OnLoadMore<Integer> getLoadMore();
    }

    interface Presenter extends BaseMvp.PaginationListener<Integer>, BaseViewHolder.OnItemClickListener<User> {

        @NonNull ArrayList<User> getList();

        @MiscType int getType();
    }
}

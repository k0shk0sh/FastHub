package com.fastaccess.ui.modules.repos.reactions;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.model.User;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.base.mvp.BaseMvp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 11 Apr 2017, 11:19 AM
 */

public interface ReactionsDialogMvp {

    interface View extends BaseMvp.FAView {
        void onNotifyAdapter(@Nullable List<User> items, int page);

        @NonNull OnLoadMore getLoadMore();
    }

    interface Presenter extends BaseMvp.PaginationListener {
        void onFragmentCreated(@Nullable Bundle bundle);

        @NonNull ArrayList<User> getUsers();
    }
}

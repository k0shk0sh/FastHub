package com.fastaccess.ui.modules.main.orgs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.model.User;
import com.fastaccess.ui.base.mvp.BaseMvp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 15 Apr 2017, 1:53 PM
 */

public interface OrgListDialogMvp {

    interface View extends BaseMvp.FAView {
        void onNotifyAdapter(@Nullable List<User> items);

    }

    interface Presenter {
        void onLoadOrgs();

        @NonNull ArrayList<User> getOrgs();
    }
}

package com.fastaccess.ui.modules.profile.org;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.model.User;
import com.fastaccess.ui.base.mvp.BaseMvp;

/**
 * Created by Kosh on 03 Dec 2016, 7:59 AM
 */

public interface OrgProfileOverviewMvp {

    interface View extends BaseMvp.FAView {
        void onInitViews(@Nullable User userModel);
    }

    interface Presenter extends BaseMvp.FAPresenter {
        void onFragmentCreated(@Nullable Bundle bundle);

        void onWorkOffline(@NonNull String login);

        @NonNull String getLogin();
    }
}

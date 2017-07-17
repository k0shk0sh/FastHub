package com.fastaccess.ui.modules.user;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.model.Login;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import lombok.Getter;

/**
 * Created by Kosh on 03 Dec 2016, 8:00 AM
 */

@Getter class UserPagerPresenter extends BasePresenter<UserPagerMvp.View> implements UserPagerMvp.Presenter {
    @com.evernote.android.state.State int isMember = -1;

    @Override public void checkOrgMembership(@NonNull String org) {
        makeRestCall(RestProvider.getOrgService(isEnterprise()).isMember(org, Login.getUser().getLogin()),
                booleanResponse -> sendToView(view -> {
                    isMember = booleanResponse.code() == 204 ? 1 : 0;
                    view.onInitOrg(isMember == 1);
                }));
    }
}

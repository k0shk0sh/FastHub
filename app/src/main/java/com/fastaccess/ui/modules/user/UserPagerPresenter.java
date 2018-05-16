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
    @com.evernote.android.state.State boolean isUserBlocked;
    @com.evernote.android.state.State boolean isUserBlockedRequested;

    @Override public void onCheckBlocking(@NonNull String login) {
        makeRestCall(RestProvider.getUserService(isEnterprise()).isUserBlocked(login),
                booleanResponse -> sendToView(view -> {
                    isUserBlockedRequested = true;
                    isUserBlocked = booleanResponse.code() == 204;
                    view.onInvalidateMenu();
                }));
    }

    @Override public void checkOrgMembership(@NonNull String org) {
        makeRestCall(RestProvider.getOrgService(isEnterprise()).isMember(org, Login.getUser().getLogin()),
                booleanResponse -> sendToView(view -> {
                    isMember = booleanResponse.code() == 204 ? 1 : 0;
                    view.onInitOrg(isMember == 1);
                }));
    }

    @Override public void onBlockUser(@NonNull String login) {
        if (isUserBlocked()) {
            onUnblockUser(login);
        } else {
            makeRestCall(RestProvider.getUserService(isEnterprise()).blockUser(login),
                    booleanResponse -> sendToView(view -> {
                        isUserBlocked = true;
                        view.onUserBlocked();
                    }));
        }
    }

    @Override public void onUnblockUser(@NonNull String login) {
        makeRestCall(RestProvider.getUserService(isEnterprise()).unBlockUser(login),
                booleanResponse -> sendToView(view -> {
                    isUserBlocked = false;
                    view.onUserUnBlocked();
                }));
    }
}

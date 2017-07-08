package com.fastaccess.ui.modules.profile.org;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

/**
 * Created by Kosh on 04 Apr 2017, 10:36 AM
 */

public class OrgProfileOverviewPresenter extends BasePresenter<OrgProfileOverviewMvp.View> implements OrgProfileOverviewMvp.Presenter {
    @com.evernote.android.state.State String login;

    @Override public void onError(@NonNull Throwable throwable) {
        if (!InputHelper.isEmpty(login)) {
            onWorkOffline(login);
        }
        super.onError(throwable);
    }

    @Override public void onFragmentCreated(@Nullable Bundle bundle) {
        if (bundle == null || bundle.getString(BundleConstant.EXTRA) == null) {
            throw new NullPointerException("Either bundle or User is null");
        }
        login = bundle.getString(BundleConstant.EXTRA);
        if (login != null) {
            makeRestCall(RestProvider.getOrgService(isEnterprise()).getOrganization(login),
                    this::onSendUserToView);
        }
    }

    @Override public void onWorkOffline(@NonNull String login) {
        onSendUserToView(User.getUser(login));
    }

    @NonNull @Override public String getLogin() {
        return login;
    }

    private void onSendUserToView(User userModel) {
        sendToView(view -> view.onInitViews(userModel));
    }
}

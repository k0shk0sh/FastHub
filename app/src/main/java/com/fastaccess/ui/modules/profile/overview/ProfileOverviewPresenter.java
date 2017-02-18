package com.fastaccess.ui.modules.profile.overview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.UserModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import rx.Observable;

/**
 * Created by Kosh on 03 Dec 2016, 9:16 AM
 */

class ProfileOverviewPresenter extends BasePresenter<ProfileOverviewMvp.View> implements ProfileOverviewMvp.Presenter {

    private String login;

    @Override public <T> T onError(@NonNull Throwable throwable, @NonNull Observable<T> observable) {
        if (!InputHelper.isEmpty(login)) {
            onWorkOffline(login);
        }
        return super.onError(throwable, observable);
    }

    @Override public void onFragmentCreated(@Nullable Bundle bundle) {
        if (bundle == null || bundle.getString(BundleConstant.EXTRA) == null) {
            throw new NullPointerException("Either bundle or User is null");
        }
        login = bundle.getString(BundleConstant.EXTRA);
        if (login != null) {
            makeRestCall(RestProvider.getUserService().getUser(login),
                    userModel -> {
                        onSendUserToView(userModel);
                        if (userModel != null) {
                            userModel.save();
                        }
                    });
        }
    }

    @Override public void onWorkOffline(@NonNull String login) {
        UserModel userModel = UserModel.getUser(login);
        if (userModel == null) return;
        onSendUserToView(userModel);
    }

    @Override public void onSendUserToView(@Nullable UserModel userModel) {
        sendToView(view -> view.onInitViews(userModel));
    }
}
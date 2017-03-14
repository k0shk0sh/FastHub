package com.fastaccess.ui.modules.profile.overview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.fastaccess.data.dao.LoginModel;
import com.fastaccess.data.dao.UserModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

/**
 * Created by Kosh on 03 Dec 2016, 9:16 AM
 */

class ProfileOverviewPresenter extends BasePresenter<ProfileOverviewMvp.View> implements ProfileOverviewMvp.Presenter {
    private boolean isSuccessResponse;
    private boolean isFollowing;
    private String login;

    @Override public void onCheckFollowStatus(@NonNull String login) {
        if (!TextUtils.equals(login, LoginModel.getUser().getLogin()))
            makeRestCall(RestProvider.getUserService().getFollowStatus(login),
                    booleanResponse -> {
                        isSuccessResponse = true;
                        isFollowing = booleanResponse.code() == 204;
                        sendToView(ProfileOverviewMvp.View::onInvalidateMenuItem);
                    });
    }

    @Override public boolean isSuccessResponse() {
        return isSuccessResponse;
    }

    @Override public boolean isFollowing() {
        return isFollowing;
    }

    @Override public void onFollowButtonClicked(@NonNull String login) {
        manageSubscription(RxHelper.getObserver(!isFollowing ? RestProvider.getUserService().followUser(login)
                                                             : RestProvider.getUserService().unfollowUser(login))
                .subscribe(booleanResponse -> {
                    if (booleanResponse.code() == 204) {
                        isFollowing = !isFollowing;
                        sendToView(ProfileOverviewMvp.View::onInvalidateMenuItem);
                    }
                }, this::onError));
    }

    @Override public void onError(@NonNull Throwable throwable) {
        if (!InputHelper.isEmpty(login)) {
            onWorkOffline(login);
        }
        sendToView(ProfileOverviewMvp.View::onInvalidateMenuItem);
        super.onError(throwable);
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
                            if (userModel.getType() != null && userModel.getType().equals("user")) {
                                onCheckFollowStatus(login);
                            }
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

    @NonNull @Override public String getLogin() {
        return login;
    }
}
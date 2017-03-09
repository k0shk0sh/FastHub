package com.fastaccess.ui.modules.user;

import android.support.annotation.NonNull;

import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

/**
 * Created by Kosh on 03 Dec 2016, 8:00 AM
 */

class UserPagerPresenter extends BasePresenter<UserPagerMvp.View> implements UserPagerMvp.Presenter {
    private boolean isSuccessResponse;
    private boolean isFollowing;

    @Override public void onCheckFollowStatus(@NonNull String login) {
        makeRestCall(RestProvider.getUserService().getFollowStatus(login),
                booleanResponse -> {
                    isSuccessResponse = true;
                    isFollowing = booleanResponse.code() == 204;
                    sendToView(UserPagerMvp.View::onInvalidateMenuItem);
                });
    }

    @Override public boolean isSuccessResponse() {
        return isSuccessResponse;
    }

    @Override public boolean isFollowing() {
        return isFollowing;
    }

    @Override public void onFollowMenuItemClicked(@NonNull String login) {
        makeRestCall(!isFollowing ? RestProvider.getUserService().followUser(login) : RestProvider.getUserService().unfollowUser(login),
                booleanResponse -> {
                    if (booleanResponse.code() == 204) {
                        isFollowing = !isFollowing;
                        sendToView(UserPagerMvp.View::onInvalidateMenuItem);
                    }
                });
    }

    @Override public void onError(@NonNull Throwable throwable) {
        sendToView(UserPagerMvp.View::onInvalidateMenuItem);
        super.onError(throwable);
    }
}

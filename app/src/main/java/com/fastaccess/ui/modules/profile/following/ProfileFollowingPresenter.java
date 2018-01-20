package com.fastaccess.ui.modules.profile.following;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class ProfileFollowingPresenter extends BasePresenter<ProfileFollowingMvp.View> implements ProfileFollowingMvp.Presenter {

    private ArrayList<User> users = new ArrayList<>();
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;

    @Override public int getCurrentPage() {
        return page;
    }

    @Override public int getPreviousTotal() {
        return previousTotal;
    }

    @Override public void setCurrentPage(int page) {
        this.page = page;
    }

    @Override public void setPreviousTotal(int previousTotal) {
        this.previousTotal = previousTotal;
    }

    @Override public void onError(@NonNull Throwable throwable) {
        sendToView(view -> {//wait view
            if (view.getLoadMore().getParameter() != null) {
                onWorkOffline(view.getLoadMore().getParameter());
            }
        });
        super.onError(throwable);
    }

    @Override public boolean onCallApi(int page, @Nullable String parameter) {
        if (parameter == null) {
            throw new NullPointerException("Username is null");
        }
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        setCurrentPage(page);
        if (page > lastPage || lastPage == 0) {
            sendToView(ProfileFollowingMvp.View::hideProgress);
            return false;
        }
        makeRestCall(RestProvider.getUserService(isEnterprise()).getFollowing(parameter, page),
                response -> {
                    lastPage = response.getLast();
                    if (getCurrentPage() == 1) {
                        manageDisposable(User.saveUserFollowingList(response.getItems(), parameter));
                    }
                    sendToView(view -> view.onNotifyAdapter(response.getItems(), page));
                });
        return true;
    }

    @NonNull @Override public ArrayList<User> getFollowing() {
        return users;
    }

    @Override public void onWorkOffline(@NonNull String login) {
        if (users.isEmpty()) {
            manageDisposable(RxHelper.getSingle(User.getUserFollowingList(login)).subscribe(userModels ->
                    sendToView(view -> view.onNotifyAdapter(userModels, 1))));
        } else {
            sendToView(ProfileFollowingMvp.View::hideProgress);
        }
    }

    @Override public void onItemClick(int position, View v, User item) {}

    @Override public void onItemLongClick(int position, View v, User item) {}
}

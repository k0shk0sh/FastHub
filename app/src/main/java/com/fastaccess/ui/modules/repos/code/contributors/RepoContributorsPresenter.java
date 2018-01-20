package com.fastaccess.ui.modules.repos.code.contributors;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class RepoContributorsPresenter extends BasePresenter<RepoContributorsMvp.View> implements RepoContributorsMvp.Presenter {

    private ArrayList<User> users = new ArrayList<>();
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;
    @com.evernote.android.state.State String repoId;
    @com.evernote.android.state.State String login;

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

    @Override public boolean onCallApi(int page, @Nullable Object parameter) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        setCurrentPage(page);
        if (page > lastPage || lastPage == 0) {
            sendToView(RepoContributorsMvp.View::hideProgress);
            return false;
        }
        makeRestCall(RestProvider.getRepoService(isEnterprise()).getContributors(login, repoId, page),
                response -> {
                    if (response != null) {
                        lastPage = response.getLast();
                    }
                    sendToView(view -> view.onNotifyAdapter(response != null ? response.getItems() : null, page));
                });
        return true;
    }

    @Override public void onFragmentCreated(@NonNull Bundle bundle) {
        repoId = bundle.getString(BundleConstant.ID);
        login = bundle.getString(BundleConstant.EXTRA);
        if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
            onCallApi(1, null);
        }
    }

    @Override public void onError(@NonNull Throwable throwable) {
        onWorkOffline();
        super.onError(throwable);
    }

    @Override public void onWorkOffline() {
        sendToView(BaseMvp.FAView::hideProgress);
    }

    @NonNull @Override public ArrayList<User> getUsers() {
        return users;
    }

    @Override public void onItemClick(int position, View v, User item) {}

    @Override public void onItemLongClick(int position, View v, User item) {}
}

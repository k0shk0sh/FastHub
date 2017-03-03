package com.fastaccess.ui.modules.profile.repos;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.fastaccess.data.dao.LoginModel;
import com.fastaccess.data.dao.NameParser;
import com.fastaccess.data.dao.RepoModel;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.RepoPagerView;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class ProfileReposPresenter extends BasePresenter<ProfileReposMvp.View> implements ProfileReposMvp.Presenter {

    private ArrayList<RepoModel> repos = new ArrayList<>();
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;
    private String currentLoggedIn;

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

    @Override public <T> T onError(@NonNull Throwable throwable, @NonNull Observable<T> observable) {
        sendToView(view -> {
            if (view.getLoadMore().getParameter() != null) {
                onWorkOffline(view.getLoadMore().getParameter());
            }
        });
        return super.onError(throwable, observable);
    }

    @Override public void onCallApi(int page, @Nullable String parameter) {
        if (currentLoggedIn == null) {
            currentLoggedIn = LoginModel.getUser().getLogin();
        }
        if (parameter == null) {
            throw new NullPointerException("Username is null");
        }
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        setCurrentPage(page);
        if (page > lastPage || lastPage == 0) {
            sendToView(ProfileReposMvp.View::hideProgress);
            return;
        }
        makeRestCall(TextUtils.equals(currentLoggedIn, parameter)
                     ? RestProvider.getUserService().getRepos(page)
                     : RestProvider.getUserService().getRepos(parameter, page),
                repoModelPageable -> {
                    lastPage = repoModelPageable.getLast();
                    if (getCurrentPage() == 1) {
                        getRepos().clear();
                        manageSubscription(RepoModel.saveMyRepos(repoModelPageable.getItems(), parameter).subscribe());
                    }
                    getRepos().addAll(repoModelPageable.getItems());
                    sendToView(ProfileReposMvp.View::onNotifyAdapter);
                });
    }

    @NonNull @Override public ArrayList<RepoModel> getRepos() {
        return repos;
    }

    @Override public void onWorkOffline(@NonNull String login) {
        if (repos.isEmpty()) {
            manageSubscription(RepoModel.getMyRepos(login).subscribe(repoModels -> {
                repos.addAll(repoModels);
                sendToView(ProfileReposMvp.View::onNotifyAdapter);
            }));
        } else {
            sendToView(ProfileReposMvp.View::hideProgress);
        }
    }

    @Override public void onItemClick(int position, View v, RepoModel item) {
        RepoPagerView.startRepoPager(v.getContext(), new NameParser(item.getHtmlUrl()));
    }

    @Override public void onItemLongClick(int position, View v, RepoModel item) {
        onItemClick(position, v, item);
    }
}

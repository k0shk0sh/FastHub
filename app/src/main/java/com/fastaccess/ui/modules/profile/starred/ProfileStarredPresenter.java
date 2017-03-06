package com.fastaccess.ui.modules.profile.starred;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.NameParser;
import com.fastaccess.data.dao.RepoModel;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.RepoPagerView;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class ProfileStarredPresenter extends BasePresenter<ProfileStarredMvp.View> implements ProfileStarredMvp.Presenter {

    private ArrayList<RepoModel> repos = new ArrayList<>();
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

    @Override public <T> T onError(@NonNull Throwable throwable, @NonNull Observable<T> observable) {
        sendToView(view -> {
            if (view.getLoadMore().getParameter() != null) {
                onWorkOffline(view.getLoadMore().getParameter());
            }
        });
        return super.onError(throwable, observable);
    }

    @Override public void onCallApi(int page, @Nullable String parameter) {
        if (parameter == null) {
            throw new NullPointerException("Username is null");
        }
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        setCurrentPage(page);
        if (page > lastPage || lastPage == 0) {
            sendToView(ProfileStarredMvp.View::hideProgress);
            return;
        }
        makeRestCall(RestProvider.getUserService().getStarred(parameter, page),
                repoModelPageable -> {
                    lastPage = repoModelPageable.getLast();
                    if (getCurrentPage() == 1) {
                        getRepos().clear();
                        manageSubscription(RepoModel.saveStarred(repoModelPageable.getItems(), parameter).subscribe());
                    }
                    getRepos().addAll(repoModelPageable.getItems());
                    sendToView(ProfileStarredMvp.View::onNotifyAdapter);
                });
    }

    @NonNull @Override public ArrayList<RepoModel> getRepos() {
        return repos;
    }

    @Override public void onWorkOffline(@NonNull String login) {
        if (repos.isEmpty()) {
            manageSubscription(RxHelper.getObserver(RepoModel.getStarred(login)).subscribe(repoModels -> {
                repos.addAll(repoModels);
                Logger.e(repoModels);
                sendToView(ProfileStarredMvp.View::onNotifyAdapter);
            }));
        } else {
            sendToView(ProfileStarredMvp.View::hideProgress);
        }
    }

    @Override public void onItemClick(int position, View v, RepoModel item) {
        RepoPagerView.startRepoPager(v.getContext(), new NameParser(item.getHtmlUrl()));
    }

    @Override public void onItemLongClick(int position, View v, RepoModel item) {
        onItemClick(position, v, item);
    }
}

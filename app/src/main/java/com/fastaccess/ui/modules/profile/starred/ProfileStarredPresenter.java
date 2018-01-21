package com.fastaccess.ui.modules.profile.starred;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class ProfileStarredPresenter extends BasePresenter<ProfileStarredMvp.View> implements ProfileStarredMvp.Presenter {

    @com.evernote.android.state.State int starredCount = -1;
    private ArrayList<Repo> repos = new ArrayList<>();
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
        sendToView(view -> {
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
            sendToView(ProfileStarredMvp.View::hideProgress);
            return false;
        }
        Observable<Pageable<Repo>> observable;
        if (starredCount == -1) {
            observable = Observable.zip(RestProvider.getUserService(isEnterprise()).getStarred(parameter, page),
                    RestProvider.getUserService(isEnterprise()).getStarredCount(parameter), (repoPageable, count) -> {
                        if (count != null) {
                            starredCount = count.getLast();
                        }
                        return repoPageable;
                    });
        } else {
            observable = RestProvider.getUserService(isEnterprise()).getStarred(parameter, page);
        }
        makeRestCall(observable, repoModelPageable -> {
            lastPage = repoModelPageable.getLast();
            if (getCurrentPage() == 1) {
                manageDisposable(Repo.saveStarred(repoModelPageable.getItems(), parameter));
            }
            sendToView(view -> {
                view.onUpdateCount(starredCount);
                view.onNotifyAdapter(repoModelPageable.getItems(), page);
            });
        });
        return true;
    }

    @NonNull @Override public ArrayList<Repo> getRepos() {
        return repos;
    }

    @Override public void onWorkOffline(@NonNull String login) {
        if (repos.isEmpty()) {
            manageDisposable(RxHelper.getObservable(Repo.getStarred(login).toObservable()).subscribe(repoModels ->
                    sendToView(view -> {
                        starredCount = -1;
                        view.onUpdateCount(repoModels != null ? repoModels.size() : 0);
                        view.onNotifyAdapter(repoModels, 1);
                    })));
        } else {
            sendToView(ProfileStarredMvp.View::hideProgress);
        }
    }

    @Override public void onItemClick(int position, View v, Repo item) {
        SchemeParser.launchUri(v.getContext(), item.getHtmlUrl());
    }

    @Override public void onItemLongClick(int position, View v, Repo item) {}
}

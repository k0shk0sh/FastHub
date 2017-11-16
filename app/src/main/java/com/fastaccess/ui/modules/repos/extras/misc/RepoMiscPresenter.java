package com.fastaccess.ui.modules.repos.extras.misc;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by Kosh on 04 May 2017, 8:33 PM
 */

public class RepoMiscPresenter extends BasePresenter<RepoMiscMVp.View> implements RepoMiscMVp.Presenter {

    private ArrayList<User> users = new ArrayList<>();
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;
    @com.evernote.android.state.State String owner;
    @com.evernote.android.state.State String repo;
    @com.evernote.android.state.State @RepoMiscMVp.MiscType int type;

    RepoMiscPresenter(@Nullable Bundle arguments) {
        if (arguments == null) return;
        if (InputHelper.isEmpty(owner) || InputHelper.isEmpty(repo)) {
            owner = arguments.getString(BundleConstant.EXTRA);
            repo = arguments.getString(BundleConstant.ID);
            type = arguments.getInt(BundleConstant.EXTRA_TYPE);
            onCallApi(1, type);
        }
    }

    @NonNull @Override public ArrayList<User> getList() {
        return users;
    }

    @Override public int getType() {
        return type;
    }

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

    @Override public boolean onCallApi(int page, @RepoMiscMVp.MiscType @Nullable Integer parameter) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        setCurrentPage(page);
        if (page > lastPage || lastPage == 0) {
            sendToView(RepoMiscMVp.View::hideProgress);
            return false;
        }
        switch (type) {
            case RepoMiscMVp.WATCHERS:
                makeRestCall(RestProvider.getRepoService(isEnterprise()).getWatchers(owner, repo, page), response -> onResponse(page, response));
                return true;
            case RepoMiscMVp.STARS:
                makeRestCall(RestProvider.getRepoService(isEnterprise()).getStargazers(owner, repo, page), response -> onResponse(page, response));
                return true;
            case RepoMiscMVp.FORKS:
                makeRestCall(RestProvider.getRepoService(isEnterprise()).getForks(owner, repo, page)
                        .flatMap(repoPageable -> {
                            lastPage = repoPageable.getLast();
                            return Observable.fromIterable(repoPageable.getItems())
                                    .map(Repo::getOwner)
                                    .toList()
                                    .toObservable();
                        }), owners -> sendToView(view -> view.onNotifyAdapter(owners, page)));
                return true;
        }
        return false;
    }

    private void onResponse(int page, @Nullable Pageable<User> response) {
        if (response != null) {
            lastPage = response.getLast();
            sendToView(view -> view.onNotifyAdapter(response.getItems(), page));
        } else {
            sendToView(BaseMvp.FAView::hideProgress);
        }
    }

    @Override public void onItemClick(int position, View v, User item) {}

    @Override public void onItemLongClick(int position, View v, User item) {}
}

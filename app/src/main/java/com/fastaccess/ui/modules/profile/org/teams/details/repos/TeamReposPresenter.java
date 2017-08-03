package com.fastaccess.ui.modules.profile.org.teams.details.repos;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class TeamReposPresenter extends BasePresenter<TeamReposMvp.View> implements TeamReposMvp.Presenter {
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
        super.onError(throwable);
    }

    @Override public boolean onCallApi(int page, @Nullable Long parameter) {
        if (parameter == null) {
            throw new NullPointerException("Username is null");
        }
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        setCurrentPage(page);
        if (page > lastPage || lastPage == 0) {
            sendToView(TeamReposMvp.View::hideProgress);
            return false;
        }
        makeRestCall(RestProvider.getOrgService(isEnterprise()).getTeamRepos(parameter, page),
                repoModelPageable -> {
                    lastPage = repoModelPageable.getLast();
                    sendToView(view -> view.onNotifyAdapter(repoModelPageable.getItems(), page));
                });
        return true;
    }

    @NonNull @Override public ArrayList<Repo> getRepos() {
        return repos;
    }

    @Override public void onWorkOffline(@NonNull String login) {
        //TODO
    }

    @Override public void onItemClick(int position, View v, Repo item) {
        SchemeParser.launchUri(v.getContext(), item.getHtmlUrl());
    }

    @Override public void onItemLongClick(int position, View v, Repo item) {}
}

package com.fastaccess.ui.modules.profile.org.repos;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.FilterOptionsModel;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class OrgReposPresenter extends BasePresenter<OrgReposMvp.View> implements OrgReposMvp.Presenter {

    private ArrayList<Repo> repos = new ArrayList<>();
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;
    @com.evernote.android.state.State FilterOptionsModel filterOptions = new FilterOptionsModel();

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
            sendToView(OrgReposMvp.View::hideProgress);
            return false;
        }
        filterOptions.setOrg(true);
        makeRestCall(RestProvider.getOrgService(isEnterprise()).getOrgRepos(parameter, filterOptions.getQueryMap(), page),
                repoModelPageable -> {
                    lastPage = repoModelPageable.getLast();
                    if (getCurrentPage() == 1) {
                        manageDisposable(Repo.saveMyRepos(repoModelPageable.getItems(), parameter));
                    }
                    sendToView(view -> view.onNotifyAdapter(repoModelPageable.getItems(), page));
                });
        return true;
    }

    @NonNull @Override public ArrayList<Repo> getRepos() {
        return repos;
    }

    @Override public void onWorkOffline(@NonNull String login) {
        if (repos.isEmpty()) {
            manageDisposable(RxHelper.getObservable(Repo.getMyRepos(login).toObservable()).subscribe(repoModels ->
                    sendToView(view -> view.onNotifyAdapter(repoModels, 1))));
        } else {
            sendToView(OrgReposMvp.View::hideProgress);
        }
    }

    @Override public void onFilterApply(String org) {
        onCallApi(1, org);
    }

    @Override public void onTypeSelected(String selectedType) {
        filterOptions.setType(selectedType);
    }

    @Override public void onItemClick(int position, View v, Repo item) {
        SchemeParser.launchUri(v.getContext(), item.getHtmlUrl());
    }

    @Override public void onItemLongClick(int position, View v, Repo item) {}

    @NonNull FilterOptionsModel getFilterOptions() {
        return filterOptions;
    }
}

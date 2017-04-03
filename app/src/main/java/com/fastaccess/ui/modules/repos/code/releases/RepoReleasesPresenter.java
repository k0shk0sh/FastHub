package com.fastaccess.ui.modules.repos.code.releases;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.model.Release;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class RepoReleasesPresenter extends BasePresenter<RepoReleasesMvp.View> implements RepoReleasesMvp.Presenter {

    private ArrayList<Release> releases = new ArrayList<>();
    private String login;
    private String repoId;
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
        onWorkOffline();
        super.onError(throwable);
    }

    @Override public void onCallApi(int page, @Nullable Object parameter) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        setCurrentPage(page);
        if (page > lastPage || lastPage == 0) {
            sendToView(RepoReleasesMvp.View::hideProgress);
            return;
        }
        if (repoId == null || login == null) return;
        makeRestCall(RestProvider.getRepoService()
                        .getReleases(login, repoId, page),
                response -> {
                    if (response.getItems() == null || response.getItems().isEmpty()) {
                        makeRestCall(RestProvider.getRepoService()
                                        .getTagReleases(login, repoId, page),
                                this::onResponse);
                        return;
                    }
                    onResponse(response);
                });

    }

    private void onResponse(Pageable<Release> response) {
        lastPage = response.getLast();
        if (getCurrentPage() == 1) {
            getReleases().clear();
            manageSubscription(Release.save(response.getItems(), repoId, login).subscribe());
        }
        getReleases().addAll(response.getItems());
        sendToView(RepoReleasesMvp.View::onNotifyAdapter);
    }

    @Override public void onFragmentCreated(@NonNull Bundle bundle) {
        repoId = bundle.getString(BundleConstant.ID);
        login = bundle.getString(BundleConstant.EXTRA);
        if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
            onCallApi(1, null);
        }
    }

    @Override public void onWorkOffline() {
        if (releases.isEmpty()) {
            manageSubscription(RxHelper.getObserver(Release.get(repoId, login))
                    .subscribe(releasesModels -> {
                        releases.addAll(releasesModels);
                        sendToView(RepoReleasesMvp.View::onNotifyAdapter);
                    }));
        } else {
            sendToView(RepoReleasesMvp.View::hideProgress);
        }
    }

    @NonNull @Override public ArrayList<Release> getReleases() {
        return releases;
    }

    @Override public void onItemClick(int position, View v, Release item) {
        if (getView() == null) return;
        if (v.getId() == R.id.download) {
            getView().onDownload(item);
        } else {
            getView().onShowDetails(item);
        }
    }

    @Override public void onItemLongClick(int position, View v, Release item) {
        onItemClick(position, v, item);
    }
}

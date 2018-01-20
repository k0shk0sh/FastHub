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
    @com.evernote.android.state.State String login;
    @com.evernote.android.state.State String repoId;
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

    @Override public boolean onCallApi(int page, @Nullable Object parameter) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        setCurrentPage(page);
        if (page > lastPage || lastPage == 0) {
            sendToView(RepoReleasesMvp.View::hideProgress);
            return false;
        }
        if (repoId == null || login == null) return false;
        makeRestCall(RestProvider.getRepoService(isEnterprise()).getReleases(login, repoId, page),
                response -> {
                    if (response.getItems() == null || response.getItems().isEmpty()) {
                        makeRestCall(RestProvider.getRepoService(isEnterprise()).getTagReleases(login, repoId, page), this::onResponse);
                        return;
                    }
                    onResponse(response);
                });
        return true;

    }

    @Override public void onFragmentCreated(@NonNull Bundle bundle) {
        repoId = bundle.getString(BundleConstant.ID);
        login = bundle.getString(BundleConstant.EXTRA);
        String tag = bundle.getString(BundleConstant.EXTRA_THREE);
        long id = bundle.getLong(BundleConstant.EXTRA_TWO, -1);
        if (!InputHelper.isEmpty(tag)) {
            manageObservable(RestProvider.getRepoService(isEnterprise()).getTagRelease(login, repoId, tag)
                    .doOnNext(release -> {
                        if (release != null) {
                            sendToView(view -> view.onShowDetails(release));
                        }
                    }));
        } else if (id > 0) {
            manageObservable(RestProvider.getRepoService(isEnterprise()).getRelease(login, repoId, id)
                    .doOnNext(release -> {
                        if (release != null) {
                            sendToView(view -> view.onShowDetails(release));
                        }
                    }));
        }
        if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
            onCallApi(1, null);
        }
    }

    @Override public void onWorkOffline() {
        if (releases.isEmpty()) {
            manageDisposable(RxHelper.getSingle(Release.get(repoId, login))
                    .subscribe(releasesModels -> sendToView(view -> view.onNotifyAdapter(releasesModels, 1))));
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

    @Override public void onItemLongClick(int position, View v, Release item) {}

    private void onResponse(Pageable<Release> response) {
        lastPage = response.getLast();
        if (getCurrentPage() == 1) {
            manageDisposable(Release.save(response.getItems(), repoId, login));
        }
        sendToView(view -> view.onNotifyAdapter(response.getItems(), getCurrentPage()));
    }
}

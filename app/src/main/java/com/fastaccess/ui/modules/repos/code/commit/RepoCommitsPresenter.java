package com.fastaccess.ui.modules.repos.code.commit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.model.Commit;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerActivity;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class RepoCommitsPresenter extends BasePresenter<RepoCommitsMvp.View> implements RepoCommitsMvp.Presenter {

    private ArrayList<Commit> commits = new ArrayList<>();
    @com.evernote.android.state.State String login;
    @com.evernote.android.state.State String repoId;
    @com.evernote.android.state.State String branch;
    @com.evernote.android.state.State String path;
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
            sendToView(RepoCommitsMvp.View::hideProgress);
            return false;
        }
        if (repoId == null || login == null) return false;
        Observable<Pageable<Commit>> observable = InputHelper.isEmpty(path)
                                                  ? RestProvider.getRepoService(isEnterprise()).getCommits(login, repoId, branch, page)
                                                  : RestProvider.getRepoService(isEnterprise()).getCommits(login, repoId, branch, path, page);
        makeRestCall(observable, response -> {
            if (response != null && response.getItems() != null) {
                lastPage = response.getLast();
                if (getCurrentPage() == 1) {
                    manageDisposable(Commit.save(response.getItems(), repoId, login));
                }
            }
            sendToView(view -> view.onNotifyAdapter(response != null ? response.getItems() : null, page));
        });
        return true;
    }

    @Override public void onFragmentCreated(@NonNull Bundle bundle) {
        repoId = bundle.getString(BundleConstant.ID);
        login = bundle.getString(BundleConstant.EXTRA);
        branch = bundle.getString(BundleConstant.EXTRA_TWO);
        path = bundle.getString(BundleConstant.EXTRA_THREE);
        if (!InputHelper.isEmpty(branch)) {
            getCommitCount(branch);
        }
        if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
            onCallApi(1, null);
        }
    }

    @NonNull @Override public ArrayList<Commit> getCommits() {
        return commits;
    }

    @Override public void onWorkOffline() {
        if (commits.isEmpty()) {
            manageDisposable(RxHelper.getObservable(Commit.getCommits(repoId, login).toObservable())
                    .subscribe(models -> sendToView(view -> view.onNotifyAdapter(models, 1))));
        } else {
            sendToView(BaseMvp.FAView::hideProgress);
        }
    }

    @Override public void onBranchChanged(@NonNull String branch) {
        if (!TextUtils.equals(branch, this.branch)) {
            this.branch = branch;
            onCallApi(1, null);
            getCommitCount(branch);
        }
    }

    @Override public String getDefaultBranch() {
        return branch;
    }

    @Override public void onItemClick(int position, View v, Commit item) {
        CommitPagerActivity.createIntentForOffline(v.getContext(), item);
    }

    @Override public void onItemLongClick(int position, View v, Commit item) {}

    private void getCommitCount(@NonNull String branch) {
        manageDisposable(RxHelper.safeObservable(RxHelper.getObservable(RestProvider.getRepoService(isEnterprise())
                .getCommitCounts(login, repoId, branch)))
                .subscribe(response -> {
                    if (response != null) {
                        sendToView(view -> view.onShowCommitCount(response.getLast()));
                    }
                }, Throwable::printStackTrace));
    }
}

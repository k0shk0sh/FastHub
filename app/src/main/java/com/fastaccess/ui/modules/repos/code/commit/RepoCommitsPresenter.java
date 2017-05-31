package com.fastaccess.ui.modules.repos.code.commit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.data.dao.BranchesModel;
import com.fastaccess.data.dao.model.Commit;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class RepoCommitsPresenter extends BasePresenter<RepoCommitsMvp.View> implements RepoCommitsMvp.Presenter {

    private ArrayList<Commit> commits = new ArrayList<>();
    private ArrayList<BranchesModel> branches = new ArrayList<>();
    @icepick.State String login;
    @icepick.State String repoId;
    @icepick.State String branch;
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
            sendToView(RepoCommitsMvp.View::hideProgress);
            return;
        }
        if (repoId == null || login == null) return;
        makeRestCall(RestProvider.getRepoService().getCommits(login, repoId, branch, page),
                response -> {
                    if (response != null && response.getItems() != null) {
                        lastPage = response.getLast();
                        if (getCurrentPage() == 1) {
                            manageObservable(Commit.save(response.getItems(), repoId, login));
                        }
                    }
                    sendToView(view -> view.onNotifyAdapter(response != null ? response.getItems() : null, page));
                });
    }

    @Override public void onFragmentCreated(@NonNull Bundle bundle) {
        repoId = bundle.getString(BundleConstant.ID);
        login = bundle.getString(BundleConstant.EXTRA);
        branch = bundle.getString(BundleConstant.EXTRA_TWO);
        if (branches.isEmpty()) {
            getCommitCount(branch);
            Observable<List<BranchesModel>> observable = RxHelper.getObserver(Observable.zip(
                    RestProvider.getRepoService().getBranches(login, repoId),
                    RestProvider.getRepoService().getTags(login, repoId),
                    (branchPageable, tags) -> {
                        ArrayList<BranchesModel> branchesModels = new ArrayList<>();
                        if (branchPageable.getItems() != null) {
                            branchesModels.addAll(Stream.of(branchPageable.getItems())
                                    .map(branchesModel -> {
                                        branchesModel.setTag(false);
                                        return branchesModel;
                                    }).collect(Collectors.toList()));
                        }
                        if (tags != null) {
                            branchesModels.addAll(Stream.of(tags.getItems())
                                    .map(branchesModel -> {
                                        branchesModel.setTag(true);
                                        return branchesModel;
                                    }).collect(Collectors.toList()));

                        }
                        return branchesModels;
                    }));
            manageDisposable(observable
                    .doOnSubscribe(disposable -> sendToView(RepoCommitsMvp.View::showBranchesProgress))
                    .doOnNext(branchesModels -> {
                        branches.clear();
                        branches.addAll(branchesModels);
                        sendToView(view -> view.setBranchesData(branches, true));
                    })
                    .subscribe(branchesModels -> {/**/}, throwable -> sendToView(view -> view.setBranchesData(branches, true))));
        }
        if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
            onCallApi(1, null);
        }
    }

    @NonNull @Override public ArrayList<Commit> getCommits() {
        return commits;
    }

    @NonNull @Override public ArrayList<BranchesModel> getBranches() {
        return branches;
    }

    @Override public void onWorkOffline() {
        if (commits.isEmpty()) {
            manageDisposable(RxHelper.getObserver(Commit.getCommits(repoId, login).toObservable())
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
        manageDisposable(RxHelper.safeObservable(RxHelper.getObserver(RestProvider.getRepoService()
                .getCommitCounts(login, repoId, branch)))
                .subscribe(response -> {
                    if (response != null) {
                        sendToView(view -> view.onShowCommitCount(response.getLast()));
                    }
                }));
    }
}

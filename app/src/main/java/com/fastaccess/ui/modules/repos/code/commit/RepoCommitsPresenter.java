package com.fastaccess.ui.modules.repos.code.commit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.CommitModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerView;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class RepoCommitsPresenter extends BasePresenter<RepoCommitsMvp.View> implements RepoCommitsMvp.Presenter {

    private ArrayList<CommitModel> commits = new ArrayList<>();
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
            sendToView(RepoCommitsMvp.View::hideProgress);
            return;
        }
        if (repoId == null || login == null) return;
        makeRestCall(RestProvider.getRepoService().getCommits(login, repoId, page),
                response -> {
                    lastPage = response.getLast();
                    if (getCurrentPage() == 1) {
                        getCommits().clear();
                        manageSubscription(CommitModel.save(response.getItems(), repoId, login).subscribe());
                    }
                    getCommits().addAll(response.getItems());
                    sendToView(RepoCommitsMvp.View::onNotifyAdapter);
                });
    }

    @Override public void onFragmentCreated(@NonNull Bundle bundle) {
        repoId = bundle.getString(BundleConstant.ID);
        login = bundle.getString(BundleConstant.EXTRA);
        if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
            onCallApi(1, null);
        }
    }

    @NonNull @Override public ArrayList<CommitModel> getCommits() {
        return commits;
    }

    @Override public void onWorkOffline() {
        if (commits.isEmpty()) {
            manageSubscription(RxHelper.getObserver(CommitModel.getCommits(repoId, login))
                    .subscribe(models -> {
                        commits.addAll(models);
                        sendToView(RepoCommitsMvp.View::onNotifyAdapter);
                    }));
        } else {
            sendToView(BaseMvp.FAView::hideProgress);
        }
    }

    @Override public void onItemClick(int position, View v, CommitModel item) {
        CommitPagerView.createIntentForOffline(v.getContext(), item);
    }

    @Override public void onItemLongClick(int position, View v, CommitModel item) {
        onItemClick(position, v, item);
    }
}

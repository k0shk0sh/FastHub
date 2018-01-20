package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.commits;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.model.Commit;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerActivity;

import java.util.ArrayList;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class PullRequestCommitsPresenter extends BasePresenter<PullRequestCommitsMvp.View> implements PullRequestCommitsMvp.Presenter {
    @com.evernote.android.state.State String login;
    @com.evernote.android.state.State String repoId;
    @com.evernote.android.state.State long number;
    private ArrayList<Commit> commits = new ArrayList<>();
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
            sendToView(PullRequestCommitsMvp.View::hideProgress);
            return false;
        }
        if (repoId == null || login == null) return false;
        makeRestCall(RestProvider.getPullRequestService(isEnterprise()).getPullRequestCommits(login, repoId, number, page),
                response -> {
                    lastPage = response.getLast();
                    if (getCurrentPage() == 1) {
                        manageDisposable(Commit.save(response.getItems(), repoId, login, number));
                    }
                    sendToView(view -> view.onNotifyAdapter(response.getItems(), page));
                });
        return true;
    }

    @Override public void onFragmentCreated(@NonNull Bundle bundle) {
        repoId = bundle.getString(BundleConstant.ID);
        login = bundle.getString(BundleConstant.EXTRA);
        number = bundle.getLong(BundleConstant.EXTRA_TWO);
        if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
            onCallApi(1, null);
        }
    }

    @NonNull @Override public ArrayList<Commit> getCommits() {
        return commits;
    }

    @Override public void onWorkOffline() {
        if (commits.isEmpty()) {
            manageDisposable(RxHelper.getSingle(Commit.getCommits(repoId, login, number))
                    .subscribe(models -> sendToView(view -> view.onNotifyAdapter(models, 1))));
        } else {
            sendToView(BaseMvp.FAView::hideProgress);
        }
    }

    @Override public void onItemClick(int position, View v, Commit item) {
        CommitPagerActivity.createIntentForOffline(v.getContext(), item);
    }

    @Override public void onItemLongClick(int position, View v, Commit item) {}
}

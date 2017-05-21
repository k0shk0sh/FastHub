package com.fastaccess.ui.modules.repos.pull_requests.pull_request;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.PullsIssuesParser;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RepoQueryProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class RepoPullRequestPresenter extends BasePresenter<RepoPullRequestMvp.View> implements RepoPullRequestMvp.Presenter {

    @icepick.State String login;
    @icepick.State String repoId;
    @icepick.State IssueState issueState;
    private ArrayList<PullRequest> pullRequests = new ArrayList<>();
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

    @Override public void onCallApi(int page, @Nullable IssueState parameter) {
        if (parameter == null) {
            sendToView(RepoPullRequestMvp.View::hideProgress);
            return;
        }
        this.issueState = parameter;
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        setCurrentPage(page);
        if (page > lastPage || lastPage == 0) {
            sendToView(RepoPullRequestMvp.View::hideProgress);
            return;
        }
        if (repoId == null || login == null) return;
        makeRestCall(RestProvider.getPullRequestService().getPullRequests(login, repoId, parameter.name(), page), response -> {
            lastPage = response.getLast();
            if (getCurrentPage() == 1) {
                manageSubscription(PullRequest.save(response.getItems(), login, repoId).subscribe());
            }
            sendToView(view -> view.onNotifyAdapter(response.getItems(), page));
        });
    }

    @Override public void onFragmentCreated(@NonNull Bundle bundle) {
        repoId = bundle.getString(BundleConstant.ID);
        login = bundle.getString(BundleConstant.EXTRA);
        issueState = (IssueState) bundle.getSerializable(BundleConstant.EXTRA_TWO);
        if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
            onCallApi(1, issueState);
            onCallCountApi(issueState);
        }
    }

    private void onCallCountApi(@NonNull IssueState issueState) {
        manageSubscription(RxHelper.getObserver(RestProvider.getPullRequestService()
                .getPullsWithCount(RepoQueryProvider.getIssuesPullRequestQuery(login, repoId, issueState, true), 0))
                .subscribe(pullRequestPageable -> sendToView(view -> view.onUpdateCount(pullRequestPageable.getTotalCount())),
                        Throwable::printStackTrace));
    }

    @Override public void onWorkOffline() {
        if (pullRequests.isEmpty()) {
            manageSubscription(RxHelper.getObserver(PullRequest.getPullRequests(repoId, login, issueState))
                    .subscribe(pulls -> sendToView(view -> {
                        view.onNotifyAdapter(pulls, 1);
                        view.onUpdateCount(pulls.size());
                    })));
        } else {
            sendToView(BaseMvp.FAView::hideProgress);
        }
    }

    @NonNull public ArrayList<PullRequest> getPullRequests() {
        return pullRequests;
    }

    @NonNull @Override public IssueState getIssueState() {
        return issueState;
    }

    @Override public void onItemClick(int position, View v, PullRequest item) {
        PullsIssuesParser parser = PullsIssuesParser.getForPullRequest(item.getHtmlUrl());
        if (parser != null && getView() != null) {
            getView().onOpenPullRequest(parser);
        }
    }

    @Override public void onItemLongClick(int position, View v, PullRequest item) {
        onItemClick(position, v, item);
    }
}

package com.fastaccess.ui.modules.repos.issues.issue;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.annimon.stream.Stream;
import com.fastaccess.data.dao.PullsIssuesParser;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RepoQueryProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class RepoIssuesPresenter extends BasePresenter<RepoIssuesMvp.View> implements RepoIssuesMvp.Presenter {

    private ArrayList<Issue> issues = new ArrayList<>();
    private String login;
    private String repoId;
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;
    private IssueState issueState;
    private boolean isLastUpdated;

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
            sendToView(RepoIssuesMvp.View::hideProgress);
            return;
        }
        this.issueState = parameter;
        Logger.e(page, page, login, repoId);
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0) {
            sendToView(RepoIssuesMvp.View::hideProgress);
            return;
        }
        String sortBy = "created";
        if (isLastUpdated) {
            sortBy = "updated";
        }
        setCurrentPage(page);
        makeRestCall(RestProvider.getIssueService().getRepositoryIssues(login, repoId, parameter.name(), sortBy, page),
                issues -> {
                    lastPage = issues.getLast();
                    List<Issue> filtered = Stream.of(issues.getItems())
                            .filter(issue -> issue.getPullRequest() == null)
                            .toList();
                    if (getCurrentPage() == 1) {
                        manageSubscription(Issue.save(filtered, repoId, login).subscribe());
                    }
                    sendToView(view -> view.onNotifyAdapter(filtered, page));
                });
    }

    private void onCallCountApi(@NonNull IssueState issueState) {
        manageSubscription(RxHelper.getObserver(RestProvider.getIssueService()
                .getIssuesWithCount(RepoQueryProvider.getIssuesPullRequestQuery(login, repoId, issueState, false), 1))
                .subscribe(pullRequestPageable -> sendToView(view -> view.onUpdateCount(pullRequestPageable.getTotalCount())),
                        Throwable::printStackTrace));
    }

    @Override public void onFragmentCreated(@NonNull Bundle bundle, @NonNull IssueState issueState) {
        repoId = bundle.getString(BundleConstant.ID);
        login = bundle.getString(BundleConstant.EXTRA);
        this.issueState = issueState;
        if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
            onCallApi(1, issueState);
            onCallCountApi(issueState);
        }
    }

    @Override public void onWorkOffline() {
        if (issues.isEmpty()) {
            manageSubscription(RxHelper.getObserver(Issue.getIssues(repoId, login, issueState))
                    .subscribe(issueModel -> sendToView(view -> {
                        view.onNotifyAdapter(issueModel, 1);
                        view.onUpdateCount(issueModel.size());
                    })));
        } else {
            sendToView(BaseMvp.FAView::hideProgress);
        }
    }

    @NonNull @Override public ArrayList<Issue> getIssues() {
        return issues;
    }

    @NonNull @Override public String repoId() {
        return repoId;
    }

    @NonNull @Override public String login() {
        return login;
    }

    @Override public void onSetSortBy(boolean isLastUpdated) {
        this.isLastUpdated = isLastUpdated;
    }

    @Override public void onItemClick(int position, View v, Issue item) {
        PullsIssuesParser parser = PullsIssuesParser.getForIssue(item.getHtmlUrl());
        if (parser != null && getView() != null) {
            getView().onOpenIssue(parser);
        }
    }

    @Override public void onItemLongClick(int position, View v, Issue item) {
        onItemClick(position, v, item);
    }
}

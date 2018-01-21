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
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RepoQueryProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class RepoIssuesPresenter extends BasePresenter<RepoIssuesMvp.View> implements RepoIssuesMvp.Presenter {

    private ArrayList<Issue> issues = new ArrayList<>();
    @com.evernote.android.state.State String login;
    @com.evernote.android.state.State String repoId;
    @com.evernote.android.state.State IssueState issueState;
    @com.evernote.android.state.State boolean isLastUpdated;
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

    @Override public boolean onCallApi(int page, @Nullable IssueState parameter) {
        if (parameter == null) {
            sendToView(RepoIssuesMvp.View::hideProgress);
            return false;
        }
        this.issueState = parameter;
        if (page == 1) {
            onCallCountApi(issueState);
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0) {
            sendToView(RepoIssuesMvp.View::hideProgress);
            return false;
        }
        String sortBy = "created";
        if (isLastUpdated) {
            sortBy = "updated";
        }
        setCurrentPage(page);
        String finalSortBy = sortBy;
        makeRestCall(RestProvider.getIssueService(isEnterprise())
                        .getRepositoryIssues(login, repoId, parameter.name(), sortBy, page)
                        .flatMap(issues -> {
                            lastPage = issues.getLast();
                            List<Issue> filtered = Stream.of(issues.getItems())
                                    .filter(issue -> issue.getPullRequest() == null)
                                    .toList();
                            if (filtered != null) {
                                if (filtered.size() < 10 && issues.getNext() > 1) {
                                    setCurrentPage(getCurrentPage() + 1);
                                    return grabMoreIssues(filtered, parameter.name(), finalSortBy, getCurrentPage());
                                }
                                return Observable.just(filtered);
                            }
                            return Observable.just(new ArrayList<Issue>());
                        })
                        .doOnNext(filtered -> {
                            if (getCurrentPage() == 1) {
                                Issue.save(filtered, repoId, login);
                            }
                        }),
                issues -> sendToView(view -> view.onNotifyAdapter(issues, page)));
        return true;
    }

    @Override public void onFragmentCreated(@NonNull Bundle bundle, @NonNull IssueState issueState) {
        repoId = bundle.getString(BundleConstant.ID);
        login = bundle.getString(BundleConstant.EXTRA);
        this.issueState = issueState;
        if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
            onCallApi(1, issueState);
        }
    }

    @Override public void onWorkOffline() {
        if (issues.isEmpty()) {
            manageDisposable(RxHelper.getSingle(Issue.getIssues(repoId, login, issueState))
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
        if (getView() != null) getView().onShowIssuePopup(item);
    }

    private void onCallCountApi(@NonNull IssueState issueState) {
        manageDisposable(RxHelper.getObservable(RestProvider.getIssueService(isEnterprise())
                .getIssuesWithCount(RepoQueryProvider.getIssuesPullRequestQuery(login, repoId, issueState, false), 1))
                .subscribe(pullRequestPageable -> sendToView(view -> view.onUpdateCount(pullRequestPageable.getTotalCount())),
                        Throwable::printStackTrace));
    }

    private Observable<List<Issue>> grabMoreIssues(@NonNull List<Issue> issues, @NonNull String state, @NonNull String sortBy, int page) {
        return RestProvider.getIssueService(isEnterprise()).getRepositoryIssues(login, repoId, state, sortBy, page)
                .flatMap(issuePageable -> {
                    if (issuePageable != null) {
                        lastPage = issuePageable.getLast();
                        List<Issue> filtered = Stream.of(issuePageable.getItems())
                                .filter(issue -> issue.getPullRequest() == null)
                                .toList();
                        if (filtered != null) {
                            issues.addAll(filtered);
                            if (issues.size() < 10 && issuePageable.getNext() > 1 && this.issues.size() < 10) {
                                setCurrentPage(getCurrentPage() + 1);
                                return grabMoreIssues(issues, state, sortBy, getCurrentPage());
                            }
                            issues.addAll(filtered);
                            return Observable.just(issues);
                        }
                    }
                    return Observable.just(issues);
                });
    }

}

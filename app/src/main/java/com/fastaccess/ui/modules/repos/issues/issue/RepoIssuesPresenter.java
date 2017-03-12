package com.fastaccess.ui.modules.repos.issues.issue;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.data.dao.IssueModel;
import com.fastaccess.data.dao.PullsIssuesParser;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.issues.issue.details.IssuePagerView;

import java.util.ArrayList;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class RepoIssuesPresenter extends BasePresenter<RepoIssuesMvp.View> implements RepoIssuesMvp.Presenter {

    private ArrayList<IssueModel> issues = new ArrayList<>();
    private String login;
    private String repoId;
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;
    private IssueState issueState;

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
        Logger.e(page, page, login, repoId);
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0) {
            sendToView(RepoIssuesMvp.View::hideProgress);
            return;
        }
        setCurrentPage(page);
        makeRestCall(RestProvider.getIssueService().getRepositoryIssues(login, repoId, issueState.name(), page),
                issues -> {
                    lastPage = issues.getLast();
                    if (getCurrentPage() == 1) {
                        getIssues().clear();
                        manageSubscription(IssueModel.save(issues.getItems(), repoId, login).subscribe());
                    }
                    getIssues().addAll(Stream.of(issues.getItems()).filter(value -> value.getPullRequest() == null).collect(Collectors.toList()));
                    sendToView(RepoIssuesMvp.View::onNotifyAdapter);
                });
    }

    @Override public void onFragmentCreated(@NonNull Bundle bundle, IssueState issueState) {
        repoId = bundle.getString(BundleConstant.ID);
        login = bundle.getString(BundleConstant.EXTRA);
        this.issueState = issueState;
        if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
            onCallApi(1, null);
        }
    }

    @Override public void onWorkOffline() {
        if (issues.isEmpty()) {
            manageSubscription(RxHelper.getObserver(IssueModel.getIssues(repoId, login, issueState))
                    .subscribe(issueModel -> {
                        issues.addAll(issueModel);
                        sendToView(RepoIssuesMvp.View::onNotifyAdapter);
                    }));
        } else {
            sendToView(BaseMvp.FAView::hideProgress);
        }
    }

    @NonNull @Override public ArrayList<IssueModel> getIssues() {
        return issues;
    }

    @NonNull @Override public String repoId() {
        return repoId;
    }

    @NonNull @Override public String login() {
        return login;
    }

    @Override public void onItemClick(int position, View v, IssueModel item) {
        Logger.e(Bundler.start().put("item", item).end().size());
        PullsIssuesParser parser = PullsIssuesParser.getForIssue(item.getHtmlUrl());
        if (parser != null) {
            v.getContext().startActivity(IssuePagerView.createIntent(v.getContext(), parser.getRepoId(),
                    parser.getLogin(), parser.getNumber()));
        }
    }

    @Override public void onItemLongClick(int position, View v, IssueModel item) {
        onItemClick(position, v, item);
    }
}

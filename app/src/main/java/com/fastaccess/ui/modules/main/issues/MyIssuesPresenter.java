package com.fastaccess.ui.modules.main.issues;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.PullsIssuesParser;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.data.dao.types.MyIssuesType;
import com.fastaccess.provider.rest.RepoQueryProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.issues.issue.details.IssuePagerActivity;

import java.util.ArrayList;

/**
 * Created by Kosh on 25 Mar 2017, 11:39 PM
 */

public class MyIssuesPresenter extends BasePresenter<MyIssuesMvp.View> implements MyIssuesMvp.Presenter {

    private ArrayList<Issue> issues = new ArrayList<>();
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;
    private MyIssuesType issuesType;
    @NonNull private String login = Login.getUser().getLogin();

    @Override public void onItemClick(int position, View v, Issue item) {
        PullsIssuesParser parser = PullsIssuesParser.getForIssue(item.getHtmlUrl());
        if (parser != null) {
            v.getContext().startActivity(IssuePagerActivity.createIntent(v.getContext(), parser.getRepoId(),
                    parser.getLogin(), parser.getNumber(), true));
        }
    }

    @Override public void onItemLongClick(int position, View v, Issue item) {
        onItemClick(position, v, item);
    }

    @NonNull @Override public ArrayList<Issue> getIssues() {
        return issues;
    }

    @Override public void onSetIssueType(@NonNull MyIssuesType issuesType) {
        this.issuesType = issuesType;
    }

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

    @Override public void onCallApi(int page, @Nullable IssueState parameter) {
        if (parameter == null) {
            throw new NullPointerException("parameter is null");
        }
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0) {
            sendToView(MyIssuesMvp.View::hideProgress);
            return;
        }
        setCurrentPage(page);
        makeRestCall(RestProvider.getIssueService().getIssuesWithCount(getUrl(parameter), page), issues -> {
            lastPage = issues.getLast();
            if (getCurrentPage() == 1) {
                sendToView(view -> view.onSetCount(issues.getTotalCount()));
            }
            sendToView(view -> view.onNotifyAdapter(issues.getItems(), page));
        });
    }

    @NonNull private String getUrl(@NonNull IssueState parameter) {
        switch (issuesType) {
            case CREATED:
                return RepoQueryProvider.getMyIssuesPullRequestQuery(login, parameter, false);
            case ASSIGNED:
                return RepoQueryProvider.getAssigned(login, parameter, false);
            case MENTIONED:
                return RepoQueryProvider.getMentioned(login, parameter, false);
        }
        return RepoQueryProvider.getMyIssuesPullRequestQuery(login, parameter, false);
    }
}

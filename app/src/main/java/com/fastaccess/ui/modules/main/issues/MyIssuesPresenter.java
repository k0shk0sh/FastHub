package com.fastaccess.ui.modules.main.issues;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.data.dao.types.MyIssuesType;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.rest.RepoQueryProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 25 Mar 2017, 11:39 PM
 */

public class MyIssuesPresenter extends BasePresenter<MyIssuesMvp.View> implements MyIssuesMvp.Presenter {

    private ArrayList<Issue> issues = new ArrayList<>();
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;
    @com.evernote.android.state.State MyIssuesType issuesType;
    @NonNull private String login = Login.getUser().getLogin();

    MyIssuesPresenter() {
        setEnterprise(PrefGetter.isEnterprise());
    }

    @Override public void onItemClick(int position, View v, Issue item) {
        SchemeParser.launchUri(v.getContext(), item.getHtmlUrl());
    }

    @Override public void onItemLongClick(int position, View v, Issue item) {
        if (getView() != null) getView().onShowPopupDetails(item);
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

    @Override public boolean onCallApi(int page, @Nullable IssueState parameter) {
        if (parameter == null) {
            throw new NullPointerException("parameter is null");
        }
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0) {
            sendToView(MyIssuesMvp.View::hideProgress);
            return false;
        }
        setCurrentPage(page);
        makeRestCall(RestProvider.getIssueService(isEnterprise()).getIssuesWithCount(getUrl(parameter), page), issues -> {
            lastPage = issues.getLast();
            if (getCurrentPage() == 1) {
                sendToView(view -> view.onSetCount(issues.getTotalCount()));
            }
            sendToView(view -> view.onNotifyAdapter(issues.getItems(), page));
        });
        return true;
    }

    @NonNull private String getUrl(@NonNull IssueState parameter) {
        switch (issuesType) {
            case CREATED:
                return RepoQueryProvider.getMyIssuesPullRequestQuery(login, parameter, false);
            case ASSIGNED:
                return RepoQueryProvider.getAssigned(login, parameter, false);
            case MENTIONED:
                return RepoQueryProvider.getMentioned(login, parameter, false);
            case PARTICIPATED:
                return RepoQueryProvider.getParticipated(login, parameter, false);
        }
        return RepoQueryProvider.getMyIssuesPullRequestQuery(login, parameter, false);
    }
}

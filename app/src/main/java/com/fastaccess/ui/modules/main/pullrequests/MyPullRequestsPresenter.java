package com.fastaccess.ui.modules.main.pullrequests;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.data.dao.types.MyIssuesType;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.rest.RepoQueryProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 25 Mar 2017, 11:53 PM
 */

public class MyPullRequestsPresenter extends BasePresenter<MyPullRequestsMvp.View> implements MyPullRequestsMvp.Presenter {

    private ArrayList<PullRequest> pullRequests = new ArrayList<>();
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;
    @com.evernote.android.state.State MyIssuesType issuesType;
    @NonNull private String login = Login.getUser().getLogin();

    MyPullRequestsPresenter() {
        setEnterprise(PrefGetter.isEnterprise());
    }

    @Override public void onItemClick(int position, View v, PullRequest item) {
        SchemeParser.launchUri(v.getContext(), item.getHtmlUrl());
    }

    @Override public void onItemLongClick(int position, View v, PullRequest item) {
        if (getView() != null) getView().onShowPopupDetails(item);
    }

    @NonNull @Override public ArrayList<PullRequest> getPullRequests() {
        return pullRequests;
    }

    @Override public void onSetPullType(@NonNull MyIssuesType issuesType) {
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
            throw new NullPointerException("Parameter is null");
        }
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0) {
            sendToView(MyPullRequestsMvp.View::hideProgress);
            return false;
        }
        setCurrentPage(page);
        makeRestCall(RestProvider.getPullRequestService(isEnterprise()).getPullsWithCount(getUrl(parameter), page), response -> {
            lastPage = response.getLast();
            if (getCurrentPage() == 1) {
                sendToView(view -> view.onSetCount(response.getTotalCount()));
            }
            sendToView(view -> view.onNotifyAdapter(response.getItems(), page));
        });
        return true;
    }

    @NonNull private String getUrl(@NonNull IssueState parameter) {
        switch (issuesType) {
            case CREATED:
                return RepoQueryProvider.getMyIssuesPullRequestQuery(login, parameter, true);
            case ASSIGNED:
                return RepoQueryProvider.getAssigned(login, parameter, true);
            case MENTIONED:
                return RepoQueryProvider.getMentioned(login, parameter, true);
            case REVIEW:
                return RepoQueryProvider.getReviewRequests(login, parameter);
        }
        return RepoQueryProvider.getMyIssuesPullRequestQuery(login, parameter, false);
    }
}

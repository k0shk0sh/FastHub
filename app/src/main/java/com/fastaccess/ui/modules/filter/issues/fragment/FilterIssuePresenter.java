package com.fastaccess.ui.modules.filter.issues.fragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.PullsIssuesParser;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.issues.issue.details.IssuePagerActivity;

import java.util.ArrayList;

/**
 * Created by Kosh on 09 Apr 2017, 7:10 PM
 */

public class FilterIssuePresenter extends BasePresenter<FilterIssuesMvp.View> implements FilterIssuesMvp.Presenter {


    private ArrayList<Issue> issues = new ArrayList<>();
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;

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

    @Override public void onCallApi(int page, @Nullable String parameter) {
        if (page == 1 || parameter == null) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0 || parameter == null) {
            sendToView(FilterIssuesMvp.View::hideProgress);
            return;
        }
        setCurrentPage(page);
        makeRestCall(RestProvider.getSearchService().searchIssues(parameter, page),
                issues -> {
                    lastPage = issues.getLast();
                    if (getCurrentPage() == 1) {
                        getIssues().clear();
                        sendToView(view -> view.onSetCount(issues.getTotalCount()));
                    }
                    getIssues().addAll(issues.getItems());
                    sendToView(FilterIssuesMvp.View::onNotifyAdapter);
                });
    }
}

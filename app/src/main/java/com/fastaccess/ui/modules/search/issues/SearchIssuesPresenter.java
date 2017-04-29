package com.fastaccess.ui.modules.search.issues;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.PullsIssuesParser;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.issues.issue.details.IssuePagerActivity;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.PullRequestPagerActivity;

import java.util.ArrayList;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class SearchIssuesPresenter extends BasePresenter<SearchIssuesMvp.View> implements SearchIssuesMvp.Presenter {

    private ArrayList<Issue> issues = new ArrayList<>();
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

    @Override public void onCallApi(int page, @Nullable String parameter) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        setCurrentPage(page);
        if (page > lastPage || lastPage == 0) {
            sendToView(SearchIssuesMvp.View::hideProgress);
            return;
        }
        if (parameter == null) {
            return;
        }
        makeRestCall(RestProvider.getSearchService().searchIssues(parameter, page),
                response -> {
                    lastPage = response.getLast();
                    sendToView(view -> {
                        view.onNotifyAdapter(response.isIncompleteResults() ? null : response.getItems(), page);
                        view.onSetTabCount(response.getTotalCount());
                    });
                });
    }

    @NonNull @Override public ArrayList<Issue> getIssues() {
        return issues;
    }

    @Override public void onItemClick(int position, View v, Issue item) {
        if (item.getPullRequest() == null) {
            PullsIssuesParser parser = PullsIssuesParser.getForIssue(item.getHtmlUrl());
            if (parser != null) {
                v.getContext().startActivity(IssuePagerActivity.createIntent(v.getContext(), parser.getRepoId(),
                        parser.getLogin(), parser.getNumber(), true));
            }
        } else {
            PullsIssuesParser parser = PullsIssuesParser.getForPullRequest(item.getHtmlUrl());
            if (parser != null) {
                v.getContext().startActivity(PullRequestPagerActivity.createIntent(v.getContext(), parser.getRepoId(),
                        parser.getLogin(), parser.getNumber(), true));
            }
        }
    }

    @Override public void onItemLongClick(int position, View v, Issue item) {
        onItemClick(position, v, item);
    }
}

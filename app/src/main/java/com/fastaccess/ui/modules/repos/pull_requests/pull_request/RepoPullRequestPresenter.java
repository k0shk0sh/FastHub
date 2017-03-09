package com.fastaccess.ui.modules.repos.pull_requests.pull_request;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.PullRequestModel;
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
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.PullRequestPagerView;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class RepoPullRequestPresenter extends BasePresenter<RepoPullRequestMvp.View> implements RepoPullRequestMvp.Presenter {

    private ArrayList<PullRequestModel> pullRequests = new ArrayList<>();
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
        makeRestCall(RestProvider.getPullRequestSerice().getPullRequests(login, repoId, issueState.name(), page),
                response -> {
                    lastPage = response.getLast();
                    if (getCurrentPage() == 1) {
                        getPullRequests().clear();
                        manageSubscription(PullRequestModel.save(response.getItems(), login, repoId).subscribe());
                    }
                    getPullRequests().addAll(response.getItems());
                    sendToView(RepoPullRequestMvp.View::onNotifyAdapter);
                });
    }

    @Override public void onFragmentCreated(@NonNull Bundle bundle) {
        repoId = bundle.getString(BundleConstant.ID);
        login = bundle.getString(BundleConstant.EXTRA);
        issueState = (IssueState) bundle.getSerializable(BundleConstant.EXTRA_TWO);
        if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
            onCallApi(1, null);
        }
    }

    @Override public void onWorkOffline() {
        if (pullRequests.isEmpty()) {
            manageSubscription(RxHelper.getObserver(PullRequestModel.getPullRequests(repoId, login, issueState))
                    .subscribe(pulls -> {
                        pullRequests.addAll(pulls);
                        sendToView(RepoPullRequestMvp.View::onNotifyAdapter);
                    }));
        } else {
            sendToView(BaseMvp.FAView::hideProgress);
        }
    }

    @NonNull public ArrayList<PullRequestModel> getPullRequests() {
        return pullRequests;
    }

    @Override public void onItemClick(int position, View v, PullRequestModel item) {
        Logger.e(Bundler.start().put("item", item).end().size());
        PullsIssuesParser parser = PullsIssuesParser.getForPullRequest(item.getHtmlUrl());
        if (parser != null) {
            Intent intent = PullRequestPagerView.createIntent(v.getContext(), parser.getRepoId(), parser.getLogin(), parser.getNumber());
            v.getContext().startActivity(intent);
        }
    }

    @Override public void onItemLongClick(int position, View v, PullRequestModel item) {
        onItemClick(position, v, item);
    }
}

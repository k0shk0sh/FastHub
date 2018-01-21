package com.fastaccess.ui.modules.pinned.pullrequest;

import android.support.annotation.NonNull;
import android.view.View;

import com.fastaccess.data.dao.model.PinnedPullRequests;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 25 Mar 2017, 8:00 PM
 */

public class PinnedPullRequestPresenter extends BasePresenter<PinnedPullRequestMvp.View> implements PinnedPullRequestMvp.Presenter {
    private ArrayList<PullRequest> pullRequests = new ArrayList<>();

    @Override protected void onAttachView(@NonNull PinnedPullRequestMvp.View view) {
        super.onAttachView(view);
        if (pullRequests.isEmpty()) {
            onReload();
        }
    }

    @NonNull @Override public ArrayList<PullRequest> getPinnedPullRequest() {
        return pullRequests;
    }

    @Override public void onReload() {
        manageDisposable(PinnedPullRequests.getMyPinnedPullRequests()
                .subscribe(repos -> sendToView(view -> view.onNotifyAdapter(repos)), throwable ->
                        sendToView(view -> view.onNotifyAdapter(null))));
    }

    @Override public void onItemClick(int position, View v, PullRequest item) {
        SchemeParser.launchUri(v.getContext(), !InputHelper.isEmpty(item.getHtmlUrl()) ? item.getHtmlUrl() : item.getUrl());
    }

    @Override public void onItemLongClick(int position, View v, PullRequest item) {
        if (getView() != null) {
            getView().onDeletePinnedPullRequest(item.getId(), position);
        }
    }
}

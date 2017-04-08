package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.CommitFileModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerView;

import java.util.ArrayList;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class PullRequestFilesPresenter extends BasePresenter<PullRequestFilesMvp.View> implements PullRequestFilesMvp.Presenter {

    private ArrayList<CommitFileModel> files = new ArrayList<>();
    private String login;
    private String repoId;
    private long number;
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

    @Override public void onCallApi(int page, @Nullable Object parameter) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        setCurrentPage(page);
        if (page > lastPage || lastPage == 0) {
            sendToView(PullRequestFilesMvp.View::hideProgress);
            return;
        }
        if (repoId == null || login == null) return;
        makeRestCall(RestProvider.getPullRequestSerice().getPullRequestFiles(login, repoId, number, page),
                response -> {
                    lastPage = response.getLast();
                    if (getCurrentPage() == 1) {
                        getFiles().clear();
                    }
                    getFiles().addAll(response.getItems());
                    sendToView(PullRequestFilesMvp.View::onNotifyAdapter);
                });
    }

    @Override public void onFragmentCreated(@NonNull Bundle bundle) {
        repoId = bundle.getString(BundleConstant.ID);
        login = bundle.getString(BundleConstant.EXTRA);
        number = bundle.getLong(BundleConstant.EXTRA_TWO);
        if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
            onCallApi(1, null);
        }
    }

    @NonNull @Override public ArrayList<CommitFileModel> getFiles() {
        return files;
    }

    @Override public void onWorkOffline() {
        sendToView(BaseMvp.FAView::hideProgress);
    }

    @Override public void onItemClick(int position, View v, CommitFileModel item) {
    }

    @Override public void onItemLongClick(int position, View v, CommitFileModel item) {
        v.getContext().startActivity(CommitPagerView.createIntent(v.getContext(), repoId, login, Uri.parse(item.getContentsUrl())
                .getQueryParameter("ref")));
    }
}

package com.fastaccess.ui.modules.repos.extras.labels;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 22 Feb 2017, 7:23 PM
 */

class LabelsPresenter extends BasePresenter<LabelsMvp.View> implements LabelsMvp.Presenter {

    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;
    private ArrayList<LabelModel> labels = new ArrayList<>();
    @com.evernote.android.state.State String login;
    @com.evernote.android.state.State String repoId;

    LabelsPresenter(@NonNull String login, @NonNull String repoId) {
        this.login = login;
        this.repoId = repoId;
    }

    @NonNull @Override public ArrayList<LabelModel> getLabels() {
        return labels;
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

    @Override public boolean onCallApi(int page, @Nullable Object parameter) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0) {
            sendToView(LabelsMvp.View::hideProgress);
            return false;
        }
        setCurrentPage(page);
        makeRestCall(RestProvider.getRepoService(isEnterprise()).getLabels(login, repoId, page), response -> {
            lastPage = response.getLast();
            sendToView(view -> view.onNotifyAdapter(response.getItems(), page));
        });
        return true;
    }
}

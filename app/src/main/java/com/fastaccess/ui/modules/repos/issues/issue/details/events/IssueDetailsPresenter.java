package com.fastaccess.ui.modules.repos.issues.issue.details.events;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.IssueEventAdapterModel;
import com.fastaccess.data.dao.IssueEventModel;
import com.fastaccess.data.dao.IssueModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by Kosh on 13 Dec 2016, 12:38 AM
 */

class IssueDetailsPresenter extends BasePresenter<IssueDetailsMvp.View> implements IssueDetailsMvp.Presenter {
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;
    private ArrayList<IssueEventAdapterModel> events = new ArrayList<>();
    private IssueModel issueModel;

    @Override public void onFragmentCreated(@Nullable Bundle bundle) {
        if (bundle == null) throw new NullPointerException("Bundle is null?");
        issueModel = bundle.getParcelable(BundleConstant.ITEM);
        if (events.isEmpty()) {
            events.add(0, new IssueEventAdapterModel(IssueEventAdapterModel.HEADER, issueModel));
            sendToView(IssueDetailsMvp.View::onNotifyAdapter);
        }
    }

    @Override public void onError(@NonNull Throwable throwable) {
        onWorkOffline();
        super.onError(throwable);
    }

    @Override public void onWorkOffline() {
        if (events.isEmpty() || events.size() == 1) {
            manageSubscription(RxHelper.getObserver(IssueEventModel.get(issueModel.getRepoId(),
                    issueModel.getLogin(), String.valueOf(issueModel.getNumber())))
                    .subscribe(
                            models -> {
                                if (models != null) {
                                    events.addAll(IssueEventAdapterModel.addEvents(models));
                                    sendToView(IssueDetailsMvp.View::onNotifyAdapter);
                                }
                            }
                    ));
        } else {
            sendToView(IssueDetailsMvp.View::hideProgress);
        }
    }

    @NonNull @Override public ArrayList<IssueEventAdapterModel> getEvents() {
        return events;
    }

    @Override public void onItemClick(int position, View v, IssueEventAdapterModel item) {
        if (item.getType() != IssueEventAdapterModel.HEADER) {
            IssueEventModel issueEventModel = item.getIssueEvent();
            if (issueEventModel.getCommitUrl() != null) {
                SchemeParser.launchUri(v.getContext(), Uri.parse(issueEventModel.getCommitUrl()));
            }
        }
    }

    @Override public void onItemLongClick(int position, View v, IssueEventAdapterModel item) {
        onItemClick(position, v, item);
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

    @Override public void onCallApi(int page, @Nullable Object parameter) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0) {
            sendToView(IssueDetailsMvp.View::hideProgress);
            return;
        }
        setCurrentPage(page);
        String login = issueModel.getLogin();
        String repoID = issueModel.getRepoId();
        int number = issueModel.getNumber();
        makeRestCall(RestProvider.getIssueService().getTimeline(login, repoID, number, page),
                response -> {
                    lastPage = response.getLast();
                    if (getCurrentPage() == 1) {
                        getEvents().subList(1, getEvents().size()).clear();
                        manageSubscription(IssueEventModel.save(response.getItems(), repoID, login, String.valueOf(number)).subscribe());
                    }
                    getEvents().addAll(IssueEventAdapterModel.addEvents(response.getItems()));
                    sendToView(IssueDetailsMvp.View::onNotifyAdapter);
                });
    }
}

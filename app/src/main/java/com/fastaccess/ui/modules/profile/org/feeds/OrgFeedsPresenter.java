package com.fastaccess.ui.modules.profile.org.feeds;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.data.dao.NameParser;
import com.fastaccess.data.dao.PayloadModel;
import com.fastaccess.data.dao.SimpleUrlsModel;
import com.fastaccess.data.dao.model.Event;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.data.dao.types.EventsType;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.RepoPagerView;

import java.util.ArrayList;

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

class OrgFeedsPresenter extends BasePresenter<OrgFeedsMvp.View> implements OrgFeedsMvp.Presenter {
    private ArrayList<Event> eventsModels = new ArrayList<>();
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
        if (page > lastPage || lastPage == 0 || parameter == null) {
            sendToView(OrgFeedsMvp.View::hideProgress);
            return;
        }
        setCurrentPage(page);
        if (Login.getUser() == null) return;
        makeRestCall(RestProvider.getOrgService().getReceivedEvents(parameter, page),
                response -> {
                    lastPage = response.getLast();
                    if (getCurrentPage() == 1) {
                        eventsModels.clear();
                    }
                    eventsModels.addAll(response.getItems());
                    sendToView(OrgFeedsMvp.View::onNotifyAdapter);
                });
    }

    @Override public void onSubscribed() {
        sendToView(view -> view.showProgress(0));
    }

    @Override public void onError(@NonNull Throwable throwable) {
        onWorkOffline();
        super.onError(throwable);
    }

    @NonNull @Override public ArrayList<Event> getEvents() {
        return eventsModels;
    }

    @Override public void onWorkOffline() {
        //TODO
    }

    @Override public void onItemClick(int position, View v, Event item) {
        if (item.getType() == EventsType.ForkEvent) {
            NameParser parser = new NameParser(item.getPayload().getForkee().getHtmlUrl());
            RepoPagerView.startRepoPager(v.getContext(), parser);
        } else {
            PayloadModel payloadModel = item.getPayload();
            if (payloadModel != null) {
                if (item.getPayload().getIssue() != null) {
                    SchemeParser.launchUri(v.getContext(), Uri.parse(item.getPayload().getIssue().getHtmlUrl()), true);
                } else if (item.getPayload().getPullRequest() != null) {
                    SchemeParser.launchUri(v.getContext(), Uri.parse(item.getPayload().getPullRequest().getHtmlUrl()), true);
                } else if (item.getPayload().getComment() != null) {
                    SchemeParser.launchUri(v.getContext(), Uri.parse(item.getPayload().getComment().getHtmlUrl()), true);
                } else {
                    Repo repoModel = item.getRepo();
                    if (item.getRepo() != null) SchemeParser.launchUri(v.getContext(), Uri.parse(repoModel.getName()), true);
                }
            }
        }
    }

    @Override public void onItemLongClick(int position, View v, Event item) {
        if (item.getType() == EventsType.ForkEvent) {
            if (getView() != null) {
                getView().onOpenRepoChooser(Stream.of(new SimpleUrlsModel(item.getRepo().getName(), item.getRepo().getUrl()),
                        new SimpleUrlsModel(item.getPayload().getForkee().getFullName(), item.getPayload().getForkee().getHtmlUrl()))
                        .collect(Collectors.toCollection(ArrayList::new)));
            }
        } else {
            onItemClick(position, v, item);
        }
    }
}

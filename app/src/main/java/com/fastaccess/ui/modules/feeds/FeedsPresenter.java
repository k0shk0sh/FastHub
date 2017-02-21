package com.fastaccess.ui.modules.feeds;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.data.dao.EventsModel;
import com.fastaccess.data.dao.LoginModel;
import com.fastaccess.data.dao.NameParser;
import com.fastaccess.data.dao.SimpleUrlsModel;
import com.fastaccess.data.dao.types.EventsType;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.RepoPagerView;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

class FeedsPresenter extends BasePresenter<FeedsMvp.View> implements FeedsMvp.Presenter {
    private ArrayList<EventsModel> eventsModels = new ArrayList<>();
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;

    @Override public void onCallApi(int page) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0) {
            sendToView(FeedsMvp.View::hideProgress);
            return;
        }
        setCurrentPage(page);
        makeRestCall(RestProvider.getUserService().getReceivedEvents(LoginModel.getUser().getLogin(), page),
                response -> {
                    lastPage = response.getLast();
                    if (getCurrentPage() == 1) {
                        manageSubscription(EventsModel.save(response.getItems()).subscribe());
                        eventsModels.clear();

                    }
                    eventsModels.addAll(response.getItems());
                    sendToView(FeedsMvp.View::onNotifyAdapter);
                });
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
        onCallApi(page);
    }

    @Override public void onSubscribed() {
        sendToView(view -> view.showProgress(0));
    }

    @Override public <T> T onError(@NonNull Throwable throwable, @NonNull Observable<T> observable) {
        onWorkOffline();
        return super.onError(throwable, observable);
    }

    @NonNull @Override public ArrayList<EventsModel> getEvents() {
        return eventsModels;
    }

    @Override public void onWorkOffline() {
        if (eventsModels.isEmpty()) {
            manageSubscription(EventsModel.getEvents().subscribe(modelList -> {
                eventsModels.addAll(modelList);
                sendToView(FeedsMvp.View::onNotifyAdapter);
            }));
        } else {
            sendToView(FeedsMvp.View::hideProgress);
        }
    }

    @Override public void onItemClick(int position, View v, EventsModel item) {
        if (item.getType() == EventsType.ForkEvent) {
            NameParser parser = new NameParser(item.getPayload().getForkee().getHtmlUrl());
            RepoPagerView.startRepoPager(v.getContext(), parser);
        } else {
            if (item.getPayload().getIssueModel() != null) {
                SchemeParser.launchUri(v.getContext(), Uri.parse(item.getPayload().getIssueModel().getHtmlUrl()));
            } else {
                SchemeParser.launchUri(v.getContext(), Uri.parse(item.getRepo().getName()));
            }
        }
    }

    @Override public void onItemLongClick(int position, View v, EventsModel item) {
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

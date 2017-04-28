package com.fastaccess.ui.modules.feeds;

import android.content.Intent;
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
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.RepoPagerActivity;
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerActivity;

import java.util.ArrayList;

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

class FeedsPresenter extends BasePresenter<FeedsMvp.View> implements FeedsMvp.Presenter {
    private ArrayList<Event> eventsModels = new ArrayList<>();
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
        if (Login.getUser() == null) return;// I can't understand how this could possibly be reached lol.
        makeRestCall(RestProvider.getUserService().getReceivedEvents(Login.getUser().getLogin(), page), response -> {
            lastPage = response.getLast();
            if (getCurrentPage() == 1) {
                manageSubscription(Event.save(response.getItems()).subscribe());
            }
            sendToView(view -> view.onNotifyAdapter(response.getItems(), page));
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

    @Override public void onError(@NonNull Throwable throwable) {
        onWorkOffline();
        super.onError(throwable);
    }

    @NonNull @Override public ArrayList<Event> getEvents() {
        return eventsModels;
    }

    @Override public void onWorkOffline() {
        if (eventsModels.isEmpty()) {
            manageSubscription(RxHelper.getObserver(Event.getEvents()).subscribe(modelList -> {
                if (modelList != null) {
                    sendToView(view -> view.onNotifyAdapter(modelList, 1));
                }
            }));
        } else {
            sendToView(FeedsMvp.View::hideProgress);
        }
    }

    @Override public void onItemClick(int position, View v, Event item) {
        if (item.getType() == EventsType.ForkEvent) {
            NameParser parser = new NameParser(item.getPayload().getForkee().getHtmlUrl());
            RepoPagerActivity.startRepoPager(v.getContext(), parser);
        } else {
            PayloadModel payloadModel = item.getPayload();
            if (payloadModel != null) {
                if (payloadModel.getHead() != null) {
                    Repo repoModel = item.getRepo();
                    Uri uri = Uri.parse(repoModel.getName());
                    if (uri == null || uri.getPathSegments().size() < 1) return;
                    Intent intent = CommitPagerActivity.createIntent(v.getContext(), uri.getLastPathSegment(), uri.getPathSegments().get(0),
                            payloadModel.getHead(), true);
                    v.getContext().startActivity(intent);
                } else if (item.getPayload().getIssue() != null) {
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

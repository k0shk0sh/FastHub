package com.fastaccess.ui.modules.feeds;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.PayloadModel;
import com.fastaccess.data.dao.SimpleUrlsModel;
import com.fastaccess.data.dao.model.Event;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.data.dao.types.EventsType;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

public class FeedsPresenter extends BasePresenter<FeedsMvp.View> implements FeedsMvp.Presenter {
    private ArrayList<Event> eventsModels = new ArrayList<>();
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;
    @com.evernote.android.state.State String user;
    @com.evernote.android.state.State boolean isOrg;

    @Override public void onFragmentCreated(@NonNull Bundle argument) {
        user = argument.getString(BundleConstant.EXTRA);
        isOrg = argument.getBoolean(BundleConstant.EXTRA_TWO);
        setEnterprise(argument.getBoolean(BundleConstant.IS_ENTERPRISE));
        if (eventsModels.isEmpty()) {
            onCallApi(1);
        }
    }

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
        Observable<Pageable<Event>> observable;
        if (user != null) {
            if (isOrg) {
                observable = RestProvider.getOrgService().getReceivedEvents(user, page);
            } else {
                observable = RestProvider.getUserService().getUserEvents(user, page);
            }
        } else {
            observable = RestProvider.getUserService().getReceivedEvents(Login.getUser().getLogin(), page);
        }
        makeRestCall(observable, response -> {
            lastPage = response.getLast();
            if (getCurrentPage() == 1) {
                manageObservable(Event.save(response.getItems()).toObservable());
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
            manageDisposable(RxHelper.getObserver(Event.getEvents().toObservable())
                    .subscribe(modelList -> {
                        if (modelList != null) {
                            sendToView(view -> view.onNotifyAdapter(modelList, 1));
                        }
                    }, Throwable::printStackTrace));
        } else {
            sendToView(FeedsMvp.View::hideProgress);
        }
    }

    @Override public void onItemClick(int position, View v, Event item) {
        if (item.getType() == EventsType.ForkEvent) {
            SchemeParser.launchUri(v.getContext(), item.getPayload().getForkee().getHtmlUrl());
        } else {
            PayloadModel payloadModel = item.getPayload();
            if (payloadModel != null) {
                if (payloadModel.getHead() != null && payloadModel.getCommits() != null) {
                    if (payloadModel.getCommits().size() > 1) {
                        sendToView(view -> view.onOpenCommitChooser(payloadModel.getCommits()));
                    } else if (payloadModel.getSize() == 1) {
                        SchemeParser.launchUri(v.getContext(), payloadModel.getCommits().get(0).getUrl());
                    }
                } else if (payloadModel.getIssue() != null) {
                    SchemeParser.launchUri(v.getContext(), Uri.parse(payloadModel.getIssue().getHtmlUrl()), true);
                } else if (payloadModel.getPullRequest() != null) {
                    SchemeParser.launchUri(v.getContext(), Uri.parse(payloadModel.getPullRequest().getHtmlUrl()), true);
                } else if (payloadModel.getComment() != null) {
                    SchemeParser.launchUri(v.getContext(), Uri.parse(payloadModel.getComment().getHtmlUrl()), true);
                } else if (item.getType() == EventsType.ReleaseEvent && payloadModel.getRelease() != null) {
                    SchemeParser.launchUri(v.getContext(), payloadModel.getRelease().getHtmlUrl());
                } else if (item.getType() == EventsType.CreateEvent && "tag".equalsIgnoreCase(payloadModel.getRefType())) {
                    Repo repoModel = item.getRepo();
                    Uri uri = Uri.parse(repoModel.getUrl())
                            .buildUpon()
                            .appendPath("releases")
                            .appendPath("tag")
                            .appendPath(payloadModel.getRef())
                            .build();
                    SchemeParser.launchUri(v.getContext(), uri);
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

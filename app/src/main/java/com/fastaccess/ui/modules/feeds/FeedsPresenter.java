package com.fastaccess.ui.modules.feeds;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.data.dao.NameParser;
import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.PayloadModel;
import com.fastaccess.data.dao.SimpleUrlsModel;
import com.fastaccess.data.dao.model.Event;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.data.dao.types.EventsType;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.RepoPagerActivity;
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerActivity;
import com.fastaccess.ui.modules.repos.code.releases.ReleasesListActivity;
import com.fastaccess.ui.modules.repos.wiki.WikiActivity;

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
        if (eventsModels.isEmpty()) {
            onCallApi(1);
        }
    }

    @Override public boolean onCallApi(int page) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0) {
            sendToView(FeedsMvp.View::hideProgress);
            return false;
        }
        setCurrentPage(page);
        Login login = Login.getUser();
        if (login == null) return false;// I can't understand how this could possibly be reached lol.
        Observable<Pageable<Event>> observable;
        Logger.e(isOrg);
        if (user != null) {
            if (isOrg) {
                observable = RestProvider.getOrgService(isEnterprise()).getReceivedEvents(login.getLogin(), user, page);
            } else {
                observable = RestProvider.getUserService(login.getLogin().equalsIgnoreCase(user)
                                                         ? PrefGetter.isEnterprise() : isEnterprise()).getUserEvents(user, page);
            }
        } else {
            observable = RestProvider.getUserService(PrefGetter.isEnterprise()).getReceivedEvents(login.getLogin(), page);
        }
        makeRestCall(observable, response -> {
            lastPage = response.getLast();
            if (getCurrentPage() == 1) {
                manageDisposable(Event.save(response.getItems(), user));
            }
            sendToView(view -> view.onNotifyAdapter(response.getItems(), page));
        });
        return true;
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
        return onCallApi(page);
    }

    @Override public void onSubscribed(boolean cancelable) {
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
        if (eventsModels.isEmpty() && InputHelper.isEmpty(user)) {
            manageDisposable(RxHelper.getObservable(Event.getEvents(Login.getUser().getLogin()).toObservable())
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
            NameParser parser = new NameParser(item.getPayload().getForkee().getHtmlUrl());
            RepoPagerActivity.startRepoPager(v.getContext(), parser);
        } else {
            PayloadModel payloadModel = item.getPayload();
            if (payloadModel != null) {
                if (payloadModel.getHead() != null) {
                    if (payloadModel.getCommits() != null && payloadModel.getCommits().size() > 1) {
                        sendToView(view -> view.onOpenCommitChooser(payloadModel.getCommits()));
                    } else {
                        Repo repoModel = item.getRepo();
                        NameParser nameParser = new NameParser(repoModel.getUrl());
                        Intent intent = CommitPagerActivity.createIntent(v.getContext(), nameParser.getName(),
                                nameParser.getUsername(), payloadModel.getHead(), true,
                                LinkParserHelper.isEnterprise(repoModel.getUrl()));
                        v.getContext().startActivity(intent);
                    }
                } else if (payloadModel.getIssue() != null) {
                    SchemeParser.launchUri(v.getContext(), Uri.parse(payloadModel.getIssue().getHtmlUrl()), true);
                } else if (payloadModel.getPullRequest() != null) {
                    SchemeParser.launchUri(v.getContext(), Uri.parse(payloadModel.getPullRequest().getHtmlUrl()), true);
                } else if (payloadModel.getComment() != null) {
                    SchemeParser.launchUri(v.getContext(), Uri.parse(payloadModel.getComment().getHtmlUrl()), true);
                } else if (item.getType() == EventsType.ReleaseEvent && payloadModel.getRelease() != null) {
                    NameParser nameParser = new NameParser(payloadModel.getRelease().getHtmlUrl());
                    v.getContext().startActivity(ReleasesListActivity.getIntent(v.getContext(), nameParser.getUsername(), nameParser.getName(),
                            payloadModel.getRelease().getId(), LinkParserHelper.isEnterprise(payloadModel.getRelease().getHtmlUrl())));

                } else if (item.getType() == EventsType.CreateEvent && "tag".equalsIgnoreCase(payloadModel.getRefType())) {
                    Repo repoModel = item.getRepo();
                    NameParser nameParser = new NameParser(repoModel.getUrl());
                    v.getContext().startActivity(ReleasesListActivity.getIntent(v.getContext(), nameParser.getUsername(), nameParser.getName(),
                            payloadModel.getRef(), LinkParserHelper.isEnterprise(repoModel.getUrl())));
                } else if (item.getType() == EventsType.GollumEvent) {
                    Repo repoModel = item.getRepo();
                    NameParser parser = new NameParser(repoModel.getUrl());
                    v.getContext().startActivity(WikiActivity.Companion.getWiki(v.getContext(), parser.getName(), parser.getUsername()));
                } else {
                    Repo repoModel = item.getRepo();
                    NameParser parser = new NameParser(repoModel.getUrl());
                    RepoPagerActivity.startRepoPager(v.getContext(), parser);
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
            Repo repo = item.getRepo();
            if (repo != null) {
                NameParser parser = new NameParser(repo.getUrl());
                RepoPagerActivity.startRepoPager(v.getContext(), parser);
            }
        }
    }
}

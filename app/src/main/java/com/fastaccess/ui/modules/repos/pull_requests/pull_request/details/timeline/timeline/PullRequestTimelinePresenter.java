package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.timeline.timeline;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.IssueEvent;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.provider.comments.ReactionsProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by Kosh on 31 Mar 2017, 7:17 PM
 */

public class PullRequestTimelinePresenter extends BasePresenter<PullRequestTimelineMvp.View> implements PullRequestTimelineMvp.Presenter {
    private ArrayList<TimelineModel> timeline = new ArrayList<>();
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;
    private ReactionsProvider reactionsProvider;

    @Override public void onItemClick(int position, View v, TimelineModel item) {
        if (item.getType() == TimelineModel.COMMENT) {
            Login user = Login.getUser();
            if (getView() != null) {
                if (v.getId() == R.id.delete) {
                    if (user != null && item.getComment().getUser().getLogin().equals(user.getLogin())) {
                        if (getView() != null) getView().onShowDeleteMsg(item.getComment().getId());
                    }
                } else if (v.getId() == R.id.reply) {
                    getView().onTagUser(item.getComment().getUser());
                } else if (v.getId() == R.id.edit) {
                    if (user != null && item.getComment().getUser().getLogin().equals(user.getLogin())) {
                        getView().onEditComment(item.getComment());
                    }
                } else {
                    onHandleReaction(v.getId(), item.getComment().getId());
                }
            }
        } else if (item.getType() == TimelineModel.EVENT) {
            IssueEvent issueEventModel = item.getEvent();
            if (issueEventModel.getCommitUrl() != null) {
                SchemeParser.launchUri(v.getContext(), Uri.parse(issueEventModel.getCommitUrl()));
            }
        }
    }

    @Override public void onItemLongClick(int position, View v, TimelineModel item) {
        onItemClick(position, v, item);
    }

    @NonNull @Override public ArrayList<TimelineModel> getEvents() {
        return timeline;
    }

    @Override public void onFragmentCreated(@Nullable Bundle bundle) {
        if (bundle == null) throw new NullPointerException("Bundle is null?");
        PullRequest issueModel = bundle.getParcelable(BundleConstant.ITEM);
        if (timeline.isEmpty() && issueModel != null) {
            timeline.add(TimelineModel.constructHeader(issueModel));
            sendToView(PullRequestTimelineMvp.View::onNotifyAdapter);
            onCallApi(1, null);
        }
    }

    @Override public void onWorkOffline() {
        //TODO
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
        if (page > lastPage || lastPage == 0 || getHeader() == null) {
            sendToView(PullRequestTimelineMvp.View::hideProgress);
            return;
        }
        setCurrentPage(page);
        String login = getHeader().getLogin();
        String repoId = getHeader().getRepoId();
        int number = getHeader().getNumber();
        Observable<List<TimelineModel>> observable = Observable.zip(RestProvider.getIssueService().getTimeline(login, repoId, number, page),
                RestProvider.getIssueService().getIssueComments(login, repoId, number, page),
                RestProvider.getPullRequestSerice().getPullStatus(login, repoId, getHeader().getHead().getSha()),
                RestProvider.getPullRequestSerice().getReviews(login, repoId, number),
                (issueEventPageable, commentPageable, statuses, reviews) -> {
                    lastPage = issueEventPageable.getLast() > commentPageable.getLast() ? issueEventPageable.getLast() : commentPageable.getLast();
                    if (statuses != null) {
                        statuses.setMergable(getHeader().isMergeable());
                    }
                    return TimelineModel.construct(commentPageable.getItems(), issueEventPageable.getItems(), statuses, reviews.getItems());
                });
        makeRestCall(observable, models -> {
            if (getCurrentPage() == 1) {
                getEvents().subList(1, getEvents().size()).clear();
            }
            getEvents().addAll(models);
            sendToView(PullRequestTimelineMvp.View::onNotifyAdapter);
        });
    }

    @Nullable private PullRequest getHeader() {
        return !timeline.isEmpty() ? timeline.get(0).getPullRequest() : null;
    }

    @Override public void onHandleDeletion(@Nullable Bundle bundle) {
        if (bundle != null) {
            long commId = bundle.getLong(BundleConstant.EXTRA, 0);
            if (commId != 0) {
                makeRestCall(RestProvider.getIssueService().deleteIssueComment(login(), repoId(), commId),
                        booleanResponse -> sendToView(view -> {
                            if (booleanResponse.code() == 204) {
                                Comment comment = new Comment();
                                comment.setId(commId);
                                getEvents().remove(TimelineModel.constructComment(comment));
                                view.onNotifyAdapter();
                            } else {
                                view.showMessage(R.string.error, R.string.error_deleting_comment);
                            }
                        }));
            }
        }
    }

    @Nullable @Override public String repoId() {
        return getHeader() != null ? getHeader().getRepoId() : null;
    }

    @Nullable @Override public String login() {
        return getHeader() != null ? getHeader().getLogin() : null;
    }

    @Override public int number() {
        return getHeader() != null ? getHeader().getNumber() : -1;
    }

    @Override public void onHandleReaction(int id, long commentId) {
        String login = login();
        String repoId = repoId();
        Observable observable = getReactionsProvider().onHandleReaction(id, commentId, login, repoId);
        if (observable != null) manageSubscription(observable.subscribe());
    }

    private ReactionsProvider getReactionsProvider() {
        if (reactionsProvider == null) {
            reactionsProvider = new ReactionsProvider();
        }
        return reactionsProvider;
    }

    @Override public boolean isPreviouslyReacted(long commentId, int vId) {
        return getReactionsProvider().isPreviouslyReacted(commentId, vId);
    }
}

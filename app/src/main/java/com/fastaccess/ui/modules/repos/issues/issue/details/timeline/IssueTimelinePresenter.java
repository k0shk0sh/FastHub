package com.fastaccess.ui.modules.repos.issues.issue.details.timeline;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.PostReactionModel;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.IssueEvent;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.ReactionsModel;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * Created by Kosh on 31 Mar 2017, 7:17 PM
 */

public class IssueTimelinePresenter extends BasePresenter<IssueTimelineMvp.View> implements IssueTimelineMvp.Presenter {
    private ArrayList<TimelineModel> timeline = new ArrayList<>();
    private Map<Long, ReactionsModel> reactionsMap = new LinkedHashMap<>();
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;

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

    @Override public boolean isPreviouslyReacted(long commentId, int vId) {
        ReactionsModel reactionsModel = getReactionsMap().get(commentId);
        if (reactionsModel == null || InputHelper.isEmpty(reactionsModel.getContent())) {
            return false;
        }
        ReactionTypes type = ReactionTypes.get(vId);
        return type != null && type.getContent().equals(reactionsModel.getContent());
    }

    @Override public void onItemLongClick(int position, View v, TimelineModel item) {
        onItemClick(position, v, item);
    }

    @NonNull @Override public ArrayList<TimelineModel> getEvents() {
        return timeline;
    }

    @Override public void onFragmentCreated(@Nullable Bundle bundle) {
        if (bundle == null) throw new NullPointerException("Bundle is null?");
        Issue issueModel = bundle.getParcelable(BundleConstant.ITEM);
        if (timeline.isEmpty() && issueModel != null) {
            timeline.add(TimelineModel.constructHeader(issueModel));
            sendToView(IssueTimelineMvp.View::onNotifyAdapter);
            onCallApi(1, null);
        }
    }

    @Override public void onWorkOffline() {
        //TODO
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
            sendToView(IssueTimelineMvp.View::hideProgress);
            return;
        }
        setCurrentPage(page);
        String login = getHeader().getLogin();
        String repoID = getHeader().getRepoId();
        int number = getHeader().getNumber();
        Observable<List<TimelineModel>> observable = Observable.zip(RestProvider.getIssueService().getTimeline(login, repoID, number, page),
                RestProvider.getIssueService().getIssueComments(login, repoID, number, page),
                (issueEventPageable, commentPageable) -> {
                    lastPage = issueEventPageable.getLast() > commentPageable.getLast() ? issueEventPageable.getLast() : commentPageable.getLast();
                    return TimelineModel.construct(commentPageable.getItems(), issueEventPageable.getItems());
                });
        makeRestCall(observable, models -> {
            if (getCurrentPage() == 1) {
                getEvents().subList(1, getEvents().size()).clear();
            }
            getEvents().addAll(models);
            sendToView(IssueTimelineMvp.View::onNotifyAdapter);
        });
    }

    @Nullable private Issue getHeader() {
        return !timeline.isEmpty() ? timeline.get(0).getIssue() : null;
    }

    @NonNull @Override public Map<Long, ReactionsModel> getReactionsMap() {
        return reactionsMap;
    }

    @Override public void onHandleReaction(int id, long commentId) {
        String login = login();
        String repoId = repoId();
        if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
            if (!isPreviouslyReacted(commentId, id)) {
                ReactionTypes reactionTypes = ReactionTypes.get(id);
                if (reactionTypes != null) {
                    manageSubscription(RxHelper.safeObservable(RestProvider.getReactionsService()
                            .postIssueReaction(new PostReactionModel(reactionTypes.getContent()), login, repoId, commentId))
                            .doOnNext(response -> getReactionsMap().put(commentId, response))
                            .subscribe());
                }
            } else {
                ReactionsModel reactionsModel = getReactionsMap().get(commentId);
                if (reactionsModel != null) {
                    manageSubscription(RxHelper.safeObservable(RestProvider.getReactionsService().delete(reactionsModel.getId()))
                            .doOnNext(booleanResponse -> {
                                if (booleanResponse.code() == 204) {
                                    getReactionsMap().remove(commentId);
                                }
                            })
                            .subscribe());
                }
            }
        }
    }
}

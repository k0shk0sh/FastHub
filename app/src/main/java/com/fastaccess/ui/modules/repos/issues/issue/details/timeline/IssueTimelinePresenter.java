package com.fastaccess.ui.modules.repos.issues.issue.details.timeline;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.PopupMenu;

import com.fastaccess.R;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.timeline.GenericEvent;
import com.fastaccess.data.dao.timeline.SourceModel;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.provider.timeline.CommentsHelper;
import com.fastaccess.provider.timeline.ReactionsProvider;
import com.fastaccess.provider.timeline.TimelineConverter;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.issues.create.CreateIssueActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import lombok.Getter;

/**
 * Created by Kosh on 31 Mar 2017, 7:17 PM
 */

@Getter public class IssueTimelinePresenter extends BasePresenter<IssueTimelineMvp.View> implements IssueTimelineMvp.Presenter {
    private ArrayList<TimelineModel> timeline = new ArrayList<>();
    private ReactionsProvider reactionsProvider;
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;

    @Override public boolean isPreviouslyReacted(long commentId, int vId) {
        return getReactionsProvider().isPreviouslyReacted(commentId, vId);
    }

    @Override public void onItemClick(int position, View v, TimelineModel item) {
        if (getView() != null) {
            if (item.getType() == TimelineModel.COMMENT) {
                if (getView().getIssue() == null) return;
                Issue issue = getView().getIssue();
                if (v.getId() == R.id.commentMenu) {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                    popupMenu.inflate(R.menu.comments_menu);
                    String username = Login.getUser().getLogin();
                    boolean isOwner = CommentsHelper.isOwner(username, issue.getLogin(), item.getComment().getUser().getLogin());
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(isOwner);
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(isOwner);
                    popupMenu.setOnMenuItemClickListener(item1 -> {
                        if (getView() == null) return false;
                        if (item1.getItemId() == R.id.delete) {
                            getView().onShowDeleteMsg(item.getComment().getId());
                        } else if (item1.getItemId() == R.id.reply) {
                            getView().onReply(item.getComment().getUser(), item.getComment().getBody());
                        } else if (item1.getItemId() == R.id.edit) {
                            getView().onEditComment(item.getComment());
                        } else if (item1.getItemId() == R.id.share) {
                            ActivityHelper.shareUrl(v.getContext(), item.getComment().getHtmlUrl());
                        }
                        return true;
                    });
                    popupMenu.show();
                } else {
                    onHandleReaction(v.getId(), item.getComment().getId(), ReactionsProvider.COMMENT);
                }
            } else if (item.getType() == TimelineModel.EVENT) {
                GenericEvent issueEventModel = item.getGenericEvent();
                if (issueEventModel.getCommitUrl() != null) {
                    SchemeParser.launchUri(v.getContext(), Uri.parse(issueEventModel.getCommitUrl()));
                } else {
                    SourceModel sourceModel = issueEventModel.getSource();
                    if (sourceModel != null) {
                        if (sourceModel.getCommit() != null) {
                            SchemeParser.launchUri(v.getContext(), Uri.parse(sourceModel.getCommit().getUrl()));
                        } else if (sourceModel.getIssue() != null) {
                            SchemeParser.launchUri(v.getContext(), Uri.parse(sourceModel.getIssue().getUrl()));
                        } else if (sourceModel.getPullRequest() != null) {
                            SchemeParser.launchUri(v.getContext(), Uri.parse(sourceModel.getPullRequest().getUrl()));
                        } else if (sourceModel.getRepository() != null) {
                            SchemeParser.launchUri(v.getContext(), Uri.parse(sourceModel.getRepository().getUrl()));
                        }
                    }
                }
            } else if (item.getType() == TimelineModel.HEADER) {
                if (v.getId() == R.id.commentMenu) {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                    popupMenu.inflate(R.menu.comments_menu);
                    String username = Login.getUser().getLogin();
                    boolean isOwner = CommentsHelper.isOwner(username, item.getIssue().getLogin(), item.getIssue().getUser().getLogin());
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(isOwner);
                    popupMenu.setOnMenuItemClickListener(item1 -> {
                        if (getView() == null) return false;
                        if (item1.getItemId() == R.id.reply) {
                            getView().onReply(item.getIssue().getUser(), item.getIssue().getBody());
                        } else if (item1.getItemId() == R.id.edit) {
                            Activity activity = ActivityHelper.getActivity(v.getContext());
                            if (activity == null) return false;
                            CreateIssueActivity.startForResult(activity,
                                    item.getIssue().getLogin(), item.getIssue().getRepoId(), item.getIssue(), isEnterprise());
                        } else if (item1.getItemId() == R.id.share) {
                            ActivityHelper.shareUrl(v.getContext(), item.getIssue().getHtmlUrl());
                        }
                        return true;
                    });
                    popupMenu.show();
                } else {
                    onHandleReaction(v.getId(), item.getIssue().getNumber(), ReactionsProvider.HEADER);
                }
            }
        }
    }

    @Override public void onItemLongClick(int position, View v, TimelineModel item) {
        if (getView() == null) return;
        if (item.getType() == TimelineModel.COMMENT || item.getType() == TimelineModel.HEADER) {
            if (v.getId() == R.id.commentMenu && item.getType() == TimelineModel.COMMENT) {
                Comment comment = item.getComment();
                if (getView() != null) getView().onReply(comment.getUser(), comment.getBody());
            } else {
                if (getView().getIssue() == null) return;
                Issue issue = getView().getIssue();
                String login = issue.getLogin();
                String repoId = issue.getRepoId();
                if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
                    ReactionTypes type = ReactionTypes.get(v.getId());
                    if (type != null) {
                        if (item.getType() == TimelineModel.HEADER) {
                            getView().showReactionsPopup(type, login, repoId, item.getIssue().getNumber(), true);
                        } else {
                            getView().showReactionsPopup(type, login, repoId, item.getComment().getId(), false);
                        }
                    } else {
                        onItemClick(position, v, item);
                    }
                }
            }
        } else {
            onItemClick(position, v, item);
        }
    }

    @NonNull @Override public ArrayList<TimelineModel> getEvents() {
        return timeline;
    }

    @Override public void onWorkOffline() {
        //TODO
    }

    @Override public void onHandleDeletion(@Nullable Bundle bundle) {
        if (bundle != null) {
            long commId = bundle.getLong(BundleConstant.EXTRA, 0);
            if (commId != 0) {
                if (getView() == null || getView().getIssue() == null) return;
                Issue issue = getView().getIssue();
                makeRestCall(RestProvider.getIssueService(isEnterprise()).deleteIssueComment(issue.getLogin(), issue.getRepoId(), commId),
                        booleanResponse -> sendToView(view -> {
                            if (booleanResponse.code() == 204) {
                                Comment comment = new Comment();
                                comment.setId(commId);
                                view.onRemove(TimelineModel.constructComment(comment));
                            } else {
                                view.showMessage(R.string.error, R.string.error_deleting_comment);
                            }
                        }));
            }
        }
    }

    @Override public void onHandleReaction(int viewId, long id, @ReactionsProvider.ReactionType int reactionType) {
        if (getView() == null || getView().getIssue() == null) return;
        Issue issue = getView().getIssue();
        String login = issue.getLogin();
        String repoId = issue.getRepoId();
        Observable observable = getReactionsProvider().onHandleReaction(viewId, id, login, repoId, reactionType, isEnterprise());
        if (observable != null) manageObservable(observable);
    }

    @Override public boolean isCallingApi(long id, int vId) {
        return getReactionsProvider().isCallingApi(id, vId);
    }

    @NonNull private ReactionsProvider getReactionsProvider() {
        if (reactionsProvider == null) {
            reactionsProvider = new ReactionsProvider();
        }
        return reactionsProvider;
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

    @Override public void onCallApi(int page, @Nullable Issue parameter) {
        if (parameter == null) {
            sendToView(BaseMvp.FAView::hideProgress);
            return;
        }
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0) {
            sendToView(IssueTimelineMvp.View::hideProgress);
            return;
        }
        setCurrentPage(page);
        String login = parameter.getLogin();
        String repoId = parameter.getRepoId();
        int number = parameter.getNumber();
        Observable<List<TimelineModel>> observable = RestProvider.getIssueService(isEnterprise())
                .getTimeline(login, repoId, number, page)
                .flatMap(response -> {
                    if (response != null) {
                        lastPage = response.getLast();
                    }
                    return TimelineConverter.INSTANCE.convert(response != null ? response.getItems() : null);
                })
                .toList()
                .toObservable();
        makeRestCall(observable, timeline -> sendToView(view -> view.onNotifyAdapter(timeline, page)));
    }
}

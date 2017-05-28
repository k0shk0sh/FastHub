package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.timeline.timeline;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.PopupMenu;

import com.fastaccess.R;
import com.fastaccess.data.dao.EditReviewCommentModel;
import com.fastaccess.data.dao.GroupedReviewModel;
import com.fastaccess.data.dao.ReviewCommentModel;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.IssueEvent;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.provider.timeline.CommentsHelper;
import com.fastaccess.provider.timeline.ReactionsProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.issues.create.CreateIssueActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Kosh on 31 Mar 2017, 7:17 PM
 */

public class PullRequestTimelinePresenter extends BasePresenter<PullRequestTimelineMvp.View> implements PullRequestTimelineMvp.Presenter {
    private ArrayList<TimelineModel> timeline = new ArrayList<>();
    private ReactionsProvider reactionsProvider;
    @icepick.State PullRequest pullRequest;
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;

    @Override public void onItemClick(int position, View v, TimelineModel item) {
        if (getView() != null) {
            if (item.getType() == TimelineModel.COMMENT) {
                if (getHeader() == null) return;
                if (v.getId() == R.id.commentMenu) {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                    popupMenu.inflate(R.menu.comments_menu);
                    String username = Login.getUser().getLogin();
                    boolean isOwner = CommentsHelper.isOwner(username, getHeader().getLogin(), item.getComment().getUser().getLogin());
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
                IssueEvent issueEventModel = item.getEvent();
                if (issueEventModel.getCommitUrl() != null) {
                    SchemeParser.launchUri(v.getContext(), Uri.parse(issueEventModel.getCommitUrl()));
                }
            } else if (item.getType() == TimelineModel.HEADER) {
                if (v.getId() == R.id.commentMenu) {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                    popupMenu.inflate(R.menu.comments_menu);
                    String username = Login.getUser().getLogin();
                    boolean isOwner = CommentsHelper.isOwner(username, item.getPullRequest().getLogin(),
                            item.getPullRequest().getUser().getLogin());
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(isOwner);
                    popupMenu.setOnMenuItemClickListener(item1 -> {
                        if (getView() == null) return false;
                        if (item1.getItemId() == R.id.reply) {
                            getView().onReply(item.getPullRequest().getUser(), item.getPullRequest().getBody());
                        } else if (item1.getItemId() == R.id.edit) {
                            Activity activity = ActivityHelper.getActivity(v.getContext());
                            if (activity == null) return false;
                            CreateIssueActivity.startForResult(activity,
                                    item.getPullRequest().getLogin(), item.getPullRequest().getRepoId(), item.getPullRequest());
                        } else if (item1.getItemId() == R.id.share) {
                            ActivityHelper.shareUrl(v.getContext(), item.getPullRequest().getHtmlUrl());
                        }
                        return true;
                    });
                    popupMenu.show();
                } else {
                    onHandleReaction(v.getId(), item.getPullRequest().getNumber(), ReactionsProvider.HEADER);
                }
            } else if (item.getType() == TimelineModel.GROUPED_REVIEW) {
                GroupedReviewModel reviewModel = item.getGroupedReview();
                if (v.getId() == R.id.addCommentPreview) {
                    EditReviewCommentModel model = new EditReviewCommentModel();
                    model.setCommentPosition(-1);
                    model.setGroupPosition(position);
                    model.setInReplyTo(reviewModel.getId());
                    getView().onReplyOrCreateReview(null, null, position, -1, model);
                }
            }
        }
    }

    @Override public void onItemLongClick(int position, View v, TimelineModel item) {
        if (getView() == null) return;
        if (item.getType() == TimelineModel.COMMENT || item.getType() == TimelineModel.HEADER) {
            String login = login();
            String repoId = repoId();
            if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
                ReactionTypes type = ReactionTypes.get(v.getId());
                if (type != null) {
                    if (item.getType() == TimelineModel.HEADER) {
                        getView().showReactionsPopup(type, login, repoId, item.getPullRequest().getNumber(), ReactionsProvider.HEADER);
                    } else {
                        getView().showReactionsPopup(type, login, repoId, item.getComment().getId(), ReactionsProvider.COMMENT);
                    }
                } else {
                    onItemClick(position, v, item);
                }
            }
        } else {
            onItemClick(position, v, item);
        }
    }

    @NonNull @Override public ArrayList<TimelineModel> getEvents() {
        return timeline;
    }

    @Override public void onFragmentCreated(@Nullable Bundle bundle) {
        if (bundle == null) throw new NullPointerException("Bundle is null?");
        pullRequest = bundle.getParcelable(BundleConstant.ITEM);
        if (timeline.isEmpty() && pullRequest != null) {
            sendToView(view -> view.onSetHeader(TimelineModel.constructHeader(pullRequest)));
            onCallApi(1, null);
        }
    }

    @Override public void onWorkOffline() {
        //TODO
    }

    @Nullable private PullRequest getHeader() {
        return pullRequest;
    }

    @Override public void onHandleDeletion(@Nullable Bundle bundle) {
        if (bundle != null) {
            long commId = bundle.getLong(BundleConstant.EXTRA, 0);
            boolean isReviewComment = bundle.getBoolean(BundleConstant.YES_NO_EXTRA);
            if (commId != 0 && !isReviewComment) {
                makeRestCall(RestProvider.getIssueService().deleteIssueComment(login(), repoId(), commId),
                        booleanResponse -> sendToView(view -> {
                            if (booleanResponse.code() == 204) {
                                Comment comment = new Comment();
                                comment.setId(commId);
                                view.onRemove(TimelineModel.constructComment(comment));
                            } else {
                                view.showMessage(R.string.error, R.string.error_deleting_comment);
                            }
                        }));
            } else {
                int groupPosition = bundle.getInt(BundleConstant.EXTRA_TWO);
                int commentPosition = bundle.getInt(BundleConstant.EXTRA_THREE);
                makeRestCall(RestProvider.getReviewService().deleteComment(login(), repoId(), commId),
                        booleanResponse -> sendToView(view -> {
                            if (booleanResponse.code() == 204) {
                                view.onRemoveReviewComment(groupPosition, commentPosition);
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

    @Override public void onHandleReaction(int vId, long idOrNumber, @ReactionsProvider.ReactionType int reactionType) {
        String login = login();
        String repoId = repoId();
        Observable observable = getReactionsProvider().onHandleReaction(vId, idOrNumber, login, repoId, reactionType);
        if (observable != null) //noinspection unchecked
            manageSubscription(RxHelper.safeObservable(observable).subscribe());
    }

    @Override public boolean isMerged() {
        return getHeader() != null && (getHeader().isMerged() || !InputHelper.isEmpty(getHeader().getMergedAt()));
    }

    @Override public boolean isCallingApi(long id, int vId) {
        return getReactionsProvider().isCallingApi(id, vId);
    }

    @Override public boolean isPreviouslyReacted(long commentId, int vId) {
        return getReactionsProvider().isPreviouslyReacted(commentId, vId);
    }

    @NonNull private ReactionsProvider getReactionsProvider() {
        if (reactionsProvider == null) {
            reactionsProvider = new ReactionsProvider();
        }
        return reactionsProvider;
    }

    @Override public void onClick(int groupPosition, int commentPosition, @NonNull View v, @NonNull ReviewCommentModel comment) {
        if (getHeader() == null || getView() == null) return;
        if (v.getId() == R.id.commentMenu) {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.inflate(R.menu.comments_menu);
            String username = Login.getUser().getLogin();
            boolean isOwner = CommentsHelper.isOwner(username, getHeader().getLogin(), comment.getUser().getLogin());
            popupMenu.getMenu().findItem(R.id.delete).setVisible(isOwner);
            popupMenu.getMenu().findItem(R.id.edit).setVisible(isOwner);
            popupMenu.setOnMenuItemClickListener(item1 -> {
                if (getView() == null) return false;
                if (item1.getItemId() == R.id.delete) {
                    getView().onShowReviewDeleteMsg(comment.getId(), groupPosition, commentPosition);
                } else if (item1.getItemId() == R.id.reply) {
                    EditReviewCommentModel model = new EditReviewCommentModel();
                    model.setGroupPosition(groupPosition);
                    model.setCommentPosition(commentPosition);
                    model.setInReplyTo(comment.getId());
                    getView().onReplyOrCreateReview(comment.getUser(), comment.getBodyHtml(), groupPosition, commentPosition, model);
                } else if (item1.getItemId() == R.id.edit) {
                    getView().onEditReviewComment(comment, groupPosition, commentPosition);
                } else if (item1.getItemId() == R.id.share) {
                    ActivityHelper.shareUrl(v.getContext(), comment.getHtmlUrl());
                }
                return true;
            });
            popupMenu.show();
        } else {
            onHandleReaction(v.getId(), comment.getId(), ReactionsProvider.REVIEW_COMMENT);
        }
    }

    @Override public void onLongClick(int groupPosition, int commentPosition, @NonNull View v, @NonNull ReviewCommentModel model) {
        if (getView() == null) return;
        String login = login();
        String repoId = repoId();
        if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
            ReactionTypes type = ReactionTypes.get(v.getId());
            if (type != null) {
                getView().showReactionsPopup(type, login, repoId, model.getId(), ReactionsProvider.REVIEW_COMMENT);
            } else {
                onClick(groupPosition, commentPosition, v, model);
            }
        }
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
        if (getHeader() == null) {
            sendToView(BaseMvp.FAView::hideProgress);
            return;
        }
        String login = getHeader().getLogin();
        String repoId = getHeader().getRepoId();
        int number = getHeader().getNumber();
        if (page <= 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0) {
            sendToView(PullRequestTimelineMvp.View::hideProgress);
            return;
        }
        setCurrentPage(page);
        loadEverything(login, repoId, number, getHeader().getHead().getSha(), getHeader().isMergeable(), page);
    }

    private void loadEverything(String login, String repoId, int number, @NonNull String sha, boolean isMergeable, int page) {
        Observable<List<TimelineModel>> observable;
        if (page > 1) {
            observable = RestProvider.getIssueService().getIssueComments(login, repoId, number, page)
                    .map(comments -> {
                        lastPage = comments != null ? comments.getLast() : 0;
                        return TimelineModel.construct(comments != null ? comments.getItems() : null);
                    });
        } else {
            observable = Observable.zip(RestProvider.getIssueService().getTimeline(login, repoId, number),
                    RestProvider.getIssueService().getIssueComments(login, repoId, number, page),
                    RestProvider.getPullRequestService().getPullStatus(login, repoId, sha),
                    RestProvider.getReviewService().getReviews(login, repoId, number),
                    RestProvider.getReviewService().getPrReviewComments(login, repoId, number),
                    (issueEventPageable, commentPageable, statuses, reviews, reviewComments) -> {
                        if (statuses != null) {
                            statuses.setMergable(isMergeable);
                        }
                        lastPage = commentPageable != null ? commentPageable.getLast() : 0;
                        return TimelineModel.construct(commentPageable != null ? commentPageable.getItems() : null,
                                issueEventPageable.getItems(), statuses, reviews.getItems(), reviewComments.getItems());
                    });
        }
        makeRestCall(observable, models -> sendToView(view -> view.onNotifyAdapter(models, page)));
    }
}

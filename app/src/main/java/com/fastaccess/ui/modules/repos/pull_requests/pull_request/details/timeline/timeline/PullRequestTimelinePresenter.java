package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.timeline.timeline;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;
import android.widget.PopupMenu;

import com.annimon.stream.Stream;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.fastaccess.App;
import com.fastaccess.R;
import com.fastaccess.data.dao.CommentRequestModel;
import com.fastaccess.data.dao.EditReviewCommentModel;
import com.fastaccess.data.dao.ReviewCommentModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.timeline.PullRequestTimelineModel;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.timeline.CommentsHelper;
import com.fastaccess.provider.timeline.ReactionsProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import pr.PullRequestTimelineQuery;

/**
 * Created by Kosh on 31 Mar 2017, 7:17 PM
 */

public class PullRequestTimelinePresenter extends BasePresenter<PullRequestTimelineMvp.View> implements PullRequestTimelineMvp.Presenter {
    private ArrayList<PullRequestTimelineModel> timeline = new ArrayList<>();
    private SparseArray<String> pages = new SparseArray<>();
    private ReactionsProvider reactionsProvider;
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;

    @Override public void onItemClick(int position, View v, PullRequestTimelineModel item) {
//        if (getView() != null && getView().getPullRequest() != null) {
//            if (item.getType() == PullRequestTimelineModel.COMMENT) {
//                PullRequest pullRequest = getView().getPullRequest();
//                if (v.getId() == R.id.commentMenu) {
//                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
//                    popupMenu.inflate(R.menu.comments_menu);
//                    String username = Login.getUser().getLogin();
//                    boolean isOwner = CommentsHelper.isOwner(username, pullRequest.getLogin(), item.getComment().getUser().getLogin());
//                    popupMenu.getMenu().findItem(R.id.delete).setVisible(isOwner);
//                    popupMenu.getMenu().findItem(R.id.edit).setVisible(isOwner);
//                    popupMenu.setOnMenuItemClickListener(item1 -> {
//                        if (getView() == null) return false;
//                        if (item1.getItemId() == R.id.delete) {
//                            getView().onShowDeleteMsg(item.getComment().getId());
//                        } else if (item1.getItemId() == R.id.reply) {
//                            getView().onReply(item.getComment().getUser(), item.getComment().getBodyHtml());
//                        } else if (item1.getItemId() == R.id.edit) {
//                            getView().onEditComment(item.getComment());
//                        } else if (item1.getItemId() == R.id.share) {
//                            ActivityHelper.shareUrl(v.getContext(), item.getComment().getHtmlUrl());
//                        }
//                        return true;
//                    });
//                    popupMenu.show();
//                } else {
//                    onHandleReaction(v.getId(), item.getComment().getId(), ReactionsProvider.COMMENT);
//                }
//            } else if (item.getType() == PullRequestTimelineModel.EVENT) {
//                IssueEvent issueEventModel = item.getEvent();
//                if (issueEventModel.getCommitUrl() != null) {
//                    SchemeParser.launchUri(v.getContext(), Uri.parse(issueEventModel.getCommitUrl()));
//                }
//            } else if (item.getType() == PullRequestTimelineModel.HEADER) {
//                if (v.getId() == R.id.commentMenu) {
//                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
//                    popupMenu.inflate(R.menu.comments_menu);
//                    String username = Login.getUser().getLogin();
//                    boolean isOwner = CommentsHelper.isOwner(username, item.getPullRequest().getLogin(),
//                            item.getPullRequest().getUser().getLogin());
//                    popupMenu.getMenu().findItem(R.id.edit).setVisible(isOwner);
//                    popupMenu.setOnMenuItemClickListener(item1 -> {
//                        if (getView() == null) return false;
//                        if (item1.getItemId() == R.id.reply) {
//                            getView().onReply(item.getPullRequest().getUser(), item.getPullRequest().getBodyHtml());
//                        } else if (item1.getItemId() == R.id.edit) {
//                            Activity activity = ActivityHelper.getActivity(v.getContext());
//                            if (activity == null) return false;
//                            CreateIssueActivity.startForResult(activity,
//                                    item.getPullRequest().getLogin(), item.getPullRequest().getRepoId(),
//                                    item.getPullRequest(), isEnterprise());
//                        } else if (item1.getItemId() == R.id.share) {
//                            ActivityHelper.shareUrl(v.getContext(), item.getPullRequest().getHtmlUrl());
//                        }
//                        return true;
//                    });
//                    popupMenu.show();
//                } else {
//                    onHandleReaction(v.getId(), item.getPullRequest().getNumber(), ReactionsProvider.HEADER);
//                }
//            } else if (item.getType() == PullRequestTimelineModel.GROUPED_REVIEW) {
//                GroupedReviewModel reviewModel = item.getGroupedReview();
//                if (v.getId() == R.id.addCommentPreview) {
//                    EditReviewCommentModel model = new EditReviewCommentModel();
//                    model.setCommentPosition(-1);
//                    model.setGroupPosition(position);
//                    model.setInReplyTo(reviewModel.getId());
//                    getView().onReplyOrCreateReview(null, null, position, -1, model);
//                }
//            }
//        }
    }

    @Override public void onItemLongClick(int position, View v, PullRequestTimelineModel item) {
        if (v.getId() == R.id.commentMenu && item.getType() == PullRequestTimelineModel.COMMENT) {
//            Comment comment = item.getComment();
//            if (getView() != null) getView().onReply(comment.getUser(), comment.getBody());
//        } else {

        }
//        if (getView() == null || getView().getPullRequest() == null) return;
//        if (item.getType() == PullRequestTimelineModel.COMMENT || item.getType() == PullRequestTimelineModel.HEADER) {
//            PullRequest pullRequest = getView().getPullRequest();
//            String login = pullRequest.getLogin();
//            String repoId = pullRequest.getRepoId();
//            if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
//                ReactionTypes type = ReactionTypes.get(v.getId());
//                if (type != null) {
//                    if (item.getType() == PullRequestTimelineModel.HEADER) {
//                        getView().showReactionsPopup(type, login, repoId, item.getPullRequest().getNumber(), ReactionsProvider.HEADER);
//                    } else {
//                        getView().showReactionsPopup(type, login, repoId, item.getComment().getId(), ReactionsProvider.COMMENT);
//                    }
//                } else {
//                    onItemClick(position, v, item);
//                }
//            }
//        } else {
//            onItemClick(position, v, item);
//        }
    }

    @NonNull @Override public ArrayList<PullRequestTimelineModel> getEvents() {
        return timeline;
    }

    @Override public void onWorkOffline() {
        //TODO
    }

    @Override public void onHandleDeletion(@Nullable Bundle bundle) {
        if (getView() == null || getView().getPullRequest() == null) return;
        if (bundle != null) {
            PullRequest pullRequest = getView().getPullRequest();
            String login = pullRequest.getLogin();
            String repoId = pullRequest.getRepoId();
            long commId = bundle.getLong(BundleConstant.EXTRA, 0);
            boolean isReviewComment = bundle.getBoolean(BundleConstant.YES_NO_EXTRA);
            if (commId != 0 && !isReviewComment) {
                makeRestCall(RestProvider.getIssueService(isEnterprise()).deleteIssueComment(login, repoId, commId),
                        booleanResponse -> sendToView(view -> {
                            if (booleanResponse.code() == 204) {
                                Comment comment = new Comment();
                                comment.setId(commId);
//                                view.onRemove(PullRequestTimelineModel.constructComment(comment));
                            } else {
                                view.showMessage(R.string.error, R.string.error_deleting_comment);
                            }
                        }));
            } else {
                int groupPosition = bundle.getInt(BundleConstant.EXTRA_TWO);
                int commentPosition = bundle.getInt(BundleConstant.EXTRA_THREE);
                makeRestCall(RestProvider.getReviewService(isEnterprise()).deleteComment(login, repoId, commId),
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

    @Override public void onHandleReaction(int vId, long idOrNumber, @ReactionsProvider.ReactionType int reactionType) {
        if (getView() == null || getView().getPullRequest() == null) return;
        PullRequest pullRequest = getView().getPullRequest();
        String login = pullRequest.getLogin();
        String repoId = pullRequest.getRepoId();
        Observable observable = getReactionsProvider().onHandleReaction(vId, idOrNumber, login, repoId, reactionType, isEnterprise());
        if (observable != null) //noinspection unchecked
            manageObservable(observable);
    }

    @Override public boolean isMerged(PullRequest pullRequest) {
        return pullRequest != null && (pullRequest.isMerged() || !InputHelper.isEmpty(pullRequest.getMergedAt()));
    }

    @Override public boolean isCallingApi(long id, int vId) {
        return getReactionsProvider().isCallingApi(id, vId);
    }

    @Override public void onHandleComment(@NonNull String text, @Nullable Bundle bundle) {
        if (getView() == null) return;
        PullRequest pullRequest = getView().getPullRequest();
        if (pullRequest != null) {
            if (bundle == null) {
                CommentRequestModel commentRequestModel = new CommentRequestModel();
                commentRequestModel.setBody(text);
                makeRestCall(RestProvider.getIssueService(isEnterprise()).createIssueComment(pullRequest.getLogin(), pullRequest.getRepoId(),
                        pullRequest.getNumber(), commentRequestModel), comment -> {
                });
            } else {

            }
        }
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
        if (getView() == null || getView().getPullRequest() == null) return;
        if (v.getId() == R.id.commentMenu) {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.inflate(R.menu.comments_menu);
            String username = Login.getUser().getLogin();
            boolean isOwner = CommentsHelper.isOwner(username, getView().getPullRequest().getLogin(), comment.getUser().getLogin());
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
        if (getView() == null || getView().getPullRequest() == null) return;
        PullRequest pullRequest = getView().getPullRequest();
        String login = pullRequest.getLogin();
        String repoId = pullRequest.getRepoId();
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

    @Override public boolean onCallApi(int page, @Nullable PullRequest parameter) {
        if (parameter == null) {
            sendToView(BaseMvp.FAView::hideProgress);
            return false;
        }
        String login = parameter.getLogin();
        String repoId = parameter.getRepoId();
        int number = parameter.getNumber();
        if (page <= 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
            pages.clear();
        }
        if (page > lastPage || lastPage == 0) {
            sendToView(PullRequestTimelineMvp.View::hideProgress);
            return false;
        }
        setCurrentPage(page);
        if (parameter.getHead() != null) {
            loadEverything(login, repoId, number, parameter.getHead().getSha(), parameter.isMergeable(), page);
            return true;
        }
        return false;
    }

    @Nullable private Observable<PullRequestTimelineModel> getTimelineObservable(Response<PullRequestTimelineQuery.Data> response,
                                                                                 boolean isMergeable) {
        if (!response.hasErrors()) {
            PullRequestTimelineQuery.Data data = response.data();
            if (data != null) {
                PullRequestTimelineQuery.Repository repo = data.repository();
                PullRequestTimelineQuery.PullRequest pullRequest = repo != null ? repo.pullRequest() : null;
                if (pullRequest != null) {
                    PullRequestTimelineQuery.Timeline timeline = pullRequest.timeline();
                    lastPage = timeline.pageInfo().hasNextPage() ? Integer.MAX_VALUE : 0;
                    pages.clear();
                    ArrayList<PullRequestTimelineModel> models = new ArrayList<>();
                    PullRequestTimelineQuery.PullRequestCommits pullRequestCommits = pullRequest.pullRequestCommits();
                    List<PullRequestTimelineQuery.PullRequestCommit> commits = pullRequestCommits.pullRequestCommit();
                    if (commits != null && !commits.isEmpty() && page <= 1) {
                        PullRequestTimelineQuery.Status status = commits.get(0).commit().status();
                        if (status != null) {
                            models.add(new PullRequestTimelineModel(status, isMergeable));
                        }
                    }
                    List<PullRequestTimelineQuery.Edge> edges = timeline.edges();
                    if (edges != null) {
                        Stream.of(edges).forEachIndexed((i, edge) -> pages.append(i, edge.cursor()));
                    }
                    List<PullRequestTimelineQuery.Node> nodes = timeline.nodes();
                    if (nodes != null) {
                        for (PullRequestTimelineQuery.Node node : nodes) {
                            models.add(new PullRequestTimelineModel(node));
                        }
                    }
                    return RxHelper.getObservable(Observable.fromIterable(models));
                }
            }
        }
        return null;
    }

    @NonNull private PullRequestTimelineQuery getTimelineBuilder(@NonNull String login, @NonNull String repoId, int number, int page) {
        return PullRequestTimelineQuery.builder()
                .owner(login)
                .name(repoId)
                .number(number)
                .page(getPage())
                .build();
    }

    @Nullable private String getPage() {
        return pages.size() != 0 ? pages.valueAt(pages.size() - 1) : "";
    }

    private void loadEverything(@NonNull String login, @NonNull String repoId, int number,
                                @NonNull String sha, boolean isMergeable, int page) {
        PullRequestTimelineQuery query = getTimelineBuilder(login, repoId, number, page);
        ApolloCall<PullRequestTimelineQuery.Data> apolloCall = App.getInstance().getApolloClient().query(query);
        Observable<PullRequestTimelineModel> observable = Rx2Apollo.from(apolloCall)
                .flatMap(response -> {
                    Observable<PullRequestTimelineModel> models = getTimelineObservable(response, isMergeable);
                    return models != null ? models : RxHelper.getObservable(Observable.fromIterable(new ArrayList<>()));
                });
        makeRestCall(observable.toList().toObservable(),
                pullRequestTimelineModels -> sendToView(view -> view.onNotifyAdapter(pullRequestTimelineModels, page)));
    }
}

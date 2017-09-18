package com.fastaccess.ui.modules.repos.code.commit.details.comments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.PopupMenu;

import com.fastaccess.R;
import com.fastaccess.data.dao.CommentRequestModel;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.timeline.CommentsHelper;
import com.fastaccess.provider.timeline.ReactionsProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

class CommitCommentsPresenter extends BasePresenter<CommitCommentsMvp.View> implements CommitCommentsMvp.Presenter {
    private ArrayList<TimelineModel> comments = new ArrayList<>();
    private ReactionsProvider reactionsProvider;
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;
    @com.evernote.android.state.State String repoId;
    @com.evernote.android.state.State String login;
    @com.evernote.android.state.State String sha;
    @com.evernote.android.state.State boolean isCollaborator;


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

    @Override public boolean onCallApi(int page, @Nullable String parameter) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0) {
            sendToView(CommitCommentsMvp.View::hideProgress);
            return false;
        }
        if (page == 1) {
            manageObservable(RestProvider.getRepoService(isEnterprise()).isCollaborator(login, repoId,
                    Login.getUser().getLogin())
                    .doOnNext(booleanResponse -> isCollaborator = booleanResponse.code() == 204));
        }
        setCurrentPage(page);
        makeRestCall(RestProvider.getRepoService(isEnterprise()).getCommitComments(login, repoId, sha, page)
                        .flatMap(listResponse -> {
                            lastPage = listResponse.getLast();
                            return TimelineModel.construct(listResponse.getItems());
                        })
                        .doOnComplete(() -> {
                            if (lastPage <= 1) {
                                sendToView(CommitCommentsMvp.View::showReload);
                            }
                        }),
                listResponse -> sendToView(view -> view.onNotifyAdapter(listResponse, page)));
        return true;
    }

    @Override public void onFragmentCreated(@Nullable Bundle bundle) {
        if (bundle == null) throw new NullPointerException("Bundle is null?");
        repoId = bundle.getString(BundleConstant.ID);
        login = bundle.getString(BundleConstant.EXTRA);
        sha = bundle.getString(BundleConstant.EXTRA_TWO);
    }

    @NonNull @Override public ArrayList<TimelineModel> getComments() {
        return comments;
    }

    @Override public void onHandleDeletion(@Nullable Bundle bundle) {
        if (bundle != null) {
            long commId = bundle.getLong(BundleConstant.EXTRA, 0);
            if (commId != 0) {
                makeRestCall(RestProvider.getRepoService(isEnterprise()).deleteComment(login, repoId, commId)
                        , booleanResponse -> sendToView(view -> {
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

    @Override public void onWorkOffline() {
        if (comments.isEmpty()) {
            manageDisposable(RxHelper.getObservable(Comment.getCommitComments(repoId(), login(), sha).toObservable())
                    .flatMap(TimelineModel::construct)
                    .subscribe(models -> sendToView(view -> view.onNotifyAdapter(models, 1))));
        } else {
            sendToView(CommitCommentsMvp.View::hideProgress);
        }
    }

    @NonNull @Override public String repoId() {
        return repoId;
    }

    @NonNull @Override public String login() {
        return login;
    }

    @Override public String sha() {
        return sha;
    }

    @Override public boolean isPreviouslyReacted(long commentId, int vId) {
        return getReactionsProvider().isPreviouslyReacted(commentId, vId);
    }

    @Override public boolean isCallingApi(long id, int vId) {
        return getReactionsProvider().isCallingApi(id, vId);
    }

    @Override public void onHandleComment(@NonNull String text, @Nullable Bundle bundle) {
        CommentRequestModel model = new CommentRequestModel();
        model.setBody(text);
        manageDisposable(RxHelper.getObservable(RestProvider.getRepoService(isEnterprise()).postCommitComment(login, repoId, sha, model))
                .doOnSubscribe(disposable -> sendToView(view -> view.showBlockingProgress(0)))
                .subscribe(comment -> sendToView(view -> view.addComment(comment)),
                        throwable -> {
                            onError(throwable);
                            sendToView(CommitCommentsMvp.View::hideBlockingProgress);
                        }));
    }

    @Override public void onItemClick(int position, View v, TimelineModel timelineModel) {
        if (getView() != null) {
            Comment item = timelineModel.getComment();
            if (v.getId() == R.id.commentMenu) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.inflate(R.menu.comments_menu);
                String username = Login.getUser().getLogin();
                boolean isOwner = CommentsHelper.isOwner(username, login, item.getUser().getLogin()) || isCollaborator;
                popupMenu.getMenu().findItem(R.id.delete).setVisible(isOwner);
                popupMenu.getMenu().findItem(R.id.edit).setVisible(isOwner);
                popupMenu.setOnMenuItemClickListener(item1 -> {
                    if (getView() == null) return false;
                    if (item1.getItemId() == R.id.delete) {
                        getView().onShowDeleteMsg(item.getId());
                    } else if (item1.getItemId() == R.id.reply) {
                        getView().onReply(item.getUser(), item.getBody());
                    } else if (item1.getItemId() == R.id.edit) {
                        getView().onEditComment(item);
                    } else if (item1.getItemId() == R.id.share) {
                        ActivityHelper.shareUrl(v.getContext(), item.getHtmlUrl());
                    }
                    return true;
                });
                popupMenu.show();
            } else {
                onHandleReaction(v.getId(), item.getId());
            }
        }
    }

    @Override public void onItemLongClick(int position, View v, TimelineModel timelineModel) {
        if (v.getId() == R.id.commentMenu) {
            Comment item = timelineModel.getComment();
            if (getView() != null) getView().onReply(item.getUser(), item.getBody());
        } else {
            ReactionTypes reactionTypes = ReactionTypes.get(v.getId());
            if (reactionTypes != null) {
                if (getView() != null) getView().showReactionsPopup(reactionTypes, login, repoId, timelineModel.getComment().getId());
            } else {
                onItemClick(position, v, timelineModel);
            }
        }
    }

    @NonNull private ReactionsProvider getReactionsProvider() {
        if (reactionsProvider == null) {
            reactionsProvider = new ReactionsProvider();
        }
        return reactionsProvider;
    }

    private void onHandleReaction(int viewId, long id) {
        Observable observable = getReactionsProvider().onHandleReaction(viewId, id, login, repoId, ReactionsProvider.COMMIT, isEnterprise());
        if (observable != null) manageObservable(observable);
    }
}

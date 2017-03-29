package com.fastaccess.ui.modules.repos.code.commit.details.comments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.tasks.git.ReactionService;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

class CommitCommentsPresenter extends BasePresenter<CommitCommentsMvp.View> implements CommitCommentsMvp.Presenter {
    private ArrayList<Comment> comments = new ArrayList<>();
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;
    private String repoId;
    private String login;
    private String sha;


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

    @Override public void onCallApi(int page, @Nullable String parameter) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0) {
            sendToView(CommitCommentsMvp.View::hideProgress);
            return;
        }
        setCurrentPage(page);
        makeRestCall(RestProvider.getRepoService().getCommitComments(login, repoId, sha, page),
                listResponse -> {
                    lastPage = listResponse.getLast();
                    if (getCurrentPage() == 1) {
                        getComments().clear();
                    }
                    getComments().addAll(listResponse.getItems());
                    sendToView(CommitCommentsMvp.View::onNotifyAdapter);
                });
    }

    @Override public void onFragmentCreated(@Nullable Bundle bundle) {
        if (bundle == null) throw new NullPointerException("Bundle is null?");
        repoId = bundle.getString(BundleConstant.ID);
        login = bundle.getString(BundleConstant.EXTRA);
        sha = bundle.getString(BundleConstant.EXTRA_TWO);
    }

    @NonNull @Override public ArrayList<Comment> getComments() {
        return comments;
    }

    @Override public void onHandleDeletion(@Nullable Bundle bundle) {
        if (bundle != null) {
            long commId = bundle.getLong(BundleConstant.EXTRA, 0);
            if (commId != 0) {
                makeRestCall(RestProvider.getRepoService().deleteComment(login, repoId, commId)
                        , booleanResponse -> sendToView(view -> {
                            if (booleanResponse.code() == 204) {
                                Comment comment = new Comment();
                                comment.setId(commId);
                                getComments().remove(comment);
                                view.onNotifyAdapter();
                            } else {
                                view.showMessage(R.string.error, R.string.error_deleting_comment);
                            }
                        }));
            }
        }
    }

    @Override public void onWorkOffline() {
        if (comments.isEmpty()) {
            manageSubscription(RxHelper.getObserver(Comment.getCommitComments(repoId(), login(), sha))
                    .subscribe(models -> {
                        if (models != null) {
                            comments.addAll(models);
                            sendToView(CommitCommentsMvp.View::onNotifyAdapter);
                        }
                    }));
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

    @Override public void onItemClick(int position, View v, Comment item) {
        Login login = Login.getUser();
        if (getView() != null) {
            if (v.getId() == R.id.delete) {
                if (login != null && item.getUser().getLogin().equals(login.getLogin())) {
                    if (getView() != null) getView().onShowDeleteMsg(item.getId());
                }
            } else if (v.getId() == R.id.reply) {
                getView().onTagUser(item.getUser());
            } else if (v.getId() == R.id.edit) {
                if (login != null && item.getUser().getLogin().equals(login.getLogin())) {
                    getView().onEditComment(item);
                }
            } else {
                handleReactions(v.getId(), item.getId(), v.getContext());
            }
        }
    }

    private void handleReactions(@IdRes int id, long commentId, @NonNull Context context) {
        ReactionTypes type = null;
        switch (id) {
            case R.id.heart:
                type = ReactionTypes.HEART;
                break;
            case R.id.sad:
                type = ReactionTypes.CONFUSED;
                break;
            case R.id.thumbsDown:
                type = ReactionTypes.MINUS_ONE;
                break;
            case R.id.thumbsUp:
                type = ReactionTypes.PLUS_ONE;
                break;
            case R.id.laugh:
                type = ReactionTypes.LAUGH;
                break;
            case R.id.hurray:
                type = ReactionTypes.HOORAY;
                break;
        }
        if (type != null) {
            ReactionService.start(context, login, repoId, commentId, type, true);
        }
    }

    @Override public void onItemLongClick(int position, View v, Comment item) {
        onItemClick(position, v, item);
    }
}

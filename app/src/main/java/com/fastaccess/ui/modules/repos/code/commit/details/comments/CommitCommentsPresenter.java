package com.fastaccess.ui.modules.repos.code.commit.details.comments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.fastaccess.data.dao.CommentsModel;
import com.fastaccess.data.dao.LoginModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.adapter.CommentsAdapter;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import java.util.ArrayList;

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

class CommitCommentsPresenter extends BasePresenter<CommitCommentsMvp.View> implements CommitCommentsMvp.Presenter {
    private ArrayList<CommentsModel> comments = new ArrayList<>();
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

    @NonNull @Override public ArrayList<CommentsModel> getComments() {
        return comments;
    }

    @Override public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data,
                                           @NonNull DynamicRecyclerView recycler,
                                           @NonNull CommentsAdapter adapter) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == BundleConstant.REQUEST_CODE) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    boolean isNew = bundle.getBoolean(BundleConstant.EXTRA);
                    CommentsModel commentsModel = bundle.getParcelable(BundleConstant.ITEM);
                    if (isNew) {
                        adapter.addItem(commentsModel);
                        recycler.smoothScrollToPosition(adapter.getItemCount());
                    } else {
                        int position = adapter.getItem(commentsModel);
                        if (position != -1) {
                            adapter.swapItem(commentsModel, position);
                            recycler.smoothScrollToPosition(position);
                        } else {
                            adapter.addItem(commentsModel);
                            recycler.smoothScrollToPosition(adapter.getItemCount());
                        }
                    }
                }
            }
        }
    }

    @Override public void onHandleDeletion(@Nullable Bundle bundle) {
        if (bundle != null) {
            long commId = bundle.getLong(BundleConstant.EXTRA, 0);
            if (commId != 0) {
                makeRestCall(RestProvider.getRepoService().deleteComment(login, repoId, commId)
                        , booleanResponse -> sendToView(view -> view.onHandleCommentDelete(booleanResponse, commId)));
            }
        }
    }

    @Override public void onWorkOffline() {
        if (comments.isEmpty()) {
            manageSubscription(CommentsModel.getCommitComments(repoId(), login(), sha)
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

    @Override public void onItemClick(int position, View v, CommentsModel item) {
        if (getView() != null) {
            if (item.getUser() != null) {
                LoginModel login = LoginModel.getUser();
                if (login != null && item.getUser().getLogin().equals(login.getLogin())) {
                    getView().onEditComment(item);
                } else {
                    getView().onTagUser(item.getUser());
                }
            } else {
                getView().onTagUser(item.getUser());
            }
        }
    }

    @Override public void onItemLongClick(int position, View v, CommentsModel item) {
        if (item.getUser() != null && TextUtils.equals(item.getUser().getLogin(), LoginModel.getUser().getLogin())) {
            if (getView() != null) getView().onShowDeleteMsg(item.getId());
        } else {
            onItemClick(position, v, item);
        }
    }
}

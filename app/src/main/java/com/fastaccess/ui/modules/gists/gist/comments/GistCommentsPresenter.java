package com.fastaccess.ui.modules.gists.gist.comments;

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
import com.fastaccess.helper.Logger;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.adapter.CommentsAdapter;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

class GistCommentsPresenter extends BasePresenter<GistCommentsMvp.View> implements GistCommentsMvp.Presenter {
    private ArrayList<CommentsModel> comments = new ArrayList<>();
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;

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

    @Override public <T> T onError(@NonNull Throwable throwable, @NonNull Observable<T> observable) {
        //noinspection ConstantConditions
        sendToView(view -> onWorkOffline(view.getLoadMore().getParameter()));
        return super.onError(throwable, observable);
    }

    @Override public void onCallApi(int page, @Nullable String parameter) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || parameter == null || lastPage == 0) {
            sendToView(GistCommentsMvp.View::hideProgress);
            return;
        }
        setCurrentPage(page);
        makeRestCall(RestProvider.getGistService().getGistComments(parameter, page),
                listResponse -> {
                    lastPage = listResponse.getLast();
                    if (getCurrentPage() == 1) {
                        getComments().clear();
                        manageSubscription(CommentsModel.saveForGist(listResponse.getItems(), parameter).subscribe());
                    }
                    getComments().addAll(listResponse.getItems());
                    sendToView(GistCommentsMvp.View::onNotifyAdapter);
                });
    }

    @NonNull @Override public ArrayList<CommentsModel> getComments() {
        return comments;
    }

    @Override public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data,
                                           @NonNull DynamicRecyclerView recycler, @NonNull CommentsAdapter adapter) {
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
            String gistId = bundle.getString(BundleConstant.ID);
            if (commId != 0 && gistId != null) {
                makeRestCall(RestProvider.getGistService().deleteGistComment(gistId, commId),
                        booleanResponse -> sendToView(view -> view.onHandleCommentDelete(booleanResponse, commId)));
            }
        }
    }

    @Override public void onWorkOffline(@NonNull String gistId) {
        if (comments.isEmpty()) {
            manageSubscription(CommentsModel.getGistComments(gistId).subscribe(
                    localComments -> {
                        if (localComments != null && !localComments.isEmpty()) {
                            Logger.e(localComments.size());
                            comments.addAll(localComments);
                            sendToView(GistCommentsMvp.View::onNotifyAdapter);
                        }
                    }
            ));
        } else {
            sendToView(BaseMvp.FAView::hideProgress);
        }
    }

    @Override public void onItemClick(int position, View v, CommentsModel item) {
        if (item.getUser() != null) {
            LoginModel userModel = LoginModel.getUser();
            if (userModel != null && item.getUser().getLogin().equals(userModel.getLogin())) {
                if (getView() != null) getView().onEditComment(item);
            } else {
                if (getView() != null) getView().onTagUser(item.getUser());
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

package com.fastaccess.ui.modules.gists.gist.comments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

class GistCommentsPresenter extends BasePresenter<GistCommentsMvp.View> implements GistCommentsMvp.Presenter {
    private ArrayList<Comment> comments = new ArrayList<>();
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

    @Override public void onError(@NonNull Throwable throwable) {
        //noinspection ConstantConditions
        sendToView(view -> onWorkOffline(view.getLoadMore().getParameter()));
        super.onError(throwable);
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
                        manageSubscription(Comment.saveForGist(listResponse.getItems(), parameter).subscribe());
                    }
                    getComments().addAll(listResponse.getItems());
                    sendToView(GistCommentsMvp.View::onNotifyAdapter);
                });
    }

    @NonNull @Override public ArrayList<Comment> getComments() {
        return comments;
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
            manageSubscription(RxHelper.getObserver(Comment.getGistComments(gistId)).subscribe(
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

    @Override public void onItemClick(int position, View v, Comment item) {
        if (item.getUser() != null) {
            Login userModel = Login.getUser();
            if (userModel != null && item.getUser().getLogin().equals(userModel.getLogin())) {
                if (getView() != null) getView().onEditComment(item);
            } else {
                if (getView() != null) getView().onTagUser(item.getUser());
            }
        }
    }

    @Override public void onItemLongClick(int position, View v, Comment item) {
        if (item.getUser() != null && TextUtils.equals(item.getUser().getLogin(), Login.getUser().getLogin())) {
            if (getView() != null) getView().onShowDeleteMsg(item.getId());
        } else {
            onItemClick(position, v, item);
        }
    }
}

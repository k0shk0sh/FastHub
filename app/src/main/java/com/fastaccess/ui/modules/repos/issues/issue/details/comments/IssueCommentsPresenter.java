package com.fastaccess.ui.modules.repos.issues.issue.details.comments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.fastaccess.data.dao.CommentsModel;
import com.fastaccess.data.dao.LoginModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

class IssueCommentsPresenter extends BasePresenter<IssueCommentsMvp.View> implements IssueCommentsMvp.Presenter {
    private ArrayList<CommentsModel> comments = new ArrayList<>();
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;
    private String repoId;
    private String login;
    private int number;


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

        return super.onError(throwable, observable);
    }

    @Override public void onCallApi(int page, @Nullable String parameter) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0) {
            sendToView(IssueCommentsMvp.View::hideProgress);
            return;
        }
        setCurrentPage(page);
        makeRestCall(RestProvider.getIssueService().getIssueComments(login, repoId, number, page),
                listResponse -> {
                    lastPage = listResponse.getLast();
                    if (getCurrentPage() == 1) {
                        getComments().clear();
                        manageSubscription(CommentsModel.saveForIssues(listResponse.getItems(), repoId(), login(),
                                String.valueOf(number)).subscribe());
                    }
                    getComments().addAll(listResponse.getItems());
                    sendToView(IssueCommentsMvp.View::onNotifyAdapter);
                });
    }

    @Override public void onFragmentCreated(@Nullable Bundle bundle) {
        if (bundle == null) throw new NullPointerException("Bundle is null?");
        repoId = bundle.getString(BundleConstant.ID);
        login = bundle.getString(BundleConstant.EXTRA);
        number = bundle.getInt(BundleConstant.EXTRA_TWO);
    }

    @NonNull @Override public ArrayList<CommentsModel> getComments() {
        return comments;
    }

    @Override public void onWorkOffline() {
        if (comments.isEmpty()) {
            manageSubscription(RxHelper.getObserver(CommentsModel.getIssueComments(repoId(), login(), String.valueOf(number)))
                    .subscribe(models -> {
                        if (models != null) {
                            comments.addAll(models);
                            sendToView(IssueCommentsMvp.View::onNotifyAdapter);
                        }
                    }));
        } else {
            sendToView(BaseMvp.FAView::hideProgress);
        }
    }

    @Override public void onHandleDeletion(@Nullable Bundle bundle) {
        if (bundle != null) {
            long commId = bundle.getLong(BundleConstant.EXTRA, 0);
            if (commId != 0) {
                makeRestCall(RestProvider.getIssueService().deleteIssueComment(login, repoId, commId),
                        booleanResponse -> sendToView(view -> view.onHandleCommentDelete(booleanResponse, commId)));
            }
        }
    }

    @NonNull @Override public String repoId() {
        return repoId;
    }

    @NonNull @Override public String login() {
        return login;
    }

    @Override public int number() {
        return number;
    }

    @Override public void onItemClick(int position, View v, CommentsModel item) {
        if (getView() != null) {
            if (item.getUser() != null) {
                LoginModel userModel = LoginModel.getUser();
                if (userModel != null && item.getUser().getLogin().equals(userModel.getLogin())) {
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

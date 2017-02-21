package com.fastaccess.ui.modules.repos.issues.issue.details;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.IssueModel;
import com.fastaccess.data.dao.IssueRequestModel;
import com.fastaccess.data.dao.LoginModel;
import com.fastaccess.data.dao.PullsIssuesParser;
import com.fastaccess.data.dao.UserModel;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.data.service.IssueService;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import retrofit2.Response;
import rx.Observable;

/**
 * Created by Kosh on 10 Dec 2016, 9:23 AM
 */

class IssuePagerPresenter extends BasePresenter<IssuePagerMvp.View> implements IssuePagerMvp.Presenter {
    private IssueModel issueModel;
    private int issueNumber;
    private String login;
    private String repoId;

    @Nullable @Override public IssueModel getIssue() {
        return issueModel;
    }

    @Override public <T> T onError(@NonNull Throwable throwable, @NonNull Observable<T> observable) {
        onWorkOffline(issueNumber, login, repoId);
        return super.onError(throwable, observable);
    }

    @Override public void onActivityCreated(@Nullable Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            issueModel = intent.getExtras().getParcelable(BundleConstant.ITEM);
            issueNumber = intent.getExtras().getInt(BundleConstant.ID);
            login = intent.getExtras().getString(BundleConstant.EXTRA);
            repoId = intent.getExtras().getString(BundleConstant.EXTRA_TWO);
            if (issueModel != null) {
                issueNumber = issueModel.getNumber();
                sendToView(IssuePagerMvp.View::onSetupIssue);
                return;
            } else if (issueNumber > 0 && !InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
                makeRestCall(RestProvider.getIssueService().getIssue(login, repoId, issueNumber),
                        issue -> {
                            issueModel = issue;
                            issueModel.setRepoId(repoId);
                            issueModel.setLogin(login);
                            sendToView(IssuePagerMvp.View::onSetupIssue);
                        });
                return;
            }
        }
        sendToView(IssuePagerMvp.View::onSetupIssue);
    }

    @Override public void onWorkOffline(long issueNumber, @NonNull String repoId, @NonNull String login) {
        if (issueModel == null) {
            manageSubscription(IssueModel.getIssueByNumber((int) issueNumber).subscribe(issueModel1 -> {
                if (issueModel1 != null) {
                    issueModel = issueModel1;
                    sendToView(IssuePagerMvp.View::onSetupIssue);
                }
            }));
        } else {
            sendToView(BaseMvp.FAView::hideProgress);
        }
    }

    @Override public boolean isOwner() {
        if (getIssue() == null) return false;
        UserModel userModel = getIssue() != null ? getIssue().getUser() : null;
        LoginModel me = LoginModel.getUser();
        PullsIssuesParser parser = PullsIssuesParser.getForIssue(getIssue().getHtmlUrl());
        return userModel != null && userModel.getLogin().equalsIgnoreCase(me.getLogin())
                || (parser != null && parser.getLogin().equalsIgnoreCase(me.getLogin()));
    }

    @Override public boolean isLocked() {
        return getIssue() != null && getIssue().isLocked();
    }

    @Override public void onHandleConfirmDialog(@Nullable Bundle bundle) {
        if (bundle != null) {
            boolean proceedCloseIssue = bundle.getBoolean(BundleConstant.EXTRA);
            boolean proceedLockUnlock = bundle.getBoolean(BundleConstant.EXTRA_TWO);
            if (proceedCloseIssue) {
                onOpenCloseIssue();
            } else if (proceedLockUnlock) {
                onLockUnlockIssue();
            }
        }
    }

    @Override public void onOpenCloseIssue() {
        IssueModel currentIssue = getIssue();
        if (currentIssue != null) {
            IssueRequestModel requestModel = IssueRequestModel.clone(currentIssue);
            manageSubscription(RxHelper.getObserver(RestProvider.getIssueService().editIssue(currentIssue.getUser().getLogin(),
                    currentIssue.getRepoId(), currentIssue.getNumber(), requestModel))
                    .doOnSubscribe(() -> sendToView(view -> view.showProgress(0)))
                    .doOnNext(issue -> {
                        if (issue != null) {
                            sendToView(view -> view.showSuccessIssueActionMsg(currentIssue.getState() == IssueState.open));
                            issue.setRepoId(issueModel.getRepoId());
                            issue.setLogin(issueModel.getLogin());
                            issueModel = issue;
                            sendToView(IssuePagerMvp.View::onSetupIssue);
                        }
                    })
                    .onErrorReturn(throwable -> {
                        sendToView(view -> view.showErrorIssueActionMsg(currentIssue.getState() == IssueState.open));
                        return null;
                    })
                    .subscribe());
        }
    }

    @Override public void onLockUnlockIssue() {
        IssueModel currentIssue = getIssue();
        if (currentIssue == null) return;
        String login = currentIssue.getUser().getLogin();
        String repoId = currentIssue.getRepoId();
        int number = currentIssue.getNumber();
        IssueService issueService = RestProvider.getIssueService();
        Observable<Response<Boolean>> observable = RxHelper
                .getObserver(isLocked() ? issueService.unlockIssue(login, repoId, number) : issueService.lockIssue(login, repoId, number));
        makeRestCall(observable, booleanResponse -> {
            int code = booleanResponse.code();
            if (code == 204) {
                issueModel.setLocked(!isLocked());
                sendToView(IssuePagerMvp.View::onSetupIssue);
            }
            sendToView(IssuePagerMvp.View::hideProgress);
        });

    }
}

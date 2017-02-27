package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.LoginModel;
import com.fastaccess.data.dao.MergeRequestModel;
import com.fastaccess.data.dao.PullRequestModel;
import com.fastaccess.data.service.IssueService;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.widgets.SpannableBuilder;

import java.util.ArrayList;

import retrofit2.Response;
import rx.Observable;

/**
 * Created by Kosh on 10 Dec 2016, 9:23 AM
 */

class PullRequestPagerPresenter extends BasePresenter<PullRequestPagerMvp.View> implements PullRequestPagerMvp.Presenter {
    private PullRequestModel pullRequest;
    private int issueNumber;
    private String login;
    private String repoId;

    @Nullable @Override public PullRequestModel getPullRequest() {
        return pullRequest;
    }

    @Override public <T> T onError(@NonNull Throwable throwable, @NonNull Observable<T> observable) {
        onWorkOffline();
        return super.onError(throwable, observable);
    }

    @Override public void onActivityCreated(@Nullable Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            issueNumber = intent.getExtras().getInt(BundleConstant.ID);
            login = intent.getExtras().getString(BundleConstant.EXTRA);
            repoId = intent.getExtras().getString(BundleConstant.EXTRA_TWO);
            if (pullRequest != null) {
                sendToView(PullRequestPagerMvp.View::onSetupIssue);
                return;
            } else if (issueNumber > 0 && !InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
                makeRestCall(RestProvider.getPullRequestSerice().getPullRequest(login, repoId, issueNumber)
                        , pullRequestModelResponse -> {
                            pullRequest = pullRequestModelResponse;
                            pullRequest.setRepoId(repoId);
                            pullRequest.setLogin(login);
                            sendToView(PullRequestPagerMvp.View::onSetupIssue);
                            manageSubscription(pullRequest.save().subscribe());
                        });
                return;
            }
        }
        sendToView(PullRequestPagerMvp.View::onSetupIssue);
    }

    @Override public void onWorkOffline() {
        if (pullRequest == null) {
            manageSubscription(PullRequestModel.getPullRequest(issueNumber, repoId, login)
                    .subscribe(pullRequestModel -> {
                        if (pullRequestModel != null) {
                            pullRequest = pullRequestModel;
                            sendToView(PullRequestPagerMvp.View::onSetupIssue);
                        }
                    }));
        }
    }

    @Override public boolean isOwner() {
        boolean isOwner;
        PullRequestModel pullRequestModel = getPullRequest();
        if (pullRequestModel == null) return false;
        if (pullRequestModel.getAssignee() != null) {
            isOwner = pullRequestModel.getAssignee().getLogin().equals(LoginModel.getUser().getLogin());
        } else {
            isOwner = pullRequestModel.getBase() != null &&
                    getPullRequest().getBase().getUser().getLogin().equals(LoginModel.getUser().getLogin());
        }
        return isOwner;
    }

    @Override public boolean isRepoOwner() {
        if (getPullRequest() == null) return false;
        LoginModel me = LoginModel.getUser();
        return TextUtils.equals(login, me.getLogin());
    }

    @Override public boolean isLocked() {
        return getPullRequest() != null && getPullRequest().isLocked();
    }

    @Override public boolean isMergeable() {
        return getPullRequest() != null && getPullRequest().isMergeable() && !getPullRequest().isMerged();
    }

    @Override public void onHandleConfirmDialog(@Nullable Bundle bundle) {
        if (bundle != null) {
            boolean proceedLockUnlock = bundle.getBoolean(BundleConstant.EXTRA);
            if (proceedLockUnlock) {
                onLockUnlockConversations();
            }
        }
    }

    @Override public void onLockUnlockConversations() {
        PullRequestModel currentPullRequest = getPullRequest();
        if (currentPullRequest == null) return;
        IssueService service = RestProvider.getIssueService();
        Observable<Response<Boolean>> observable = RxHelper
                .getObserver(isLocked() ? service.unlockIssue(login, repoId, issueNumber) :
                             service.lockIssue(login, repoId, issueNumber));
        makeRestCall(observable, booleanResponse -> {
            int code = booleanResponse.code();
            if (code == 204) {
                pullRequest.setLocked(!isLocked());
                sendToView(PullRequestPagerMvp.View::onSetupIssue);
            }
        });
    }

    @NonNull @Override public SpannableBuilder getMergeBy(@NonNull PullRequestModel pullRequest, @NonNull Context context) {
        return PullRequestModel.getMergeBy(pullRequest, context);
    }

    @Override public void onLoadLabels() {
        manageSubscription(
                RxHelper.getObserver(RestProvider.getRepoService().getLabels(login, repoId))
                        .doOnSubscribe(this::onSubscribed)
                        .doOnNext(response -> {
                            if (response.getItems() != null && !response.getItems().isEmpty()) {
                                sendToView(view -> view.onLabelsRetrieved(response.getItems()));
                            } else {
                                sendToView(view -> view.showMessage(R.string.error, R.string.no_labels));
                            }
                        })
                        .onErrorReturn(throwable -> {
                            sendToView(view -> view.showMessage(R.string.error, R.string.no_labels));
                            return null;
                        })
                        .subscribe()
        );
    }

    @Override public void onPutLabels(@NonNull ArrayList<LabelModel> labels) {
        makeRestCall(RestProvider.getIssueService().putLabels(login, repoId, issueNumber,
                Stream.of(labels).filter(value -> value != null && value.getName() != null)
                        .map(LabelModel::getName).collect(Collectors.toList())),
                labelModels -> sendToView(view -> view.showMessage(R.string.success, R.string.labels_added_successfully)));
    }

    @Override public void onMerge() {
        if (isMergeable() && (isOwner() || isRepoOwner())) {//double the checking
            MergeRequestModel mergeRequestModel = new MergeRequestModel();
//            mergeRequestModel.setBase(String.valueOf(getPullRequest().getBase().getId()));
//            mergeRequestModel.setHead(String.valueOf(getPullRequest().getHead().getId()));
//            mergeRequestModel.setSha(getPullRequest().getBase().getSha());
//            mergeRequestModel.setCommitMessage("Hello World");
            manageSubscription(
                    RxHelper.getObserver(RestProvider.getPullRequestSerice().mergePullRequest(login, repoId, issueNumber, mergeRequestModel))
                            .doOnSubscribe(() -> sendToView(view -> view.showProgress(0)))
                            .doOnNext(mergeResponseModel -> {
                                if (mergeResponseModel.isMerged()) {
                                    sendToView(view -> view.showMessage(R.string.success, R.string.success_merge));
                                } else {
                                    sendToView(view -> view.showErrorMessage(mergeResponseModel.getMessage()));
                                }
                            })
                            .onErrorReturn(throwable -> {
                                sendToView(view -> view.showErrorMessage(throwable.getMessage()));
                                return null;
                            })
                            .subscribe()
            );
        }
    }
}

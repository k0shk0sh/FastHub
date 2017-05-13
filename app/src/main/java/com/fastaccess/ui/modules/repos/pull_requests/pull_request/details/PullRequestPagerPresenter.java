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
import com.fastaccess.data.dao.AssigneesRequestModel;
import com.fastaccess.data.dao.IssueRequestModel;
import com.fastaccess.data.dao.LabelListModel;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.MergeRequestModel;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.data.dao.PullsIssuesParser;
import com.fastaccess.data.dao.UsersListModel;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.types.IssueState;
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
    private PullRequest pullRequest;
    private int issueNumber;
    private String login;
    private String repoId;
    private boolean isCollaborator;
    private boolean showToRepoBtn;

    @Nullable @Override public PullRequest getPullRequest() {
        return pullRequest;
    }

    @Override public void onError(@NonNull Throwable throwable) {
        if (RestProvider.getErrorCode(throwable) == 404) {
            sendToView(PullRequestPagerMvp.View::onFinishActivity);
        } else {
            onWorkOffline();
        }
        super.onError(throwable);
    }

    @Override public void onActivityCreated(@Nullable Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            issueNumber = intent.getExtras().getInt(BundleConstant.ID);
            login = intent.getExtras().getString(BundleConstant.EXTRA);
            repoId = intent.getExtras().getString(BundleConstant.EXTRA_TWO);
            showToRepoBtn = intent.getExtras().getBoolean(BundleConstant.EXTRA_THREE);
            if (pullRequest != null) {
                sendToView(PullRequestPagerMvp.View::onSetupIssue);
                return;
            } else if (issueNumber > 0 && !InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
                makeRestCall(RestProvider.getPullRequestService()
                        .getPullRequest(login, repoId, issueNumber)
                        .flatMap(pullRequest1 -> RestProvider.getRepoService().isCollaborator(login, repoId, Login.getUser().getLogin()),
                                (pullRequest1, booleanResponse) -> {
                                    isCollaborator = booleanResponse.code() == 204;
                                    return pullRequest1;
                                })
                        .flatMap(pullRequest1 -> RestProvider.getIssueService().getIssue(login, repoId, issueNumber),
                                (pullRequest1, issue) -> {//hack to get reactions from issue api
                                    if (issue != null) {
                                        pullRequest1.setReactions(issue.getReactions());
                                    }
                                    return pullRequest1;
                                }), pullRequestModelResponse -> {
                    pullRequest = pullRequestModelResponse;
                    pullRequest.setRepoId(repoId);
                    pullRequest.setLogin(login);
                    sendToView(PullRequestPagerMvp.View::onSetupIssue);
                    manageSubscription(pullRequest.save(pullRequest).subscribe());
                });
                return;
            }
        }
        sendToView(PullRequestPagerMvp.View::onSetupIssue);
    }

    @Override public void onWorkOffline() {
        if (pullRequest == null) {
            manageSubscription(PullRequest.getPullRequestByNumber(issueNumber, repoId, login)
                    .subscribe(pullRequestModel -> {
                        if (pullRequestModel != null) {
                            pullRequest = pullRequestModel;
                            sendToView(PullRequestPagerMvp.View::onSetupIssue);
                        }
                    }));
        }
    }

    @Override public boolean isOwner() {
        if (getPullRequest() == null) return false;
        User userModel = getPullRequest() != null ? getPullRequest().getUser() : null;
        Login me = Login.getUser();
        PullsIssuesParser parser = PullsIssuesParser.getForIssue(getPullRequest().getHtmlUrl());
        return (userModel != null && userModel.getLogin().equalsIgnoreCase(me.getLogin()))
                || (parser != null && parser.getLogin().equalsIgnoreCase(me.getLogin()));
    }

    @Override public boolean isRepoOwner() {
        if (getPullRequest() == null) return false;
        Login me = Login.getUser();
        return TextUtils.equals(login, me.getLogin());
    }

    @Override public boolean isLocked() {
        return getPullRequest() != null && getPullRequest().isLocked();
    }

    @Override public boolean isMergeable() {
        return getPullRequest() != null && getPullRequest().isMergeable() && !getPullRequest().isMerged();
    }

    @Override public boolean showToRepoBtn() {
        return showToRepoBtn;
    }

    @Override public void onHandleConfirmDialog(@Nullable Bundle bundle) {
        if (bundle != null) {
            boolean proceedCloseIssue = bundle.getBoolean(BundleConstant.EXTRA);
            boolean proceedLockUnlock = bundle.getBoolean(BundleConstant.EXTRA_TWO);
            if (proceedCloseIssue) {
                onOpenCloseIssue();
            } else if (proceedLockUnlock) {
                onLockUnlockConversations();
            }
        }
    }

    @Override public void onLockUnlockConversations() {
        PullRequest currentPullRequest = getPullRequest();
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

    @Override public void onOpenCloseIssue() {
        if (getPullRequest() != null) {
            IssueRequestModel requestModel = IssueRequestModel.clone(getPullRequest(), true);
            manageSubscription(RxHelper.getObserver(RestProvider.getPullRequestService().editPullRequest(login, repoId,
                    issueNumber, requestModel))
                    .doOnSubscribe(() -> sendToView(view -> view.showProgress(0)))
                    .doOnNext(issue -> {
                        if (issue != null) {
                            sendToView(view -> view.showSuccessIssueActionMsg(getPullRequest().getState() == IssueState.open));
                            issue.setRepoId(getPullRequest().getRepoId());
                            issue.setLogin(getPullRequest().getLogin());
                            pullRequest = issue;
                            sendToView(PullRequestPagerMvp.View::onSetupIssue);
                        }
                    })
                    .onErrorReturn(throwable -> {
                        sendToView(view -> view.showErrorIssueActionMsg(getPullRequest().getState() == IssueState.open));
                        return null;
                    })
                    .subscribe());
        }
    }

    @NonNull @Override public SpannableBuilder getMergeBy(@NonNull PullRequest pullRequest, @NonNull Context context) {
        return PullRequest.getMergeBy(pullRequest, context, false);
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
                labelModels -> {
                    sendToView(PullRequestPagerMvp.View::onUpdateTimeline);
                    LabelListModel listModel = new LabelListModel();
                    listModel.addAll(labels);
                    pullRequest.setLabels(listModel);
                    manageSubscription(pullRequest.save(pullRequest).subscribe());
                });
    }

    @Override public void onPutMilestones(@NonNull MilestoneModel milestone) {
        pullRequest.setMilestone(milestone);
        IssueRequestModel issueRequestModel = IssueRequestModel.clone(pullRequest, false);
        makeRestCall(RestProvider.getPullRequestService().editIssue(login, repoId, issueNumber, issueRequestModel),
                pr -> {
                    this.pullRequest = pr;
                    pullRequest.setLogin(login);
                    pullRequest.setRepoId(repoId);
                    manageSubscription(pr.save(pullRequest).subscribe());
                    sendToView(PullRequestPagerMvp.View::onUpdateTimeline);
                });

    }

    @Override public void onPutAssignees(@NonNull ArrayList<User> users, boolean isAssignees) {
        AssigneesRequestModel assigneesRequestModel = new AssigneesRequestModel();
        ArrayList<String> assignees = Stream.of(users)
                .map(User::getLogin)
                .collect(Collectors.toCollection(ArrayList::new));
        if (isAssignees) {
            assigneesRequestModel.setAssignees(assignees);
            makeRestCall(RestProvider.getPullRequestService().putAssignees(login, repoId, issueNumber, assigneesRequestModel),
                    pullRequestResponse -> {
                        this.pullRequest = pullRequestResponse;
                        pullRequest.setLogin(login);
                        pullRequest.setRepoId(repoId);
                        UsersListModel assignee = new UsersListModel();
                        assignee.addAll(users);
                        pullRequest.setAssignees(assignee);
                        manageSubscription(pullRequest.save(pullRequest).subscribe());
                        sendToView(PullRequestPagerMvp.View::onUpdateTimeline);
                    }
            );
        } else {
            assigneesRequestModel.setReviewers(assignees);
            makeRestCall(RestProvider.getPullRequestService().putReviewers(login, repoId, issueNumber, assigneesRequestModel),
                    pullRequestResponse -> sendToView(PullRequestPagerMvp.View::onUpdateTimeline)
            );
        }
    }

    @Override public void onMerge(@NonNull String msg) {
        if (isMergeable() && (isCollaborator() || isRepoOwner())) {//double the checking
            if (getPullRequest() == null || getPullRequest().getHead() == null || getPullRequest().getHead().getSha() == null) return;
            MergeRequestModel mergeRequestModel = new MergeRequestModel();
            mergeRequestModel.setSha(getPullRequest().getHead().getSha());
            mergeRequestModel.setCommitMessage(msg);
            manageSubscription(
                    RxHelper.getObserver(RestProvider.getPullRequestService().mergePullRequest(login, repoId, issueNumber, mergeRequestModel))
                            .doOnSubscribe(() -> sendToView(view -> view.showProgress(0)))
                            .doOnNext(mergeResponseModel -> {
                                if (mergeResponseModel.isMerged()) {
                                    sendToView(view -> {
                                        view.showMessage(R.string.success, R.string.success_merge);
                                        view.onUpdateTimeline();
                                    });
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

    @Override public String getLogin() {
        return login;
    }

    @Override public String getRepoId() {
        return repoId;
    }

    @Override public boolean isCollaborator() {
        return isCollaborator;
    }

    @Override public void onUpdatePullRequest(@NonNull PullRequest pullRequestModel) {
        this.pullRequest = pullRequestModel;
        this.pullRequest.setLogin(login);
        this.pullRequest.setRepoId(repoId);
        manageSubscription(pullRequest.save(pullRequest).subscribe());
        sendToView(PullRequestPagerMvp.View::onSetupIssue);
    }
}

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
import com.fastaccess.data.dao.CommentRequestModel;
import com.fastaccess.data.dao.IssueRequestModel;
import com.fastaccess.data.dao.LabelListModel;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.LockIssuePrModel;
import com.fastaccess.data.dao.MergeRequestModel;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.data.dao.NotificationSubscriptionBodyModel;
import com.fastaccess.data.dao.PullsIssuesParser;
import com.fastaccess.data.dao.UsersListModel;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.PinnedPullRequests;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.data.service.IssueService;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.widgets.SpannableBuilder;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.Response;

/**
 * Created by Kosh on 10 Dec 2016, 9:23 AM
 */

class PullRequestPagerPresenter extends BasePresenter<PullRequestPagerMvp.View> implements PullRequestPagerMvp.Presenter {
    @com.evernote.android.state.State PullRequest pullRequest;
    @com.evernote.android.state.State int issueNumber;
    @com.evernote.android.state.State String login;
    @com.evernote.android.state.State String repoId;
    @com.evernote.android.state.State boolean isCollaborator;
    @com.evernote.android.state.State boolean showToRepoBtn;
    @com.evernote.android.state.State ArrayList<CommentRequestModel> reviewComments = new ArrayList<>();

    @Nullable @Override public PullRequest getPullRequest() {
        return pullRequest;
    }

    @Override public void onError(@NonNull Throwable throwable) {
        if (RestProvider.getErrorCode(throwable) == 404) {
            sendToView(BaseMvp.FAView::onOpenUrlInBrowser);
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
                sendToView(view -> view.onSetupIssue(false));
                return;
            } else if (issueNumber > 0 && !InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
                callApi();
                return;
            }
        }
        sendToView(view -> view.onSetupIssue(false));
    }

    @Override public void onWorkOffline() {
        if (pullRequest == null) {
            manageDisposable(PullRequest.getPullRequestByNumber(issueNumber, repoId, login)
                    .subscribe(pullRequestModel -> {
                        if (pullRequestModel != null) {
                            pullRequest = pullRequestModel;
                            sendToView(view -> view.onSetupIssue(false));
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
                onLockUnlockConversations(null);
            }
        }
    }

    @Override public void onLockUnlockConversations(String reason) {
        PullRequest currentPullRequest = getPullRequest();
        if (currentPullRequest == null) return;
        IssueService service = RestProvider.getIssueService(isEnterprise());
        LockIssuePrModel model = null;
        if (!isLocked() && !InputHelper.isEmpty(reason)) {
            model = new LockIssuePrModel(true, reason);
        }
        Observable<Response<Boolean>> observable = RxHelper
                .getObservable(model == null ? service.unlockIssue(login, repoId, issueNumber) :
                               service.lockIssue(model, login, repoId, issueNumber));
        makeRestCall(observable, booleanResponse -> {
            int code = booleanResponse.code();
            if (code == 204) {
                pullRequest.setLocked(!isLocked());
                sendToView(view -> view.onSetupIssue(false));
            }
        });
    }

    @Override public void onOpenCloseIssue() {
        if (getPullRequest() != null) {
            IssueRequestModel requestModel = IssueRequestModel.clone(getPullRequest(), true);
            manageDisposable(RxHelper.getObservable(RestProvider.getPullRequestService(isEnterprise()).editPullRequest(login, repoId,
                    issueNumber, requestModel))
                    .doOnSubscribe(disposable -> sendToView(view -> view.showProgress(0)))
                    .subscribe(issue -> {
                        if (issue != null) {
                            sendToView(view -> view.showSuccessIssueActionMsg(getPullRequest().getState() == IssueState.open));
                            issue.setRepoId(getPullRequest().getRepoId());
                            issue.setLogin(getPullRequest().getLogin());
                            pullRequest = issue;
                            sendToView(view -> view.onSetupIssue(false));
                        }
                    }, throwable -> sendToView(view -> view.showErrorIssueActionMsg(getPullRequest().getState() == IssueState.open))));
        }
    }

    @NonNull @Override public SpannableBuilder getMergeBy(@NonNull PullRequest pullRequest, @NonNull Context context) {
        return PullRequest.getMergeBy(pullRequest, context, false);
    }

    @Override public void onPutLabels(@NonNull ArrayList<LabelModel> labels) {
        makeRestCall(RestProvider.getIssueService(isEnterprise()).putLabels(login, repoId, issueNumber,
                Stream.of(labels).filter(value -> value != null && value.getName() != null)
                        .map(LabelModel::getName).collect(Collectors.toList())),
                labelModels -> {
                    sendToView(view -> updateTimeline(view, R.string.labels_added_successfully));
                    LabelListModel listModel = new LabelListModel();
                    listModel.addAll(labels);
                    pullRequest.setLabels(listModel);
                    manageObservable(pullRequest.save(pullRequest).toObservable());
                });
    }

    @Override public void onPutMilestones(@NonNull MilestoneModel milestone) {
        pullRequest.setMilestone(milestone);
        IssueRequestModel issueRequestModel = IssueRequestModel.clone(pullRequest, false);
        makeRestCall(RestProvider.getPullRequestService(isEnterprise()).editIssue(login, repoId, issueNumber, issueRequestModel),
                pr -> {
                    this.pullRequest.setMilestone(pr.getMilestone());
                    manageObservable(pr.save(pullRequest).toObservable());
                    sendToView(view -> updateTimeline(view, R.string.labels_added_successfully));
                });

    }

    @Override public void onPutAssignees(@NonNull ArrayList<User> users, boolean isAssignees) {
        AssigneesRequestModel assigneesRequestModel = new AssigneesRequestModel();
        ArrayList<String> assignees = Stream.of(users)
                .map(User::getLogin)
                .collect(Collectors.toCollection(ArrayList::new));
        if (isAssignees) {
            assigneesRequestModel.setAssignees(assignees.isEmpty() ? Stream.of(pullRequest.getAssignees()).map(User::getLogin).toList() : assignees);
            makeRestCall(!assignees.isEmpty() ?
                         RestProvider.getIssueService(isEnterprise()).putAssignees(login, repoId, issueNumber, assigneesRequestModel) :
                         RestProvider.getIssueService(isEnterprise()).deleteAssignees(login, repoId, issueNumber, assigneesRequestModel),
                    pullRequestResponse -> {
                        UsersListModel usersListModel = new UsersListModel();
                        usersListModel.addAll(users);
                        this.pullRequest.setAssignees(usersListModel);
                        manageObservable(pullRequest.save(pullRequest).toObservable());
                        sendToView(view -> updateTimeline(view, R.string.assignee_added));
                    }
            );
        } else {
            assigneesRequestModel.setReviewers(assignees);
            makeRestCall(RestProvider.getPullRequestService(isEnterprise()).putReviewers(login, repoId, issueNumber, assigneesRequestModel),
                    pullRequestResponse -> sendToView(view -> updateTimeline(view, R.string.reviewer_added))
            );
        }
    }

    @Override public void onMerge(@NonNull String msg, @NonNull String mergeMethod) {
        if (isMergeable() && (isCollaborator() || isRepoOwner())) {//double the checking
            if (getPullRequest() == null || getPullRequest().getHead() == null || getPullRequest().getHead().getSha() == null) return;
            MergeRequestModel mergeRequestModel = new MergeRequestModel();
            mergeRequestModel.setSha(getPullRequest().getHead().getSha());
            mergeRequestModel.setCommitMessage(msg);
            mergeRequestModel.setMergeMethod(mergeMethod.toLowerCase());
            manageDisposable(RxHelper.getObservable(RestProvider.getPullRequestService(isEnterprise())
                    .mergePullRequest(login, repoId, issueNumber, mergeRequestModel))
                    .doOnSubscribe(disposable -> sendToView(view -> view.showProgress(0)))
                    .subscribe(mergeResponseModel -> {
                        if (mergeResponseModel.isMerged()) {
                            pullRequest.setMerged(true);
                            sendToView(view -> updateTimeline(view, R.string.success_merge));
                        } else {
                            sendToView(view -> view.showErrorMessage(mergeResponseModel.getMessage()));
                        }
                    }, throwable -> sendToView(view -> view.showErrorMessage(throwable.getMessage())))
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
        this.pullRequest.setTitle(pullRequestModel.getTitle());
        this.pullRequest.setBody(pullRequestModel.getBody());
        this.pullRequest.setBodyHtml(pullRequestModel.getBodyHtml());
        this.pullRequest.setLogin(login);
        this.pullRequest.setRepoId(repoId);
        manageObservable(pullRequest.save(pullRequest).toObservable());
        sendToView(view -> view.onSetupIssue(true));
    }

    @Override public void onRefresh() {
        callApi();
    }

    @Override public void onPinUnpinPullRequest() {
        if (getPullRequest() == null) return;
        PinnedPullRequests.pinUpin(getPullRequest());
        sendToView(PullRequestPagerMvp.View::onUpdateMenu);
    }

    @NonNull @Override public ArrayList<CommentRequestModel> getCommitComment() {
        return reviewComments;
    }

    @Override public void onAddComment(@NonNull CommentRequestModel comment) {
        int index = reviewComments.indexOf(comment);
        if (index != -1) {
            reviewComments.set(index, comment);
        } else {
            reviewComments.add(comment);
        }
    }

    @Override public boolean hasReviewComments() {
        return reviewComments.size() > 0;
    }

    @Override public void onSubscribeOrMute(boolean mute) {
        if (getPullRequest() == null) return;
        makeRestCall(mute ? RestProvider.getNotificationService(isEnterprise()).subscribe(getPullRequest().getId(),
                new NotificationSubscriptionBodyModel(false, true))
                          : RestProvider.getNotificationService(isEnterprise()).subscribe(getPullRequest().getId(),
                new NotificationSubscriptionBodyModel(true, false)),
                booleanResponse -> {
                    if (booleanResponse.code() == 204 || booleanResponse.code() == 200) {
                        sendToView(view -> view.showMessage(R.string.success, R.string.successfully_submitted));
                    } else {
                        sendToView(view -> view.showMessage(R.string.error, R.string.network_error));
                    }
                });
    }

    private void callApi() {
        Login loggedInUser = Login.getUser();
        if (loggedInUser == null) return;
        makeRestCall(RxHelper.getObservable(Observable.zip(RestProvider.getPullRequestService(isEnterprise())
                        .getPullRequest(login, repoId, issueNumber),
                RestProvider.getRepoService(isEnterprise()).isCollaborator(login, repoId, loggedInUser.getLogin()),
                RestProvider.getIssueService(isEnterprise()).getIssue(login, repoId, issueNumber),
                (pullRequestModel, booleanResponse, issue) -> {
                    this.pullRequest = pullRequestModel;
                    if (issue != null) {
                        this.pullRequest.setReactions(issue.getReactions());
                        this.pullRequest.setTitle(issue.getTitle());
                        this.pullRequest.setBody(issue.getBody());
                        this.pullRequest.setBodyHtml(issue.getBodyHtml());
                    }
                    this.pullRequest.setLogin(login);
                    this.pullRequest.setRepoId(repoId);
                    isCollaborator = booleanResponse.code() == 204;
                    return pullRequest;
                })), pullRequest -> {
            sendToView(view -> view.onSetupIssue(false));
            manageDisposable(PinnedPullRequests.updateEntry(pullRequest.getId()));
            manageObservable(pullRequest.save(pullRequest).toObservable());
        });
    }

    private void updateTimeline(PullRequestPagerMvp.View view, int assignee_added) {
        view.showMessage(R.string.success, assignee_added);
        view.onUpdateTimeline();
    }
}

package com.fastaccess.ui.modules.repos.issues.issue.details;

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
import com.fastaccess.data.dao.LockIssuePrModel;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.data.dao.NotificationSubscriptionBodyModel;
import com.fastaccess.data.dao.PullsIssuesParser;
import com.fastaccess.data.dao.UsersListModel;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.PinnedIssues;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.data.service.IssueService;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.Response;

/**
 * Created by Kosh on 10 Dec 2016, 9:23 AM
 */

class IssuePagerPresenter extends BasePresenter<IssuePagerMvp.View> implements IssuePagerMvp.Presenter {
    @com.evernote.android.state.State Issue issueModel;
    @com.evernote.android.state.State int issueNumber;
    @com.evernote.android.state.State String login;
    @com.evernote.android.state.State String repoId;
    @com.evernote.android.state.State boolean isCollaborator;
    @com.evernote.android.state.State boolean showToRepoBtn;
    @com.evernote.android.state.State long commentId;

    @Nullable @Override public Issue getIssue() {
        return issueModel;
    }

    @Override public void onError(@NonNull Throwable throwable) {
        if (RestProvider.getErrorCode(throwable) == 404) {
            sendToView(BaseMvp.FAView::onOpenUrlInBrowser);
        } else {
            onWorkOffline(issueNumber, login, repoId);
        }
        super.onError(throwable);
    }

    @Override public void onActivityCreated(@Nullable Intent intent) {
        Logger.e(isEnterprise());
        if (intent != null && intent.getExtras() != null) {
            issueModel = intent.getExtras().getParcelable(BundleConstant.ITEM);
            issueNumber = intent.getExtras().getInt(BundleConstant.ID);
            login = intent.getExtras().getString(BundleConstant.EXTRA);
            repoId = intent.getExtras().getString(BundleConstant.EXTRA_TWO);
            showToRepoBtn = intent.getExtras().getBoolean(BundleConstant.EXTRA_THREE);
            commentId = intent.getExtras().getLong(BundleConstant.EXTRA_SIX);
            if (issueModel != null) {
                issueNumber = issueModel.getNumber();
                sendToView(view -> view.onSetupIssue(false));
                return;
            } else if (issueNumber > 0 && !InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
                getIssueFromApi();
                return;
            }
        }
        sendToView(view -> view.onSetupIssue(false));
    }

    @Override public void onWorkOffline(long issueNumber, @NonNull String repoId, @NonNull String login) {
        if (issueModel == null) {
            manageDisposable(RxHelper.getObservable(Issue.getIssueByNumber((int) issueNumber, repoId, login))
                    .subscribe(issueModel1 -> {
                        if (issueModel1 != null) {
                            issueModel = issueModel1;
                            sendToView(view -> view.onSetupIssue(false));
                        }
                    }));
        } else {
            sendToView(BaseMvp.FAView::hideProgress);
        }
    }

    @Override public boolean isOwner() {
        if (getIssue() == null) return false;
        User userModel = getIssue() != null ? getIssue().getUser() : null;
        Login me = Login.getUser();
        PullsIssuesParser parser = PullsIssuesParser.getForIssue(getIssue().getHtmlUrl());
        return (userModel != null && userModel.getLogin().equalsIgnoreCase(me.getLogin()))
                || (parser != null && parser.getLogin().equalsIgnoreCase(me.getLogin()));
    }

    @Override public boolean isRepoOwner() {
        if (getIssue() == null) return false;
        Login me = Login.getUser();
        return TextUtils.equals(login, me.getLogin());
    }

    @Override public boolean isLocked() {
        return getIssue() != null && getIssue().isLocked();
    }

    @Override public boolean isCollaborator() {
        return isCollaborator;
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
                onLockUnlockIssue(null);
            }
        }
    }

    @Override public void onOpenCloseIssue() {
        Issue currentIssue = getIssue();
        if (currentIssue != null) {
            IssueRequestModel requestModel = IssueRequestModel.clone(currentIssue, true);
            manageDisposable(RxHelper.getObservable(RestProvider.getIssueService(isEnterprise()).editIssue(login, repoId,
                    issueNumber, requestModel))
                    .doOnSubscribe(disposable -> sendToView(view -> view.showProgress(0)))
                    .subscribe(issue -> {
                        if (issue != null) {
                            sendToView(view -> view.showSuccessIssueActionMsg(currentIssue.getState() == IssueState.open));
                            issue.setRepoId(issueModel.getRepoId());
                            issue.setLogin(issueModel.getLogin());
                            issueModel = issue;
                            sendToView(view -> view.onSetupIssue(false));
                        }
                    }, this::onError));
        }
    }

    @Override public void onLockUnlockIssue(String reason) {
        Issue currentIssue = getIssue();
        if (currentIssue == null) return;
        String login = getLogin();
        String repoId = getRepoId();
        int number = currentIssue.getNumber();
        LockIssuePrModel model = null;
        if (!isLocked() && !InputHelper.isEmpty(reason)) {
            model = new LockIssuePrModel(true, reason);
        }
        IssueService issueService = RestProvider.getIssueService(isEnterprise());
        Observable<Response<Boolean>> observable = RxHelper
                .getObservable(model == null
                               ? issueService.unlockIssue(login, repoId, number) :
                               issueService.lockIssue(model, login, repoId, number));
        makeRestCall(observable, booleanResponse -> {
            int code = booleanResponse.code();
            if (code == 204) {
                issueModel.setLocked(!isLocked());
                sendToView(view -> view.onSetupIssue(true));
            }
            sendToView(IssuePagerMvp.View::hideProgress);
        });

    }

    @Override public void onPutMilestones(@NonNull MilestoneModel milestone) {
        issueModel.setMilestone(milestone);
        IssueRequestModel issueRequestModel = IssueRequestModel.clone(issueModel, false);
        makeRestCall(RestProvider.getIssueService(isEnterprise()).editIssue(login, repoId, issueNumber, issueRequestModel),
                issue -> {
                    this.issueModel.setMilestone(issue.getMilestone());
                    manageObservable(issue.save(issueModel).toObservable());
                    sendToView(view -> updateTimeline(view, R.string.labels_added_successfully));
                });

    }

    @Override public void onPutLabels(@NonNull ArrayList<LabelModel> labels) {
        makeRestCall(RestProvider.getIssueService(isEnterprise()).putLabels(login, repoId, issueNumber,
                Stream.of(labels).filter(value -> value != null && value.getName() != null)
                        .map(LabelModel::getName).collect(Collectors.toList())),
                labelModels -> {
                    sendToView(view -> updateTimeline(view, R.string.labels_added_successfully));
                    LabelListModel listModel = new LabelListModel();
                    listModel.addAll(labels);
                    issueModel.setLabels(listModel);
                    manageObservable(issueModel.save(issueModel).toObservable());
                });
    }

    @Override public void onPutAssignees(@NonNull ArrayList<User> users) {
        AssigneesRequestModel assigneesRequestModel = new AssigneesRequestModel();
        ArrayList<String> assignees = new ArrayList<>();
        Stream.of(users).forEach(userModel -> assignees.add(userModel.getLogin()));
        assigneesRequestModel.setAssignees(assignees.isEmpty() ? Stream.of(issueModel.getAssignees()).map(User::getLogin).toList() : assignees);
        makeRestCall(!assignees.isEmpty() ?
                     RestProvider.getIssueService(isEnterprise()).putAssignees(login, repoId, issueNumber, assigneesRequestModel) :
                     RestProvider.getIssueService(isEnterprise()).deleteAssignees(login, repoId, issueNumber, assigneesRequestModel),
                issue -> {
                    UsersListModel assignee = new UsersListModel();
                    assignee.addAll(users);
                    issueModel.setAssignees(assignee);
                    manageObservable(issueModel.save(issueModel).toObservable());
                    sendToView(view -> updateTimeline(view, R.string.assignee_added));
                }
        );
    }

    @Override public String getLogin() {
        return login;
    }

    @Override public String getRepoId() {
        return repoId;
    }

    @Override public void onUpdateIssue(@NonNull Issue issue) {
        this.issueModel.setBody(issue.getBody());
        this.issueModel.setBodyHtml(issue.getBodyHtml());
        this.issueModel.setTitle(issue.getTitle());
        this.issueModel.setLogin(login);
        this.issueModel.setRepoId(repoId);
        manageObservable(issueModel.save(issueModel).toObservable());
        sendToView(view -> view.onSetupIssue(true));
    }

    @Override public void onSubscribeOrMute(boolean mute) {
        if (getIssue() == null) return;
        makeRestCall(mute ? RestProvider.getNotificationService(isEnterprise()).subscribe(getIssue().getId(),
                new NotificationSubscriptionBodyModel(false, true))
                          : RestProvider.getNotificationService(isEnterprise()).subscribe(getIssue().getId(),
                new NotificationSubscriptionBodyModel(true, false)),
                booleanResponse -> {
                    if (booleanResponse.code() == 204 || booleanResponse.code() == 200) {
                        sendToView(view -> view.showMessage(R.string.success, R.string.successfully_submitted));
                    } else {
                        sendToView(view -> view.showMessage(R.string.error, R.string.network_error));
                    }
                });
    }

    @Override public void onPinUnpinIssue() {
        if (getIssue() == null) return;
        PinnedIssues.pinUpin(getIssue());
        sendToView(IssuePagerMvp.View::onUpdateMenu);
    }

    private void getIssueFromApi() {
        Login loginUser = Login.getUser();
        if (loginUser == null) return;
        makeRestCall(RxHelper.getObservable(Observable.zip(RestProvider.getIssueService(isEnterprise()).getIssue(login, repoId, issueNumber),
                RestProvider.getRepoService(isEnterprise()).isCollaborator(login, repoId, loginUser.getLogin()),
                (issue, booleanResponse) -> {
                    isCollaborator = booleanResponse.code() == 204;
                    return issue;
                })), this::setupIssue);
    }

    private void setupIssue(Issue issue) {
        issueModel = issue;
        issueModel.setRepoId(repoId);
        issueModel.setLogin(login);
        sendToView(view -> view.onSetupIssue(false));
        manageDisposable(PinnedIssues.updateEntry(issue.getId()));
    }

    private void updateTimeline(IssuePagerMvp.View view, int assignee_added) {
        view.showMessage(R.string.success, assignee_added);
        view.onUpdateTimeline();
    }
}

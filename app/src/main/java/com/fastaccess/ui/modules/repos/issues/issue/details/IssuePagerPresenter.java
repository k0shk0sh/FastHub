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
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.data.dao.PullsIssuesParser;
import com.fastaccess.data.dao.UsersListModel;
import com.fastaccess.data.dao.model.AbstractRepo;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.data.service.IssueService;
import com.fastaccess.data.service.NotificationService;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

import retrofit2.Response;
import rx.Observable;

/**
 * Created by Kosh on 10 Dec 2016, 9:23 AM
 */

class IssuePagerPresenter extends BasePresenter<IssuePagerMvp.View> implements IssuePagerMvp.Presenter {
    private Issue issueModel;
    private int issueNumber;
    private String login;
    private String repoId;
    private boolean isCollaborator;
    private boolean showToRepoBtn;

    @Nullable @Override public Issue getIssue() {
        return issueModel;
    }

    @Override public void onError(@NonNull Throwable throwable) {
        if (RestProvider.getErrorCode(throwable) == 404) {
            sendToView(IssuePagerMvp.View::onFinishActivity);
        } else {
            onWorkOffline(issueNumber, login, repoId);
        }
        super.onError(throwable);
    }

    @Override public void onActivityCreated(@Nullable Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            issueModel = intent.getExtras().getParcelable(BundleConstant.ITEM);
            issueNumber = intent.getExtras().getInt(BundleConstant.ID);
            login = intent.getExtras().getString(BundleConstant.EXTRA);
            repoId = intent.getExtras().getString(BundleConstant.EXTRA_TWO);
            showToRepoBtn = intent.getExtras().getBoolean(BundleConstant.EXTRA_THREE);
            if (issueModel != null) {
                issueNumber = issueModel.getNumber();
                sendToView(IssuePagerMvp.View::onSetupIssue);
                return;
            } else if (issueNumber > 0 && !InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
                getIssueFromApi();
                return;
            }
        }
        sendToView(IssuePagerMvp.View::onSetupIssue);
    }

    private void getIssueFromApi() {
        Observable<Issue> observable = RestProvider.getIssueService().getIssue(login, repoId, issueNumber)
                .flatMap(issue -> RestProvider.getRepoService().isCollaborator(login, repoId, Login.getUser().getLogin()),
                        (issue, booleanResponse) -> {
                            isCollaborator = booleanResponse.code() == 204;
                            return issue;
                        });
        makeRestCall(observable, this::setupIssue);
    }

    private void setupIssue(Issue issue) {
        issueModel = issue;
        issueModel.setRepoId(repoId);
        issueModel.setLogin(login);
        sendToView(IssuePagerMvp.View::onSetupIssue);
    }

    @Override public void onWorkOffline(long issueNumber, @NonNull String repoId, @NonNull String login) {
        if (issueModel == null) {
            manageSubscription(RxHelper.getObserver(Issue.getIssueByNumber((int) issueNumber, repoId, login))
                    .subscribe(issueModel1 -> {
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
                onLockUnlockIssue();
            }
        }
    }

    @Override public void onOpenCloseIssue() {
        Issue currentIssue = getIssue();
        if (currentIssue != null) {
            IssueRequestModel requestModel = IssueRequestModel.clone(currentIssue, true);
            manageSubscription(RxHelper.getObserver(RestProvider.getIssueService().editIssue(login, repoId,
                    issueNumber, requestModel))
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
        Issue currentIssue = getIssue();
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

    @Override public void onPutMilestones(@NonNull MilestoneModel milestone) {
        issueModel.setMilestone(milestone);
        IssueRequestModel issueRequestModel = IssueRequestModel.clone(issueModel, false);
        makeRestCall(RestProvider.getIssueService().editIssue(login, repoId, issueNumber, issueRequestModel),
                issue -> {
                    this.issueModel = issue;
                    issueModel.setLogin(login);
                    issueModel.setRepoId(repoId);
                    manageSubscription(issue.save(issueModel).subscribe());
                    sendToView(IssuePagerMvp.View::onUpdateTimeline);
                });

    }

    @Override public void onPutLabels(@NonNull ArrayList<LabelModel> labels) {
        makeRestCall(RestProvider.getIssueService().putLabels(login, repoId, issueNumber,
                Stream.of(labels).filter(value -> value != null && value.getName() != null)
                        .map(LabelModel::getName).collect(Collectors.toList())),
                labelModels -> {
                    sendToView(IssuePagerMvp.View::onUpdateTimeline);
                    LabelListModel listModel = new LabelListModel();
                    listModel.addAll(labels);
                    issueModel.setLabels(listModel);
                    manageSubscription(issueModel.save(issueModel).subscribe());
                });
    }

    @Override public void onPutAssignees(@NonNull ArrayList<User> users) {
        AssigneesRequestModel assigneesRequestModel = new AssigneesRequestModel();
        ArrayList<String> assignees = new ArrayList<>();
        Stream.of(users).forEach(userModel -> assignees.add(userModel.getLogin()));
        assigneesRequestModel.setAssignees(assignees);
        makeRestCall(RestProvider.getIssueService().putAssignees(login, repoId, issueNumber, assigneesRequestModel),
                issue -> {
                    this.issueModel = issue;
                    issueModel.setLogin(login);
                    issueModel.setRepoId(repoId);
                    UsersListModel assignee = new UsersListModel();
                    assignee.addAll(users);
                    issueModel.setAssignees(assignee);
                    manageSubscription(issueModel.save(issueModel).subscribe());
                    sendToView(IssuePagerMvp.View::onUpdateTimeline);
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
        this.issueModel = issue;
        this.issueModel.setLogin(login);
        this.issueModel.setRepoId(repoId);
        manageSubscription(issueModel.save(issueModel).subscribe());
        sendToView(IssuePagerMvp.View::onSetupIssue);
    }

    @Override public void onSubscribeOrMute(boolean mute) {
        if (getIssue() == null) return;
        String url = NotificationService.SUBSCRIPTION_URL;
        String utf = NotificationService.UTF8;
        String issue = NotificationService.ISSUE_THREAD_CLASS;
        String token = PrefGetter.getToken();
        String id = mute ? NotificationService.MUTE : NotificationService.SUBSCRIBE;
        makeRestCall(AbstractRepo.getRepo(repoId, login)
                        .flatMap(repo -> RestProvider.getNotificationService()
                                .subscribe(url, repo.getId(), getIssue().getId(), issue, id, token, utf)),
                booleanResponse -> {
                    if (booleanResponse.code() == 204 || booleanResponse.code() == 200) {
                        sendToView(view -> view.showMessage(R.string.success, R.string.successfully_submitted));
                    } else {
                        sendToView(view -> view.showMessage(R.string.error, R.string.network_error));
                    }
                });
    }
}

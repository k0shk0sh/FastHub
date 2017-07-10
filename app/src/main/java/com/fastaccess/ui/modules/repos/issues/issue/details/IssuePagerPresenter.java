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
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.PrefGetter;
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
        Logger.e(isEnterprise());
        if (intent != null && intent.getExtras() != null) {
            issueModel = intent.getExtras().getParcelable(BundleConstant.ITEM);
            issueNumber = intent.getExtras().getInt(BundleConstant.ID);
            login = intent.getExtras().getString(BundleConstant.EXTRA);
            repoId = intent.getExtras().getString(BundleConstant.EXTRA_TWO);
            showToRepoBtn = intent.getExtras().getBoolean(BundleConstant.EXTRA_THREE);
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
            manageDisposable(RxHelper.getObserver(Issue.getIssueByNumber((int) issueNumber, repoId, login))
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
                onLockUnlockIssue();
            }
        }
    }

    @Override public void onOpenCloseIssue() {
        Issue currentIssue = getIssue();
        if (currentIssue != null) {
            IssueRequestModel requestModel = IssueRequestModel.clone(currentIssue, true);
            manageDisposable(RxHelper.getObserver(RestProvider.getIssueService(isEnterprise()).editIssue(login, repoId,
                    issueNumber, requestModel))
                    .doOnSubscribe(disposable -> sendToView(view -> view.showProgress(0)))
                    .doOnNext(issue -> {
                        if (issue != null) {
                            sendToView(view -> view.showSuccessIssueActionMsg(currentIssue.getState() == IssueState.open));
                            issue.setRepoId(issueModel.getRepoId());
                            issue.setLogin(issueModel.getLogin());
                            issueModel = issue;
                            sendToView(view -> view.onSetupIssue(true));
                        }
                    })
                    .subscribe(issue -> {/**/}, this::onError));
        }
    }

    @Override public void onLockUnlockIssue() {
        Issue currentIssue = getIssue();
        if (currentIssue == null) return;
        String login = currentIssue.getUser().getLogin();
        String repoId = currentIssue.getRepoId();
        int number = currentIssue.getNumber();
        IssueService issueService = RestProvider.getIssueService(isEnterprise());
        Observable<Response<Boolean>> observable = RxHelper
                .getObserver(isLocked() ? issueService.unlockIssue(login, repoId, number) : issueService.lockIssue(login, repoId, number));
        makeRestCall(observable, booleanResponse -> {
            int code = booleanResponse.code();
            if (code == 204) {
                issueModel.setLocked(!isLocked());
                sendToView(view -> view.onSetupIssue(true));
            }
            sendToView(IssuePagerMvp.View::hideProgress);
        });

    }

    @Override public void onLoadLabels() {
        manageDisposable(
                RxHelper.getObserver(RestProvider.getRepoService(isEnterprise()).getLabels(login, repoId))
                        .doOnSubscribe(disposable -> onSubscribed())
                        .doOnNext(response -> {
                            if (response.getItems() != null && !response.getItems().isEmpty()) {
                                sendToView(view -> view.onLabelsRetrieved(response.getItems()));
                            } else {
                                sendToView(view -> view.showMessage(R.string.error, R.string.no_labels));
                            }
                        })
                        .subscribe(labelModelPageable -> {/**/}, throwable -> {
                            sendToView(view -> view.showMessage(R.string.error, R.string.no_labels));
                        })
        );
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
        assigneesRequestModel.setAssignees(assignees);
        makeRestCall(RestProvider.getIssueService(isEnterprise()).putAssignees(login, repoId, issueNumber, assigneesRequestModel),
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
        String url = NotificationService.SUBSCRIPTION_URL;
        String utf = NotificationService.UTF8;
        String issue = NotificationService.ISSUE_THREAD_CLASS;
        String token = PrefGetter.getToken();
        String id = mute ? NotificationService.MUTE : NotificationService.SUBSCRIBE;
        makeRestCall(AbstractRepo.getRepo(repoId, login)
                        .flatMapObservable(repo -> RestProvider.getNotificationService(isEnterprise())
                                .subscribe(url, repo.getId(), getIssue().getId(), issue, id, token, utf)),
                booleanResponse -> {
                    if (booleanResponse.code() == 204 || booleanResponse.code() == 200) {
                        sendToView(view -> view.showMessage(R.string.success, R.string.successfully_submitted));
                    } else {
                        sendToView(view -> view.showMessage(R.string.error, R.string.network_error));
                    }
                });
    }

    private void getIssueFromApi() {
        makeRestCall(RxHelper.getObserver(Observable.zip(RestProvider.getIssueService(isEnterprise()).getIssue(login, repoId, issueNumber),
                RestProvider.getRepoService(isEnterprise()).isCollaborator(login, repoId, Login.getUser().getLogin()),
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
    }

    private void updateTimeline(IssuePagerMvp.View view, int assignee_added) {
        view.showMessage(R.string.success, assignee_added);
        view.onUpdateTimeline();
    }
}

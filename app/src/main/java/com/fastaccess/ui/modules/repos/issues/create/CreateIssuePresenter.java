package com.fastaccess.ui.modules.repos.issues.create;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.data.dao.CreateIssueModel;
import com.fastaccess.data.dao.IssueRequestModel;
import com.fastaccess.data.dao.LabelListModel;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.data.dao.UsersListModel;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 19 Feb 2017, 12:18 PM
 */

public class CreateIssuePresenter extends BasePresenter<CreateIssueMvp.View> implements CreateIssueMvp.Presenter {

    @com.evernote.android.state.State boolean isCollaborator;


    @Override public void checkAuthority(@NonNull String login, @NonNull String repoId) {
        manageViewDisposable(RxHelper.getObservable(RestProvider.getRepoService(isEnterprise()).
                isCollaborator(login, repoId, Login.getUser().getLogin()))
                .subscribe(booleanResponse -> {
                    isCollaborator = booleanResponse.code() == 204;
                    sendToView(CreateIssueMvp.View::onShowIssueMisc);
                }, Throwable::printStackTrace));
    }

    @Override public void onActivityForResult(int resultCode, int requestCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK && requestCode == BundleConstant.REQUEST_CODE) {
            if (intent != null && intent.getExtras() != null) {
                CharSequence charSequence = intent.getExtras().getCharSequence(BundleConstant.EXTRA);
                if (!InputHelper.isEmpty(charSequence)) {
                    sendToView(view -> view.onSetCode(charSequence));
                }
            }
        }
    }

    @Override public void onSubmit(@NonNull String title, @NonNull CharSequence description, @NonNull String login,
                                   @NonNull String repo, @Nullable Issue issue, @Nullable PullRequest pullRequestModel,
                                   @Nullable ArrayList<LabelModel> labels, @Nullable MilestoneModel milestoneModel,
                                   @Nullable ArrayList<User> users) {

        boolean isEmptyTitle = InputHelper.isEmpty(title);
        if (getView() != null) {
            getView().onTitleError(isEmptyTitle);
        }
        if (!isEmptyTitle) {
            if (issue == null && pullRequestModel == null) {
                CreateIssueModel createIssue = new CreateIssueModel();
                createIssue.setBody(InputHelper.toString(description));
                createIssue.setTitle(title);
                if (isCollaborator) {
                    if (labels != null && !labels.isEmpty()) {
                        createIssue.setLabels(Stream.of(labels).map(LabelModel::getName).collect(Collectors.toCollection(ArrayList::new)));
                    }
                    if (users != null && !users.isEmpty()) {
                        createIssue.setAssignees(Stream.of(users).map(User::getLogin).collect(Collectors.toCollection(ArrayList::new)));
                    }
                    if (milestoneModel != null) {
                        createIssue.setMilestone((long) milestoneModel.getNumber());
                    }
                }
                makeRestCall(RestProvider.getIssueService(isEnterprise()).createIssue(login, repo, createIssue),
                        issueModel -> {
                            if (issueModel != null) {
                                sendToView(view -> view.onSuccessSubmission(issueModel));
                            } else {
                                sendToView(view -> view.showMessage(R.string.error, R.string.error_creating_issue));
                            }
                        }, false);
            } else {
                if (issue != null) {
                    issue.setBody(InputHelper.toString(description));
                    issue.setTitle(title);
                    int number = issue.getNumber();
                    if (isCollaborator) {
                        if (labels != null) {
                            LabelListModel labelModels = new LabelListModel();
                            labelModels.addAll(labels);
                            issue.setLabels(labelModels);
                        }
                        if (milestoneModel != null) {
                            issue.setMilestone(milestoneModel);
                        }
                        if (users != null) {
                            UsersListModel usersListModel = new UsersListModel();
                            usersListModel.addAll(users);
                            issue.setAssignees(usersListModel);
                        }
                    }
                    IssueRequestModel requestModel = IssueRequestModel.clone(issue, false);
                    makeRestCall(RestProvider.getIssueService(isEnterprise()).editIssue(login, repo, number, requestModel),
                            issueModel -> {
                                if (issueModel != null) {
                                    sendToView(view -> view.onSuccessSubmission(issueModel));
                                } else {
                                    sendToView(view -> view.showMessage(R.string.error, R.string.error_creating_issue));
                                }
                            }, false);
                }
                if (pullRequestModel != null) {
                    int number = pullRequestModel.getNumber();
                    pullRequestModel.setBody(InputHelper.toString(description));
                    pullRequestModel.setTitle(title);
                    if (isCollaborator) {
                        if (labels != null) {
                            LabelListModel labelModels = new LabelListModel();
                            labelModels.addAll(labels);
                            pullRequestModel.setLabels(labelModels);
                        }
                        if (milestoneModel != null) {
                            pullRequestModel.setMilestone(milestoneModel);
                        }
                        if (users != null) {
                            UsersListModel usersListModel = new UsersListModel();
                            usersListModel.addAll(users);
                            pullRequestModel.setAssignees(usersListModel);
                        }
                    }
                    IssueRequestModel requestModel = IssueRequestModel.clone(pullRequestModel, false);
                    makeRestCall(RestProvider.getPullRequestService(isEnterprise()).editPullRequest(login, repo, number, requestModel)
                            .flatMap(pullRequest1 -> RestProvider.getIssueService(isEnterprise()).getIssue(login, repo, number),
                                    (pullRequest1, issueReaction) -> {//hack to get reactions from issue api
                                        if (issueReaction != null) {
                                            pullRequest1.setReactions(issueReaction.getReactions());
                                        }
                                        return pullRequest1;
                                    }), pr -> {
                        if (pr != null) {
                            sendToView(view -> view.onSuccessSubmission(pr));
                        } else {
                            sendToView(view -> view.showMessage(R.string.error, R.string.error_creating_issue));
                        }
                    }, false);
                }
            }
        }

    }

    @Override public void onCheckAppVersion() {
        makeRestCall(RestProvider.getRepoService(false).getLatestRelease("k0shk0sh", "FastHub"),
                release -> {
                    if (release != null) {
                        if (!BuildConfig.VERSION_NAME.contains(release.getTagName())) {
                            sendToView(CreateIssueMvp.View::onShowUpdate);
                        } else {
                            sendToView(BaseMvp.FAView::hideProgress);
                        }
                    }
                }, false);
    }

    @Override public boolean isCollaborator() {
        return isCollaborator;
    }
}

package com.fastaccess.ui.modules.repos.issues.create;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.R;
import com.fastaccess.data.dao.CreateIssueModel;
import com.fastaccess.data.dao.IssueRequestModel;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

/**
 * Created by Kosh on 19 Feb 2017, 12:18 PM
 */

public class CreateIssuePresenter extends BasePresenter<CreateIssueMvp.View> implements CreateIssueMvp.Presenter {

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

    @Override public void onSubmit(@NonNull String title, @NonNull CharSequence description,
                                   @NonNull String login, @NonNull String repo,
                                   @Nullable Issue issue, @Nullable PullRequest pullRequestModel) {
        boolean isEmptyTitle = InputHelper.isEmpty(title);
        if (getView() != null) {
            getView().onTitleError(isEmptyTitle);
        }
        if (!isEmptyTitle) {
            if (issue == null && pullRequestModel == null) {
                CreateIssueModel createIssue = new CreateIssueModel();
                createIssue.setBody(InputHelper.toString(description));
                createIssue.setTitle(title);
                makeRestCall(RestProvider.getIssueService().createIssue(login, repo, createIssue),
                        issueModel -> {
                            if (issueModel != null) {
                                sendToView(view -> view.onSuccessSubmission(issueModel));
                            } else {
                                sendToView(view -> view.showMessage(R.string.error, R.string.error_creating_issue));
                            }
                        });
            } else {
                if (issue != null) {
                    issue.setBody(InputHelper.toString(description));
                    issue.setTitle(title);
                    int number = issue.getNumber();
                    IssueRequestModel requestModel = IssueRequestModel.clone(issue, false);
                    makeRestCall(RestProvider.getIssueService().editIssue(login, repo, number, requestModel),
                            issueModel -> {
                                if (issueModel != null) {
                                    sendToView(view -> view.onSuccessSubmission(issueModel));
                                } else {
                                    sendToView(view -> view.showMessage(R.string.error, R.string.error_creating_issue));
                                }
                            });
                }
                if (pullRequestModel != null) {
                    int number = pullRequestModel.getNumber();
                    pullRequestModel.setBody(InputHelper.toString(description));
                    pullRequestModel.setTitle(title);
                    IssueRequestModel requestModel = IssueRequestModel.clone(pullRequestModel, false);
                    makeRestCall(RestProvider.getPullRequestService().editPullRequest(login, repo, number, requestModel)
                            .flatMap(pullRequest1 -> RestProvider.getIssueService().getIssue(login, repo, number),
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
                    });
                }
            }
        }
    }
}

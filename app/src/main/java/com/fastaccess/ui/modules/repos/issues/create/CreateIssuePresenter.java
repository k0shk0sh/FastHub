package com.fastaccess.ui.modules.repos.issues.create;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.fastaccess.R;
import com.fastaccess.data.dao.CreateIssueModel;
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
                                   @NonNull String login, @NonNull String repo) {
        boolean isEmptyTitle = InputHelper.isEmpty(title);
        if (getView() != null) {
            getView().onTitleError(isEmptyTitle);
        }
        if (!isEmptyTitle) {
            CreateIssueModel createIssueModel = new CreateIssueModel();
            createIssueModel.setBody(InputHelper.toString(description));
            createIssueModel.setTitle(title);
            makeRestCall(RestProvider.getIssueService().createIssue(login, repo, createIssueModel),
                    issueModel -> {
                        if (issueModel != null) {
                            sendToView(view -> view.onSuccessSubmission(issueModel));
                        } else {
                            sendToView(view -> view.showMessage(R.string.error, R.string.error_creating_issue));
                        }
                    });
        }
    }
}

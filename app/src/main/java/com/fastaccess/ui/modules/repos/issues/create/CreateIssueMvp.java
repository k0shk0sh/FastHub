package com.fastaccess.ui.modules.repos.issues.create;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.IssueModel;
import com.fastaccess.data.dao.PullRequestModel;
import com.fastaccess.ui.base.mvp.BaseMvp;

/**
 * Created by Kosh on 19 Feb 2017, 12:12 PM
 */

public interface CreateIssueMvp {

    interface View extends BaseMvp.FAView {
        void onSetCode(@NonNull CharSequence charSequence);

        void onTitleError(boolean isEmptyTitle);

        void onDescriptionError(boolean isEmptyDesc);

        void onSuccessSubmission(IssueModel issueModel);

        void onSuccessSubmission(PullRequestModel issueModel);
    }

    interface Presenter extends BaseMvp.FAPresenter {
        void onActivityForResult(int resultCode, int requestCode, Intent intent);

        void onSubmit(@NonNull String title, @NonNull CharSequence description, @NonNull String login,
                      @NonNull String repo, @Nullable IssueModel issueModel, @Nullable PullRequestModel pullRequestModel);
    }
}

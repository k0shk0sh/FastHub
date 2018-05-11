package com.fastaccess.ui.modules.repos.issues.create;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.repos.extras.assignees.AssigneesMvp;
import com.fastaccess.ui.modules.repos.extras.labels.LabelsMvp;
import com.fastaccess.ui.modules.repos.extras.milestone.MilestoneMvp;

import java.util.ArrayList;

/**
 * Created by Kosh on 19 Feb 2017, 12:12 PM
 */

public interface CreateIssueMvp {

    interface View extends BaseMvp.FAView, LabelsMvp.SelectedLabelsListener, AssigneesMvp.SelectedAssigneesListener,
            MilestoneMvp.OnMilestoneSelected {
        void onSetCode(@NonNull CharSequence charSequence);

        void onTitleError(boolean isEmptyTitle);

        void onDescriptionError(boolean isEmptyDesc);

        void onSuccessSubmission(Issue issueModel);

        void onSuccessSubmission(PullRequest issueModel);

        void onShowUpdate();

        void onShowIssueMisc();
    }

    interface Presenter extends BaseMvp.FAPresenter {

        void checkAuthority(@NonNull String login, @NonNull String repoId);

        void onActivityForResult(int resultCode, int requestCode, Intent intent);

        void onSubmit(@NonNull String title, @NonNull CharSequence description, @NonNull String login,
                      @NonNull String repo, @Nullable Issue issueModel, @Nullable PullRequest pullRequestModel,
                      @Nullable ArrayList<LabelModel> labels, @Nullable MilestoneModel milestoneModel,
                      @Nullable ArrayList<User> users);

        void onCheckAppVersion();

        boolean isCollaborator();
    }
}

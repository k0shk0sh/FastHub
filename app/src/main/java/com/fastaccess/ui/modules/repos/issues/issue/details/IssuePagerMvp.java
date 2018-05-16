package com.fastaccess.ui.modules.repos.issues.issue.details;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.editor.comment.CommentEditorFragment;
import com.fastaccess.ui.modules.repos.extras.assignees.AssigneesMvp;
import com.fastaccess.ui.modules.repos.extras.labels.LabelsMvp;
import com.fastaccess.ui.modules.repos.extras.locking.LockIssuePrCallback;

import java.util.ArrayList;

/**
 * Created by Kosh on 10 Dec 2016, 9:21 AM
 */

public interface IssuePagerMvp {

    interface View extends BaseMvp.FAView, LabelsMvp.SelectedLabelsListener,
            AssigneesMvp.SelectedAssigneesListener, IssuePrCallback<Issue>,
            CommentEditorFragment.CommentListener, LockIssuePrCallback {
        void onSetupIssue(boolean isUpdate);

        void showSuccessIssueActionMsg(boolean isClose);

        void showErrorIssueActionMsg(boolean isClose);

        void onUpdateTimeline();

        void onUpdateMenu();

        void onMileStoneSelected(@NonNull MilestoneModel milestoneModel);

        void onFinishActivity();
    }

    interface Presenter extends BaseMvp.FAPresenter {

        @Nullable Issue getIssue();

        void onActivityCreated(@Nullable Intent intent);

        void onWorkOffline(long issueNumber, @NonNull String repoId, @NonNull String login);

        boolean isOwner();

        boolean isRepoOwner();

        boolean isLocked();

        boolean isCollaborator();

        boolean showToRepoBtn();

        void onHandleConfirmDialog(@Nullable Bundle bundle);

        void onOpenCloseIssue();

        void onLockUnlockIssue(String reason);

        void onPutMilestones(@NonNull MilestoneModel milestone);

        void onPutLabels(@NonNull ArrayList<LabelModel> labels);

        void onPutAssignees(@NonNull ArrayList<User> users);

        String getLogin();

        String getRepoId();

        void onUpdateIssue(@NonNull Issue issueModel);

        void onSubscribeOrMute(boolean mute);

        void onPinUnpinIssue();
    }

    interface IssuePrCallback<T> {
        @Nullable T getData();
    }
}

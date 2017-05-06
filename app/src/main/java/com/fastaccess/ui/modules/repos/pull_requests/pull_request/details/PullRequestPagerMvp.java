package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.repos.extras.assignees.AssigneesMvp;
import com.fastaccess.ui.modules.repos.extras.labels.LabelsMvp;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.merge.MergePullReqeustMvp;
import com.fastaccess.ui.widgets.SpannableBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 10 Dec 2016, 9:21 AM
 */

public interface PullRequestPagerMvp {

    interface View extends BaseMvp.FAView, LabelsMvp.SelectedLabelsListener,
            AssigneesMvp.SelectedAssigneesListener, MergePullReqeustMvp.MergeCallback {

        void onSetupIssue();

        void onLabelsRetrieved(@NonNull List<LabelModel> items);

        void onUpdateMenu();

        void showSuccessIssueActionMsg(boolean isClose);

        void showErrorIssueActionMsg(boolean isClose);

        void onUpdateTimeline();

        void onMileStoneSelected(@NonNull MilestoneModel milestoneModel);

        void onFinishActivity();
    }

    interface Presenter extends BaseMvp.FAPresenter {

        @Nullable PullRequest getPullRequest();

        void onActivityCreated(@Nullable Intent intent);

        void onWorkOffline();

        boolean isOwner();

        boolean isRepoOwner();

        boolean isLocked();

        boolean isMergeable();

        boolean showToRepoBtn();

        void onHandleConfirmDialog(@Nullable Bundle bundle);

        void onOpenCloseIssue();

        void onLockUnlockConversations();

        @NonNull SpannableBuilder getMergeBy(@NonNull PullRequest pullRequest, @NonNull Context context);

        void onMerge(String msg);

        void onLoadLabels();

        void onPutLabels(@NonNull ArrayList<LabelModel> labels);

        void onPutMilestones(@NonNull MilestoneModel milestone);

        void onPutAssignees(@NonNull ArrayList<User> users, boolean isAssignee);

        String getLogin();

        String getRepoId();

        boolean isCollaborator();

        void onUpdatePullRequest(@NonNull PullRequest pullRequestModel);
    }

}

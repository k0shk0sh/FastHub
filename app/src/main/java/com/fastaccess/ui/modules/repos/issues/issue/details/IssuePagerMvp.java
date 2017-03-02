package com.fastaccess.ui.modules.repos.issues.issue.details;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.IssueModel;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.repos.labels.LabelsMvp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 10 Dec 2016, 9:21 AM
 */

interface IssuePagerMvp {

    interface View extends BaseMvp.FAView, LabelsMvp.SelectedLabelsListener {
        void onSetupIssue();

        void showSuccessIssueActionMsg(boolean isClose);

        void showErrorIssueActionMsg(boolean isClose);

        void onLabelsRetrieved(@NonNull List<LabelModel> items);

        void onLabelsAdded();

        void onUpdateMenu();
    }

    interface Presenter extends BaseMvp.FAPresenter {

        @Nullable IssueModel getIssue();

        void onActivityCreated(@Nullable Intent intent);

        void onWorkOffline(long issueNumber, @NonNull String repoId, @NonNull String login);

        boolean isOwner();

        boolean isRepoOwner();

        boolean isLocked();

        boolean isCollaborator();

        void onHandleConfirmDialog(@Nullable Bundle bundle);

        void onOpenCloseIssue();

        void onLockUnlockIssue();

        void onLoadLabels();

        void onPutLabels(@NonNull ArrayList<LabelModel> labels);

        String getLogin();

        String getRepoId();
    }

}

package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.PullRequestModel;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.repos.labels.LabelsMvp;
import com.fastaccess.ui.widgets.SpannableBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 10 Dec 2016, 9:21 AM
 */

interface PullRequestPagerMvp {

    interface View extends BaseMvp.FAView, LabelsMvp.SelectedLabelsListener {

        void onSetupIssue();

        void onLabelsRetrieved(@NonNull List<LabelModel> items);
    }

    interface Presenter extends BaseMvp.FAPresenter {

        @Nullable PullRequestModel getPullRequest();

        void onActivityCreated(@Nullable Intent intent);

        void onWorkOffline();

        boolean isOwner();

        boolean isRepoOwner();

        boolean isLocked();

        boolean isMergeable();

        void onHandleConfirmDialog(@Nullable Bundle bundle);

        void onLockUnlockConversations();

        @NonNull SpannableBuilder getMergeBy(@NonNull PullRequestModel pullRequest, @NonNull Context context);

        void onMerge();

        void onLoadLabels();

        void onPutLabels(@NonNull ArrayList<LabelModel> labels);

        String getLogin();

        String getRepoId();
    }

}

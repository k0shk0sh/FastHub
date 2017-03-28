package com.fastaccess.ui.modules.repos.extras.milestone.create;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.ui.base.mvp.BaseMvp;

/**
 * Created by Kosh on 04 Mar 2017, 10:47 PM
 */

public interface CreateMilestoneMvp {

    interface OnMilestoneAdded {
        void onMilestoneAdded(@NonNull MilestoneModel milestoneModel);
    }

    interface View extends BaseMvp.FAView {
        void onShowTitleError(boolean isError);

        void onMilestoneAdded(@NonNull MilestoneModel milestoneModel);
    }

    interface Presenter {
        void onSubmit(@Nullable String title, @Nullable String dueOn, @Nullable String description,
                      @NonNull String login, @NonNull String repo);
    }
}

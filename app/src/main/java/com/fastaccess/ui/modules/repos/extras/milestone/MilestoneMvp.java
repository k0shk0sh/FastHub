package com.fastaccess.ui.modules.repos.extras.milestone;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.repos.extras.milestone.create.CreateMilestoneMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 04 Mar 2017, 9:38 PM
 */

public interface MilestoneMvp {


    interface OnMilestoneSelected {
        void onMilestoneSelected(@NonNull MilestoneModel milestoneModel);
    }

    interface View extends BaseMvp.FAView, CreateMilestoneMvp.OnMilestoneAdded {
        void onNotifyAdapter();

        void onMilestoneSelected(@NonNull MilestoneModel milestoneModel);
    }

    interface Presenter extends BaseViewHolder.OnItemClickListener<MilestoneModel> {
        void onLoadMilestones(@NonNull String login, @NonNull String repo);

        @NonNull ArrayList<MilestoneModel> getMilestones();
    }
}

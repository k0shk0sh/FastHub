package com.fastaccess.ui.modules.repos.extras.assignees;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.model.User;
import com.fastaccess.ui.adapter.AssigneesAdapter;
import com.fastaccess.ui.base.mvp.BaseMvp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 22 Feb 2017, 7:22 PM
 */

public interface AssigneesMvp {

    interface SelectedAssigneesListener {
        void onSelectedAssignees(@NonNull ArrayList<User> users, boolean isAssignees);
    }

    interface View extends BaseMvp.FAView, AssigneesAdapter.OnSelectAssignee {
        void onNotifyAdapter(@Nullable List<User> items);
    }

    interface Presenter {
        void onCallApi(@NonNull String login, @NonNull String repo, boolean isAssignees);

       @NonNull ArrayList<User> getList();
    }
}

package com.fastaccess.ui.modules.repos.extras.assignees;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.model.User;
import com.fastaccess.ui.adapter.AssigneesAdapter;
import com.fastaccess.ui.base.mvp.BaseMvp;

import java.util.ArrayList;

/**
 * Created by Kosh on 22 Feb 2017, 7:22 PM
 */

public interface AssigneesMvp {

    interface SelectedAssigneesListener {
        void onSelectedAssignees(@NonNull ArrayList<User> users);
    }

    interface View extends BaseMvp.FAView, AssigneesAdapter.OnSelectAssignee {}

    interface Presenter {}
}

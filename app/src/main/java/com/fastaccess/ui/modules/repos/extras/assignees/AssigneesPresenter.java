package com.fastaccess.ui.modules.repos.extras.assignees;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.model.User;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 05 Mar 2017, 11:52 AM
 */

class AssigneesPresenter extends BasePresenter<AssigneesMvp.View> implements AssigneesMvp.Presenter {
    private ArrayList<User> users = new ArrayList<>();

    @Override public void onCallApi(@NonNull String login, @NonNull String repo, boolean isAssignees) {
        makeRestCall(isAssignees ? RestProvider.getRepoService(isEnterprise()).getAssignees(login, repo)
                                 : RestProvider.getRepoService(isEnterprise()).getCollaborator(login, repo),
                response -> sendToView(view -> view.onNotifyAdapter(response != null ? response.getItems() : null)));
    }

    @NonNull @Override public ArrayList<User> getList() {
        return users;
    }
}

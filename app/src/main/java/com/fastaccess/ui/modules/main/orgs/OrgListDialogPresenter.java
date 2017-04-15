package com.fastaccess.ui.modules.main.orgs;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.model.User;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 15 Apr 2017, 1:54 PM
 */

public class OrgListDialogPresenter extends BasePresenter<OrgListDialogMvp.View> implements OrgListDialogMvp.Presenter {
    private ArrayList<User> orgs = new ArrayList<>();

    @Override public void onLoadOrgs() {
        makeRestCall(RestProvider.getOrgService().getMyOrganizations(),
                userPageable -> {
                    if (userPageable != null) {
                        if (userPageable.getItems() != null && !userPageable.getItems().isEmpty()) {
                            orgs.clear();
                            orgs.addAll(userPageable.getItems());
                        }
                    }
                    sendToView(OrgListDialogMvp.View::onNotifyAdapter);
                });
    }

    @NonNull @Override public ArrayList<User> getOrgs() {
        return orgs;
    }
}

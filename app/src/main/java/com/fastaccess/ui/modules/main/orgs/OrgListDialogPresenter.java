package com.fastaccess.ui.modules.main.orgs;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.model.User;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by Kosh on 15 Apr 2017, 1:54 PM
 */

public class OrgListDialogPresenter extends BasePresenter<OrgListDialogMvp.View> implements OrgListDialogMvp.Presenter {
    private ArrayList<User> orgs = new ArrayList<>();

    @Override public void onLoadOrgs() {
        makeRestCall(RestProvider.getOrgService().getMyOrganizations()
                .flatMap(userPageable -> userPageable.getItems() != null ? Observable.from(userPageable.getItems()) : Observable.empty())
                .map(user -> {
                    if (user != null) user.setType("Organization");
                    return user;
                })
                .toList(), list -> {
            List<User> myOrgs = new ArrayList<>();
            if (list != null && !list.isEmpty()) {
                myOrgs.addAll(list);
            }
            sendToView(view -> view.onNotifyAdapter(myOrgs));
        });
    }

    @NonNull @Override public ArrayList<User> getOrgs() {
        return orgs;
    }
}

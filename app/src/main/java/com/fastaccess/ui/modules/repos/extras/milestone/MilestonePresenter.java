package com.fastaccess.ui.modules.repos.extras.milestone;

import android.support.annotation.NonNull;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 04 Mar 2017, 9:41 PM
 */

public class MilestonePresenter extends BasePresenter<MilestoneMvp.View> implements MilestoneMvp.Presenter {
    private ArrayList<MilestoneModel> milestoneModels = new ArrayList<>();

    @Override public void onItemClick(int position, View v, MilestoneModel item) {
        if (getView() != null) getView().onMilestoneSelected(item);
    }

    @Override public void onItemLongClick(int position, View v, MilestoneModel item) {}

    @Override public void onLoadMilestones(@NonNull String login, @NonNull String repo) {
        makeRestCall(RestProvider.getRepoService(isEnterprise()).getMilestones(login, repo),
                response -> {
                    if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
                        sendToView(view -> view.showMessage(R.string.error, R.string.no_milestones));
                        return;
                    }
                    sendToView(view -> view.onNotifyAdapter(response.getItems()));
                });
    }

    @NonNull @Override public ArrayList<MilestoneModel> getMilestones() {
        return milestoneModels;
    }
}

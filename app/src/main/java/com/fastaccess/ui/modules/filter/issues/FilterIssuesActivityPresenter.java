package com.fastaccess.ui.modules.filter.issues;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

import lombok.Getter;
import rx.Observable;

/**
 * Created by Kosh on 09 Apr 2017, 6:22 PM
 */

@Getter public class FilterIssuesActivityPresenter extends BasePresenter<FilterIssuesActivityMvp.View> implements FilterIssuesActivityMvp.Presenter {

    @icepick.State @NonNull ArrayList<LabelModel> labels = new ArrayList<>();
    @icepick.State @NonNull ArrayList<MilestoneModel> milestones = new ArrayList<>();
    @icepick.State @NonNull ArrayList<User> assignees = new ArrayList<>();

    @Override public void onStart(@NonNull String login, @NonNull String repoId) {
        Observable<Pageable<MilestoneModel>> observable = RestProvider.getRepoService().getLabels(login, repoId)
                .flatMap(labelModelPageable -> {
                    if (labelModelPageable != null && labelModelPageable.getItems() != null) {
                        labels.clear();
                        labels.addAll(labelModelPageable.getItems());
                    }
                    return RestProvider.getRepoService().getAssignees(login, repoId);
                })
                .flatMap(userPageable -> {
                    if (userPageable != null && userPageable.getItems() != null) {
                        assignees.clear();
                        assignees.addAll(userPageable.getItems());
                    }
                    return RestProvider.getRepoService().getMilestones(login, repoId);
                });
        makeRestCall(observable, response -> {
            if (response != null && response.getItems() != null) {
                milestones.clear();
                milestones.addAll(response.getItems());
            }
            sendToView(BaseMvp.FAView::hideProgress);
        });
    }
}

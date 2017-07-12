package com.fastaccess.ui.modules.repos.extras.milestone.create;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.R;
import com.fastaccess.data.dao.CreateMilestoneModel;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.Date;

/**
 * Created by Kosh on 04 Mar 2017, 10:49 PM
 */

public class CreateMilestonePresenter extends BasePresenter<CreateMilestoneMvp.View> implements CreateMilestoneMvp.Presenter {
    @Override public void onSubmit(@Nullable String title, @Nullable String dueOn, @Nullable String description,
                                   @NonNull String login, @NonNull String repo) {
        if (getView() != null) {
            boolean isEmptyTitle = InputHelper.isEmpty(title);
            getView().onShowTitleError(isEmptyTitle);
            if (!isEmptyTitle) {
                CreateMilestoneModel createMilestoneModel = new CreateMilestoneModel();
                createMilestoneModel.setTitle(title);
                if (!InputHelper.isEmpty(dueOn)) {
                    Date date = ParseDateFormat.getDateFromString(dueOn);
                    if (date != null) createMilestoneModel.setDueOn(ParseDateFormat.toGithubDate(date));
                }
                if (!InputHelper.isEmpty(description)) {
                    createMilestoneModel.setDescription(description);
                }
                makeRestCall(RestProvider.getRepoService(isEnterprise()).createMilestone(login, repo, createMilestoneModel),
                        milestoneModel -> {
                            if (milestoneModel != null) {
                                sendToView(view -> view.onMilestoneAdded(milestoneModel));
                            } else {
                                sendToView(view -> view.showMessage(R.string.error, R.string.error_creating_milestone));
                            }
                        });
            }
        }
    }
}

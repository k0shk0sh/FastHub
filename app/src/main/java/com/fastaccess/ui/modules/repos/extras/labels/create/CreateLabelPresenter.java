package com.fastaccess.ui.modules.repos.extras.labels.create;

import android.support.annotation.NonNull;
import android.view.View;

import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

/**
 * Created by Kosh on 02 Apr 2017, 5:35 PM
 */

public class CreateLabelPresenter extends BasePresenter<CreateLabelMvp.View> implements CreateLabelMvp.Presenter {

    @Override public void onItemClick(int position, View v, String item) {
        if (getView() != null) {
            getView().onColorSelected(item);
        }
    }

    @Override public void onItemLongClick(int position, View v, String item) {}

    @Override public void onSubmitLabel(@NonNull String name, @NonNull String color, @NonNull String repo, @NonNull String login) {
        LabelModel labelModel = new LabelModel();
        labelModel.setColor(color.replaceAll("#", ""));
        labelModel.setName(name);
        makeRestCall(RestProvider.getRepoService(isEnterprise())
                        .addLabel(login, repo, labelModel),
                labelModel1 -> sendToView(view -> view.onSuccessfullyCreated(labelModel1)));
    }
}

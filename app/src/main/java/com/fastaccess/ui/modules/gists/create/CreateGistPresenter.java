package com.fastaccess.ui.modules.gists.create;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.CreateGistModel;
import com.fastaccess.data.dao.FilesListModel;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.HashMap;

/**
 * Created by Kosh on 30 Nov 2016, 10:51 AM
 */

class CreateGistPresenter extends BasePresenter<CreateGistMvp.View> implements CreateGistMvp.Presenter {
    @Override public void onSubmit(@NonNull String description, @NonNull HashMap<String, FilesListModel> files, boolean isPublic) {
        if (files.isEmpty()) return;
        CreateGistModel createGistModel = new CreateGistModel();
        createGistModel.setDescription(InputHelper.toString(description));
        createGistModel.setPublicGist(isPublic);
        createGistModel.setFiles(files);
        onSubmit(createGistModel);
    }

    @Override public void onSubmit(@NonNull CreateGistModel model) {
        makeRestCall(RestProvider.getGistService(isEnterprise()).createGist(model),
                gistsModel -> sendToView(view -> view.onSuccessSubmission(gistsModel)), false);
    }

    @Override public void onSubmitUpdate(@NonNull String id, @NonNull String description, @NonNull HashMap<String, FilesListModel> files) {
        boolean isEmptyDesc = InputHelper.isEmpty(description);
        if (getView() != null) {
            getView().onDescriptionError(isEmptyDesc);
        }
        if (isEmptyDesc) return;
        CreateGistModel createGistModel = new CreateGistModel();
        createGistModel.setDescription(InputHelper.toString(description));
        createGistModel.setFiles(files);
        makeRestCall(RestProvider.getGistService(isEnterprise()).editGist(createGistModel, id),
                gistsModel -> sendToView(view -> view.onSuccessSubmission(gistsModel)), false);
    }
}

package com.fastaccess.ui.modules.gists.create;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;

import com.fastaccess.data.dao.CreateGistModel;
import com.fastaccess.data.dao.FilesListModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.HashMap;

/**
 * Created by Kosh on 30 Nov 2016, 10:51 AM
 */

class CreateGistPresenter extends BasePresenter<CreateGistMvp.View> implements CreateGistMvp.Presenter {

    @Override public void onActivityForResult(int resultCode, int requestCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK && requestCode == BundleConstant.REQUEST_CODE) {
            if (intent != null && intent.getExtras() != null) {
                CharSequence charSequence = intent.getExtras().getCharSequence(BundleConstant.EXTRA);
                if (!InputHelper.isEmpty(charSequence)) {
                    sendToView(view -> view.onSetCode(charSequence));
                }
            }
        }
    }

    @Override public void onSubmit(@NonNull TextInputLayout description, @NonNull TextInputLayout fileName,
                                   @NonNull CharSequence fileContent, boolean isPublic) {
        boolean isEmptyDesc = InputHelper.isEmpty(description);
        boolean isEmptyFileName = InputHelper.isEmpty(fileName);
        boolean isEmptyFileContent = InputHelper.isEmpty(fileContent);
        if (getView() != null) {
            getView().onDescriptionError(isEmptyDesc);
            getView().onFileNameError(isEmptyDesc);
            getView().onFileContentError(isEmptyDesc);
        }
        if (!isEmptyDesc && !isEmptyFileName && !isEmptyFileContent) {
            CreateGistModel createGistModel = new CreateGistModel();
            createGistModel.setDescription(InputHelper.toString(description));
            createGistModel.setPublicGist(isPublic);
            HashMap<String, FilesListModel> modelHashMap = new HashMap<>();
            FilesListModel file = new FilesListModel();
            file.setFilename(InputHelper.toString(fileName));
            file.setContent(InputHelper.toString(fileContent));
            modelHashMap.put(InputHelper.toString(fileName), file);
            createGistModel.setFiles(modelHashMap);
            onSubmit(createGistModel);
        }
    }

    @Override public void onSubmit(@NonNull CreateGistModel model) {
        makeRestCall(RestProvider.getGistService(isEnterprise()).createGist(model),
                gistsModel -> sendToView(view -> view.onSuccessSubmission(gistsModel)));
    }
}

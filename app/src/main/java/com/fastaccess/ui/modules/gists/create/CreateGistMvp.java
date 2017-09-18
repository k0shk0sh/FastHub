package com.fastaccess.ui.modules.gists.create;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.CreateGistModel;
import com.fastaccess.data.dao.FilesListModel;
import com.fastaccess.data.dao.model.Gist;
import com.fastaccess.ui.base.mvp.BaseMvp;

import java.util.HashMap;

/**
 * Created by Kosh on 30 Nov 2016, 10:43 AM
 */

interface CreateGistMvp {

    interface View extends BaseMvp.FAView {
        void onDescriptionError(boolean isEmptyDesc);

        void onFileNameError(boolean isEmptyDesc);

        void onFileContentError(boolean isEmptyDesc);

        void onSuccessSubmission(Gist gistsModel);
    }

    interface Presenter extends BaseMvp.FAPresenter {

        void onSubmit(@NonNull String description, @NonNull HashMap<String, FilesListModel> files, boolean isPublic);

        void onSubmit(@NonNull CreateGistModel model);

        void onSubmitUpdate(@NonNull String id, @NonNull String description, @NonNull HashMap<String, FilesListModel> files);
    }
}

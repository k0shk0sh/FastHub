package com.fastaccess.ui.modules.gists.create;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;

import com.fastaccess.data.dao.CreateGistModel;
import com.fastaccess.data.dao.model.Gist;
import com.fastaccess.ui.base.mvp.BaseMvp;

/**
 * Created by Kosh on 30 Nov 2016, 10:43 AM
 */

interface CreateGistMvp {

    interface View extends BaseMvp.FAView {
        void onSetCode(@NonNull CharSequence charSequence);

        void onDescriptionError(boolean isEmptyDesc);

        void onFileNameError(boolean isEmptyDesc);

        void onFileContentError(boolean isEmptyDesc);

        void onSuccessSubmission(Gist gistsModel);
    }

    interface Presenter extends BaseMvp.FAPresenter {
        void onActivityForResult(int resultCode, int requestCode, Intent intent);

        void onSubmit(@NonNull TextInputLayout description, @NonNull TextInputLayout fileName,
                      @NonNull CharSequence fileContent, boolean isPublic);

        void onSubmit(@NonNull CreateGistModel model);
    }
}

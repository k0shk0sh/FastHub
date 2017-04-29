package com.fastaccess.ui.modules.search.repos.files;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.FontEditText;


interface SearchFileMvp {

    interface View extends BaseMvp.FAView {
        void onValidSearchQuery(@NonNull String query);
    }

    interface Presenter extends BaseMvp.FAPresenter {
        void onSearchClicked(@NonNull FontEditText editText, boolean inPath);

        void onActivityCreated(Bundle extras);
    }
}

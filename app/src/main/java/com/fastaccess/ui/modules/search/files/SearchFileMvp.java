package com.fastaccess.ui.modules.search.files;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.AutoCompleteTextView;

import com.fastaccess.data.dao.model.SearchHistory;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.search.code.SearchCodeFragment;

import java.util.ArrayList;


interface SearchFileMvp {

    interface View extends BaseMvp.FAView {
        void onNotifyAdapter(@Nullable SearchHistory query);
    }

    interface Presenter extends BaseMvp.FAPresenter {

        @NonNull
        ArrayList<SearchHistory> getHints();

        void onSearchClicked(@NonNull AutoCompleteTextView editText, @NonNull SearchCodeFragment searchCodeFragment);

        void onActivityCreated(Bundle extras);
    }
}

package com.fastaccess.ui.modules.search.repos.files;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.fastaccess.R;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.widgets.FontEditText;

public class SearchFilePresenter extends BasePresenter<SearchFileMvp.View> implements SearchFileMvp.Presenter {
    @com.evernote.android.state.State String repoId;
    @com.evernote.android.state.State String login;

    @Override protected void onAttachView(@NonNull SearchFileMvp.View view) {
        super.onAttachView(view);
    }

    @Override public void onSearchClicked(@NonNull FontEditText editText, boolean inPath) {
        boolean isEmpty = InputHelper.isEmpty(editText) || InputHelper.toString(editText).length() < 2;
        editText.setError(isEmpty ? editText.getResources().getString(R.string.minimum_three_chars) : null);
        if (!isEmpty) {
            AppHelper.hideKeyboard(editText);
            String query = InputHelper.toString(editText);
            if (getView() != null && isViewAttached()) getView().onValidSearchQuery(modifyQueryForFileSearch(query, inPath));
        }
    }

    @Override public void onActivityCreated(Bundle extras) {
        repoId = extras.getString(BundleConstant.ID);
        login = extras.getString(BundleConstant.EXTRA);
    }

    @NonNull private String modifyQueryForFileSearch(@NonNull String query, boolean inPath) {
        //restrict the search to file paths and the current repo user is looking at
        return query + "+" + "in:" + (inPath ? "path" : "" + "file") + "+" + "repo:" + login + "/" + repoId;
    }
}

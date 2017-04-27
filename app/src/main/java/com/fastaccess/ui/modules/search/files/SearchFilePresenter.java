package com.fastaccess.ui.modules.search.files;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.AutoCompleteTextView;

import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.fastaccess.data.dao.model.SearchHistory;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.search.code.SearchCodeFragment;

import java.util.ArrayList;

public class SearchFilePresenter extends BasePresenter<SearchFileMvp.View> implements SearchFileMvp.Presenter {

    private ArrayList<SearchHistory> hints = new ArrayList<>();
    private String repoId;
    private String login;

    @Override protected void onAttachView(@NonNull SearchFileMvp.View view) {
        super.onAttachView(view);
        if (hints.isEmpty()) {
            manageSubscription(SearchHistory.getHistory()
                    .subscribe(strings -> {
                        hints.clear();
                        if (strings != null) hints.addAll(strings);
                        view.onNotifyAdapter(null);
                    }));
        }
    }



    @NonNull
    @Override
    public ArrayList<SearchHistory> getHints() {
        return hints;
    }

    @Override
    public void onSearchClicked(@NonNull AutoCompleteTextView editText, @NonNull SearchCodeFragment searchCodeFragment) {
        boolean isEmpty = InputHelper.isEmpty(editText) || InputHelper.toString(editText).length() < 3;
        editText.setError(isEmpty ? editText.getResources().getString(R.string.minimum_three_chars) : null);
        if (!isEmpty) {
            editText.dismissDropDown();
            AppHelper.hideKeyboard(editText);
            String query = InputHelper.toString(editText);

            searchCodeFragment.onSetSearchQuery(modifyQueryForFileSearch(query));
            boolean noneMatch = Stream.of(hints).noneMatch(value -> value.getText().equalsIgnoreCase(query));
            if (noneMatch) {
                SearchHistory searchHistory = new SearchHistory(query);
                manageSubscription(searchHistory.save(searchHistory).subscribe());
                sendToView(view -> view.onNotifyAdapter(new SearchHistory(query)));
            }
        }
    }

    private String modifyQueryForFileSearch(String query) {
        //restrict the search to file paths and the current repo user is looking at
        return query + "+" + "in:path" + "+" + "repo:" + login + "/" + repoId;
    }

    @Override
    public void onActivityCreated(Bundle extras) {
        repoId = extras.getString(BundleConstant.ID);
        login = extras.getString(BundleConstant.EXTRA);
    }
}

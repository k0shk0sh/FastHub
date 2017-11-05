package com.fastaccess.ui.modules.search;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.widget.AutoCompleteTextView;

import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.fastaccess.data.dao.model.SearchHistory;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.search.code.SearchCodeFragment;
import com.fastaccess.ui.modules.search.issues.SearchIssuesFragment;
import com.fastaccess.ui.modules.search.repos.SearchReposFragment;
import com.fastaccess.ui.modules.search.users.SearchUsersFragment;

import java.util.ArrayList;


/**
 * Created by Kosh on 08 Dec 2016, 8:20 PM
 */
public class SearchPresenter extends BasePresenter<SearchMvp.View> implements SearchMvp.Presenter {
    private ArrayList<SearchHistory> hints = new ArrayList<>();

    @Override protected void onAttachView(@NonNull SearchMvp.View view) {
        super.onAttachView(view);
        if (hints.isEmpty()) {
            manageDisposable(SearchHistory.getHistory()
                    .subscribe(strings -> {
                        hints.clear();
                        if (strings != null) hints.addAll(strings);
                        view.onNotifyAdapter(null);
                    }));
        }
    }

    @NonNull @Override public ArrayList<SearchHistory> getHints() {
        return hints;
    }

    @Override public void onSearchClicked(@NonNull ViewPager viewPager, @NonNull AutoCompleteTextView editText) {
        boolean isEmpty = InputHelper.isEmpty(editText) || InputHelper.toString(editText).length() < 2;
        editText.setError(isEmpty ? editText.getResources().getString(R.string.minimum_three_chars) : null);
        if (!isEmpty) {
            editText.dismissDropDown();
            AppHelper.hideKeyboard(editText);
            String query = InputHelper.toString(editText);
            SearchReposFragment repos = (SearchReposFragment) viewPager.getAdapter().instantiateItem(viewPager, 0);
            SearchUsersFragment users = (SearchUsersFragment) viewPager.getAdapter().instantiateItem(viewPager, 1);
            SearchIssuesFragment issues = (SearchIssuesFragment) viewPager.getAdapter().instantiateItem(viewPager, 2);
            SearchCodeFragment code = (SearchCodeFragment) viewPager.getAdapter().instantiateItem(viewPager, 3);
            repos.onQueueSearch(query);
            users.onQueueSearch(query);
            issues.onQueueSearch(query);
            code.onQueueSearch(query, true);
            boolean noneMatch = Stream.of(hints).noneMatch(value -> value.getText().equalsIgnoreCase(query));
            if (noneMatch) {
                SearchHistory searchHistory = new SearchHistory(query);
                manageObservable(searchHistory.save(searchHistory).toObservable());
                sendToView(view -> view.onNotifyAdapter(new SearchHistory(query)));
            }
        }
    }
}

package com.fastaccess.ui.modules.search.users;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.model.User;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class SearchUsersPresenter extends BasePresenter<SearchUsersMvp.View> implements SearchUsersMvp.Presenter {

    private ArrayList<User> users = new ArrayList<>();
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;

    @Override public int getCurrentPage() {
        return page;
    }

    @Override public int getPreviousTotal() {
        return previousTotal;
    }

    @Override public void setCurrentPage(int page) {
        this.page = page;
    }

    @Override public void setPreviousTotal(int previousTotal) {
        this.previousTotal = previousTotal;
    }

    @Override public void onCallApi(int page, @Nullable String parameter) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        setCurrentPage(page);
        if (page > lastPage || lastPage == 0) {
            sendToView(SearchUsersMvp.View::hideProgress);
            return;
        }
        if (parameter == null) {
            return;
        }
        makeRestCall(RestProvider.getSearchService().searchUsers(parameter, page),
                response -> {
                    lastPage = response.getLast();
                    sendToView(view -> {
                        view.onNotifyAdapter(response.isIncompleteResults() ? null : response.getItems(), page);
                        view.onSetTabCount(response.getTotalCount());
                    });
                });
    }

    @NonNull @Override public ArrayList<User> getUsers() {
        return users;
    }

    @Override public void onItemClick(int position, View v, User item) {

    }

    @Override public void onItemLongClick(int position, View v, User item) {}
}

package com.fastaccess.ui.modules.pinned.repo;

import android.support.annotation.NonNull;
import android.view.View;

import com.fastaccess.data.dao.model.AbstractPinnedRepos;
import com.fastaccess.data.dao.model.PinnedRepos;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 25 Mar 2017, 8:00 PM
 */

public class PinnedReposPresenter extends BasePresenter<PinnedReposMvp.View> implements PinnedReposMvp.Presenter {
    private ArrayList<PinnedRepos> pinnedRepos = new ArrayList<>();

    @Override protected void onAttachView(@NonNull PinnedReposMvp.View view) {
        super.onAttachView(view);
        if (pinnedRepos.isEmpty()) {
            onReload();
        }
    }

    @NonNull @Override public ArrayList<PinnedRepos> getPinnedRepos() {
        return pinnedRepos;
    }

    @Override public void onReload() {
        manageDisposable(AbstractPinnedRepos.getMyPinnedRepos()
                .subscribe(repos -> sendToView(view -> view.onNotifyAdapter(repos)), throwable ->
                        sendToView(view -> view.onNotifyAdapter(null))));
    }

    @Override public void onItemClick(int position, View v, PinnedRepos item) {
        SchemeParser.launchUri(v.getContext(), item.getPinnedRepo().getHtmlUrl());
    }

    @Override public void onItemLongClick(int position, View v, PinnedRepos item) {
        if (getView() != null) {
            if (item.getRepoFullName().equalsIgnoreCase("k0shk0sh/FastHub")) {
                return;
            }
            getView().onDeletePinnedRepo(item.getId(), position);
        }
    }
}

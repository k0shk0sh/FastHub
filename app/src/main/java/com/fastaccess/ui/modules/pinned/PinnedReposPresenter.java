package com.fastaccess.ui.modules.pinned;

import android.support.annotation.NonNull;
import android.view.View;

import com.fastaccess.data.dao.NameParser;
import com.fastaccess.data.dao.model.AbstractPinnedRepos;
import com.fastaccess.data.dao.model.PinnedRepos;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.RepoPagerActivity;

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
            if (!AbstractPinnedRepos.isPinned("k0shk0sh/FastHub"))
                manageSubscription(Repo.getRepo("FastHub", "k0shk0sh")
                        .map(repo -> repo != null && AbstractPinnedRepos.pinUpin(repo))
                        .subscribe());
        }
    }

    @NonNull @Override public ArrayList<PinnedRepos> getPinnedRepos() {
        return pinnedRepos;
    }

    @Override public void onReload() {
        manageSubscription(AbstractPinnedRepos.getMyPinnedRepos()
                .subscribe(repos -> sendToView(view -> view.onNotifyAdapter(repos))));
    }

    @Override public void onItemClick(int position, View v, PinnedRepos item) {
        RepoPagerActivity.startRepoPager(v.getContext(), new NameParser(item.getPinnedRepo().getHtmlUrl()));
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

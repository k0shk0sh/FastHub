package com.fastaccess.ui.modules.pinned.gist;

import android.support.annotation.NonNull;
import android.view.View;

import com.fastaccess.data.dao.model.Gist;
import com.fastaccess.data.dao.model.PinnedGists;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 25 Mar 2017, 8:00 PM
 */

public class PinnedGistPresenter extends BasePresenter<PinnedGistMvp.View> implements PinnedGistMvp.Presenter {
    private ArrayList<Gist> issues = new ArrayList<>();

    @Override protected void onAttachView(@NonNull PinnedGistMvp.View view) {
        super.onAttachView(view);
        if (issues.isEmpty()) {
            onReload();
        }
    }

    @NonNull @Override public ArrayList<Gist> getPinnedGists() {
        return issues;
    }

    @Override public void onReload() {
        manageDisposable(PinnedGists.getMyPinnedGists()
                .subscribe(repos -> sendToView(view -> view.onNotifyAdapter(repos)), throwable ->
                        sendToView(view -> view.onNotifyAdapter(null))));
    }

    @Override public void onItemClick(int position, View v, Gist item) {
        SchemeParser.launchUri(v.getContext(), !InputHelper.isEmpty(item.getHtmlUrl()) ? item.getHtmlUrl() : item.getUrl());
    }

    @Override public void onItemLongClick(int position, View v, Gist item) {
        if (getView() != null) {
            getView().onDeletePinnedGist(item.getId(), position);
        }
    }
}

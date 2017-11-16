package com.fastaccess.ui.modules.gists;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.model.Gist;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.gists.gist.GistActivity;

import java.util.ArrayList;

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

class GistsPresenter extends BasePresenter<GistsMvp.View> implements GistsMvp.Presenter {
    private ArrayList<Gist> gistsModels = new ArrayList<>();
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

    @Override public void onError(@NonNull Throwable throwable) {
        onWorkOffline();
        super.onError(throwable);
    }

    @Override public boolean onCallApi(int page, @Nullable Object parameter) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0) {
            sendToView(GistsMvp.View::hideProgress);
            return false;
        }
        setCurrentPage(page);
        makeRestCall(RestProvider.getGistService(isEnterprise()).getPublicGists(RestProvider.PAGE_SIZE, page),
                listResponse -> {
                    lastPage = listResponse.getLast();
                    sendToView(view -> view.onNotifyAdapter(listResponse.getItems(), page));
                });
        return true;
    }

    @NonNull @Override public ArrayList<Gist> getGists() {
        return gistsModels;
    }

    @Override public void onWorkOffline() {
        if (gistsModels.isEmpty()) {
            manageDisposable(RxHelper.getObservable(Gist.getGists().toObservable())
                    .subscribe(gists -> sendToView(view -> view.onNotifyAdapter(gists, 1))));
        } else {
            sendToView(GistsMvp.View::hideProgress);
        }
    }

    @Override public void onItemClick(int position, View v, Gist item) {
        v.getContext().startActivity(GistActivity.createIntent(v.getContext(), item.getGistId(), isEnterprise()));
    }

    @Override public void onItemLongClick(int position, View v, Gist item) {}
}

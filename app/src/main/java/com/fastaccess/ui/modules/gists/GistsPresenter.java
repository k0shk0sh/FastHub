package com.fastaccess.ui.modules.gists;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.GistsModel;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.gists.gist.GistView;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

class GistsPresenter extends BasePresenter<GistsMvp.View> implements GistsMvp.Presenter {
    private ArrayList<GistsModel> gistsModels = new ArrayList<>();
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

    @Override public void onCallApi(int page, @Nullable Object parameter) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0) {
            sendToView(GistsMvp.View::hideProgress);
            return;
        }
        setCurrentPage(page);
        makeRestCall(RestProvider.getGistService().getPublicGists(RestProvider.PAGE_SIZE, page),
                listResponse -> {
                    lastPage = listResponse.getLast();
                    if (getCurrentPage() == 1) {
                        getGists().clear();
                        manageSubscription(GistsModel.save(listResponse.getItems()).subscribe());
                    }
                    getGists().addAll(listResponse.getItems());
                    sendToView(GistsMvp.View::onNotifyAdapter);
                });
    }

    @NonNull @Override public ArrayList<GistsModel> getGists() {
        return gistsModels;
    }

    @Override public void onWorkOffline() {
        if (gistsModels.isEmpty()) {
            manageSubscription(RxHelper.getObserver(GistsModel.getGists()).subscribe(gistsModels1 -> {
                gistsModels.addAll(gistsModels1);
                sendToView(GistsMvp.View::onNotifyAdapter);
            }));
        } else {
            sendToView(GistsMvp.View::hideProgress);
        }
    }

    @Override public void onItemClick(int position, View v, GistsModel item) {
        v.getContext().startActivity(GistView.createIntent(v.getContext(), item.getGistId()));
    }

    @Override public void onItemLongClick(int position, View v, GistsModel item) {
        onItemClick(position, v, item);
    }
}

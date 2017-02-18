package com.fastaccess.ui.modules.profile.gists;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.GistsModel;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.gists.gist.GistView;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

class ProfileGistsPresenter extends BasePresenter<ProfileGistsMvp.View> implements ProfileGistsMvp.Presenter {
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

    @Override public <T> T onError(@NonNull Throwable throwable, @NonNull Observable<T> observable) {
        sendToView(view -> {
            if (view.getLoadMore().getParameter() != null) {
                onWorkOffline(view.getLoadMore().getParameter());
            }
        });
        return super.onError(throwable, observable);
    }

    @Override public void onCallApi(int page, @Nullable String parameter) {
        if (parameter == null) {
            throw new NullPointerException("Username is null");
        }
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        setCurrentPage(page);
        if (page > lastPage || lastPage == 0) {
            sendToView(ProfileGistsMvp.View::hideProgress);
            return;
        }
        makeRestCall(RestProvider.getGistService().getUserGists(parameter, RestProvider.PAGE_SIZE, page),
                listResponse -> {
                    lastPage = listResponse.getLast();
                    if (getCurrentPage() == 1) {
                        getGists().clear();
                        manageSubscription(GistsModel.save(listResponse.getItems(), parameter).subscribe());
                    }
                    getGists().addAll(listResponse.getItems());
                    sendToView(ProfileGistsMvp.View::onNotifyAdapter);
                });
    }

    @NonNull @Override public ArrayList<GistsModel> getGists() {
        return gistsModels;
    }

    @Override public void onWorkOffline(@NonNull String login) {
        if (gistsModels.isEmpty()) {
            manageSubscription(GistsModel.getMyGists(login).subscribe(gistsModels1 -> {
                gistsModels.addAll(gistsModels1);
                sendToView(ProfileGistsMvp.View::onNotifyAdapter);
            }));
        } else {
            sendToView(ProfileGistsMvp.View::hideProgress);
        }
    }

    @Override public void onItemClick(int position, View v, GistsModel item) {
        v.getContext().startActivity(GistView.createIntent(v.getContext(), item.getGistId()));
    }

    @Override public void onItemLongClick(int position, View v, GistsModel item) {
        onItemClick(position, v, item);
    }
}

package com.fastaccess.ui.modules.profile.gists;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.annimon.stream.Stream;
import com.fastaccess.data.dao.model.Gist;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

class ProfileGistsPresenter extends BasePresenter<ProfileGistsMvp.View> implements ProfileGistsMvp.Presenter {
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
        sendToView(view -> {
            if (view.getLoadMore().getParameter() != null) {
                onWorkOffline(view.getLoadMore().getParameter());
            }
        });
        super.onError(throwable);
    }

    @Override public boolean onCallApi(int page, @Nullable String parameter) {
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
            return false;
        }
        makeRestCall(RestProvider.getGistService(isEnterprise()).getUserGists(parameter, page),
                listResponse -> {
                    lastPage = listResponse.getLast();
                    sendToView(view -> view.onNotifyAdapter(listResponse.getItems(), page));
                    manageDisposable(Gist.save(Stream.of(listResponse.getItems()).toList(), parameter));
                });
        return true;
    }

    @NonNull @Override public ArrayList<Gist> getGists() {
        return gistsModels;
    }

    @Override public void onWorkOffline(@NonNull String login) {
        if (gistsModels.isEmpty()) {
            manageDisposable(RxHelper.getObservable(Gist.getMyGists(login).toObservable()).subscribe(gistsModels1 ->
                    sendToView(view -> view.onNotifyAdapter(gistsModels1, 1))));
        } else {
            sendToView(ProfileGistsMvp.View::hideProgress);
        }
    }

    @Override public void onItemClick(int position, View v, Gist item) {
        SchemeParser.launchUri(v.getContext(), item.getHtmlUrl());
    }

    @Override public void onItemLongClick(int position, View v, Gist item) {}
}

package com.fastaccess.ui.modules.gists.gist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.GistsModel;
import com.fastaccess.data.dao.LoginModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

/**
 * Created by Kosh on 12 Nov 2016, 12:17 PM
 */

class GistPresenter extends BasePresenter<GistMvp.View> implements GistMvp.Presenter {


    private boolean isGistStarred;
    private boolean isGistForked;
    private GistsModel gist;
    private String gistId;

    @Nullable @Override public GistsModel getGist() {
        return gist;
    }

    @NonNull @Override public String gistId() {
        return gistId;
    }

    @SuppressWarnings("unchecked") @Override public void onActivityCreated(@Nullable Intent intent) {
        if (intent == null || intent.getExtras() == null) {
            return;
        }
        Bundle bundle = intent.getExtras();
        gistId = bundle.getString(BundleConstant.EXTRA);
        if (gist != null) {
            checkStarring(gist.getGistId());
            sendToView(GistMvp.View::onSetupDetails);
        } else if (gistId != null) {
            checkStarring(gistId);
            makeRestCall(RestProvider.getGistService().getGist(gistId),
                    gistsModel -> {
                        this.gist = gistsModel;
                        sendToView(GistMvp.View::onSetupDetails);
                    });
        } else {
            sendToView(GistMvp.View::onSetupDetails); // tell the activity to finish!
        }
    }

    @Override public void onDeleteGist() {
        if (getGist() == null) return;
        manageSubscription(RxHelper.getObserver(RestProvider.getGistService().deleteGist(getGist().getGistId()))
                .doOnSubscribe(this::onSubscribed)
                .doOnNext(booleanResponse -> {
                    if (booleanResponse.code() == 204) {
                        sendToView(GistMvp.View::onSuccessDeleted);
                    } else {
                        sendToView(GistMvp.View::onErrorDeleting);
                    }
                })
                .onErrorReturn(throwable -> {
                    sendToView(view -> view.showErrorMessage(throwable.getMessage()));
                    return null;
                })
                .subscribe());
    }

    @Override public boolean isOwner() {
        return getGist() != null && getGist().getOwner() != null &&
                getGist().getOwner().getLogin().equals(LoginModel.getUser().getLogin());
    }

    @Override public void onStarGist() {
        isGistStarred = !isGistStarred;
        sendToView(view -> view.onGistStarred(isGistStarred));
    }

    @Override public void onForkGist() {
        isGistForked = !isGistForked;
        sendToView(view -> view.onGistForked(isGistForked));
    }

    @Override public boolean isForked() {
        return isGistForked;
    }

    @Override public boolean isStarred() {
        return isGistStarred;
    }

    @Override public void checkStarring(@NonNull String gistId) {
        makeRestCall(RestProvider.getGistService().checkGistStar(gistId),
                booleanResponse -> {
                    isGistStarred = booleanResponse.code() == 204;
                    sendToView(view -> view.onGistStarred(isGistStarred));
                });
    }

    @Override public void onWorkOffline(@NonNull String gistId) {
        if (gist == null) {
            manageSubscription(RxHelper.getObserver(GistsModel.getGist(gistId))
                    .subscribe(gistsModel -> {
                        this.gist = gistsModel;
                        sendToView(GistMvp.View::onSetupDetails);
                    }));
        }
    }
}

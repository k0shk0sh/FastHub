package com.fastaccess.ui.modules.gists.gist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.model.Gist;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.PinnedGists;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

/**
 * Created by Kosh on 12 Nov 2016, 12:17 PM
 */

class GistPresenter extends BasePresenter<GistMvp.View> implements GistMvp.Presenter {
    @com.evernote.android.state.State boolean isGistStarred;
    @com.evernote.android.state.State boolean isGistForked;
    @com.evernote.android.state.State Gist gist;
    @com.evernote.android.state.State String gistId;

    @Nullable @Override public Gist getGist() {
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
            callApi();
        } else {
            sendToView(GistMvp.View::onSetupDetails);
        }
    }

    @Override public void onDeleteGist() {
        if (getGist() == null) return;
        manageDisposable(RxHelper.getObservable(RestProvider.getGistService(isEnterprise()).deleteGist(getGist().getGistId()))
                .doOnSubscribe(disposable -> onSubscribed(false))
                .doOnNext(booleanResponse -> {
                    if (booleanResponse.code() == 204) {
                        sendToView(GistMvp.View::onSuccessDeleted);
                    } else {
                        sendToView(GistMvp.View::onErrorDeleting);
                    }
                })
                .subscribe(booleanResponse -> {/**/}, throwable -> sendToView(view -> view.showErrorMessage(throwable.getMessage()))));
    }

    @Override public boolean isOwner() {
        return getGist() != null && getGist().getOwner() != null &&
                getGist().getOwner().getLogin().equals(Login.getUser().getLogin());
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
        makeRestCall(RestProvider.getGistService(isEnterprise()).checkGistStar(gistId),
                booleanResponse -> {
                    isGistStarred = booleanResponse.code() == 204;
                    sendToView(view -> view.onGistStarred(isGistStarred));
                });
    }

    @Override public void onWorkOffline(@NonNull String gistId) {
        if (gist == null) {
            manageDisposable(RxHelper.getObservable(Gist.getGist(gistId))
                    .subscribe(gistsModel -> {
                        this.gist = gistsModel;
                        sendToView(GistMvp.View::onSetupDetails);
                    }));
        }
    }

    @Override public void onPinUnpinGist() {
        if (getGist() == null) return;
        PinnedGists.pinUpin(getGist());
        sendToView(view -> view.onUpdatePinIcon(getGist()));
    }

    @Override public void callApi() {
        if (!InputHelper.isEmpty(gistId)) {
            checkStarring(gistId);
            makeRestCall(RestProvider.getGistService(isEnterprise()).getGist(gistId), gistsModel -> {
                this.gist = gistsModel;
                sendToView(GistMvp.View::onSetupDetails);
            });
        }
    }
}

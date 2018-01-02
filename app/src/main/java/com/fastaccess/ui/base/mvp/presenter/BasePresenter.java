package com.fastaccess.ui.base.mvp.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.evernote.android.state.StateSaver;
import com.fastaccess.R;
import com.fastaccess.data.dao.GitHubErrorResponse;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;

import net.grandcentrix.thirtyinch.TiPresenter;
import net.grandcentrix.thirtyinch.rx2.RxTiPresenterDisposableHandler;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import retrofit2.HttpException;


/**
 * Created by Kosh on 25 May 2016, 9:12 PM
 */

public class BasePresenter<V extends BaseMvp.FAView> extends TiPresenter<V> implements BaseMvp.FAPresenter {
    @com.evernote.android.state.State boolean enterprise;

    private boolean apiCalled;
    private final RxTiPresenterDisposableHandler subscriptionHandler = new RxTiPresenterDisposableHandler(this);

    @Override public void onSaveInstanceState(Bundle outState) {
        StateSaver.saveInstanceState(this, outState);
    }

    @Override public void onRestoreInstanceState(Bundle outState) {
        if (outState != null) StateSaver.restoreInstanceState(this, outState);
    }

    @Override public void manageDisposable(@Nullable Disposable... disposables) {
        if (disposables != null) {
            subscriptionHandler.manageDisposables(disposables);
        }
    }

    @Override public <T> void manageObservable(@Nullable Observable<T> observable) {
        if (observable != null) {
            manageDisposable(RxHelper.getObservable(observable).subscribe(t -> {/**/}, Throwable::printStackTrace));
        }
    }

    @Override public void manageViewDisposable(@Nullable Disposable... disposables) {
        if (disposables != null) {
            if (isViewAttached()) {
                subscriptionHandler.manageViewDisposables(disposables);
            } else {
                sendToView(v -> manageViewDisposable(disposables));
            }
        }
    }

    @Override public boolean isApiCalled() {
        return apiCalled;
    }

    @Override public void onSubscribed(boolean cancelable) {
        sendToView(v -> {
            if (cancelable) {
                v.showProgress(R.string.in_progress);
            } else {
                v.showBlockingProgress(R.string.in_progress);
            }
        });
    }

    @Override public void onError(@NonNull Throwable throwable) {
        apiCalled = true;
        throwable.printStackTrace();
        int code = RestProvider.getErrorCode(throwable);
        if (code == 401) {
            sendToView(BaseMvp.FAView::onRequireLogin);
            return;
        }
        GitHubErrorResponse errorResponse = RestProvider.getErrorResponse(throwable);
        if (errorResponse != null && errorResponse.getMessage() != null) {
            sendToView(v -> v.showErrorMessage(errorResponse.getMessage()));
        } else {
            sendToView(v -> v.showMessage(R.string.error, getPrettifiedErrorMessage(throwable)));
        }
    }

    @Override public <T> void makeRestCall(@NonNull Observable<T> observable, @NonNull Consumer<T> onNext) {
        makeRestCall(observable, onNext, true);
    }

    @Override public <T> void makeRestCall(@NonNull Observable<T> observable, @NonNull Consumer<T> onNext, boolean cancelable) {
        manageDisposable(
                RxHelper.getObservable(observable)
                        .doOnSubscribe(disposable -> onSubscribed(cancelable))
                        .subscribe(onNext, this::onError, () -> apiCalled = true)
        );
    }

    @StringRes private int getPrettifiedErrorMessage(@Nullable Throwable throwable) {
        int resId = R.string.network_error;
        if (throwable instanceof HttpException) {
            resId = R.string.network_error;
        } else if (throwable instanceof IOException) {
            resId = R.string.request_error;
        } else if (throwable instanceof TimeoutException) {
            resId = R.string.unexpected_error;
        }
        return resId;
    }

    public void onCheckGitHubStatus() {
        manageObservable(RestProvider.gitHubStatus()
                .doOnNext(gitHubStatusModel -> {
                    if (!"good".equalsIgnoreCase(gitHubStatusModel.getStatus())) {
                        sendToView(v -> v.showErrorMessage("Github Status:\n" + gitHubStatusModel.getBody()));
                    }
                }));
    }

    public boolean isEnterprise() {
        return enterprise;
    }

    public void setEnterprise(boolean enterprise) {
        this.enterprise = enterprise;
    }
}

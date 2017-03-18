package com.fastaccess.ui.base.mvp.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.fastaccess.R;
import com.fastaccess.data.dao.GitHubErrorResponse;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.rest.handler.RetrofitException;
import com.fastaccess.ui.base.mvp.BaseMvp;

import net.grandcentrix.thirtyinch.TiPresenter;
import net.grandcentrix.thirtyinch.rx.RxTiPresenterSubscriptionHandler;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Kosh on 25 May 2016, 9:12 PM
 */

public class BasePresenter<V extends BaseMvp.FAView> extends TiPresenter<V> implements BaseMvp.FAPresenter {
    private boolean apiCalled;
    private RxTiPresenterSubscriptionHandler subscriptionHandler = new RxTiPresenterSubscriptionHandler(this);

    @Override public void manageSubscription(@Nullable Subscription... subscription) {
        if (subscription != null) {
            subscriptionHandler.manageSubscription(subscription);
        }
    }

    @Override public boolean isApiCalled() {
        return apiCalled;
    }

    @Override public void onSubscribed() {
        sendToView(v -> v.showProgress(R.string.in_progress));
    }

    @Override public void onError(@NonNull Throwable throwable) {
        throwable.printStackTrace();
        if (RestProvider.getErrorCode(throwable) == 401) {
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

    @Override public <T> void makeRestCall(@NonNull Observable<T> observable, @NonNull Action1<T> onNext) {
        manageSubscription(
                RxHelper.getObserver(observable)
                        .doOnSubscribe(this::onSubscribed)
                        .subscribe(onNext, this::onError, () -> apiCalled = true)
        );
    }

    @StringRes private int getPrettifiedErrorMessage(@Nullable Throwable throwable) {
        int resId = R.string.network_error;
        RetrofitException exception = (RetrofitException) throwable;
        if (exception != null) {
            if (exception.getKind() != null) {
                switch (exception.getKind()) {
                    case NETWORK:
                        resId = R.string.network_error;
                        break;
                    case HTTP:
                        resId = R.string.request_error;
                        break;
                    case UNEXPECTED:
                        resId = R.string.unexpected_error;
                        break;
                }
            }
        }
        return resId;
    }
}

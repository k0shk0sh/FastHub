package com.fastaccess.helper;

import android.support.annotation.NonNull;

import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Kosh on 11 Nov 2016, 11:53 AM
 */

public class RxHelper {
    public static <T> Observable<T> getObserver(@NonNull Observable<T> observable) {
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable<T> safeObservable(@NonNull Observable<T> observable) {
        return getObserver(observable)
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> null);
    }

    public static <T> Single<T> safeObservable(@NonNull Single<T> observable) {
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> null);
    }
}

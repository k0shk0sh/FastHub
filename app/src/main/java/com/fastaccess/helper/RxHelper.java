package com.fastaccess.helper;

import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Kosh on 11 Nov 2016, 11:53 AM
 */

public class RxHelper {
    public static <T> Observable<T> getObservable(@NonNull Observable<T> observable) {
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable<T> safeObservable(@NonNull Observable<T> observable) {
        return getObservable(observable)
                .doOnError(Throwable::printStackTrace);
    }

    public static <T> Single<T> getSingle(@NonNull Single<T> single) {
        return single
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}

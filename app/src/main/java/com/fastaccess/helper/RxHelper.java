package com.fastaccess.helper;

import android.support.annotation.NonNull;

import rx.Observable;
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

    public static <T> Observable<T> getObserverComputation(@NonNull Observable<T> observable) {
        return observable
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }
}

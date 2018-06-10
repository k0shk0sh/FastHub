package com.fastaccess.domain.usecase.base

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Kosh on 12.05.18.
 */
abstract class BaseObservableUseCase<T> : BaseUseCase<T>() {
    abstract override fun buildObservable(): Observable<T>

    fun disposeAndExecuteObservable(observer: Observer<T>) {
        disposeAndExecute(buildObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { observer.onSubscribe(it) }
                .subscribe
                (
                        { observer.onNext(it) },
                        { observer.onError(it) },
                        { observer.onComplete() }
                ))
    }

    fun executeObservable(observer: Observer<T>) {
        execute(buildObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { observer.onSubscribe(it) }
                .subscribe
                (
                        { observer.onNext(it) },
                        { observer.onError(it) },
                        { observer.onComplete() }
                ))
    }

    fun executeSafely(observable: Observable<T>) {
        execute(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, { t -> t.printStackTrace() }))
    }
}

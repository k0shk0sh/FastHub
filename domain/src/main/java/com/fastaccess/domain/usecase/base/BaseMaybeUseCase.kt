package com.fastaccess.domain.usecase.base

import io.reactivex.Maybe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Kosh on 12.05.18.
 */
abstract class BaseMaybeUseCase<T> : BaseUseCase<T>() {
    abstract override fun buildMaybe(): Maybe<T>

    fun disposeAndExecuteObservable(observer: Observer<T>) {
        disposeAndExecute(buildMaybe()
                .doOnSubscribe { observer.onSubscribe(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe
                (
                        { observer.onNext(it) },
                        { observer.onError(it) },
                        { observer.onComplete() }
                ))
    }

    fun executeObservable(observer: Observer<T>) {
        execute(buildMaybe()
                .doOnSubscribe { observer.onSubscribe(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe
                (
                        { observer.onNext(it) },
                        { observer.onError(it) },
                        { observer.onComplete() }
                ))
    }
}

package com.fastaccess.domain.usecase.base

import io.reactivex.Maybe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Kosh on 12.05.18.
 */
abstract class BaseMaybeUseCase : BaseUseCase() {
    abstract override fun buildMaybe(): Maybe<*>

    fun disposeAndExecuteObservable(observer: Observer<Any>) {
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

    fun executeObservable(observer: Observer<Any>) {
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

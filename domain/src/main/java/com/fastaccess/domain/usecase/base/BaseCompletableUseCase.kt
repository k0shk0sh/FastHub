package com.fastaccess.domain.usecase.base

import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Kosh on 12.05.18.
 */
abstract class BaseCompletableUseCase : BaseUseCase() {
    abstract override fun buildCompletable(): Completable

    fun disposeAndExecuteObservable(observer: CompletableObserver) {
        disposeAndExecute(buildCompletable()
                .doOnSubscribe { observer.onSubscribe(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe
                (
                        { observer.onComplete() },
                        { observer.onError(it) }
                ))
    }

    fun executeObservable(observer: CompletableObserver) {
        execute(buildCompletable()
                .doOnSubscribe { observer.onSubscribe(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe
                (
                        { observer.onComplete() },
                        { observer.onError(it) }
                ))
    }
}

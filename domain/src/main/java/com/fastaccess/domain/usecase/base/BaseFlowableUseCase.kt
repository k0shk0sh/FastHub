package com.fastaccess.domain.usecase.base

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber

/**
 * Created by Kosh on 12.05.18.
 */
abstract class BaseFlowableUseCase<T> : BaseUseCase<T>() {
    abstract override fun buildFlowable(): Flowable<T>

    fun disposeAndExecuteObservable(observer: DisposableSubscriber<T>) {
        disposeAndExecute(buildFlowable()
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

    fun executeObservable(observer: DisposableSubscriber<T>) {
        execute(buildFlowable()
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

package com.fastaccess.domain.usecase.base

import io.reactivex.Observable
import io.reactivex.Observer

/**
 * Created by Kosh on 12.05.18.
 */
abstract class BaseObservableUseCase : BaseUseCase() {
    abstract override fun buildObservable(): Observable<*>

    fun disposeAndExecuteObservable(observer: Observer<Any>) {
        disposeAndExecute(buildObservable()
            .doOnSubscribe { observer.onSubscribe(it) }
            .subscribe(
                { observer.onNext(it) },
                { observer.onError(it) },
                { observer.onComplete() }
            ))
    }

    fun executeObservable(observer: Observer<Any>) {
        execute(buildObservable()
            .doOnSubscribe { observer.onSubscribe(it) }
            .subscribe(
                { observer.onNext(it) },
                { observer.onError(it) },
                { observer.onComplete() }
            ))
    }

    fun <T> executeSafely(observable: Observable<T>) {
        execute(observable.subscribe({}, { t -> t.printStackTrace() }))
    }
}

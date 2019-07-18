package com.fastaccess.domain.usecase.base

import io.reactivex.Maybe
import io.reactivex.Observer

/**
 * Created by Kosh on 12.05.18.
 */
abstract class BaseMaybeUseCase : BaseUseCase() {
    abstract override fun buildMaybe(): Maybe<*>

    fun disposeAndExecuteObservable(observer: Observer<Any>) {
        disposeAndExecute(buildMaybe()
            .doOnSubscribe { observer.onSubscribe(it) }
            .subscribe(
                { observer.onNext(it) },
                { observer.onError(it) },
                { observer.onComplete() }
            ))
    }

    fun executeObservable(observer: Observer<Any>) {
        execute(buildMaybe()
            .doOnSubscribe { observer.onSubscribe(it) }
            .subscribe(
                { observer.onNext(it) },
                { observer.onError(it) },
                { observer.onComplete() }
            ))
    }
}

package com.fastaccess.domain.usecase.base

import io.reactivex.Single
import io.reactivex.SingleObserver

/**
 * Created by Kosh on 12.05.18.
 */
abstract class BaseSingleUseCase : BaseUseCase() {
    abstract override fun buildSingle(): Single<*>

    fun disposeAndExecuteObservable(observer: SingleObserver<Any>) {
        disposeAndExecute(buildSingle()
            .doOnSubscribe { observer.onSubscribe(it) }
            .subscribe(
                { observer.onSuccess(it) },
                { observer.onError(it) }
            ))
    }

    fun executeObservable(observer: SingleObserver<Any>) {
        execute(buildSingle()
            .doOnSubscribe { observer.onSubscribe(it) }
            .subscribe(
                { observer.onSuccess(it) },
                { observer.onError(it) }
            ))
    }
}

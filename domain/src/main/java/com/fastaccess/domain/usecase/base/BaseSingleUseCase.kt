package com.fastaccess.domain.usecase.base

import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Kosh on 12.05.18.
 */
abstract class BaseSingleUseCase<T> : BaseUseCase<T>() {
    abstract override fun buildSingle(): Single<T>

    fun disposeAndExecuteObservable(observer: SingleObserver<T>) {
        disposeAndExecute(buildSingle()
                .doOnSubscribe { observer.onSubscribe(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe
                (
                        { observer.onSuccess(it) },
                        { observer.onError(it) }
                ))
    }

    fun executeObservable(observer: SingleObserver<T>) {
        execute(buildSingle()
                .doOnSubscribe { observer.onSubscribe(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe
                (
                        { observer.onSuccess(it) },
                        { observer.onError(it) }
                ))
    }
}

package com.fastaccess.domain.usecase.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Kosh on 12.05.18.
 */
abstract class BaseUseCase : Disposable, BaseUseCaseDefault {

    private val disposable = CompositeDisposable()

    override fun isDisposed() = disposable.isDisposed

    override fun dispose() = disposable.dispose()

    protected fun disposeAndExecute(disposableSubscriber: Disposable) {
        this.disposable.delete(disposableSubscriber)
        this.disposable.add(disposableSubscriber)
    }

    protected fun execute(disposableSubscriber: Disposable) {
        this.disposable.add(disposableSubscriber)
    }

}
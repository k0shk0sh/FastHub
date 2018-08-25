package com.fastaccess.domain

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by Kosh on 03.06.18.
 */
class FastHubObserver<T> : Observer<T> {
    override fun onComplete() = Unit
    override fun onSubscribe(d: Disposable) = Unit
    override fun onNext(t: T) = Unit
    override fun onError(e: Throwable) = e.printStackTrace()
}
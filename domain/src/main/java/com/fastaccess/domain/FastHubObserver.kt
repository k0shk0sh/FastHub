package com.fastaccess.domain

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by Kosh on 03.06.18.
 */
class FastHubObserver<T> : Observer<T> {
    override fun onComplete() {

    }

    override fun onSubscribe(d: Disposable) {

    }

    override fun onNext(t: T) {

    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
    }
}
package com.fastaccess.domain.rx

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by Kosh on 09.06.18.
 */
class FastHubSubscriber<T> : Observer<T> {
    override fun onComplete() {}

    override fun onSubscribe(d: Disposable) {}

    override fun onNext(t: T) {}

    override fun onError(e: Throwable) {
        e.printStackTrace()
    }
}
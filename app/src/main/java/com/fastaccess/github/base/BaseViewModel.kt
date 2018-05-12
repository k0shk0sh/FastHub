package com.fastaccess.github.base

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Kosh on 12.05.18.
 */
abstract class BaseViewModel : ViewModel() {
    private val disposable = CompositeDisposable()
    protected fun add(disposable: Disposable) = this.disposable.add(disposable)
    protected fun disposeAll() = disposable.clear()
    override fun onCleared() {
        super.onCleared()
        disposeAll()
    }
}
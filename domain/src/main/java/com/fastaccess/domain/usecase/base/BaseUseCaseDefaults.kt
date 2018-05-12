package com.fastaccess.domain.usecase.base

import io.reactivex.*

interface BaseUseCaseDefaults<T> {
    fun buildObservable(): Observable<T>? = null
    fun buildSingle(): Single<T>? = null
    fun buildFlowable(): Flowable<T>? = null
    fun buildMaybe(): Maybe<T>? = null
    fun buildCompletable(): Completable? = null
}
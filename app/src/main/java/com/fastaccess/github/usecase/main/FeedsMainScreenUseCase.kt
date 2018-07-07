package com.fastaccess.github.usecase.main

import com.fastaccess.data.repository.FeedsRepositoryProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 26.06.18.
 */
class FeedsMainScreenUseCase @Inject constructor(private val feedsRepositoryProvider: FeedsRepositoryProvider) : BaseObservableUseCase() {
    override fun buildObservable(): Observable<*> = feedsRepositoryProvider.getMainFeeds()
}
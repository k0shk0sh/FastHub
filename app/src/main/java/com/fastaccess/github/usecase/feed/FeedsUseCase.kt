package com.fastaccess.github.usecase.feed

import com.fastaccess.data.repository.FeedsRepositoryProvider
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.response.FeedResponse
import com.fastaccess.domain.response.PageableResponse
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 26.06.18.
 */
class FeedsUseCase @Inject constructor(
    private val feedsRepositoryProvider: FeedsRepositoryProvider,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {
    var page: Int = 0
    override fun buildObservable(): Observable<PageableResponse<FeedResponse>> = feedsRepositoryProvider.getReceivedEvents(page)
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())
}
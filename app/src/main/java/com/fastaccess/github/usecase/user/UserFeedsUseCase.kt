package com.fastaccess.github.usecase.user

import com.fastaccess.data.repository.FeedsRepositoryProvider
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.response.FeedResponse
import com.fastaccess.domain.response.PageableResponse
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 20.10.18.
 */
class UserFeedsUseCase @Inject constructor(
    private val feedsRepositoryProvider: FeedsRepositoryProvider,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {
    var page: Int = 0
    var login: String? = null

    override fun buildObservable(): Observable<PageableResponse<FeedResponse>> = when {
        login.isNullOrEmpty() -> Observable.empty()
        else -> feedsRepositoryProvider.getFeeds(login ?: "", page)
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.ioThread())
    }
}
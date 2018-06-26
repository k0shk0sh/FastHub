package com.fastaccess.data.repository

import com.fastaccess.domain.repository.services.UserService
import com.fastaccess.domain.response.FeedResponse
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 26.06.18.
 */
class FeedsRepositoryProvider @Inject constructor(private val userService: UserService,
                                                  private val loginRepositoryProvider: LoginRepositoryProvider) : FeedsRepository {
    override fun getMainFeeds(): Observable<List<FeedResponse>> = loginRepositoryProvider.getLogin()
            .filter { !it.login.isNullOrEmpty() }
            .flatMapObservable { userService.getMainScreenReceivedEvents(it.login ?: "") }
            .map { it.items ?: arrayListOf() }

    override fun getFeeds(page: Int): Observable<List<FeedResponse>> = loginRepositoryProvider.getLogin()
            .filter { !it.login.isNullOrEmpty() }
            .flatMapObservable { userService.getReceivedEvents(it.login ?: "", page) }
            .map { it.items ?: arrayListOf() }

}

interface FeedsRepository {
    fun getMainFeeds(): Observable<List<FeedResponse>>
    fun getFeeds(page: Int): Observable<List<FeedResponse>>
}
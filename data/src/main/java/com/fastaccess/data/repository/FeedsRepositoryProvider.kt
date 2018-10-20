package com.fastaccess.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.fastaccess.data.persistence.dao.FeedDao
import com.fastaccess.data.persistence.models.FeedModel
import com.fastaccess.domain.repository.services.UserService
import com.fastaccess.domain.response.FeedResponse
import com.fastaccess.domain.response.PageableResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 26.06.18.
 */
class FeedsRepositoryProvider @Inject constructor(
        private val feedsDao: FeedDao,
        private val userService: UserService,
        private val loginRepositoryProvider: LoginRepositoryProvider,
        private val gson: Gson
) : FeedsRepository {

    override fun getFeeds(login: String): DataSource.Factory<Int, FeedModel> = feedsDao.getFeeds(login)
    override fun getMainFeedsAsLiveData(): LiveData<List<FeedModel>> = feedsDao.getMainFeeds()
    override fun deleteAll() = feedsDao.deleteAll()

    override fun getMainFeeds(): Observable<*> = loginRepositoryProvider.getLogin()
            .filter { !it.login.isNullOrEmpty() }
            .toObservable()
            .flatMap({ it ->
                userService.getMainScreenReceivedEvents(it.login ?: "")
                        .map { gson.fromJson<List<FeedModel>>(gson.toJson(it.items), object : TypeToken<List<FeedModel>>() {}.type) }
            }, { _, list ->
                feedsDao.deleteAll()
                feedsDao.insert(list)
                return@flatMap true // we don't care about the return statement
            })

    override fun getFeeds(login: String, page: Int): Observable<PageableResponse<FeedResponse>> = userService.getUserEvents(login, page)
            .map { response ->
                val list = gson.fromJson<List<FeedModel>>(gson.toJson(response.items), object : TypeToken<List<FeedModel>>() {}.type)
                if (page <= 1) feedsDao.deleteAll(login)
                val newList = list.asSequence().map { feeds ->
                    feeds.login = login
                    feeds
                }.toList()
                feedsDao.insert(newList)
                return@map response
            }
}

interface FeedsRepository {
    fun getMainFeeds(): Observable<*>
    fun getFeeds(login: String, page: Int): Observable<*>
    fun getFeeds(login: String): DataSource.Factory<Int, FeedModel>
    fun getMainFeedsAsLiveData(): LiveData<List<FeedModel>>
    fun deleteAll()
}
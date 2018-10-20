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
            .flatMap { userService.getMainScreenReceivedEvents(it.login ?: "") }
            .map { response ->
                val list = gson.fromJson<List<FeedModel>>(gson.toJson(response.items), object : TypeToken<List<FeedModel>>() {}.type)
                feedsDao.deleteAll()
                list?.let { feedsDao.insert(it) }
            }

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

    override fun getReceivedEvents(page: Int): Observable<PageableResponse<FeedResponse>> = loginRepositoryProvider.getLogin()
            .filter { !it.login.isNullOrEmpty() }
            .toObservable()
            .flatMap { userService.getReceivedEvents(it.login ?: "", page) }
            .map { response ->
                val list = gson.fromJson<List<FeedModel>>(gson.toJson(response.items), object : TypeToken<List<FeedModel>>() {}.type)
                if (page <= 1) feedsDao.deleteAll()
                list?.let { feedsDao.insert(it) }
                return@map response
            }

    override fun getReceivedEventAsLiveData(): DataSource.Factory<Int, FeedModel> = feedsDao.getReceivedEventAsLiveData()
}

interface FeedsRepository {
    fun getMainFeeds(): Observable<*>
    fun getFeeds(login: String, page: Int): Observable<PageableResponse<FeedResponse>>
    fun getFeeds(login: String): DataSource.Factory<Int, FeedModel>
    fun getMainFeedsAsLiveData(): LiveData<List<FeedModel>>
    fun getReceivedEvents(page: Int): Observable<PageableResponse<FeedResponse>>
    fun getReceivedEventAsLiveData(): DataSource.Factory<Int, FeedModel>
    fun deleteAll()
}
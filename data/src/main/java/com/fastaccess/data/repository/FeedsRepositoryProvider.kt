package com.fastaccess.data.repository

import androidx.lifecycle.LiveData
import com.fastaccess.data.persistence.dao.FeedDao
import com.fastaccess.data.persistence.models.FeedModel
import com.fastaccess.domain.repository.services.UserService
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

    override fun getFeeds(): LiveData<List<FeedModel>> = feedsDao.getFeeds()
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

    override fun getFeeds(page: Int): Observable<*> = loginRepositoryProvider.getLogin()
            .filter { !it.login.isNullOrEmpty() }
            .toObservable()
            .flatMap({ it ->
                userService.getReceivedEvents(it.login ?: "", page)
                        .map { gson.fromJson<List<FeedModel>>(gson.toJson(it.items), object : TypeToken<List<FeedModel>>() {}.type) }
            }, { _, list ->
                if (page <= 1) feedsDao.deleteAll()
                feedsDao.insert(list)
                return@flatMap true // we don't care about the return statement
            })

}

interface FeedsRepository {
    fun getMainFeeds(): Observable<*>
    fun getFeeds(page: Int): Observable<*>
    fun getFeeds(): LiveData<List<FeedModel>>
    fun getMainFeedsAsLiveData(): LiveData<List<FeedModel>>
    fun deleteAll()
}
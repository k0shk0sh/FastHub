package com.fastaccess.data.repository

import androidx.lifecycle.LiveData
import com.fastaccess.data.persistence.dao.FeedDao
import com.fastaccess.data.persistence.models.FeedModel
import com.fastaccess.domain.repository.services.UserService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import java.util.*
import javax.inject.Inject

/**
 * Created by Kosh on 26.06.18.
 */
class FeedsRepositoryProvider @Inject constructor(private val feedsDao: FeedDao,
                                                  private val userService: UserService,
                                                  private val loginRepositoryProvider: LoginRepositoryProvider,
                                                  private val gson: Gson) : FeedsRepository {
    override fun getFeeds(login: String): LiveData<List<FeedModel>> = feedsDao.getFeeds(login)
    override fun getMainFeeds(login: String): LiveData<List<FeedModel>> = feedsDao.getMainFeeds(login)
    override fun deleteAll() = feedsDao.deleteAll()
    override fun deleteOldFeeds() = feedsDao.deleteOldFeeds()

    override fun getMainFeeds(): Observable<*> = loginRepositoryProvider.getLogin()
            .filter { !it.login.isNullOrEmpty() }
            .toObservable()
            .flatMap({ it ->
                userService.getMainScreenReceivedEvents(it.login ?: "")
                        .map { gson.fromJson<List<FeedModel>>(gson.toJson(it.items), object : TypeToken<List<FeedModel>>() {}.type) }
            }, { user, list ->
                feedsDao.deleteOldFeeds()
                list.forEach {
                    it.savedDate = Date()
                    it.login = user.login
                    feedsDao.upsert(it)
                }
                return@flatMap true // we don't care about the return statement
            })

    override fun getFeeds(page: Int): Observable<*> = loginRepositoryProvider.getLogin()
            .filter { !it.login.isNullOrEmpty() }
            .toObservable()
            .flatMap({ it ->
                userService.getReceivedEvents(it.login ?: "", page)
                        .map { gson.fromJson<List<FeedModel>>(gson.toJson(it.items), object : TypeToken<List<FeedModel>>() {}.type) }
            }, { user, list ->
                feedsDao.deleteOldFeeds()
                list.forEach {
                    it.login = user.login
                    feedsDao.upsert(it)
                }
                return@flatMap true // we don't care about the return statement
            })

}

interface FeedsRepository {
    fun getMainFeeds(): Observable<*>
    fun getFeeds(page: Int): Observable<*>
    fun getFeeds(login: String): LiveData<List<FeedModel>>
    fun getMainFeeds(login: String): LiveData<List<FeedModel>>
    fun deleteAll()
    fun deleteOldFeeds()
}
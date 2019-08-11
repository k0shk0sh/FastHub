package com.fastaccess.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.persistence.dao.UserFollowersFollowingsDao
import com.fastaccess.data.persistence.models.FollowingFollowerModel
import com.fastaccess.data.persistence.models.FollowingsFollowersModel
import github.GetProfileFollowersQuery
import github.GetProfileFollowingQuery
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 11.10.18.
 */
class UserFollowersFollowingRepositoryProvider @Inject constructor(
    private val dao: UserFollowersFollowingsDao,
    private val apolloClient: ApolloClient
) : FollowersFollowingRepository {

    override fun getFollowersFromRemote(
        login: String,
        page: String?
    ): Observable<FollowingsFollowersModel> {
        return Rx2Apollo.from(apolloClient.query(GetProfileFollowersQuery(login, Input.optional(page))))
            .filter { !it.hasErrors() }
            .map {
                val data = FollowingsFollowersModel.newFollowersInstance(it.data(), login)
                if (page.isNullOrBlank()) dao.deleteAll(login, true)
                data?.users?.let { repos -> dao.insert(repos) }
                return@map data
            }
    }

    override fun getFollowingFromRemote(
        login: String,
        page: String?
    ): Observable<FollowingsFollowersModel> {
        return Rx2Apollo.from(apolloClient.query(GetProfileFollowingQuery(login, Input.optional(page))))
            .filter { !it.hasErrors() }
            .map {
                val data = FollowingsFollowersModel.newFollowingInstance(it.data(), login)
                if (page.isNullOrBlank()) dao.deleteAll(login, false)
                data?.users?.let { repos -> dao.insert(repos) }
                return@map data
            }
    }

    override fun getFollowersOrFollowing(
        login: String,
        isFollowers: Boolean
    ): DataSource.Factory<Int, FollowingFollowerModel> {
        return when (isFollowers) {
            true -> dao.getFollowers(login)
            else -> dao.getFollowing(login)
        }
    }

    override fun getUser(login: String): LiveData<FollowingFollowerModel> = dao.getUser(login)
    override fun deleteAll() = dao.deleteAll()
    override fun deleteAll(
        login: String,
        isFollowers: Boolean
    ) = dao.deleteAll(login, isFollowers)

}

interface FollowersFollowingRepository {
    fun getFollowersFromRemote(
        login: String,
        page: String?
    ): Observable<FollowingsFollowersModel>

    fun getFollowingFromRemote(
        login: String,
        page: String?
    ): Observable<FollowingsFollowersModel>

    fun getFollowersOrFollowing(
        login: String,
        isFollowers: Boolean
    ): DataSource.Factory<Int, FollowingFollowerModel>

    fun getUser(login: String): LiveData<FollowingFollowerModel>
    fun deleteAll()
    fun deleteAll(
        login: String,
        isFollowers: Boolean
    )
}


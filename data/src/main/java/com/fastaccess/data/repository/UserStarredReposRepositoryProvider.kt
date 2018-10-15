package com.fastaccess.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.persistence.dao.UserStarredReposDao
import com.fastaccess.data.persistence.models.ProfileStarredRepoModel
import com.fastaccess.data.persistence.models.ProfileStarredReposModel
import github.GetProfileStarredReposQuery
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 11.10.18.
 */
class UserStarredReposRepositoryProvider @Inject constructor(
        private val reposDao: UserStarredReposDao,
        private val apolloClient: ApolloClient
) : UserStarredReposRepository {

    override fun getStarredReposFromRemote(login: String, page: String?): Observable<ProfileStarredReposModel> {
        return Rx2Apollo.from(apolloClient.query(GetProfileStarredReposQuery(login, Input.optional(page))))
                .filter { !it.hasErrors() }
                .map {
                    val data = ProfileStarredReposModel.newInstance(it.data(), login)
                    if (page.isNullOrBlank()) reposDao.deleteAll(login)
                    data?.repos?.let { repos -> reposDao.insert(repos) }
                    return@map data
                }
    }

    override fun getStarredRepos(login: String): DataSource.Factory<Int, ProfileStarredRepoModel> = reposDao.getStarredRepos(login)
    override fun getStarredRepo(id: String): LiveData<ProfileStarredRepoModel> = reposDao.getStarredRepo(id)
    override fun deleteAll() = reposDao.deleteAll()
    override fun deleteAll(login: String) = reposDao.deleteAll(login)

}

interface UserStarredReposRepository {
    fun getStarredReposFromRemote(login: String, page: String?): Observable<ProfileStarredReposModel>
    fun getStarredRepos(login: String): DataSource.Factory<Int, ProfileStarredRepoModel>
    fun getStarredRepo(id: String): LiveData<ProfileStarredRepoModel>
    fun deleteAll()
    fun deleteAll(login: String)
}


package com.fastaccess.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.persistence.dao.UserReposDao
import com.fastaccess.data.persistence.models.ProfileRepoModel
import com.fastaccess.data.persistence.models.ProfileReposModel
import github.GetProfileReposQuery
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 11.10.18.
 */
class UserReposRepositoryProvider @Inject constructor(
        private val reposDao: UserReposDao,
        private val apolloClient: ApolloClient
) : UserReposRepository {

    override fun getReposFromRemote(login: String, page: String?): Observable<ProfileReposModel> {
        return Rx2Apollo.from(apolloClient.query(GetProfileReposQuery(login, Input.optional(page))))
                .filter { !it.hasErrors() }
                .map {
                    val data = ProfileReposModel.newInstance(it.data(), login)
                    if (page.isNullOrBlank()) reposDao.deleteAll(login)
                    data?.repos?.let { repos -> reposDao.insert(repos) }
                    return@map data
                }

    }

    override fun getRepos(login: String): DataSource.Factory<Int, ProfileRepoModel> = reposDao.getRepos(login)
    override fun getRepo(id: String): LiveData<ProfileRepoModel> = reposDao.getRepo(id)
    override fun deleteAll() = reposDao.deleteAll()
    override fun deleteAll(login: String) = reposDao.deleteAll(login)

}

interface UserReposRepository {
    fun getReposFromRemote(login: String, page: String?): Observable<ProfileReposModel>
    fun getRepos(login: String): DataSource.Factory<Int, ProfileRepoModel>
    fun getRepo(id: String): LiveData<ProfileRepoModel>
    fun deleteAll()
    fun deleteAll(login: String)
}


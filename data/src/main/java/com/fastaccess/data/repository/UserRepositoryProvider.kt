package com.fastaccess.data.repository

import androidx.lifecycle.LiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.CountModel
import com.fastaccess.data.persistence.dao.UserDao
import com.fastaccess.data.persistence.models.*
import github.GetProfileQuery
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 10.06.18.
 */

class UserRepositoryProvider @Inject constructor(private val userDao: UserDao,
                                                 private val apolloClient: ApolloClient) : UserRepository {

    override fun getUserFromRemote(login: String): Observable<UserModel> = Rx2Apollo.from(apolloClient.query(GetProfileQuery(login)))
            .filter { !it.hasErrors() }
            .map { it ->
                return@map it.data()?.user?.let { queryUser ->
                    UserModel(queryUser.databaseId
                            ?: 0, queryUser.login, queryUser.avatarUrl.toString(), queryUser.url.toString(), queryUser.name, queryUser.company,
                            queryUser.websiteUrl.toString(), queryUser.location, queryUser.email, queryUser.bio, queryUser.createdAt,
                            queryUser.createdAt, queryUser.isViewerCanFollow, queryUser.isViewerIsFollowing, queryUser.isViewer,
                            queryUser.isDeveloperProgramMember, CountModel(queryUser.followers.totalCount),
                            CountModel(queryUser.following.totalCount),
                            UserOrganizationModel(queryUser.organizations.totalCount, queryUser.organizations.nodes?.asSequence()?.map {
                                UserOrganizationNodesModel(it.avatarUrl.toString(), it.location, it.email, it.login, it.name)
                            }?.toList()), UserPinnedReposModel(queryUser.pinnedRepositories.totalCount,
                            queryUser.pinnedRepositories.nodes?.asSequence()?.map {
                                UserPinnedRepoNodesModel(it.name, it.nameWithOwner,
                                        UserPinnedRepoLanguageModel(it.primaryLanguage?.name, it.primaryLanguage?.color),
                                        CountModel(it.stargazers.totalCount), CountModel(it.issues.totalCount),
                                        CountModel(it.pullRequests.totalCount), it.forkCount)
                            }?.toList()))
                }?.apply {
                    userDao.upsert(this)
                }
            }

    override fun getUsers(): LiveData<List<UserModel>> = userDao.getUsers()
    override fun getUser(login: String): LiveData<UserModel> = userDao.getUser(login)
    override fun deleteAll() = userDao.deleteAll()
}

interface UserRepository {
    fun getUsers(): LiveData<List<UserModel>>
    fun getUser(login: String): LiveData<UserModel>
    fun getUserFromRemote(login: String): Observable<UserModel>
    fun deleteAll()
}

package com.fastaccess.data.repository

import androidx.lifecycle.LiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.CountModel
import com.fastaccess.data.model.RepoLanguageModel
import com.fastaccess.data.persistence.dao.UserDao
import com.fastaccess.data.persistence.models.*
import com.fastaccess.domain.repository.services.UserService
import github.GetProfileQuery
import io.reactivex.Observable
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by Kosh on 10.06.18.
 */

class UserRepositoryProvider @Inject constructor(private val userDao: UserDao,
                                                 private val apolloClient: ApolloClient,
                                                 private val userService: UserService) : UserRepository {

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
                            RepoLanguageModel(it.primaryLanguage?.name, it.primaryLanguage?.color),
                            CountModel(it.stargazers.totalCount), CountModel(it.issues.totalCount),
                            CountModel(it.pullRequests.totalCount), it.forkCount)
                    }?.toList()))
            }?.apply {
                userDao.upsert(this)
            }
        }

    override fun getUsers(): LiveData<List<UserModel>> = userDao.getUsers()
    override fun getUserBlocking(login: String): UserModel? = userDao.getUserBlocking(login)
    override fun getUser(login: String): LiveData<UserModel> = userDao.getUser(login)
    override fun deleteAll() = userDao.deleteAll()
    override fun updateUser(userModel: UserModel) = userDao.update(userModel)
    override fun isUserBlock(login: String): Observable<Response<Boolean>> = userService.isUserBlocked(login)

    override fun blockUnblockUser(login: String, block: Boolean): Observable<Response<Boolean>> = when (block) {
        true -> userService.blockUser(login)
        else -> userService.unBlockUser(login)
    }

    override fun followUnfollowUser(login: String, follow: Boolean): Observable<Response<Boolean>> = when (follow) {
        true -> userService.followUser(login)
        else -> userService.unfollowUser(login)
    }
}

interface UserRepository {
    fun getUsers(): LiveData<List<UserModel>>
    fun getUser(login: String): LiveData<UserModel>
    fun getUserBlocking(login: String): UserModel?
    fun getUserFromRemote(login: String): Observable<UserModel>
    fun deleteAll()
    fun isUserBlock(login: String): Observable<Response<Boolean>>
    fun blockUnblockUser(login: String, block: Boolean): Observable<Response<Boolean>>
    fun followUnfollowUser(login: String, follow: Boolean): Observable<Response<Boolean>>
    fun updateUser(userModel: UserModel)
}

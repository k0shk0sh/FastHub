package com.fastaccess.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.persistence.dao.UserGistsDao
import com.fastaccess.data.persistence.models.ProfileGistModel
import com.fastaccess.data.persistence.models.GistsModel
import github.GetProfileGistsQuery
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 11.10.18.
 */
class UserGistsRepositoryProvider @Inject constructor(
        private val dao: UserGistsDao,
        private val apolloClient: ApolloClient
) : UserGistsRepository {

    override fun getGistsFromRemote(login: String, page: String?): Observable<GistsModel> {
        return Rx2Apollo.from(apolloClient.query(GetProfileGistsQuery(login, Input.optional(page))))
                .filter { !it.hasErrors() }
                .map {
                    val data = GistsModel.newInstance(it.data(), login)
                    if (page.isNullOrBlank()) dao.deleteAll(login)
                    data?.gists?.let { repos -> dao.insert(repos) }
                    return@map data
                }

    }

    override fun getGists(login: String): DataSource.Factory<Int, ProfileGistModel> = dao.getGists(login)
    override fun getGist(id: String): LiveData<ProfileGistModel> = dao.getGist(id)
    override fun deleteAll() = dao.deleteAll()
    override fun deleteAll(login: String) = dao.deleteAll(login)

}

interface UserGistsRepository {
    fun getGistsFromRemote(login: String, page: String?): Observable<GistsModel>
    fun getGists(login: String): DataSource.Factory<Int, ProfileGistModel>
    fun getGist(id: String): LiveData<ProfileGistModel>
    fun deleteAll()
    fun deleteAll(login: String)
}


package com.fastaccess.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.persistence.dao.OrgsDao
import com.fastaccess.data.persistence.models.OrganizationModel
import com.fastaccess.data.persistence.models.OrgsModel
import github.GetProfileOrgsQuery
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 10.06.18.
 */

class OrgRepositoryProvider @Inject constructor(
    private val dao: OrgsDao,
    private val apolloClient: ApolloClient,
    private val loginRepositoryProvider: LoginRepositoryProvider
) : OrgRepository {

    /**
     * TODO(extract this to a usecase and all the other getFromRemote funs from other providers.)
     */
    override fun getOrgFromRemote(page: String?): Observable<OrgsModel> = loginRepositoryProvider.getLogin()
        .flatMapObservable { Rx2Apollo.from(apolloClient.query(GetProfileOrgsQuery(it.login ?: "", Input.optional(page)))) }
        .filter { !it.hasErrors() }
        .map {
            val data = OrgsModel.newInstance(it.data())
            if (page.isNullOrBlank()) dao.deleteAll()
            data?.orgs?.let { orgs -> dao.insert(orgs) }
            return@map data
        }

    override fun getOrgs():  DataSource.Factory<Int, OrganizationModel> = dao.getOrgs()
    override fun getOrgBlocking(login: String): OrganizationModel? = dao.getOrgBlocking(login)
    override fun getOrg(login: String): LiveData<OrganizationModel> = dao.getOrg(login)
    override fun deleteAll() = dao.deleteAll()
}

interface OrgRepository {
    fun getOrgs(): DataSource.Factory<Int, OrganizationModel>
    fun getOrg(login: String): LiveData<OrganizationModel>
    fun getOrgBlocking(login: String): OrganizationModel?
    fun getOrgFromRemote(page: String?): Observable<OrgsModel>
    fun deleteAll()
}
